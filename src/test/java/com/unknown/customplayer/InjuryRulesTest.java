package com.unknown.customplayer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.unknown.customplayer.custom.enums.body.Ailment;
import com.unknown.customplayer.custom.enums.body.BodyPart;
import com.unknown.customplayer.injury.InjuryRule;
import com.unknown.customplayer.injury.InjuryRules;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class InjuryRulesTest {

    @Test
    @DisplayName("a threshold is inclusive")
    void thresholdIsInclusive() {
        InjuryRule rule = InjuryRule.anyPart(8.0F, Ailment.WOUND);
        assertTrue(rule.triggers(8.0F));
        assertTrue(rule.triggers(8.1F));
        assertEquals(false, rule.triggers(7.9F));
    }

    @Test
    @DisplayName("a fall under 8 damage injures nothing")
    void aSmallFallDoesNothing() {
        assertNull(InjuryRules.worstTriggered(InjuryRules.FALL, 0.0F));
        assertNull(InjuryRules.worstTriggered(InjuryRules.FALL, 7.9F));
    }

    @Test
    @DisplayName("the fall ladder: 8 wounds a leg, 16 cripples it")
    void theFallLadder() {
        assertSame(Ailment.WOUND, InjuryRules.worstTriggered(InjuryRules.FALL, 8.0F).ailment());
        assertSame(Ailment.WOUND, InjuryRules.worstTriggered(InjuryRules.FALL, 15.9F).ailment());
        assertSame(Ailment.CRIPPLED, InjuryRules.worstTriggered(InjuryRules.FALL, 16.0F).ailment());
    }

    @Test
    @DisplayName("⚠ only the WORST qualifying rule lands -- running both would push a WOUND that is then discarded")
    void onlyTheWorstRuleLands() {
        InjuryRule worst = InjuryRules.worstTriggered(InjuryRules.FALL, 100.0F);
        assertSame(Ailment.CRIPPLED, worst.ailment());
    }

    @Test
    @DisplayName("burn is measured in SECONDS, not damage -- 5 wounds the skin, 15 cripples it")
    void theBurnLadderIsInSeconds() {
        assertNull(InjuryRules.worstTriggered(InjuryRules.BURN, 4.9F));
        assertSame(Ailment.WOUND, InjuryRules.worstTriggered(InjuryRules.BURN, 5.0F).ailment());
        assertSame(Ailment.CRIPPLED, InjuryRules.worstTriggered(InjuryRules.BURN, 15.0F).ailment());
    }

    @Test
    @DisplayName("burn names SKIN itself; a fall leaves the part to the caller's roll")
    void whoNamesThePart() {
        assertEquals(java.util.List.of(BodyPart.SKIN),
                InjuryRules.worstTriggered(InjuryRules.BURN, 5.0F).parts());
        assertTrue(InjuryRules.worstTriggered(InjuryRules.FALL, 8.0F).parts().isEmpty());
    }

    @Test
    @DisplayName("an unknown channel has no rules and never throws")
    void anUnknownChannelIsEmpty() {
        assertTrue(InjuryRules.rules("nothing_registered_here").isEmpty());
        assertNull(InjuryRules.worstTriggered("nothing_registered_here", 1000.0F));
    }

    @Test
    @DisplayName("both built-in channels grade on the BODY scale, so SKIN and the legs can hold them")
    void theBuiltInRulesFitTheirParts() {
        for (String channel : new String[] {InjuryRules.FALL, InjuryRules.BURN}) {
            for (InjuryRule rule : InjuryRules.rules(channel)) {
                for (BodyPart part : rule.parts()) {
                    assertTrue(part.accepts(rule.ailment()), part + " cannot hold " + rule.ailment());
                }
            }
        }
        assertTrue(BodyPart.LEG_LEFT.accepts(Ailment.WOUND));
        assertTrue(BodyPart.LEG_RIGHT.accepts(Ailment.CRIPPLED));
    }
}
