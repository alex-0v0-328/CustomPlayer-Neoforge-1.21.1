package com.unknown.customplayer.datagen.damage;

import com.unknown.customplayer.CustomPlayer;
import com.unknown.customplayer.registry.ModDamageTypes;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.minecraft.world.damagesource.DamageScaling;
import net.minecraft.world.damagesource.DamageType;

public class ModDamageTypeProvider extends DatapackBuiltinEntriesProvider {

    private static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
            .add(Registries.DAMAGE_TYPE, context -> context.register(ModDamageTypes.BRAIN_DESTROYED,
                    new DamageType("customplayer.brain_destroyed", DamageScaling.NEVER, 0.0F)));

    public ModDamageTypeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, BUILDER, Set.of(CustomPlayer.MOD_ID));
    }
}
