package com.unknown.customplayer.network;

import com.unknown.customplayer.CustomPlayer;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

//  "I pressed the key." ⚠⚠ The ONE payload -- CLIENT INTENT, never player data; sync is server->client
//  only, so a keypress cannot ride it upstream. Downstream data loosens the sync predicate instead. Vault.
public record OpenBodyPayload() implements CustomPacketPayload {

    public static final Type<OpenBodyPayload> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(CustomPlayer.MOD_ID, "open_body"));

    //  Nothing to encode: which body to open is never in question, it is the sender's own.
    public static final StreamCodec<ByteBuf, OpenBodyPayload> STREAM_CODEC =
            StreamCodec.unit(new OpenBodyPayload());

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {return TYPE;}
}
