package com.basashi;

import cpw.mods.fml.common.registry.GameRegistry;
import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

public final class ModItems {

    // 登録順を保つため LinkedHashMap（key=登録名）
    private static final Map<String, Item> ITEMS = new LinkedHashMap<String, Item>();

    // 馬刺しMOD 独自クリエイティブタブ
    public static final CreativeTabs TAB = new CreativeTabs("basashi") {
        @Override
        public Item getTabIconItem() {
            return BASASHI;
        }
    };

    public static final Item BASASHI = food("basashi", 5, 0.3F);
    public static final Item HORSE_TATAKI = food("horse_tataki", 10, 0.8F);
    public static final Item HORSE_YUKKE = food("horse_yukke", 8, 0.6F);
    public static final Item HORSE_TARTARE = food("horse_tartare", 13, 0.9F);
    public static final Item HORSE_HAMBURG = food("horse_hamburg", 10, 0.8F);
    public static final Item HORSE_HAMBURG_DELUXE = food("horse_hamburg_deluxe", 14, 0.9F);

    public static final Item HORSE_LIVER = setup(
        new ItemFood(8, 0.3F, false) {
            @Override
            protected void onFoodEaten(ItemStack stack, World world, EntityPlayer player) {
                if (!world.isRemote) {
                    player.addPotionEffect(new PotionEffect(Potion.regeneration.id, 6000, 1));
                    player.addPotionEffect(new PotionEffect(Potion.resistance.id, 6000, 1));
                }
                super.onFoodEaten(stack, world, player);
            }
        },
        "horse_liver"
    );

    public static final Item COOKED_HORSE_LIVER = setup(
        new ItemFood(15, 0.8F, false) {
            @Override
            protected void onFoodEaten(ItemStack stack, World world, EntityPlayer player) {
                if (!world.isRemote) {
                    player.addPotionEffect(new PotionEffect(Potion.regeneration.id, 6000, 1));
                    player.addPotionEffect(new PotionEffect(Potion.resistance.id, 6000, 1));
                }
                super.onFoodEaten(stack, world, player);
            }
        },
        "cooked_horse_liver"
    );

    public static final Item GOLDEN_WHEAT = setup(new Item(), "golden_wheat");

    public static final Item GOLDEN_BREAD = setup(
        new ItemFood(5, 1.2F, false) {
            @Override
            protected void onFoodEaten(ItemStack stack, World world, EntityPlayer player) {
                if (!world.isRemote) {
                    player.addPotionEffect(new PotionEffect(Potion.regeneration.id, 100, 1));
                    // 衝撃吸収（1.7.10 では未命名: Potion.field_76444_x）
                    player.addPotionEffect(new PotionEffect(Potion.field_76444_x.id, 2400, 0));
                }
                super.onFoodEaten(stack, world, player);
            }
        },
        "golden_bread"
    );

    private ModItems() {}

    private static Item food(String name, int amount, float saturation) {
        return setup(new ItemFood(amount, saturation, false), name);
    }

    private static Item setup(Item item, String name) {
        item.setUnlocalizedName(BasashiMod.MOD_ID + "." + name);
        item.setTextureName(BasashiMod.MOD_ID + ":" + name); // assets/basashi/textures/items/<name>.png
        item.setCreativeTab(TAB);
        ITEMS.put(name, item);
        return item;
    }

    public static void register() {
        for (Map.Entry<String, Item> e : ITEMS.entrySet()) {
            GameRegistry.registerItem(e.getValue(), e.getKey());
        }
    }
}
