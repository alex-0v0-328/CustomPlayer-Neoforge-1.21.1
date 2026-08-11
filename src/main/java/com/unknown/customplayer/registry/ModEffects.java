package com.unknown.customplayer.registry;

import com.unknown.customplayer.CustomPlayer;
import com.unknown.customplayer.effect.SenseDebuffEffect;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * The sense-debuff effects, one per sense.
 *
 * @author Alex
 * @since 1.0.0
 */
public final class ModEffects {

    private ModEffects() {}

    public static final DeferredRegister<MobEffect> MOB_EFFECTS =
            DeferredRegister.create(Registries.MOB_EFFECT, CustomPlayer.MOD_ID);

    private static final int GLARE_COLOR = 0xF2E8B0;
    private static final int TINNITUS_COLOR = 0xB8C7E0;
    private static final int HOARSE_COLOR = 0xD9A6A6;
    private static final int CONGESTION_COLOR = 0xA6C4A6;
    private static final int DIZZY_COLOR = 0xC2A6D9;

    public static final DeferredHolder<MobEffect, SenseDebuffEffect> GLARE = register("glare", GLARE_COLOR);
    public static final DeferredHolder<MobEffect, SenseDebuffEffect> TINNITUS =
            register("tinnitus", TINNITUS_COLOR);
    public static final DeferredHolder<MobEffect, SenseDebuffEffect> HOARSE = register("hoarse", HOARSE_COLOR);
    public static final DeferredHolder<MobEffect, SenseDebuffEffect> CONGESTION =
            register("congestion", CONGESTION_COLOR);
    public static final DeferredHolder<MobEffect, SenseDebuffEffect> DIZZY = register("dizzy", DIZZY_COLOR);

    private static DeferredHolder<MobEffect, SenseDebuffEffect> register(String name, int color) {
        return MOB_EFFECTS.register(name, () -> new SenseDebuffEffect(MobEffectCategory.HARMFUL, color));
    }

    public static void register(IEventBus modEventBus) {
        MOB_EFFECTS.register(modEventBus);
    }
}
