package com.unknown.customplayer.attachment.data.body;

import com.mojang.serialization.Codec;
import com.unknown.customplayer.custom.enums.body.Ailment;
import com.unknown.customplayer.custom.enums.body.BodyPart;
import com.unknown.customplayer.serialization.ModStreamCodecs;
import io.netty.buffer.ByteBuf;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import net.minecraft.network.codec.StreamCodec;

public record BodyPartData(Map<BodyPart, PartState> parts) {

    public static final BodyPartData DEFAULT = new BodyPartData(Map.of());

    public static final Codec<BodyPartData> CODEC =
            Codec.unboundedMap(BodyPart.CODEC, PartState.CODEC).xmap(BodyPartData::new, BodyPartData::parts);

    public static final StreamCodec<ByteBuf, BodyPartData> STREAM_CODEC =
            ModStreamCodecs.enumMap(BodyPart.class, PartState.STREAM_CODEC)
                    .map(BodyPartData::new, BodyPartData::parts);

    public BodyPartData {
        Map<BodyPart, PartState> pruned = new EnumMap<>(BodyPart.class);
        parts.forEach((part, state) -> {
            PartState kept = state.ailment() != null && !part.accepts(state.ailment())
                    ? state.withAilment(null)
                    : state;
            if (!kept.isDefault()) pruned.put(part, kept);
        });
        parts = Collections.unmodifiableMap(pruned);
    }

    public PartState get(BodyPart part) {return parts.getOrDefault(part, PartState.DEFAULT);}
    public Ailment ailment(BodyPart part) {return get(part).ailment();}

    public BodyPartData with(BodyPart part, PartState state) {
        Map<BodyPart, PartState> next = new EnumMap<>(BodyPart.class);
        next.putAll(parts);
        next.put(part, state);
        return new BodyPartData(next);
    }

    public BodyPartData healed() {
        Map<BodyPart, PartState> next = new EnumMap<>(BodyPart.class);
        parts.forEach((part, state) -> next.put(part, state.healed()));
        return new BodyPartData(next);
    }
}
