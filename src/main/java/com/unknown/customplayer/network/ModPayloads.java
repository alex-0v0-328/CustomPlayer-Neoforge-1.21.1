package com.unknown.customplayer.network;

import com.unknown.customplayer.CustomPlayer;
import com.unknown.customplayer.menu.BodyPartMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

//  ⚠ One payload, and that is the ceiling. See OpenBodyPayload for why this one is allowed.
@EventBusSubscriber(modid = CustomPlayer.MOD_ID)
public final class ModPayloads {

    private ModPayloads() {}

    private static final String VERSION = "1";
    private static final String MENU_TITLE = "customplayer.menu.body";

    @SubscribeEvent
    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(VERSION);
        registrar.playToServer(OpenBodyPayload.TYPE, OpenBodyPayload.STREAM_CODEC, ModPayloads::openBody);
    }

    //  ⚠ PayloadRegistrar defaults to HandlerThread.MAIN in NeoForge 21.1, so this already runs on the
    //  server thread -- no enqueueWork needed. Do not add one "to be safe"; it only hides the question.
    private static void openBody(OpenBodyPayload payload, IPayloadContext context) {
        if (!(context.player() instanceof ServerPlayer player)) return;

        MenuProvider provider = new SimpleMenuProvider(
                (id, inventory, who) -> new BodyPartMenu(id, inventory),
                Component.translatable(MENU_TITLE));
        player.openMenu(provider);
    }
}
