package com.basashi;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;

// 屠殺エンチャントの「全動物向け」効果。馬の独自ドロップは HorseDropHandler が担当するため
// EntityHorse は対象外（二重加算を避ける）。
// 1.7.10 は LootingLevelEvent もルートテーブルも無いため、ドロップ数UPは drops 複製で近似し、
// レア率UPは非対応。子供の大人戦利品は age-reset で実現する。
public final class SlaughterBoostHandler {

    private static final String BABY_TAG = "basashi_was_baby";
    private static final String AGE_TAG = "basashi_baby_age";

    private static int slaughterOf(DamageSource source) {
        if (source != null && source.getEntity() instanceof EntityLivingBase) {
            EntityLivingBase killer = (EntityLivingBase) source.getEntity();
            return EnchantmentHelper.getEnchantmentLevel(ModEnchantments.SLAUGHTER_ID, killer.getHeldItem());
        }
        return 0;
    }

    private static boolean targets(Object e) {
        return e instanceof EntityAnimal && !(e instanceof EntityHorse);
    }

    // 子供の動物: 屠殺II以上なら一時的に大人化して大人と同じ戦利品を出す（屠殺Iは子供効果なし）
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onDeath(LivingDeathEvent event) {
        if (!targets(event.entityLiving) || event.entityLiving.worldObj.isRemote) {
            return;
        }
        EntityAnimal animal = (EntityAnimal) event.entityLiving;
        if (!animal.isChild() || slaughterOf(event.source) < 2) {
            return;
        }
        animal.getEntityData().setBoolean(BABY_TAG, true);
        animal.getEntityData().setInteger(AGE_TAG, animal.getGrowingAge());
        animal.setGrowingAge(0);
    }

    // ドロップ数UP（looting相当の +0〜level/ドロップ を複製で近似）→ 年齢を子供へ戻す
    @SubscribeEvent
    public void onDrops(LivingDropsEvent event) {
        if (!targets(event.entityLiving)) {
            return;
        }
        EntityAnimal animal = (EntityAnimal) event.entityLiving;
        World world = animal.worldObj;
        if (world.isRemote) {
            return;
        }

        int level = slaughterOf(event.source);
        boolean wasBaby = animal.getEntityData().getBoolean(BABY_TAG);

        // 子供は II では個数バフ無し（大人と同じ物を落とすだけ）、III のみバフ。大人は常にバフ。
        if (level > 0 && !(wasBaby && level < 3)) {
            List<EntityItem> extra = new ArrayList<EntityItem>();
            for (EntityItem ei : event.drops) {
                int bonus = world.rand.nextInt(level + 1);
                if (bonus > 0) {
                    ItemStack s = ei.getEntityItem().copy();
                    s.stackSize = bonus;
                    EntityItem add = new EntityItem(world, animal.posX, animal.posY, animal.posZ, s);
                    add.delayBeforeCanPickup = 10;
                    extra.add(add);
                }
            }
            event.drops.addAll(extra);
        }

        // 年齢を子供へ戻す（死体を子供サイズに保つ）
        if (wasBaby) {
            animal.setGrowingAge(animal.getEntityData().getInteger(AGE_TAG));
            animal.getEntityData().removeTag(BABY_TAG);
            animal.getEntityData().removeTag(AGE_TAG);
        }
    }
}
