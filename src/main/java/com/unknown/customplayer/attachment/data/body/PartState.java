package com.unknown.customplayer.attachment.data.body;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.unknown.customplayer.custom.enums.body.Ailment;
import com.unknown.customplayer.serialization.ModStreamCodecs;
import io.netty.buffer.ByteBuf;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

/**
 * One part's condition: an ailment, plus whatever marks a dependent mod has put on it.
 *
 * <p>⚠ At most ONE ailment, never a set -- the worse reading wins. Ordinal order within a scale IS
 * the severity order, and nothing but {@code worseThan()} may read it that way.
 *
 * @author Alex
 * @since 1.0.0
 */
public record PartState(@Nullable Ailment ailment, Map<ResourceLocation, PartMark> marks) {

    public static final PartState DEFAULT = new PartState(null, Map.of());

    public static final Codec<PartState> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Ailment.CODEC.optionalFieldOf("ailment").forGetter(state -> Optional.ofNullable(state.ailment())),
            Codec.unboundedMap(ResourceLocation.CODEC, PartMark.CODEC).optionalFieldOf("marks", Map.of())
                    .forGetter(PartState::marks)
    ).apply(instance, (ailment, marks) -> new PartState(ailment.orElse(null), marks)));

    public static final StreamCodec<ByteBuf, PartState> STREAM_CODEC = StreamCodec.composite(
            ModStreamCodecs.ofNullableEnum(Ailment.class), PartState::ailment,
            ModStreamCodecs.keyedMap(PartMark.STREAM_CODEC), PartState::marks,
            PartState::new);

    public PartState {
        Map<ResourceLocation, PartMark> pruned = new TreeMap<>();
        marks.forEach((key, value) -> {if (!value.isDefault()) pruned.put(key, value);});
        marks = Collections.unmodifiableMap(pruned);
    }

    public boolean isDefault() {return ailment == null && marks.isEmpty();}
    public boolean hurt() {return ailment != null;}
    public PartMark mark(ResourceLocation key) {return marks.getOrDefault(key, PartMark.DEFAULT);}

    public PartState withAilment(@Nullable Ailment value) {return new PartState(value, marks);}

    public PartState worsened(Ailment value) {
        return value.worseThan(ailment) ? withAilment(value) : this;
    }

    public PartState healed() {return marks.isEmpty() ? DEFAULT : new PartState(null, marks);}

    public PartState with(ResourceLocation key, PartMark value) {
        Map<ResourceLocation, PartMark> next = new TreeMap<>(marks);
        next.put(key, value);
        return new PartState(ailment, next);
    }
}
