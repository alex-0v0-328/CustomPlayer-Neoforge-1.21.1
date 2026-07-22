package com.unknown.customplayer.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import org.lwjgl.glfw.GLFW;

//  ⚠ R is unbound in vanilla 1.21.1, which is why it is free to take -- and it is rebindable anyway,
//  so a pack that wants it back loses nothing.
public final class ModKeyMappings {

    private ModKeyMappings() {}

    public static final String CATEGORY = "key.categories.customplayer";

    public static final KeyMapping OPEN_BODY = new KeyMapping(
            "key.customplayer.open_body",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_R,
            CATEGORY);
}
