package com.basashi;

import dev.architectury.registry.level.entity.trade.TradeRegistry;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.MerchantOffer;

public final class ModTrades {

    private ModTrades() {}

    public static void register() {
        // 見習い(1): 馬刺し買取
        TradeRegistry.registerVillagerTrade(VillagerProfession.BUTCHER, 1, buy(ModItems.BASASHI.get(), 6, 16, 2));
        // 一人前(2): タタキ / ユッケ 販売
        TradeRegistry.registerVillagerTrade(
            VillagerProfession.BUTCHER,
            2,
            sell(ModItems.HORSE_TATAKI.get(), 4, 1, 16, 5),
            sell(ModItems.HORSE_YUKKE.get(), 1, 2, 12, 5)
        );
        // 熟練(3): ハンバーグ 販売
        TradeRegistry.registerVillagerTrade(
            VillagerProfession.BUTCHER,
            3,
            sell(ModItems.HORSE_HAMBURG.get(), 1, 3, 12, 10)
        );
        // 職人(4): 金の小麦 販売 / 生レバー 買取
        TradeRegistry.registerVillagerTrade(
            VillagerProfession.BUTCHER,
            4,
            sell(ModItems.GOLDEN_WHEAT.get(), 1, 4, 12, 15),
            buy(ModItems.HORSE_LIVER.get(), 1, 12, 15)
        );
        // 達人(5): 具入りハンバーグ / 焼きレバー 販売
        TradeRegistry.registerVillagerTrade(
            VillagerProfession.BUTCHER,
            5,
            sell(ModItems.HORSE_HAMBURG_DELUXE.get(), 1, 5, 9, 30),
            sell(ModItems.COOKED_HORSE_LIVER.get(), 1, 8, 9, 30)
        );
    }

    // emeraldCost エメラルドで item を count 個 販売
    private static VillagerTrades.ItemListing sell(Item item, int count, int emeraldCost, int maxUses, int xp) {
        return (trader, random) ->
            new MerchantOffer(new ItemStack(Items.EMERALD, emeraldCost), new ItemStack(item, count), maxUses, xp, 0.05F);
    }

    // item を count 個 で 1 エメラルド 買取
    private static VillagerTrades.ItemListing buy(Item item, int count, int maxUses, int xp) {
        return (trader, random) ->
            new MerchantOffer(new ItemStack(item, count), new ItemStack(Items.EMERALD, 1), maxUses, xp, 0.05F);
    }
}
