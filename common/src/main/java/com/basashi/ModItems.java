package com.basashi;

import dev.architectury.registry.CreativeTabRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;

public final class ModItems {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(BasashiMod.MOD_ID, Registries.ITEM);

    public static final RegistrySupplier<Item> BASASHI = ITEMS.register("basashi", () ->
        new Item(
            new Item.Properties().food(new FoodProperties.Builder().nutrition(3).saturationMod(0.3F).meat().build())
        )
    );

    public static final RegistrySupplier<Item> UMA_NO_TATAKI = ITEMS.register("uma_no_tataki", () ->
        new Item(
            new Item.Properties().food(new FoodProperties.Builder().nutrition(8).saturationMod(0.8F).meat().build())
        )
    );

    public static final RegistrySupplier<Item> UMA_YUKKE = ITEMS.register("uma_yukke", () ->
        new Item(
            new Item.Properties().food(new FoodProperties.Builder().nutrition(6).saturationMod(0.6F).meat().build())
        )
    );

    public static final RegistrySupplier<Item> UMA_TARTARE = ITEMS.register("uma_tartare", () ->
        new Item(
            new Item.Properties().food(new FoodProperties.Builder().nutrition(11).saturationMod(0.9F).meat().build())
        )
    );

    private ModItems() {}

    public static void register() {
        ITEMS.register();

        CreativeTabRegistry.append(CreativeModeTabs.FOOD_AND_DRINKS, BASASHI, UMA_NO_TATAKI, UMA_YUKKE, UMA_TARTARE);
    }
}
