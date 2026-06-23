package com.basashi;

import dev.architectury.registry.CreativeTabRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public final class ModItems {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(BasashiMod.MOD_ID, Registries.ITEM);
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(
        BasashiMod.MOD_ID,
        Registries.CREATIVE_MODE_TAB
    );

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

    // 馬刺しMOD 独自のクリエイティブタブ（全アイテムをまとめる）
    public static final RegistrySupplier<CreativeModeTab> TAB = TABS.register("basashi", () ->
        CreativeTabRegistry.create(builder ->
            builder
                .title(Component.translatable("itemGroup.basashi"))
                .icon(() -> new ItemStack(BASASHI.get()))
                .displayItems((params, output) -> {
                    output.accept(BASASHI.get());
                    output.accept(HORSE_TATAKI.get());
                    output.accept(HORSE_YUKKE.get());
                    output.accept(HORSE_TARTARE.get());
                    output.accept(HORSE_HAMBURG.get());
                    output.accept(HORSE_HAMBURG_DELUXE.get());
                    output.accept(HORSE_LIVER.get());
                    output.accept(COOKED_HORSE_LIVER.get());
                    output.accept(GOLDEN_WHEAT.get());
                    output.accept(GOLDEN_BREAD.get());
                })
        )
    );

    private ModItems() {}

    public static void register() {
        ITEMS.register();
        TABS.register();
    }
}
