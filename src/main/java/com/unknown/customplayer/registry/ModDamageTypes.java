package com.unknown.customplayer.registry;

import com.unknown.customplayer.CustomPlayer;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageType;

//  ⚠ A DATAPACK registry, not a DeferredRegister: this class owns only the ResourceKeys; JSON at runData.
public final class ModDamageTypes {

    private ModDamageTypes() {}

    //  A destroyed brain -- the only ailment in this mod that ends the player rather than costing them.
    public static final ResourceKey<DamageType> BRAIN_DESTROYED = key("brain_destroyed");

    private static ResourceKey<DamageType> key(String name) {
        return ResourceKey.create(Registries.DAMAGE_TYPE,
                ResourceLocation.fromNamespaceAndPath(CustomPlayer.MOD_ID, name));
    }
}
