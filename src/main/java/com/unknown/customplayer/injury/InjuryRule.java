package com.unknown.customplayer.injury;

import com.unknown.customplayer.custom.enums.body.Ailment;
import com.unknown.customplayer.custom.enums.body.BodyPart;
import java.util.List;

//  "This much of that, to this part, at this grade." ⚠ The threshold is a NUMBER, not a damage amount:
//  each channel names its own measure (fall = the hit, burn = seconds alight). See InjuryRules / vault.
public record InjuryRule(float threshold, List<BodyPart> parts, Ailment ailment) {

    public InjuryRule {
        parts = List.copyOf(parts);
    }

    //  Convenience for the common "one specific part" rule.
    public static InjuryRule of(float threshold, BodyPart part, Ailment ailment) {
        return new InjuryRule(threshold, List.of(part), ailment);
    }

    //  ⚠ EMPTY parts means "the caller picks the part" -- a fall knows it hit a leg, but which is its roll.
    public static InjuryRule anyPart(float threshold, Ailment ailment) {
        return new InjuryRule(threshold, List.of(), ailment);
    }

    public boolean triggers(float measure) {return measure >= threshold;}
}
