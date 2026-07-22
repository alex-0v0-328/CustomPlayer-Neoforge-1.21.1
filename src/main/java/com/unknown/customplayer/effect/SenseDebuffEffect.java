package com.unknown.customplayer.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

//  Degree ONE of the sense scale: 眩光 / 耳鸣 / 沙哑 / 鼻塞 / 眩晕. Short, timed, and gone on its own.
//  ⚠ A MARKER, deliberately -- no applyEffectTick, no attribute, no consequence. What "dazzled" should
//  actually cost the player is a design decision that has not been made, and guessing it would install
//  a rule nobody agreed to. Every one of them will get its behaviour here, one at a time.
//  ⚠⚠ These are MobEffects rather than stored part data for a concrete reason: vanilla already counts
//  them down, syncs them, draws an icon with the remaining time, and clears them on death. Storing a
//  per-part countdown would re-implement all four, worse.
public class SenseDebuffEffect extends MobEffect {

    public SenseDebuffEffect(MobEffectCategory category, int color) {
        super(category, color);
    }
}
