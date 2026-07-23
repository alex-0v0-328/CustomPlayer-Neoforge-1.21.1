package com.unknown.customplayer.attachment.service.body;

import com.unknown.customplayer.custom.enums.body.Ailment;
import com.unknown.customplayer.custom.enums.body.BodyPart;
import com.unknown.customplayer.registry.ModDamageTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

//  Ailments: what is wrong with a part, not what it costs.
//  ⚠⚠ NO effect yet (bar checkLethal); don't invent one -- every future effect hooks in HERE. See vault.
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

    //  Whether the part can hold something right now. ⚠ Distinct from installable() (can it host at all).
    public static boolean healthyEnoughToInstall(Player p, BodyPart part) {
        if (!part.installable()) return false;
        Ailment current = ailment(p, part);
        return current != Ailment.DESTROYED && current != Ailment.RUINED && current != Ailment.LOST;
    }

    //  ---- write ----
    //  ⚠ Silently refuses a wrong-scale ailment (unrepresentable); a red "no" is the command's, via accepts.
    public static void apply(ServerPlayer p, BodyPart part, Ailment ailment) {
        if (!part.accepts(ailment)) return;
        BodyPartService.store(p, part, BodyPartService.state(p, part).withAilment(ailment));
        checkLethal(p, part, ailment);
    }

    //  ⚠ Only ever upward -- rules fire on every qualifying hit; a later smaller one must not heal.
    public static void worsen(ServerPlayer p, BodyPart part, Ailment ailment) {
        if (!part.accepts(ailment)) return;
        BodyPartService.store(p, part, BodyPartService.state(p, part).worsened(ailment));
        checkLethal(p, part, ailment);
    }

    public static void heal(ServerPlayer p, BodyPart part) {
        BodyPartService.store(p, part, BodyPartService.state(p, part).healed());
    }

    //  The whole body. ⚠ What a keepInventory-off respawn does -- the clone handler decides. See vault.
    public static void healAll(ServerPlayer p) {
        BodyPartService.store(p, BodyPartService.get(p).healed());
    }

    //  ⚠⚠ The ONE consequence today: a destroyed brain is an ending. Real DamageType, not kill(), so the
    //  death message, stats and /damage behave -- and creative can still stop it. See vault.
    private static void checkLethal(ServerPlayer player, BodyPart part, Ailment ailment) {
        if (part != BodyPart.BRAIN || ailment != Ailment.DESTROYED) return;

        DamageSource source = new DamageSource(player.registryAccess()
                .holderOrThrow(ModDamageTypes.BRAIN_DESTROYED));
        player.hurt(source, Float.MAX_VALUE);
    }
}
