package com.unknown.customplayer.attachment.data.body;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

/**
 * Two independent counts held against one mark key.
 *
 * <p>⚠ This mod never learns what a mark means, so whether those counts convert into one another is
 * the owning mod's rule and must not be decided here.
 *
 * @author Alex
 * @since 1.0.0
 */
public record PartMark(long mark, long speck) {

    public static final PartMark DEFAULT = new PartMark(0L, 0L);

    public static final Codec<PartMark> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.LONG.optionalFieldOf("mark", 0L).forGetter(PartMark::mark),
            Codec.LONG.optionalFieldOf("speck", 0L).forGetter(PartMark::speck)
    ).apply(instance, PartMark::new));

    public static final StreamCodec<ByteBuf, PartMark> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_LONG, PartMark::mark,
            ByteBufCodecs.VAR_LONG, PartMark::speck,
            PartMark::new);

    public PartMark {
        mark = Math.max(0L, mark);
        speck = Math.max(0L, speck);
    }

    public boolean isDefault() {return mark == 0L && speck == 0L;}
    public PartMark withMark(long v) {return new PartMark(v, speck);}
    public PartMark withSpeck(long v) {return new PartMark(mark, v);}
}
