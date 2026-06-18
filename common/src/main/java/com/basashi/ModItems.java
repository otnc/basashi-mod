package com.basashi;

import dev.architectury.registry.CreativeTabRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;

/**
 * 追加アイテム（すべて食料）の登録。
 * 食料の隠し満腹度 = nutrition × saturationMod × 2。
 */
public final class ModItems {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(BasashiMod.MOD_ID, Registries.ITEM);

    // 馬刺し（生）: 生の牛肉相当
    public static final RegistrySupplier<Item> BASASHI = ITEMS.register("basashi", () ->
        new Item(
            new Item.Properties().food(new FoodProperties.Builder().nutrition(3).saturationMod(0.3F).meat().build())
        )
    );

    // 馬のタタキ（焼き）: ステーキ相当
    public static final RegistrySupplier<Item> UMA_NO_TATAKI = ITEMS.register("uma_no_tataki", () ->
        new Item(
            new Item.Properties().food(new FoodProperties.Builder().nutrition(8).saturationMod(0.8F).meat().build())
        )
    );

    // 馬肉のユッケ（料理）
    public static final RegistrySupplier<Item> UMA_YUKKE = ITEMS.register("uma_yukke", () ->
        new Item(
            new Item.Properties().food(new FoodProperties.Builder().nutrition(6).saturationMod(0.6F).meat().build())
        )
    );

    // 馬肉のタルタルステーキ（料理）
    public static final RegistrySupplier<Item> UMA_TARTARE = ITEMS.register("uma_tartare", () ->
        new Item(
            new Item.Properties().food(new FoodProperties.Builder().nutrition(11).saturationMod(0.9F).meat().build())
        )
    );

    private ModItems() {}

    public static void register() {
        ITEMS.register();

        // バニラの「食料と飲み物」タブへ追加
        CreativeTabRegistry.append(
            CreativeModeTabs.FOOD_AND_DRINKS,
            BASASHI.get(),
            UMA_NO_TATAKI.get(),
            UMA_YUKKE.get(),
            UMA_TARTARE.get()
        );
    }
}
