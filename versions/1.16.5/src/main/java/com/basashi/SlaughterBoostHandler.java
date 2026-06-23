package com.basashi;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.horse.HorseEntity;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LootingLevelEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

// 屠殺エンチャントの「全動物向け」効果。馬の独自ドロップは HorseDropHandler が担当するため
// HorseEntity は対象外（looting 二重加算を避ける）。
@Mod.EventBusSubscriber(modid = BasashiMod.MOD_ID)
public final class SlaughterBoostHandler {

    private static final String BABY_TAG = "basashi_was_baby";
    private static final String AGE_TAG = "basashi_baby_age";

    private SlaughterBoostHandler() {}

    private static int slaughterOf(DamageSource source) {
        if (source != null && source.getEntity() instanceof LivingEntity) {
            LivingEntity killer = (LivingEntity) source.getEntity();
            return EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.SLAUGHTER.get(), killer.getMainHandItem());
        }
        return 0;
    }

    private static boolean targets(Object entity) {
        return entity instanceof AnimalEntity && !(entity instanceof HorseEntity);
    }

    // 子供の動物: 屠殺II以上なら一時的に大人化して大人と同じ戦利品を出す（屠殺Iは子供効果なし）
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onDeath(LivingDeathEvent event) {
        LivingEntity entity = event.getEntityLiving();
        if (entity.level.isClientSide || !targets(entity)) {
            return;
        }
        AnimalEntity animal = (AnimalEntity) entity;
        if (!animal.isBaby() || slaughterOf(event.getSource()) < 2) {
            return;
        }
        // 元の年齢を保存し、個数バフ判定用フラグを立ててから大人化
        animal.getPersistentData().putBoolean(BABY_TAG, true);
        animal.getPersistentData().putInt(AGE_TAG, animal.getAge());
        animal.setAge(0);
    }

    // 屠殺を Looting に加算して、全動物のドロップ数・レア率を上げる
    @SubscribeEvent
    public static void onLooting(LootingLevelEvent event) {
        LivingEntity entity = event.getEntityLiving();
        if (!targets(entity)) {
            return;
        }
        int level = slaughterOf(event.getDamageSource());
        if (level <= 0) {
            return;
        }
        // 子供は II では個数バフ無し（大人と同じ物を落とすだけ）、III のみバフ
        if (entity.getPersistentData().getBoolean(BABY_TAG) && level < 3) {
            return;
        }
        event.setLootingLevel(event.getLootingLevel() + level);
    }

    // 戦利品確定後（looting計算の後）に年齢を子供へ戻し、死体が大人に見えるのを防ぐ。
    // 年齢変更は同tick内で相殺されるため、クライアントには子供のまま同期される。
    @SubscribeEvent
    public static void onDrops(LivingDropsEvent event) {
        LivingEntity entity = event.getEntityLiving();
        if (!(entity instanceof AnimalEntity)) {
            return;
        }
        AnimalEntity animal = (AnimalEntity) entity;
        if (animal.getPersistentData().getBoolean(BABY_TAG)) {
            animal.setAge(animal.getPersistentData().getInt(AGE_TAG));
            animal.getPersistentData().remove(BABY_TAG);
            animal.getPersistentData().remove(AGE_TAG);
        }
    }
}
