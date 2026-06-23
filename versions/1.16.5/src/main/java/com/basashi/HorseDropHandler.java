package com.basashi;

import java.util.Random;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.horse.HorseEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public final class HorseDropHandler {

    @SubscribeEvent
    public void onLivingDeath(LivingDeathEvent event) {
        if (!(event.getEntityLiving() instanceof HorseEntity)) {
            return;
        }
        HorseEntity horse = (HorseEntity) event.getEntityLiving();
        if (horse.level.isClientSide) {
            return;
        }

        DamageSource source = event.getSource();
        boolean fiery = horse.isOnFire() || source.isFire();

        int looting = 0;
        int slaughter = 0;
        if (source.getEntity() instanceof LivingEntity) {
            LivingEntity killer = (LivingEntity) source.getEntity();
            looting = EnchantmentHelper.getMobLooting(killer);
            slaughter = EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.SLAUGHTER.get(), killer.getMainHandItem());
        }
        int level = looting + slaughter;

        Random rng = horse.level.random;

        int count = 1 + rng.nextInt(2) + (level > 0 ? rng.nextInt(level + 1) : 0);
        horse.spawnAtLocation(new ItemStack(fiery ? ModItems.HORSE_TATAKI.get() : ModItems.BASASHI.get(), count));

        if (rng.nextFloat() < 0.05F + 0.05F * level) {
            horse.spawnAtLocation(new ItemStack(ModItems.HORSE_LIVER.get(), 1));
        }

        float boneChance = 0.40F + 0.15F * level;
        int bones = 0;
        if (rng.nextFloat() < boneChance) {
            bones++;
            if (rng.nextFloat() < boneChance) {
                bones++;
            }
        }
        if (bones > 0) {
            horse.spawnAtLocation(new ItemStack(Items.BONE, bones));
        }
    }
}
