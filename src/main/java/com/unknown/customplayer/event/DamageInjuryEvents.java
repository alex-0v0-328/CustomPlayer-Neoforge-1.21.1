package com.unknown.customplayer.event;

import com.unknown.customplayer.CustomPlayer;
import com.unknown.customplayer.custom.enums.body.BodyPart;
import com.unknown.customplayer.injury.InjuryRules;
import com.unknown.customplayer.registry.ModAttachments;
import java.util.List;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

//  Where harm becomes injury. ⚠ The two built-in channels are measured differently on purpose -- see
//  InjuryRules; a fall is one hit, burning is one point per tick.
@EventBusSubscriber(modid = CustomPlayer.MOD_ID)
public final class DamageInjuryEvents {

    private DamageInjuryEvents() {}

    private static final List<BodyPart> LEGS = List.of(BodyPart.LEG_LEFT, BodyPart.LEG_RIGHT);
    private static final int TICKS_PER_SECOND = 20;

    //  ⚠ Post, not Pre: the amount here is what actually landed after armour and resistance, which is
    //  the number a threshold should read. Grading on the raw incoming hit would ignore every mitigation.
    @SubscribeEvent
    public static void onDamage(LivingDamageEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if (!event.getSource().is(DamageTypeTags.IS_FALL)) return;

        InjuryRules.fire(player, InjuryRules.FALL, event.getNewDamage(), LEGS);
    }

    //  Burning is graded on how LONG, so it needs a counter of its own -- vanilla's remaining fire ticks
    //  count down toward the end of this fire, never up across it.
    //  ⚠ The counter is unsynced and unserialized: it resets when the fire goes out, and a relog is
    //  indistinguishable from that. Serializing it would preserve a number nothing can observe.
    @SubscribeEvent
    public static void onEntityTick(EntityTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        int[] burn = player.getData(ModAttachments.BURN_TICKS.get());
        if (!player.isOnFire()) {
            burn[0] = 0;
            return;
        }

        burn[0]++;
        //  Once a second, not every tick: the threshold is in seconds, and firing 20 times a second
        //  would push 20 identical packets on the second it crosses.
        if (burn[0] % TICKS_PER_SECOND != 0) return;

        InjuryRules.fire(player, InjuryRules.BURN, burn[0] / (float) TICKS_PER_SECOND, List.of());
    }
}
