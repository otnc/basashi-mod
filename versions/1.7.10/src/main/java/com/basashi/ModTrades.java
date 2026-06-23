package com.basashi;

import cpw.mods.fml.common.registry.VillagerRegistry;
import cpw.mods.fml.common.registry.VillagerRegistry.IVillageTradeHandler;
import java.util.Random;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;

// 1.7.10 はバニラ職業に取引レベルが無く、ハンドラで肉屋(profession=4)の取引リストへ追加する
public final class ModTrades implements IVillageTradeHandler {

    public static void register() {
        VillagerRegistry.instance().registerVillageTradeHandler(4, new ModTrades()); // 4 = butcher
    }

    @Override
    public void manipulateTradesForVillager(EntityVillager villager, MerchantRecipeList recipeList, Random random) {
        recipeList.add(buy(ModItems.BASASHI, 6));
        recipeList.add(sell(ModItems.HORSE_TATAKI, 4, 1));
        recipeList.add(sell(ModItems.HORSE_YUKKE, 1, 2));
        recipeList.add(sell(ModItems.HORSE_HAMBURG, 1, 3));
        recipeList.add(sell(ModItems.GOLDEN_WHEAT, 1, 4));
        recipeList.add(buy(ModItems.HORSE_LIVER, 1));
        recipeList.add(sell(ModItems.HORSE_HAMBURG_DELUXE, 1, 5));
        recipeList.add(sell(ModItems.COOKED_HORSE_LIVER, 1, 8));
    }

    // emeralds エメラルドで item を count 個 販売
    private static MerchantRecipe sell(Item item, int count, int emeralds) {
        return new MerchantRecipe(new ItemStack(Items.emerald, emeralds), new ItemStack(item, count));
    }

    // item を count 個 で 1 エメラルド 買取
    private static MerchantRecipe buy(Item item, int count) {
        return new MerchantRecipe(new ItemStack(item, count), new ItemStack(Items.emerald, 1));
    }
}
