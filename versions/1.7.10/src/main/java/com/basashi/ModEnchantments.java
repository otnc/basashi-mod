package com.basashi;

import net.minecraft.enchantment.Enchantment;

public final class ModEnchantments {

    // 1.7.10 はエンチャントIDが固定長配列(0-255)。バニラ(~71)と被らない値を使用。
    public static final int SLAUGHTER_ID = 120;

    public static Enchantment SLAUGHTER;

    private ModEnchantments() {}

    // Enchantment は super コンストラクタで自身を enchantmentsList に登録する
    public static void register() {
        SLAUGHTER = new SlaughterEnchantment(SLAUGHTER_ID);
    }
}
