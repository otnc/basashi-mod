package com.basashi;

/**
 * 馬刺しMOD ローダー非依存の共通エントリポイント。
 * forge / neoforge の各エントリから {@link #init()} が呼ばれる。
 */
public final class BasashiMod {

    public static final String MOD_ID = "basashi";

    private BasashiMod() {}

    public static void init() {
        ModItems.register();
        HorseDropHandler.register();
    }
}
