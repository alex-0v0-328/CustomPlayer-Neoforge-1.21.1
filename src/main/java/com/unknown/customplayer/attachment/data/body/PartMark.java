package com.unknown.customplayer.attachment.data.body;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

//  Two independent counts a body part carries for one key. What the key means, and whether the two ever
//  convert into each other, belong to the mod that owns the key -- this one only stores them.
//  ⚠ Every field optionalFieldOf, so a later field cannot invalidate an old save.
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

    //  Indistinguishable from "absent" -- that is what lets PartState stay sparse.
    public boolean isDefault() {return mark == 0L && speck == 0L;}
    public PartMark withMark(long v) {return new PartMark(v, speck);}
    public PartMark withSpeck(long v) {return new PartMark(mark, v);}
}
