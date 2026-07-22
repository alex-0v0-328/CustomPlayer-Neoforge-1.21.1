package com.unknown.customplayer.attachment.service.body;

import com.unknown.customplayer.custom.enums.body.Ailment;
import com.unknown.customplayer.custom.enums.body.BodyPart;
import com.unknown.customplayer.registry.ModDamageTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

//  Ailments: what is wrong with a part, and nothing about what that costs.
//  ⚠⚠ An ailment has NO effect yet, with ONE exception below. Not an oversight and not a stub -- "blind"
//  meaning you cannot see, "crippled leg" meaning you cannot sprint, are each a separate design decision
//  that has not been made. All of them will hook in through this class, so there is one place to look.
//  ⚠ Do not invent them. An empty effect layer is honest; a guessed one is a rule nobody agreed to.
public final class InjuryService {

    private InjuryService() {}

    //  ---- read ----
    public static @Nullable Ailment ailment(Player p, BodyPart part) {
        return BodyPartService.state(p, part).ailment();
    }

    public static boolean has(Player p, BodyPart part, Ailment ailment) {
        return ailment(p, part) == ailment;
    }

    public static boolean destroyed(Player p, BodyPart part) {
        return has(p, part, Ailment.DESTROYED) || has(p, part, Ailment.RUINED);
    }

    //  Whether the part can hold something right now. ⚠ Two questions, deliberately kept apart: whether
    //  the part hosts anything at all (BodyPart.installable) and whether it is fit to (this).
    //  A strained arm still holds what is in it; a destroyed eye socket does not.
    public static boolean healthyEnoughToInstall(Player p, BodyPart part) {
        if (!part.installable()) return false;
        Ailment current = ailment(p, part);
        return current != Ailment.DESTROYED && current != Ailment.RUINED && current != Ailment.LOST;
    }

    //  ---- write ----
    //  ⚠ Silently refuses an ailment from the wrong scale -- the pair is unrepresentable, so there is
    //  nothing to report. A command that wants to say "no" in red must ask BodyPart.accepts first.
    public static void apply(ServerPlayer p, BodyPart part, Ailment ailment) {
        if (!part.accepts(ailment)) return;
        BodyPartService.store(p, part, BodyPartService.state(p, part).withAilment(ailment));
        checkLethal(p, part, ailment);
    }

    //  ⚠ Only ever upward. Injury rules fire on every qualifying hit, so a later smaller one must not
    //  quietly heal what a bigger one did.
    public static void worsen(ServerPlayer p, BodyPart part, Ailment ailment) {
        if (!part.accepts(ailment)) return;
        BodyPartService.store(p, part, BodyPartService.state(p, part).worsened(ailment));
        checkLethal(p, part, ailment);
    }

    public static void heal(ServerPlayer p, BodyPart part) {
        BodyPartService.store(p, part, BodyPartService.state(p, part).healed());
    }

    //  The whole body. ⚠ This is what a respawn does when keepInventory is off -- see the clone handler,
    //  the one place that decides.
    public static void healAll(ServerPlayer p) {
        BodyPartService.store(p, BodyPartService.get(p).healed());
    }

    //  ⚠⚠ The ONE consequence an ailment carries today, and it is a hard one: a destroyed brain is not a
    //  penalty, it is an ending. Routed through a real DamageType rather than kill() so the death message,
    //  the statistics and /damage all behave -- and so armour cannot stop it while creative still can.
    private static void checkLethal(ServerPlayer player, BodyPart part, Ailment ailment) {
        if (part != BodyPart.BRAIN || ailment != Ailment.DESTROYED) return;

        DamageSource source = new DamageSource(player.registryAccess()
                .holderOrThrow(ModDamageTypes.BRAIN_DESTROYED));
        player.hurt(source, Float.MAX_VALUE);
    }
}
