package com.unknown.customplayer.attachment.data.body;

import com.mojang.serialization.Codec;
import com.unknown.customplayer.custom.enums.body.BodyPart;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import net.minecraft.world.item.ItemStack;

public record PartStorage(Map<BodyPart, ItemStack> installed) {

    public static final PartStorage DEFAULT = new PartStorage(Map.of());

    public static final Codec<PartStorage> CODEC = Codec.unboundedMap(BodyPart.CODEC, ItemStack.CODEC)
            .xmap(PartStorage::new, PartStorage::installed);

    public PartStorage {
        Map<BodyPart, ItemStack> pruned = new EnumMap<>(BodyPart.class);
        installed.forEach((part, stack) -> {
            if (part.installable() && !stack.isEmpty()) pruned.put(part, stack.copy());
        });
        installed = Collections.unmodifiableMap(pruned);
    }

    public ItemStack get(BodyPart part) {return installed.getOrDefault(part, ItemStack.EMPTY).copy();}
    public boolean isEmpty() {return installed.isEmpty();}

    public PartStorage with(BodyPart part, ItemStack stack) {
        Map<BodyPart, ItemStack> next = new EnumMap<>(BodyPart.class);
        next.putAll(installed);
        if (stack.isEmpty()) {
            next.remove(part);
        } else {
            next.put(part, stack.copy());
        }
        return new PartStorage(next);
    }
}
