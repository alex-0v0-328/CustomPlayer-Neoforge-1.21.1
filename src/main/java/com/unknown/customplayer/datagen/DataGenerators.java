package com.unknown.customplayer.datagen;

import com.unknown.customplayer.CustomPlayer;
import com.unknown.customplayer.datagen.damage.ModDamageTypeProvider;
import com.unknown.customplayer.datagen.damage.ModDamageTypeTagsProvider;
import com.unknown.customplayer.datagen.item.ModItemTagsProvider;
import com.unknown.customplayer.datagen.lang.EnUsLanguageProvider;
import com.unknown.customplayer.datagen.lang.ZhCnLanguageProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

//  ⚠ src/generated/resources is a source set: everything written here must be committed.
@EventBusSubscriber(modid = CustomPlayer.MOD_ID)
public final class DataGenerators {

    private DataGenerators() {}

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();

        generator.addProvider(event.includeClient(), new EnUsLanguageProvider(output));
        generator.addProvider(event.includeClient(), new ZhCnLanguageProvider(output));
        generator.addProvider(event.includeServer(), new ModItemTagsProvider(
                output, event.getLookupProvider(), event.getExistingFileHelper()));

        //  ⚠ The damage type must be written BEFORE its tags: the tag provider resolves the key against
        //  the registry the provider above it contributes.
        ModDamageTypeProvider damageTypes = new ModDamageTypeProvider(output, event.getLookupProvider());
        generator.addProvider(event.includeServer(), damageTypes);
        generator.addProvider(event.includeServer(), new ModDamageTypeTagsProvider(
                output, damageTypes.getRegistryProvider(), event.getExistingFileHelper()));
    }
}
