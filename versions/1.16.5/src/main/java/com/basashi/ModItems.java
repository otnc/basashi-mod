package com.basashi;

import net.minecraft.item.Food;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public final class ModItems {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, BasashiMod.MOD_ID);

    // 馬刺しMOD 独自のクリエイティブタブ（全アイテムをまとめる）
    public static final ItemGroup TAB = new ItemGroup("basashi") {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(BASASHI.get());
        }
    };

    private ModItems() {}

    private static Item.Properties props() {
        return new Item.Properties().tab(TAB);
    }

    private static Food meatFood(int nutrition, float sat) {
        return new Food.Builder().nutrition(nutrition).saturationMod(sat).meat().build();
    }

    public static final RegistryObject<Item> BASASHI = ITEMS.register(
        "basashi",
        () -> new Item(props().food(meatFood(5, 0.3F)))
    );

    public static final RegistryObject<Item> HORSE_TATAKI = ITEMS.register(
        "horse_tataki",
        () -> new Item(props().food(meatFood(10, 0.8F)))
    );

    public static final RegistryObject<Item> HORSE_YUKKE = ITEMS.register(
        "horse_yukke",
        () -> new Item(props().food(meatFood(8, 0.6F)))
    );

    public static final RegistryObject<Item> HORSE_TARTARE = ITEMS.register(
        "horse_tartare",
        () -> new Item(props().food(meatFood(13, 0.9F)))
    );

    public static final RegistryObject<Item> HORSE_HAMBURG = ITEMS.register(
        "horse_hamburg",
        () -> new Item(props().food(meatFood(10, 0.8F)))
    );

    public static final RegistryObject<Item> HORSE_HAMBURG_DELUXE = ITEMS.register(
        "horse_hamburg_deluxe",
        () -> new Item(props().food(meatFood(14, 0.9F)))
    );

    public static final RegistryObject<Item> HORSE_LIVER = ITEMS.register(
        "horse_liver",
        () ->
            new Item(
                props()
                    .food(
                        new Food.Builder()
                            .nutrition(8)
                            .saturationMod(0.3F)
                            .meat()
                            .effect(new EffectInstance(Effects.REGENERATION, 6000, 1), 1.0F)
                            .effect(new EffectInstance(Effects.DAMAGE_RESISTANCE, 6000, 1), 1.0F)
                            .build()
                    )
            )
    );

    public static final RegistryObject<Item> COOKED_HORSE_LIVER = ITEMS.register(
        "cooked_horse_liver",
        () ->
            new Item(
                props()
                    .food(
                        new Food.Builder()
                            .nutrition(15)
                            .saturationMod(0.8F)
                            .meat()
                            .effect(new EffectInstance(Effects.REGENERATION, 6000, 1), 1.0F)
                            .effect(new EffectInstance(Effects.DAMAGE_RESISTANCE, 6000, 1), 1.0F)
                            .build()
                    )
            )
    );

    public static final RegistryObject<Item> GOLDEN_WHEAT = ITEMS.register("golden_wheat", () -> new Item(props()));

    public static final RegistryObject<Item> GOLDEN_BREAD = ITEMS.register(
        "golden_bread",
        () ->
            new Item(
                props()
                    .food(
                        new Food.Builder()
                            .nutrition(5)
                            .saturationMod(1.2F)
                            .effect(new EffectInstance(Effects.REGENERATION, 100, 1), 1.0F)
                            .effect(new EffectInstance(Effects.ABSORPTION, 2400, 0), 1.0F)
                            .build()
                    )
            )
    );
}
