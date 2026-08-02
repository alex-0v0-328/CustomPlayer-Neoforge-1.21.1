package com.unknown.customplayer;

import com.mojang.logging.LogUtils;
import com.unknown.customplayer.registry.ModAttachments;
import com.unknown.customplayer.registry.ModEffects;
import com.unknown.customplayer.registry.ModMenus;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;

@Mod(CustomPlayer.MOD_ID)
public class CustomPlayer {

    public static final String MOD_ID = "customplayer";
    public static final Logger LOGGER = LogUtils.getLogger();

    public CustomPlayer(IEventBus modEventBus, ModContainer modContainer) {
        ModAttachments.register(modEventBus);
        ModEffects.register(modEventBus);
        ModMenus.register(modEventBus);
    }
}
