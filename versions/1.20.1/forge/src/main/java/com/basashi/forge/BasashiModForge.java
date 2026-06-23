package com.basashi.forge;

import com.basashi.BasashiMod;
import com.basashi.ModItems;
import dev.architectury.platform.forge.EventBuses;
import net.minecraft.core.registries.Registries;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.MissingMappingsEvent;

@Mod(BasashiMod.MOD_ID)
public final class BasashiModForge {

    public BasashiModForge() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        EventBuses.registerModEventBus(BasashiMod.MOD_ID, modBus);
        // MissingMappingsEvent は Forge(ゲーム)バスのイベント
        MinecraftForge.EVENT_BUS.addListener(this::onMissingMappings);
        BasashiMod.init();
    }

    // 旧ID(uma_*)を新ID(horse_*)へリマップし、既存ワールドのアイテムが消えないようにする
    private void onMissingMappings(MissingMappingsEvent event) {
        for (MissingMappingsEvent.Mapping<net.minecraft.world.item.Item> m : event.getMappings(
            Registries.ITEM,
            BasashiMod.MOD_ID
        )) {
            switch (m.getKey().getPath()) {
                case "uma_no_tataki" -> m.remap(ModItems.HORSE_TATAKI.get());
                case "uma_yukke" -> m.remap(ModItems.HORSE_YUKKE.get());
                case "uma_tartare" -> m.remap(ModItems.HORSE_TARTARE.get());
                default -> {}
            }
        }
    }
}
