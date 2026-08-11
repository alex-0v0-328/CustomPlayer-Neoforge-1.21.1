package com.unknown.customplayer.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

/**
 * The mildest degree of a sense ailment, worn as an effect instead of being stored.
 *
 * <p>⚠ A marker with no behavior of its own. Only the two worse degrees are ever written into the
 * stored part state.
 *
 * @author Alex
 * @since 1.0.0
 */
public class SenseDebuffEffect extends MobEffect {

    public SenseDebuffEffect(MobEffectCategory category, int color) {
        super(category, color);
    }
}
