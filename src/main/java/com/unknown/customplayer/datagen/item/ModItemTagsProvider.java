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

/**
 * Writes one item tag per installable part, every one of them empty.
 *
 * <p>⚠ Empty is the point. This mod owns no items, so naming one here would make a library mod depend
 * on a content mod.
 *
 * @author Alex
 * @since 1.0.0
 */
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
