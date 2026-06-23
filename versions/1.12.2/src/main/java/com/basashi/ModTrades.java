package com.basashi;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Random;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.VillagerRegistry.VillagerCareer;
import net.minecraftforge.fml.common.registry.VillagerRegistry.VillagerProfession;

public final class ModTrades {

    private ModTrades() {}

    // バニラ肉屋(butcher career)へ取引を追加。1.12.2 は VillagerTradesEvent が無く career へ直接追加するため、
    // private な careers リストにリフレクションでアクセスする。
    public static void register() {
        VillagerProfession butcher =
            ForgeRegistries.VILLAGER_PROFESSIONS.getValue(new ResourceLocation("minecraft", "butcher"));
        if (butcher == null) {
            return;
        }
        VillagerCareer career = getButcherCareer(butcher);
        if (career == null) {
            return;
        }

        career.addTrade(1, buy(ModItems.BASASHI, 6));
        career.addTrade(2, sell(ModItems.HORSE_TATAKI, 4, 1), sell(ModItems.HORSE_YUKKE, 1, 2));
        career.addTrade(3, sell(ModItems.HORSE_HAMBURG, 1, 3));
        career.addTrade(4, sell(ModItems.GOLDEN_WHEAT, 1, 4), buy(ModItems.HORSE_LIVER, 1));
        career.addTrade(5, sell(ModItems.HORSE_HAMBURG_DELUXE, 1, 5), sell(ModItems.COOKED_HORSE_LIVER, 1, 8));
    }

    @SuppressWarnings("unchecked")
    private static VillagerCareer getButcherCareer(VillagerProfession butcher) {
        try {
            Field f = VillagerProfession.class.getDeclaredField("careers");
            f.setAccessible(true);
            List<VillagerCareer> careers = (List<VillagerCareer>) f.get(butcher);
            for (VillagerCareer c : careers) {
                if ("butcher".equals(c.getName())) {
                    return c;
                }
            }
            return careers.isEmpty() ? null : careers.get(0);
        } catch (ReflectiveOperationException e) {
            return null;
        }
    }

    // emeralds エメラルドで item を count 個 販売（プレイヤーが買う）
    private static EntityVillager.ITradeList sell(Item item, int count, int emeralds) {
        return (IMerchant merchant, MerchantRecipeList recipeList, Random random) ->
            recipeList.add(new MerchantRecipe(new ItemStack(Items.EMERALD, emeralds), new ItemStack(item, count)));
    }

    // item を count 個 で 1 エメラルド 買取（プレイヤーが売る）
    private static EntityVillager.ITradeList buy(Item item, int count) {
        return (IMerchant merchant, MerchantRecipeList recipeList, Random random) ->
            recipeList.add(new MerchantRecipe(new ItemStack(item, count), new ItemStack(Items.EMERALD, 1)));
    }
}
