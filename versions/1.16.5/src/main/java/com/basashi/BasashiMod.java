package com.basashi;

import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(BasashiMod.MOD_ID)
public class BasashiMod {

    public static final String MOD_ID = "basashi";

    public BasashiMod() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModItems.ITEMS.register(modBus);
        ModEnchantments.ENCHANTMENTS.register(modBus);
        modBus.addGenericListener(Item.class, this::onMissingMappings);

        MinecraftForge.EVENT_BUS.register(new HorseDropHandler());
        MinecraftForge.EVENT_BUS.register(new GoldenWheatHandler());
        MinecraftForge.EVENT_BUS.register(new ModTrades());
    }

    // 旧ID(uma_*) を新ID(horse_*) へ自動リマップ（既存ワールドのアイテム互換）
    private void onMissingMappings(RegistryEvent.MissingMappings<Item> event) {
        for (RegistryEvent.MissingMappings.Mapping<Item> m : event.getAllMappings()) {
            if (!m.key.getNamespace().equals(MOD_ID)) {
                continue;
            }
            switch (m.key.getPath()) {
                case "uma_no_tataki":
                    m.remap(ModItems.HORSE_TATAKI.get());
                    break;
                case "uma_yukke":
                    m.remap(ModItems.HORSE_YUKKE.get());
                    break;
                case "uma_tartare":
                    m.remap(ModItems.HORSE_TARTARE.get());
                    break;
                default:
                    break;
            }
        }
    }
}
