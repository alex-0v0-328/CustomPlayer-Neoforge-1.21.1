package com.unknown.customplayer.attachment.service.body;

import com.unknown.customplayer.custom.enums.body.Ailment;
import com.unknown.customplayer.custom.enums.body.BodyPart;
import com.unknown.customplayer.registry.ModDamageTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

/**
 * Applying, worsening and healing an ailment, and the one consequence any of them has today.
 *
 * <p>⚠ {@code apply} sets and {@code worsen} only ever climbs. Rules fire on every qualifying hit,
 * so a later, smaller one must never undo what an earlier one did.
 *
 * @author Alex
 * @since 1.0.0
 */
public final class InjuryService {

    private InjuryService() {}

    public static @Nullable Ailment ailment(Player p, BodyPart part) {
        return BodyPartService.state(p, part).ailment();
    }

    public static boolean has(Player p, BodyPart part, Ailment ailment) {
        return ailment(p, part) == ailment;
    }

    public static boolean destroyed(Player p, BodyPart part) {
        return has(p, part, Ailment.DESTROYED) || has(p, part, Ailment.RUINED);
    }

    public static boolean healthyEnoughToInstall(Player p, BodyPart part) {
        if (!part.installable()) return false;
        Ailment current = ailment(p, part);
        return current != Ailment.DESTROYED && current != Ailment.RUINED && current != Ailment.LOST;
    }

    public static void apply(ServerPlayer p, BodyPart part, Ailment ailment) {
        if (!part.accepts(ailment)) return;
        BodyPartService.store(p, part, BodyPartService.state(p, part).withAilment(ailment));
        checkLethal(p, part, ailment);
    }

    public static void worsen(ServerPlayer p, BodyPart part, Ailment ailment) {
        if (!part.accepts(ailment)) return;
        BodyPartService.store(p, part, BodyPartService.state(p, part).worsened(ailment));
        checkLethal(p, part, ailment);
    }

    public static void heal(ServerPlayer p, BodyPart part) {
        BodyPartService.store(p, part, BodyPartService.state(p, part).healed());
    }

    public static void healAll(ServerPlayer p) {
        BodyPartService.store(p, BodyPartService.get(p).healed());
    }

    private static void checkLethal(ServerPlayer player, BodyPart part, Ailment ailment) {
        if (part != BodyPart.BRAIN || ailment != Ailment.DESTROYED) return;

        DamageSource source = new DamageSource(player.registryAccess()
                .holderOrThrow(ModDamageTypes.BRAIN_DESTROYED));
        player.hurt(source, Float.MAX_VALUE);
    }
}
