package com.basashi;

import net.minecraft.enchantment.Enchantment;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = BasashiMod.MOD_ID)
public final class ModEnchantments {

    public static final Enchantment SLAUGHTER = new SlaughterEnchantment();

    private ModEnchantments() {}

    @SubscribeEvent
    public static void onRegister(RegistryEvent.Register<Enchantment> event) {
        event.getRegistry().register(SLAUGHTER);
    }
}
