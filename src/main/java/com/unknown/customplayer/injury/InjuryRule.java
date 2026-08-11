package com.unknown.customplayer.injury;

import com.unknown.customplayer.custom.enums.body.Ailment;
import com.unknown.customplayer.custom.enums.body.BodyPart;
import java.util.List;

/**
 * One rule: past this measure, these parts take this ailment.
 *
 * <p>⚠ An empty part list means "the caller picks". A fall knows it landed on a leg; which leg is a
 * roll, and that roll is not this rule's business.
 *
 * @author Alex
 * @since 1.0.0
 */
public record InjuryRule(float threshold, List<BodyPart> parts, Ailment ailment) {

    public InjuryRule {
        parts = List.copyOf(parts);
    }

    public static InjuryRule of(float threshold, BodyPart part, Ailment ailment) {
        return new InjuryRule(threshold, List.of(part), ailment);
    }

    public static InjuryRule anyPart(float threshold, Ailment ailment) {
        return new InjuryRule(threshold, List.of(), ailment);
    }

    public boolean triggers(float measure) {return measure >= threshold;}
}
