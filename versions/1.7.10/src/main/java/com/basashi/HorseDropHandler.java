package com.basashi;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import java.util.Random;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingDropsEvent;

public final class HorseDropHandler {

    @SubscribeEvent
    public void onLivingDrops(LivingDropsEvent event) {
        if (!(event.entityLiving instanceof EntityHorse)) {
            return;
        }
        EntityHorse horse = (EntityHorse) event.entityLiving;
        World world = horse.worldObj;
        if (world.isRemote) {
            return;
        }

        DamageSource source = event.source;
        boolean fiery = horse.isBurning() || source.isFireDamage();

        int slaughter = 0;
        if (source.getEntity() instanceof EntityLivingBase) {
            EntityLivingBase killer = (EntityLivingBase) source.getEntity();
            slaughter = EnchantmentHelper.getEnchantmentLevel(ModEnchantments.SLAUGHTER_ID, killer.getHeldItem());
        }

        // 子供の馬: 屠殺I以下はドロップ無し。屠殺IIは大人と同じ物を基本量で（個数バフ無し）、IIIは大人同様にバフ
        boolean baby = horse.isChild();
        if (baby && slaughter < 2) {
            return;
        }
        int level = (baby && slaughter < 3) ? 0 : (event.lootingLevel + slaughter);

        Random rng = world.rand;

        int count = 1 + rng.nextInt(2) + (level > 0 ? rng.nextInt(level + 1) : 0);
        drop(event, horse, new ItemStack(fiery ? ModItems.HORSE_TATAKI : ModItems.BASASHI, count));

        if (rng.nextFloat() < 0.05F + 0.05F * level) {
            drop(event, horse, new ItemStack(ModItems.HORSE_LIVER, 1));
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
            drop(event, horse, new ItemStack(Items.bone, bones));
        }
    }

    private static void drop(LivingDropsEvent event, EntityHorse horse, ItemStack stack) {
        EntityItem ei = new EntityItem(horse.worldObj, horse.posX, horse.posY, horse.posZ, stack);
        ei.delayBeforeCanPickup = 10;
        event.drops.add(ei);
    }
}
