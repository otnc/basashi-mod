package com.basashi;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.common.MinecraftForge;

@Mod(modid = BasashiMod.MOD_ID, name = "馬刺しMOD", useMetadata = true, acceptedMinecraftVersions = "[1.7.10]")
public class BasashiMod {

    public static final String MOD_ID = "basashi";

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        ModItems.register();
        ModEnchantments.register();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        ModRecipes.register();
        ModTrades.register();
        MinecraftForge.EVENT_BUS.register(new HorseDropHandler());
        MinecraftForge.EVENT_BUS.register(new GoldenWheatHandler());
        MinecraftForge.EVENT_BUS.register(new SlaughterBoostHandler());
    }
}
