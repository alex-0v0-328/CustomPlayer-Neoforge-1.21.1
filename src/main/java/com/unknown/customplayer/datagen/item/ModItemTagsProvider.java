package com.unknown.customplayer.datagen.item;

import com.unknown.customplayer.CustomPlayer;
import com.unknown.customplayer.custom.enums.body.BodyPart;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

//  Writes one EMPTY tag per installable part.
//  ⚠⚠ Empty is the point. This mod owns no items and must never name one; the tags exist so a dependent
//  has a documented, discoverable place to add its own. A tag nobody fills simply matches nothing, and
//  tags merge across mods by id -- guzhenren writes data/customplayer/tags/item/installable/eyes.json
//  in ITS OWN jar and both files are combined at load.
public class ModItemTagsProvider extends ItemTagsProvider {

    public ModItemTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider,
                               @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, CompletableFuture.completedFuture(TagsProvider.TagLookup.<Block>empty()),
                CustomPlayer.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {
        for (BodyPart part : BodyPart.values()) {
            if (part.installable()) tag(part.installTag());
        }
    }
}
