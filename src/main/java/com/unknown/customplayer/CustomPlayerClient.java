package com.unknown.customplayer;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

//  ⚠ BARE on purpose. The template shipped a ConfigurationScreen registration here, but this mod has no
//  config at all -- that screen opens empty. Do not re-add it; add a real config first if one is needed.
@Mod(value = CustomPlayer.MOD_ID, dist = Dist.CLIENT)
public class CustomPlayerClient {

    public CustomPlayerClient(ModContainer container) {
    }
}
