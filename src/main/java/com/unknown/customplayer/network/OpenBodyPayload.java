package com.unknown.customplayer.network;

import com.unknown.customplayer.CustomPlayer;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record OpenBodyPayload() implements CustomPacketPayload {

    public static final Type<OpenBodyPayload> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(CustomPlayer.MOD_ID, "open_body"));

    public static final StreamCodec<ByteBuf, OpenBodyPayload> STREAM_CODEC =
            StreamCodec.unit(new OpenBodyPayload());

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {return TYPE;}
}
