package com.basashi;

import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = BasashiMod.MOD_ID)
public final class GoldenWheatHandler {

    private GoldenWheatHandler() {}

    @SubscribeEvent
    public static void onInteract(PlayerInteractEvent.EntityInteract event) {
        if (!(event.getTarget() instanceof EntityHorse)) {
            return;
        }
        EntityHorse horse = (EntityHorse) event.getTarget();
        EntityPlayer player = event.getEntityPlayer();
        EnumHand hand = event.getHand();
        ItemStack stack = player.getHeldItem(hand);
        if (stack.getItem() != ModItems.GOLDEN_WHEAT) {
            return;
        }

        boolean server = !horse.world.isRemote;
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
                if (horse.world.rand.nextInt(100) < horse.getTemper()) {
                    horse.setHorseTamed(true);
                    horse.setOwnerUniqueId(player.getUniqueID());
                    horse.world.setEntityState(horse, (byte) 7);
                } else {
                    horse.world.setEntityState(horse, (byte) 6);
                }
            }
            acted = true;
        } else if (horse.getGrowingAge() == 0 && !horse.isInLove()) {
            // 繁殖
            if (server) {
                horse.setInLove(player);
            }
            acted = true;
        }

        if (!acted) {
            return;
        }

        if (server && !player.capabilities.isCreativeMode) {
            stack.shrink(1);
        }
        player.swingArm(hand);
        event.setCanceled(true);
        event.setCancellationResult(EnumActionResult.SUCCESS);
    }
}
