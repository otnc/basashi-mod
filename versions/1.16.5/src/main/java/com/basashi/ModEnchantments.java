package com.basashi;

import net.minecraft.enchantment.Enchantment;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public final class ModEnchantments {

    public static final DeferredRegister<Enchantment> ENCHANTMENTS = DeferredRegister.create(
        ForgeRegistries.ENCHANTMENTS,
        BasashiMod.MOD_ID
    );

    public static final RegistryObject<Enchantment> SLAUGHTER = ENCHANTMENTS.register(
        "slaughter",
        SlaughterEnchantment::new
    );

    private ModEnchantments() {}
}
