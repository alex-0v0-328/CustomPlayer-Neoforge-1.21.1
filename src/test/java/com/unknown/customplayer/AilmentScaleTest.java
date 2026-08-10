package com.unknown.customplayer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.unknown.customplayer.custom.enums.body.Ailment;
import com.unknown.customplayer.custom.enums.body.BodyPart;
import com.unknown.customplayer.custom.enums.body.PartScale;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class AilmentScaleTest {

    @Test
    @DisplayName("a part accepts only its OWN scale -- a leg can never read blind")
    void aPartAcceptsOnlyItsOwnScale() {
        assertTrue(BodyPart.EYES.accepts(Ailment.LOST));
        assertTrue(BodyPart.EYES.accepts(Ailment.DESTROYED));
        assertFalse(BodyPart.EYES.accepts(Ailment.WOUND));
        assertFalse(BodyPart.EYES.accepts(Ailment.RUINED));

        assertTrue(BodyPart.LEG_LEFT.accepts(Ailment.STRAIN));
        assertTrue(BodyPart.LEG_LEFT.accepts(Ailment.RUINED));
        assertFalse(BodyPart.LEG_LEFT.accepts(Ailment.LOST));
        assertFalse(BodyPart.LEG_LEFT.accepts(Ailment.DESTROYED));
    }

    @Test
    @DisplayName("every part accepts exactly the grades of its scale, and nothing else")
    void everyPairIsAccountedFor() {
        for (BodyPart part : BodyPart.values()) {
            for (Ailment grade : Ailment.values()) {
                assertEquals(part.scale() == grade.scale(), part.accepts(grade),
                        part + " vs " + grade);
            }
        }
    }

    @Test
    @DisplayName("ordinal order within a scale IS the severity order")
    void severityFollowsOrdinal() {
        assertTrue(Ailment.DESTROYED.worseThan(Ailment.LOST));
        assertFalse(Ailment.LOST.worseThan(Ailment.DESTROYED));

        assertTrue(Ailment.WOUND.worseThan(Ailment.STRAIN));
        assertTrue(Ailment.CRIPPLED.worseThan(Ailment.WOUND));
        assertTrue(Ailment.RUINED.worseThan(Ailment.CRIPPLED));
        assertFalse(Ailment.STRAIN.worseThan(Ailment.RUINED));
    }

    @Test
    @DisplayName("nothing is worse than itself, so a repeat hit never re-sends")
    void nothingIsWorseThanItself() {
        for (Ailment grade : Ailment.values()) {
            assertFalse(grade.worseThan(grade), grade + " reported itself as worse");
        }
    }

    @Test
    @DisplayName("anything is worse than no ailment at all")
    void anythingBeatsNull() {
        for (Ailment grade : Ailment.values()) {
            assertTrue(grade.worseThan(null), grade + " did not beat a healthy part");
        }
    }

    @Test
    @DisplayName("across scales neither is worse -- the comparison is not a total order, by design")
    void acrossScalesNeitherWins() {
        assertFalse(Ailment.RUINED.worseThan(Ailment.LOST));
        assertFalse(Ailment.LOST.worseThan(Ailment.RUINED));
        assertFalse(Ailment.DESTROYED.worseThan(Ailment.STRAIN));
        assertFalse(Ailment.STRAIN.worseThan(Ailment.DESTROYED));
    }

    @Test
    @DisplayName("the four whole-body parts are NOT regional, the other ten are")
    void wholeBodyPartsAreNotRegional() {
        assertFalse(BodyPart.BONE.regional());
        assertFalse(BodyPart.SKIN.regional());
        assertFalse(BodyPart.MUSCLE.regional());
        assertFalse(BodyPart.SINEW.regional());

        int regional = 0;
        for (BodyPart part : BodyPart.values()) {
            if (part.regional()) regional++;
        }
        assertEquals(10, regional);
    }

    @Test
    @DisplayName("a BODY grade shares one key; a SENSE loss is keyed PER PART")
    void twoKeyingSchemes() {
        assertEquals(Ailment.WOUND.getTranslationKey(BodyPart.LEG_LEFT),
                Ailment.WOUND.getTranslationKey(BodyPart.TORSO));

        assertEquals(false, Ailment.LOST.getTranslationKey(BodyPart.EYES)
                .equals(Ailment.LOST.getTranslationKey(BodyPart.EARS)));
    }

    @Test
    @DisplayName("every part belongs to exactly one of the two scales")
    void everyPartHasAScale() {
        for (BodyPart part : BodyPart.values()) {
            assertTrue(part.scale() == PartScale.SENSE || part.scale() == PartScale.BODY, part.name());
        }
    }
}
