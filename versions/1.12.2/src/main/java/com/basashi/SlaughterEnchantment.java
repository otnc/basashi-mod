package com.basashi;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;

public class SlaughterEnchantment extends Enchantment {

    public SlaughterEnchantment() {
        super(Rarity.UNCOMMON, EnumEnchantmentType.WEAPON, new EntityEquipmentSlot[] { EntityEquipmentSlot.MAINHAND });
        this.setName("basashi.slaughter"); // 翻訳キー enchantment.basashi.slaughter
        this.setRegistryName(BasashiMod.MOD_ID, "slaughter");
    }

    @Override
    public int getMinEnchantability(int level) {
        return 15 + (level - 1) * 9;
    }

    @Override
    public int getMaxEnchantability(int level) {
        return this.getMinEnchantability(level) + 50;
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }

    // 剣・斧の両方に付与可能
    @Override
    public boolean canApply(ItemStack stack) {
        return stack.getItem() instanceof ItemSword || stack.getItem() instanceof ItemAxe || super.canApply(stack);
    }
}
