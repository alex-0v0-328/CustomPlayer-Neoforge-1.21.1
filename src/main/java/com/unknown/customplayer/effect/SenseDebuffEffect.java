package com.unknown.customplayer.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

//  Degree ONE of the sense scale (眩光/耳鸣/沙哑/鼻塞/眩晕): short, timed, gone on its own.
//  ⚠ A MARKER -- no tick, no attribute, no consequence yet; don't guess one. MobEffect, not stored. Vault.
public class SenseDebuffEffect extends MobEffect {

    public SenseDebuffEffect(MobEffectCategory category, int color) {
        super(category, color);
    }
}
