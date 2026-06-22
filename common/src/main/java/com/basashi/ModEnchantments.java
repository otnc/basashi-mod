package com.basashi;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.enchantment.Enchantment;

public final class ModEnchantments {

    public static final DeferredRegister<Enchantment> ENCHANTMENTS = DeferredRegister.create(
        BasashiMod.MOD_ID,
        Registries.ENCHANTMENT
    );

    public static final RegistrySupplier<Enchantment> SLAUGHTER = ENCHANTMENTS.register(
        "slaughter",
        SlaughterEnchantment::new
    );

    private ModEnchantments() {}

    public static void register() {
        ENCHANTMENTS.register();
    }
}
