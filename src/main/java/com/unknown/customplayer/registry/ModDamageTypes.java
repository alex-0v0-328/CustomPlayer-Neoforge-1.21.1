package com.unknown.customplayer.registry;

import com.unknown.customplayer.CustomPlayer;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageType;

/**
 * The keys of this mod's damage types.
 *
 * <p>⚠ Damage types are a datapack registry rather than a deferred one, so this class owns only the
 * keys and the JSON is written at datagen time.
 *
 * @author Alex
 * @since 1.0.0
 */
public final class ModDamageTypes {

    private ModDamageTypes() {}

    public static final ResourceKey<DamageType> BRAIN_DESTROYED = key("brain_destroyed");

    private static ResourceKey<DamageType> key(String name) {
        return ResourceKey.create(Registries.DAMAGE_TYPE,
                ResourceLocation.fromNamespaceAndPath(CustomPlayer.MOD_ID, name));
    }
}
