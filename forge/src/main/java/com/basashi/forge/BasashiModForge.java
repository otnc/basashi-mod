package com.basashi.forge;

import com.basashi.BasashiMod;
import dev.architectury.platform.forge.EventBuses;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(BasashiMod.MOD_ID)
public final class BasashiModForge {

    public BasashiModForge() {
        // Architectury の登録/イベントを Forge のMODイベントバスへ接続
        EventBuses.registerModEventBus(BasashiMod.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        BasashiMod.init();
    }
}
