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

//  Every part of one body. Sparse -- a healthy, unmarked part is absent and get() never returns null.
//  ⚠ Ailment scale is checked HERE, the only place that knows both part and state. See vault.
public record BodyPartData(Map<BodyPart, PartState> parts) {

    public static final BodyPartData DEFAULT = new BodyPartData(Map.of());

    public static final Codec<BodyPartData> CODEC =
            Codec.unboundedMap(BodyPart.CODEC, PartState.CODEC).xmap(BodyPartData::new, BodyPartData::parts);

    public static final StreamCodec<ByteBuf, BodyPartData> STREAM_CODEC =
            ModStreamCodecs.enumMap(BodyPart.class, PartState.STREAM_CODEC)
                    .map(BodyPartData::new, BodyPartData::parts);

    public BodyPartData {
        //  EnumMap: stable ordinal order in NBT and on the wire.
        Map<BodyPart, PartState> pruned = new EnumMap<>(BodyPart.class);
        parts.forEach((part, state) -> {
            //  ⚠ Drops an ailment from the wrong scale -- an edited save or older jar could otherwise
            //  leave a leg reading "blind" forever.
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

    //  Every ailment on every part, marks untouched.
    public BodyPartData healed() {
        Map<BodyPart, PartState> next = new EnumMap<>(BodyPart.class);
        parts.forEach((part, state) -> next.put(part, state.healed()));
        return new BodyPartData(next);
    }
}
