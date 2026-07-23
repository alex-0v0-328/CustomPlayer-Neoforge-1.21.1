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

//  ⚠ "Bypasses armour" is a TAG, not a field on DamageType -- the record carries only scaling, message
//  id and exhaustion. Everything about what can blunt a hit lives here.
public class ModDamageTypeTagsProvider extends TagsProvider<DamageType> {

    public ModDamageTypeTagsProvider(PackOutput output,
                                     CompletableFuture<HolderLookup.Provider> lookupProvider,
                                     @Nullable ExistingFileHelper existingFileHelper) {
        super(output, Registries.DAMAGE_TYPE, lookupProvider, CustomPlayer.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {
        //  A destroyed brain is an ending, not a hit -- nothing blunts it. ⚠ Creative is NOT covered and
        //  must not be: vanilla's own invulnerability still applies. See vault.
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
