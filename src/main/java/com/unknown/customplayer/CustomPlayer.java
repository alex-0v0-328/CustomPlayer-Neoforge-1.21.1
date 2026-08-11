package com.unknown.customplayer;

import com.mojang.logging.LogUtils;
import com.unknown.customplayer.registry.ModAttachments;
import com.unknown.customplayer.registry.ModEffects;
import com.unknown.customplayer.registry.ModMenus;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;

/**
 * Mod entry point of a library mod: it defines the player's body and nothing else.
 *
 * <p>⚠ What a mark means, and what may be installed into a part, are the dependent mod's business.
 * This jar owns no items at all and must never name one.
 *
 * @author Alex
 * @since 1.0.0
 */
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
