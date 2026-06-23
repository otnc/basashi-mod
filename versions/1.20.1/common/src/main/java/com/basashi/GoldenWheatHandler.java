package com.basashi;

import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.InteractionEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public final class GoldenWheatHandler {

    private GoldenWheatHandler() {}

    public static void register() {
        InteractionEvent.INTERACT_ENTITY.register(GoldenWheatHandler::onInteract);
    }

    private static EventResult onInteract(Player player, Entity entity, InteractionHand hand) {
        if (!(entity instanceof Horse horse)) {
            return EventResult.pass();
        }
        ItemStack stack = player.getItemInHand(hand);
        if (!stack.is(ModItems.GOLDEN_WHEAT.get())) {
            return EventResult.pass();
        }

        Level level = horse.level();
        boolean acted = false;

        // 回復
        if (horse.getHealth() < horse.getMaxHealth()) {
            if (!level.isClientSide()) {
                horse.heal(4.0F);
            }
            acted = true;
        }

        // 子馬の成長促進
        if (horse.isBaby()) {
            if (!level.isClientSide()) {
                horse.ageUp(10);
            }
            acted = true;
        }

        if (!horse.isTamed()) {
            // 手なずけ（未テイム）: temper を加算し、確率で手なずけ成立
            if (!level.isClientSide()) {
                horse.modifyTemper(5);
                if (horse.getRandom().nextInt(100) < horse.getTemper()) {
                    horse.setTamed(true);
                    horse.setOwnerUUID(player.getUUID());
                    level.broadcastEntityEvent(horse, (byte) 7);
                } else {
                    level.broadcastEntityEvent(horse, (byte) 6);
                }
            }
            acted = true;
        } else if (!horse.isBaby() && horse.canFallInLove()) {
            // 繁殖（成体・テイム済み）
            if (!level.isClientSide()) {
                horse.setInLove(player);
            }
            acted = true;
        }

        if (!acted) {
            return EventResult.pass();
        }

        if (!level.isClientSide() && !player.getAbilities().instabuild) {
            stack.shrink(1);
        }
        player.swing(hand, true);
        return EventResult.interruptTrue();
    }
}
