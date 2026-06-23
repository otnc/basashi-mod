package com.basashi;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public final class ModRecipes {

    private ModRecipes() {}

    public static void register() {
        // クラフト（並べ方自由）
        GameRegistry.addShapelessRecipe(
            new ItemStack(ModItems.HORSE_YUKKE),
            new ItemStack(ModItems.BASASHI),
            new ItemStack(Items.egg)
        );
        GameRegistry.addShapelessRecipe(
            new ItemStack(ModItems.HORSE_TARTARE),
            new ItemStack(ModItems.BASASHI),
            new ItemStack(Items.carrot),
            new ItemStack(Items.egg)
        );
        GameRegistry.addShapelessRecipe(
            new ItemStack(ModItems.GOLDEN_WHEAT),
            new ItemStack(Items.wheat),
            new ItemStack(Items.gold_nugget)
        );
        // 金のパン（金の小麦×3 横一列）
        GameRegistry.addRecipe(new ItemStack(ModItems.GOLDEN_BREAD), "XXX", 'X', ModItems.GOLDEN_WHEAT);

        // 馬鎧（バニラには革しか無いので追加）
        GameRegistry.addRecipe(new ItemStack(Items.iron_horse_armor), "X X", "XXX", "X X", 'X', Items.iron_ingot);
        GameRegistry.addRecipe(new ItemStack(Items.golden_horse_armor), "X X", "XXX", "X X", 'X', Items.gold_ingot);
        GameRegistry.addRecipe(new ItemStack(Items.diamond_horse_armor), "X X", "XXX", "X X", 'X', Items.diamond);

        // かまど焼き
        GameRegistry.addSmelting(ModItems.BASASHI, new ItemStack(ModItems.HORSE_TATAKI), 0.35F);
        GameRegistry.addSmelting(ModItems.HORSE_YUKKE, new ItemStack(ModItems.HORSE_HAMBURG), 0.35F);
        GameRegistry.addSmelting(ModItems.HORSE_TARTARE, new ItemStack(ModItems.HORSE_HAMBURG_DELUXE), 0.35F);
        GameRegistry.addSmelting(ModItems.HORSE_LIVER, new ItemStack(ModItems.COOKED_HORSE_LIVER), 0.35F);
    }
}
