package com.basashi;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LootingLevelEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

// 屠殺エンチャントの「全動物向け」効果。馬の独自ドロップ(基本量・レバー・骨)は HorseDropHandler が担当するため
// EntityHorse は対象外（looting 二重加算を避ける）。
@Mod.EventBusSubscriber(modid = BasashiMod.MOD_ID)
public final class SlaughterBoostHandler {

    private static final String BABY_TAG = "basashi_was_baby";
    private static final String AGE_TAG = "basashi_baby_age";

    private SlaughterBoostHandler() {}

    private static int slaughterOf(DamageSource source) {
        if (source != null && source.getTrueSource() instanceof EntityLivingBase) {
            EntityLivingBase killer = (EntityLivingBase) source.getTrueSource();
            return EnchantmentHelper.getEnchantmentLevel(ModEnchantments.SLAUGHTER, killer.getHeldItemMainhand());
        }
        return 0;
    }

    private static boolean targets(Object entity) {
        return entity instanceof EntityAnimal && !(entity instanceof EntityHorse);
    }

    // 子供の動物: 屠殺II以上なら一時的に大人化して大人と同じ戦利品を出す（屠殺Iは子供効果なし）
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onDeath(LivingDeathEvent event) {
        if (event.getEntityLiving().world.isRemote || !targets(event.getEntityLiving())) {
            return;
        }
        EntityAnimal animal = (EntityAnimal) event.getEntityLiving();
        if (!animal.isChild() || slaughterOf(event.getSource()) < 2) {
            return;
        }
        // 元の年齢を保存し、個数バフ判定用フラグを立ててから大人化
        animal.getEntityData().setBoolean(BABY_TAG, true);
        animal.getEntityData().setInteger(AGE_TAG, animal.getGrowingAge());
        animal.setGrowingAge(0);
    }

    // 屠殺を Looting に加算して、全動物のドロップ数・レア率を上げる
    @SubscribeEvent
    public static void onLooting(LootingLevelEvent event) {
        if (!targets(event.getEntityLiving())) {
            return;
        }
        int level = slaughterOf(event.getDamageSource());
        if (level <= 0) {
            return;
        }
        // 子供は I/II では個数バフ無し（大人と同じ物を落とすだけ）、III のみバフ
        if (event.getEntityLiving().getEntityData().getBoolean(BABY_TAG) && level < 3) {
            return;
        }
        event.setLootingLevel(event.getLootingLevel() + level);
    }

    // 戦利品確定後（looting計算の後）に年齢を子供へ戻し、死体が大人に見えるのを防ぐ。
    // 年齢変更は同tick内で相殺されるため、クライアントには子供のまま同期される。
    @SubscribeEvent
    public static void onDrops(LivingDropsEvent event) {
        if (!(event.getEntityLiving() instanceof EntityAnimal)) {
            return;
        }
        EntityAnimal animal = (EntityAnimal) event.getEntityLiving();
        if (animal.getEntityData().getBoolean(BABY_TAG)) {
            animal.setGrowingAge(animal.getEntityData().getInteger(AGE_TAG));
            animal.getEntityData().removeTag(BABY_TAG);
            animal.getEntityData().removeTag(AGE_TAG);
        }
    }
}
