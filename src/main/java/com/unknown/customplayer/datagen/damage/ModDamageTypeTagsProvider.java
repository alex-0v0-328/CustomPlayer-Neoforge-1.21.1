package com.unknown.customplayer.datagen.damage;

import com.unknown.customplayer.CustomPlayer;
import com.unknown.customplayer.registry.ModDamageTypes;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Writes the damage type tags.
 *
 * <p>⚠ "Bypasses armor" is a tag, not a field on the damage type. Creative is deliberately NOT
 * covered here -- vanilla's own invulnerability is what should stop it.
 *
 * @author Alex
 * @since 1.0.0
 */
public class ModDamageTypeTagsProvider extends TagsProvider<DamageType> {

    public ModDamageTypeTagsProvider(PackOutput output,
                                     CompletableFuture<HolderLookup.Provider> lookupProvider,
                                     @Nullable ExistingFileHelper existingFileHelper) {
        super(output, Registries.DAMAGE_TYPE, lookupProvider, CustomPlayer.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {
        unstoppable(DamageTypeTags.BYPASSES_ARMOR);
        unstoppable(DamageTypeTags.BYPASSES_EFFECTS);
        unstoppable(DamageTypeTags.BYPASSES_ENCHANTMENTS);
        unstoppable(DamageTypeTags.BYPASSES_RESISTANCE);
        unstoppable(DamageTypeTags.BYPASSES_SHIELD);
        unstoppable(DamageTypeTags.NO_KNOCKBACK);
    }

    private void unstoppable(TagKey<DamageType> tag) {
        tag(tag).add(ModDamageTypes.BRAIN_DESTROYED);
    }
}
