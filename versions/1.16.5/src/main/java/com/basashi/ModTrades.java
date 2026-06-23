package com.basashi;

import java.util.List;
import net.minecraft.entity.merchant.villager.VillagerProfession;
import net.minecraft.entity.merchant.villager.VillagerTrades;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.MerchantOffer;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;

public final class ModTrades {

    @SubscribeEvent
    public void onVillagerTrades(VillagerTradesEvent event) {
        if (event.getType() != VillagerProfession.BUTCHER) {
            return;
        }
        java.util.Map<Integer, List<VillagerTrades.ITrade>> trades = event.getTrades();
        trades.get(1).add(buy(ModItems.BASASHI, 6, 16, 2));
        trades.get(2).add(sell(ModItems.HORSE_TATAKI, 4, 1, 16, 5));
        trades.get(2).add(sell(ModItems.HORSE_YUKKE, 1, 2, 12, 5));
        trades.get(3).add(sell(ModItems.HORSE_HAMBURG, 1, 3, 12, 10));
        trades.get(4).add(sell(ModItems.GOLDEN_WHEAT, 1, 4, 12, 15));
        trades.get(4).add(buy(ModItems.HORSE_LIVER, 1, 12, 15));
        trades.get(5).add(sell(ModItems.HORSE_HAMBURG_DELUXE, 1, 5, 9, 30));
        trades.get(5).add(sell(ModItems.COOKED_HORSE_LIVER, 1, 8, 9, 30));
    }

    // emeraldCost エメラルドで item を count 個 販売
    private static VillagerTrades.ITrade sell(RegistryObject<Item> item, int count, int emeraldCost, int maxUses, int xp) {
        return (trader, random) ->
            new MerchantOffer(
                new ItemStack(Items.EMERALD, emeraldCost),
                new ItemStack(item.get(), count),
                maxUses,
                xp,
                0.05F
            );
    }

    // item を count 個 で 1 エメラルド 買取
    private static VillagerTrades.ITrade buy(RegistryObject<Item> item, int count, int maxUses, int xp) {
        return (trader, random) ->
            new MerchantOffer(new ItemStack(item.get(), count), new ItemStack(Items.EMERALD, 1), maxUses, xp, 0.05F);
    }
}
