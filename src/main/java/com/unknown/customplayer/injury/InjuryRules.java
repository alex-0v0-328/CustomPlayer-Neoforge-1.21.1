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

//  The table of "this kind of harm does this to the body", open for a dependent to extend.
//  ⚠ FALL grades on the hit, BURN on seconds alight -- two different measures, on purpose. See vault.
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

    //  ⚠ LinkedHashMap: registration order is offer order; a dependent must not reorder the built-ins.
    private static final Map<String, List<InjuryRule>> RULES = new LinkedHashMap<>();

    static {
        register(FALL, InjuryRule.anyPart(FALL_WOUND, Ailment.WOUND));
        register(FALL, InjuryRule.anyPart(FALL_CRIPPLED, Ailment.CRIPPLED));
        register(BURN, InjuryRule.of(BURN_WOUND, BodyPart.SKIN, Ailment.WOUND));
        register(BURN, InjuryRule.of(BURN_CRIPPLED, BodyPart.SKIN, Ailment.CRIPPLED));
    }

    //  ⚠ A dependent registers under its own channel and drives it from its own event. See vault.
    public static void register(String channel, InjuryRule rule) {
        RULES.computeIfAbsent(channel, key -> new ArrayList<>()).add(rule);
    }

    public static List<InjuryRule> rules(String channel) {
        return RULES.getOrDefault(channel, List.of());
    }

    //  Applies the WORST qualifying rule, not every one. ⚠ Running all would push a WOUND packet that
    //  worsen() then discards -- but the packet does not un-send. See vault.
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
