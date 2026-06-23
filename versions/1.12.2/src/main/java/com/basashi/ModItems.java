package com.basashi;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = BasashiMod.MOD_ID)
public final class ModItems {

    public static final List<Item> ITEMS = new ArrayList<Item>();

    // 馬刺しMOD 独自クリエイティブタブ
    public static final CreativeTabs TAB = new CreativeTabs("basashi") {
        @Override
        public ItemStack getTabIconItem() {
            return new ItemStack(BASASHI);
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
                    player.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 6000, 1));
                    player.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, 6000, 1));
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
                    player.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 6000, 1));
                    player.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, 6000, 1));
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
                    player.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 100, 1));
                    player.addPotionEffect(new PotionEffect(MobEffects.ABSORPTION, 2400, 0));
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
        item.setRegistryName(BasashiMod.MOD_ID, name);
        item.setUnlocalizedName(BasashiMod.MOD_ID + "." + name);
        item.setCreativeTab(TAB);
        ITEMS.add(item);
        return item;
    }

    @SubscribeEvent
    public static void onRegisterItems(RegistryEvent.Register<Item> event) {
        for (Item item : ITEMS) {
            event.getRegistry().register(item);
        }
    }

    // 旧ID(uma_*) を新ID(horse_*) へ自動リマップ（既存ワールドのアイテム互換）
    @SubscribeEvent
    public static void onMissingMappings(RegistryEvent.MissingMappings<Item> event) {
        for (RegistryEvent.MissingMappings.Mapping<Item> m : event.getAllMappings()) {
            if (!m.key.getResourceDomain().equals(BasashiMod.MOD_ID)) {
                continue;
            }
            String path = m.key.getResourcePath();
            if ("uma_no_tataki".equals(path)) {
                m.remap(HORSE_TATAKI);
            } else if ("uma_yukke".equals(path)) {
                m.remap(HORSE_YUKKE);
            } else if ("uma_tartare".equals(path)) {
                m.remap(HORSE_TARTARE);
            }
        }
    }
}
