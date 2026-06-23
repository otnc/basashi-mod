package com.basashi;

import net.minecraft.entity.passive.horse.HorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public final class GoldenWheatHandler {

    @SubscribeEvent
    public void onInteract(PlayerInteractEvent.EntityInteract event) {
        if (!(event.getTarget() instanceof HorseEntity)) {
            return;
        }
        HorseEntity horse = (HorseEntity) event.getTarget();
        PlayerEntity player = event.getPlayer();
        Hand hand = event.getHand();
        ItemStack stack = player.getItemInHand(hand);
        if (stack.getItem() != ModItems.GOLDEN_WHEAT.get()) {
            return;
        }

        boolean server = !horse.level.isClientSide;
        boolean acted = false;

        // 回復
        if (horse.getHealth() < horse.getMaxHealth()) {
            if (server) {
                horse.heal(4.0F);
            }
            acted = true;
        }
        // 子馬の成長促進
        if (horse.isBaby()) {
            if (server) {
                horse.ageUp(10);
            }
            acted = true;
        }

        if (!horse.isTamed()) {
            // 手なずけ
            if (server) {
                horse.modifyTemper(5);
                if (horse.level.random.nextInt(100) < horse.getTemper()) {
                    horse.setTamed(true);
                    horse.setOwnerUUID(player.getUUID());
                    horse.level.broadcastEntityEvent(horse, (byte) 7);
                } else {
                    horse.level.broadcastEntityEvent(horse, (byte) 6);
                }
            }
            acted = true;
        } else if (!horse.isBaby() && horse.canFallInLove()) {
            // 繁殖
            if (server) {
                horse.setInLove(player);
            }
            acted = true;
        }

        if (!acted) {
            return;
        }

        if (server && !player.abilities.instabuild) {
            stack.shrink(1);
        }
        player.swing(hand);
        event.setCanceled(true);
        event.setCancellationResult(ActionResultType.SUCCESS);
    }
}
