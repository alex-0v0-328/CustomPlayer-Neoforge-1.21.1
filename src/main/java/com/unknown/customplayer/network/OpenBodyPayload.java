package com.unknown.customplayer.network;

import com.unknown.customplayer.CustomPlayer;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

/**
 * Client intent: a keypress asking the server to open the body menu.
 *
 * <p>⚠ One payload is the ceiling. Sync runs server to client only, so intent cannot ride it upstream,
 * and anything traveling downstream loosens the sync predicate instead of gaining a packet.
 *
 * @author Alex
 * @since 1.0.0
 */
public record OpenBodyPayload() implements CustomPacketPayload {

    public static final Type<OpenBodyPayload> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(CustomPlayer.MOD_ID, "open_body"));

    public static final StreamCodec<ByteBuf, OpenBodyPayload> STREAM_CODEC =
            StreamCodec.unit(new OpenBodyPayload());

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {return TYPE;}
}
