package com.unknown.customplayer.serialization;

import io.netty.buffer.ByteBuf;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public final class ModStreamCodecs {

    private ModStreamCodecs() {}

    public static <E extends Enum<E>> StreamCodec<ByteBuf, E> ofEnum(Class<E> type) {
        E[] values = type.getEnumConstants();
        return ByteBufCodecs.VAR_INT.map(ordinal -> values[ordinal], Enum::ordinal);
    }

    public static <E extends Enum<E>> StreamCodec<ByteBuf, E> ofNullableEnum(Class<E> type) {
        E[] values = type.getEnumConstants();
        return ByteBufCodecs.VAR_INT.map(i -> i == 0 ? null : values[i - 1],
                value -> value == null ? 0 : value.ordinal() + 1);
    }

    public static <K extends Enum<K>, V> StreamCodec<ByteBuf, Map<K, V>> enumMap(
            Class<K> key, StreamCodec<ByteBuf, V> value) {
        return ByteBufCodecs.map(HashMap::new, ofEnum(key), value);
    }

    public static <V> StreamCodec<ByteBuf, Map<ResourceLocation, V>> keyedMap(StreamCodec<ByteBuf, V> value) {
        return ByteBufCodecs.map(HashMap::new, ResourceLocation.STREAM_CODEC, value);
    }
}
