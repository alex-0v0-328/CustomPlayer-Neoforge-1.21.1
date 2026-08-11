package com.unknown.customplayer.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import org.lwjgl.glfw.GLFW;

/**
 * The mod's key bindings.
 *
 * @author Alex
 * @since 1.0.0
 */
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
