package com.unknown.customplayer.injury;

import com.unknown.customplayer.custom.enums.body.Ailment;
import com.unknown.customplayer.custom.enums.body.BodyPart;
import java.util.List;
import java.util.function.Function;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;

//  "This much of that, to this part, at this grade."
//  ⚠ The trigger is a NUMBER, not a damage amount: a fall is one big hit while burning is one point per
//  tick, so a single "damage >= n" test could never fire for fire. Each rule names its own measure --
//  see InjuryRules, where the fall rules read the hit and the burn rules read seconds alight.
public record InjuryRule(float threshold, List<BodyPart> parts, Ailment ailment) {

    public InjuryRule {
        parts = List.copyOf(parts);
    }

    //  Convenience for the common "one specific part" rule.
    public static InjuryRule of(float threshold, BodyPart part, Ailment ailment) {
        return new InjuryRule(threshold, List.of(part), ailment);
    }

    //  ⚠ EMPTY parts means "whoever fires this decides which part" -- a fall knows it hit a leg, but
    //  which leg is the caller's roll. Never pass null: List.of(null) throws.
    public static InjuryRule anyPart(float threshold, Ailment ailment) {
        return new InjuryRule(threshold, List.of(), ailment);
    }

    //  Which part this firing hits. ⚠ Random among the candidates, which is what makes "a leg" mean
    //  either leg rather than always the left one.
    public BodyPart pick(RandomSource random) {
        return parts.size() == 1 ? parts.getFirst() : parts.get(random.nextInt(parts.size()));
    }

    public boolean triggers(float measure) {return measure >= threshold;}

    //  How a caller turns a player into the measure this rule is graded on.
    @FunctionalInterface
    public interface Measure extends Function<ServerPlayer, Float> {}
}
