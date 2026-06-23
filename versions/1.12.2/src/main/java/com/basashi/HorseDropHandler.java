package com.basashi;

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
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = BasashiMod.MOD_ID)
public final class HorseDropHandler {

    private HorseDropHandler() {}

    @SubscribeEvent
    public static void onLivingDrops(LivingDropsEvent event) {
        if (!(event.getEntityLiving() instanceof EntityHorse)) {
            return;
        }
        EntityHorse horse = (EntityHorse) event.getEntityLiving();
        World world = horse.world;
        if (world.isRemote) {
            return;
        }

        DamageSource source = event.getSource();
        boolean fiery = horse.isBurning() || source.isFireDamage();

        int slaughter = 0;
        if (source.getTrueSource() instanceof EntityLivingBase) {
            EntityLivingBase killer = (EntityLivingBase) source.getTrueSource();
            slaughter = EnchantmentHelper.getEnchantmentLevel(ModEnchantments.SLAUGHTER, killer.getHeldItemMainhand());
        }
        // 子供の馬: 屠殺I以下はドロップ無し。屠殺IIは大人と同じ物を基本量で（個数バフ無し）、IIIは大人同様にバフ
        boolean baby = horse.isChild();
        if (baby && slaughter < 2) {
            return;
        }
        int level = (baby && slaughter < 3) ? 0 : (event.getLootingLevel() + slaughter);

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
            drop(event, horse, new ItemStack(Items.BONE, bones));
        }
    }

    private static void drop(LivingDropsEvent event, EntityHorse horse, ItemStack stack) {
        EntityItem ei = new EntityItem(horse.world, horse.posX, horse.posY, horse.posZ, stack);
        ei.setDefaultPickupDelay();
        event.getDrops().add(ei);
    }
}
