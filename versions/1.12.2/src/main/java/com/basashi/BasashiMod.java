package com.basashi;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

@Mod(modid = BasashiMod.MOD_ID, name = "馬刺しMOD", useMetadata = true, acceptedMinecraftVersions = "[1.12.2]")
public class BasashiMod {

    public static final String MOD_ID = "basashi";

    // アイテム/エンチャント/モデル/ドロップ/金の小麦は各 @Mod.EventBusSubscriber が自動登録。
    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        // かまど焼き（1.12.2 はJSONの焼きレシピが無いためコードで登録）
        GameRegistry.addSmelting(ModItems.BASASHI, new ItemStack(ModItems.HORSE_TATAKI), 0.35F);
        GameRegistry.addSmelting(ModItems.HORSE_YUKKE, new ItemStack(ModItems.HORSE_HAMBURG), 0.35F);
        GameRegistry.addSmelting(ModItems.HORSE_TARTARE, new ItemStack(ModItems.HORSE_HAMBURG_DELUXE), 0.35F);
        GameRegistry.addSmelting(ModItems.HORSE_LIVER, new ItemStack(ModItems.COOKED_HORSE_LIVER), 0.35F);

        // バニラ肉屋の取引に馬関連を追加
        ModTrades.register();
    }
}
