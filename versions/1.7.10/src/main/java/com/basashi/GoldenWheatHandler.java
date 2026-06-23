package com.basashi;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.EntityInteractEvent;

public final class GoldenWheatHandler {

    @SubscribeEvent
    public void onInteract(EntityInteractEvent event) {
        if (!(event.target instanceof EntityHorse)) {
            return;
        }
        EntityHorse horse = (EntityHorse) event.target;
        EntityPlayer player = event.entityPlayer;
        ItemStack stack = player.getHeldItem();
        if (stack == null || stack.getItem() != ModItems.GOLDEN_WHEAT) {
            return;
        }

        World world = horse.worldObj;
        boolean server = !world.isRemote;
        boolean acted = false;

        // 回復
        if (horse.getHealth() < horse.getMaxHealth()) {
            if (server) {
                horse.heal(4.0F);
            }
            acted = true;
        }
        // 子馬の成長促進
        if (horse.isChild()) {
            if (server) {
                horse.addGrowth(10);
            }
            acted = true;
        }

        if (!horse.isTame()) {
            // 手なずけ
            if (server) {
                horse.increaseTemper(5);
                if (world.rand.nextInt(100) < horse.getTemper()) {
                    horse.setTamedBy(player);
                    world.setEntityState(horse, (byte) 7);
                } else {
                    world.setEntityState(horse, (byte) 6);
                }
            }
            acted = true;
        } else if (!horse.isChild() && horse.getGrowingAge() == 0 && !horse.isInLove()) {
            // 繁殖（func_146082_f = setInLove）
            if (server) {
                horse.func_146082_f(player);
            }
            acted = true;
        }

        if (!acted) {
            return;
        }

        if (server && !player.capabilities.isCreativeMode) {
            stack.stackSize--;
            if (stack.stackSize <= 0) {
                player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
            }
        }
        player.swingItem();
        event.setCanceled(true);
    }
}
