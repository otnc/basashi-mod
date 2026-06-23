package com.basashi.forge;

import com.basashi.ModEnchantments;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LootingLevelEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

// 屠殺エンチャントの「全動物向け」効果（Forgeバス）。馬の独自ドロップは common の HorseDropHandler が担当するため
// Horse は対象外（looting 二重加算を避ける）。
public final class SlaughterBoostHandler {

    private static final String BABY_TAG = "basashi_was_baby";
    private static final String AGE_TAG = "basashi_baby_age";

    private static int slaughterOf(DamageSource source) {
        if (source != null && source.getEntity() instanceof LivingEntity killer) {
            return EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.SLAUGHTER.get(), killer.getMainHandItem());
        }
        return 0;
    }

    private static boolean targets(Object entity) {
        return entity instanceof Animal && !(entity instanceof Horse);
    }

    // 子供の動物: 屠殺II以上なら一時的に大人化して大人と同じ戦利品を出す（屠殺Iは子供効果なし）
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onDeath(LivingDeathEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity.level().isClientSide() || !targets(entity)) {
            return;
        }
        Animal animal = (Animal) entity;
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
    public void onLooting(LootingLevelEvent event) {
        LivingEntity entity = event.getEntity();
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
    public void onDrops(LivingDropsEvent event) {
        if (!(event.getEntity() instanceof Animal animal)) {
            return;
        }
        if (animal.getPersistentData().getBoolean(BABY_TAG)) {
            animal.setAge(animal.getPersistentData().getInt(AGE_TAG));
            animal.getPersistentData().remove(BABY_TAG);
            animal.getPersistentData().remove(AGE_TAG);
        }
    }
}
