package com.unknown.customplayer.client;

import com.unknown.customplayer.CustomPlayer;
import com.unknown.customplayer.client.screen.BodyPartScreen;
import com.unknown.customplayer.network.OpenBodyPayload;
import com.unknown.customplayer.registry.ModMenus;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(modid = CustomPlayer.MOD_ID, value = Dist.CLIENT)
public final class ClientEvents {

    private ClientEvents() {}

    @SubscribeEvent
    public static void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(ModKeyMappings.OPEN_BODY);
    }

    @SubscribeEvent
    public static void onRegisterScreens(RegisterMenuScreensEvent event) {
        event.register(ModMenus.BODY_PART_MENU.get(), BodyPartScreen::new);
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null) return;

        while (ModKeyMappings.OPEN_BODY.consumeClick()) {
            if (minecraft.screen == null) PacketDistributor.sendToServer(new OpenBodyPayload());
        }
    }
}
