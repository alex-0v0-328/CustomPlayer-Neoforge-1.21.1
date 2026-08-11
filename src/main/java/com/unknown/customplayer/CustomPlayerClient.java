package com.unknown.customplayer;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

/**
 * Client-only entry point. It is bare on purpose: there is no config, so there is no config screen.
 *
 * @author Alex
 * @since 1.0.0
 */
@Mod(value = CustomPlayer.MOD_ID, dist = Dist.CLIENT)
public class CustomPlayerClient {

    public CustomPlayerClient(ModContainer container) {
    }
}
