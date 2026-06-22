package com.basashi;

import dev.architectury.registry.CreativeTabRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;

public final class ModItems {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(BasashiMod.MOD_ID, Registries.ITEM);

    public static final RegistrySupplier<Item> BASASHI = ITEMS.register("basashi", () ->
        new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(5).saturationMod(0.3F).meat().build()))
    );

    public static final RegistrySupplier<Item> HORSE_TATAKI = ITEMS.register("horse_tataki", () ->
        new Item(
            new Item.Properties().food(new FoodProperties.Builder().nutrition(10).saturationMod(0.8F).meat().build())
        )
    );

    public static final RegistrySupplier<Item> HORSE_YUKKE = ITEMS.register("horse_yukke", () ->
        new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(8).saturationMod(0.6F).meat().build()))
    );

    public static final RegistrySupplier<Item> HORSE_TARTARE = ITEMS.register("horse_tartare", () ->
        new Item(
            new Item.Properties().food(new FoodProperties.Builder().nutrition(13).saturationMod(0.9F).meat().build())
        )
    );

    public static final RegistrySupplier<Item> HORSE_HAMBURG = ITEMS.register("horse_hamburg", () ->
        new Item(
            new Item.Properties().food(new FoodProperties.Builder().nutrition(10).saturationMod(0.8F).meat().build())
        )
    );

    public static final RegistrySupplier<Item> HORSE_HAMBURG_DELUXE = ITEMS.register("horse_hamburg_deluxe", () ->
        new Item(
            new Item.Properties().food(new FoodProperties.Builder().nutrition(14).saturationMod(0.9F).meat().build())
        )
    );

    public static final RegistrySupplier<Item> HORSE_LIVER = ITEMS.register("horse_liver", () ->
        new Item(
            new Item.Properties()
                .food(
                    new FoodProperties.Builder()
                        .nutrition(8)
                        .saturationMod(0.3F)
                        .meat()
                        .effect(new MobEffectInstance(MobEffects.REGENERATION, 6000, 1), 1.0F)
                        .effect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 6000, 1), 1.0F)
                        .build()
                )
        )
    );

    public static final RegistrySupplier<Item> COOKED_HORSE_LIVER = ITEMS.register("cooked_horse_liver", () ->
        new Item(
            new Item.Properties()
                .food(
                    new FoodProperties.Builder()
                        .nutrition(15)
                        .saturationMod(0.8F)
                        .meat()
                        .effect(new MobEffectInstance(MobEffects.REGENERATION, 6000, 1), 1.0F)
                        .effect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 6000, 1), 1.0F)
                        .build()
                )
        )
    );

    public static final RegistrySupplier<Item> GOLDEN_WHEAT = ITEMS.register("golden_wheat", () -> new Item(new Item.Properties()));

    public static final RegistrySupplier<Item> GOLDEN_BREAD = ITEMS.register("golden_bread", () ->
        new Item(
            new Item.Properties()
                .food(
                    new FoodProperties.Builder()
                        .nutrition(5)
                        .saturationMod(1.2F)
                        .effect(new MobEffectInstance(MobEffects.REGENERATION, 100, 1), 1.0F)
                        .effect(new MobEffectInstance(MobEffects.ABSORPTION, 2400, 0), 1.0F)
                        .build()
                )
        )
    );

    private ModItems() {}

    public static void register() {
        ITEMS.register();

        CreativeTabRegistry.append(
            CreativeModeTabs.FOOD_AND_DRINKS,
            BASASHI,
            HORSE_TATAKI,
            HORSE_YUKKE,
            HORSE_TARTARE,
            HORSE_HAMBURG,
            HORSE_HAMBURG_DELUXE,
            HORSE_LIVER,
            COOKED_HORSE_LIVER,
            GOLDEN_WHEAT,
            GOLDEN_BREAD
        );
    }
}
