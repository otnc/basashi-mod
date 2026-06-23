package com.basashi;

public final class BasashiMod {

    public static final String MOD_ID = "basashi";

    private BasashiMod() {}

    public static void init() {
        ModItems.register();
        ModEnchantments.register();
        HorseDropHandler.register();
        GoldenWheatHandler.register();
        ModTrades.register();
    }
}
