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
import net.minecraft.world.item.Items;
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

        // ドロップ増加: Looting ＋ 屠殺(Slaughter) を合算
        int looting = 0;
        int slaughter = 0;
        if (source.getEntity() instanceof LivingEntity killer) {
            looting = EnchantmentHelper.getMobLooting(killer);
            slaughter = EnchantmentHelper.getItemEnchantmentLevel(
                ModEnchantments.SLAUGHTER.get(),
                killer.getMainHandItem()
            );
        }
        int level = looting + slaughter;

        RandomSource rng = horse.getRandom();

        // 馬刺し / タタキ（燃焼時）: 1〜2個 ＋ レベルごと +0〜1個
        int count = 1 + rng.nextInt(2) + (level > 0 ? rng.nextInt(level + 1) : 0);
        Item main = fiery ? ModItems.HORSE_TATAKI.get() : ModItems.BASASHI.get();
        horse.spawnAtLocation(new ItemStack(main, count));

        // 生の馬レバー（レア）: 5% ＋ レベル×5%
        float liverChance = 0.05F + 0.05F * level;
        if (rng.nextFloat() < liverChance) {
            horse.spawnAtLocation(new ItemStack(ModItems.HORSE_LIVER.get(), 1));
        }

        // 馬の骨（中確率）: 40% ＋ レベル×15%、最大2本
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

        return EventResult.pass();
    }
}
