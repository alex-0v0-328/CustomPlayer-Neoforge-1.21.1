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

@EventBusSubscriber(modid = CustomPlayer.MOD_ID)
public final class DamageInjuryEvents {

    private DamageInjuryEvents() {}

    private static final List<BodyPart> LEGS = List.of(BodyPart.LEG_LEFT, BodyPart.LEG_RIGHT);
    private static final int TICKS_PER_SECOND = 20;

    @SubscribeEvent
    public static void onDamage(LivingDamageEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if (!event.getSource().is(DamageTypeTags.IS_FALL)) return;

        InjuryRules.fire(player, InjuryRules.FALL, event.getNewDamage(), LEGS);
    }

    @SubscribeEvent
    public static void onEntityTick(EntityTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        int[] burn = player.getData(ModAttachments.BURN_TICKS.get());
        if (!player.isOnFire()) {
            burn[0] = 0;
            return;
        }

        burn[0]++;
        if (burn[0] % TICKS_PER_SECOND != 0) return;

        InjuryRules.fire(player, InjuryRules.BURN, burn[0] / (float) TICKS_PER_SECOND, List.of());
    }
}
