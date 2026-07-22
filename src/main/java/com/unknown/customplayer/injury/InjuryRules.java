package com.unknown.customplayer.injury;

import com.unknown.customplayer.attachment.service.body.InjuryService;
import com.unknown.customplayer.custom.enums.body.Ailment;
import com.unknown.customplayer.custom.enums.body.BodyPart;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.server.level.ServerPlayer;

//  The table of "this kind of harm does this to the body", open for a dependent mod to extend.
//  ⚠ Two built-in families ship here, and their triggers are measured differently ON PURPOSE:
//  a fall is one large hit, burning is one point per tick. Reading both as "damage >= n" would mean
//  fire could never qualify. FALL grades on the hit; BURN grades on seconds alight.
public final class InjuryRules {

    private InjuryRules() {}

    //  ---- built-in thresholds ----
    //  Vanilla fall damage is roughly (blocks - 3), so 8 is about an 11-block drop and 16 about 19.
    private static final float FALL_WOUND = 8.0F;
    private static final float FALL_CRIPPLED = 16.0F;

    //  Seconds alight, cumulative, reset when the fire goes out.
    private static final float BURN_WOUND = 5.0F;
    private static final float BURN_CRIPPLED = 15.0F;

    public static final String FALL = "fall";
    public static final String BURN = "burn";

    //  ⚠ LinkedHashMap: registration order is the order rules are offered, and a dependent adding one
    //  should not silently reorder the built-ins.
    private static final Map<String, List<InjuryRule>> RULES = new LinkedHashMap<>();

    static {
        register(FALL, InjuryRule.anyPart(FALL_WOUND, Ailment.WOUND));
        register(FALL, InjuryRule.anyPart(FALL_CRIPPLED, Ailment.CRIPPLED));
        register(BURN, InjuryRule.of(BURN_WOUND, BodyPart.SKIN, Ailment.WOUND));
        register(BURN, InjuryRule.of(BURN_CRIPPLED, BodyPart.SKIN, Ailment.CRIPPLED));
    }

    //  ⚠ A dependent registers under its own channel name and drives it from its own event; this mod
    //  only fires the two it knows about. Nothing here ever names another mod's damage type.
    public static void register(String channel, InjuryRule rule) {
        RULES.computeIfAbsent(channel, key -> new ArrayList<>()).add(rule);
    }

    public static List<InjuryRule> rules(String channel) {
        return RULES.getOrDefault(channel, List.of());
    }

    //  Applies the WORST rule that qualifies, not every one of them.
    //  ⚠ Running them all would apply WOUND then CRIPPLED on a big fall, and the first write would be
    //  pure noise -- worsen() would discard it anyway, but the packet it pushed would not un-send.
    public static void fire(ServerPlayer player, String channel, float measure, List<BodyPart> candidates) {
        InjuryRule worst = rules(channel).stream()
                .filter(rule -> rule.triggers(measure))
                .max(Comparator.comparingInt(rule -> rule.ailment().ordinal()))
                .orElse(null);
        if (worst == null) return;

        List<BodyPart> parts = worst.parts().isEmpty() ? candidates : worst.parts();
        if (parts.isEmpty()) return;

        BodyPart part = parts.size() == 1
                ? parts.getFirst()
                : parts.get(player.getRandom().nextInt(parts.size()));
        InjuryService.worsen(player, part, worst.ailment());
    }
}
