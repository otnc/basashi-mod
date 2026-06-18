package com.basashi;

import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.EntityEvent;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

public final class HorseDropHandler {

    private HorseDropHandler() {}

    public static void register() {
        EntityEvent.LIVING_DEATH.register(HorseDropHandler::onLivingDeath);
    }

    private static EventResult onLivingDeath(LivingEntity entity, DamageSource source) {
        if (!(entity instanceof Horse horse)) {
            return EventResult.pass();
        }
        if (horse.level().isClientSide()) {
            return EventResult.pass();
        }

        boolean fiery = horse.isOnFire() || source.is(DamageTypeTags.IS_FIRE);

        int looting = 0;
        if (source.getEntity() instanceof LivingEntity killer) {
            looting = EnchantmentHelper.getMobLooting(killer);
        }

        RandomSource rng = horse.getRandom();
        int count = 1 + rng.nextInt(2) + (looting > 0 ? rng.nextInt(looting + 1) : 0);

        Item drop = fiery ? ModItems.UMA_NO_TATAKI.get() : ModItems.BASASHI.get();
        horse.spawnAtLocation(new ItemStack(drop, count));

        return EventResult.pass();
    }
}
