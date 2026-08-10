package com.unknown.customplayer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.unknown.customplayer.attachment.data.body.PartMark;
import com.unknown.customplayer.attachment.data.body.PartState;
import com.unknown.customplayer.custom.enums.body.Ailment;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PartStateTest {

    private static final ResourceLocation KEY =
            ResourceLocation.fromNamespaceAndPath("guzhenren", "strength");
    private static final ResourceLocation OTHER =
            ResourceLocation.fromNamespaceAndPath("guzhenren", "refinement");

    @Test
    @DisplayName("a mark can never go negative")
    void marksClampAtZero() {
        assertEquals(0L, new PartMark(-5L, -1L).mark());
        assertEquals(0L, new PartMark(-5L, -1L).speck());
        assertEquals(7L, new PartMark(7L, 0L).mark());
    }

    @Test
    @DisplayName("an all-zero mark is indistinguishable from absent, which is what keeps the map sparse")
    void defaultMarksArePruned() {
        PartState state = new PartState(null, Map.of(KEY, PartMark.DEFAULT));
        assertTrue(state.marks().isEmpty());
        assertTrue(state.isDefault());
    }

    @Test
    @DisplayName("a real mark survives the pruning, a zero one beside it does not")
    void onlyTheZeroOneIsPruned() {
        PartState state = new PartState(null, Map.of(KEY, new PartMark(3L, 0L), OTHER, PartMark.DEFAULT));
        assertEquals(1, state.marks().size());
        assertEquals(3L, state.mark(KEY).mark());
        assertTrue(state.mark(OTHER).isDefault());
    }

    @Test
    @DisplayName("an absent key reads as the default, never null")
    void anAbsentKeyIsNeverNull() {
        assertSame(PartMark.DEFAULT, PartState.DEFAULT.mark(KEY));
    }

    @Test
    @DisplayName("worsened only ever CLIMBS -- a later smaller hit must not heal")
    void worsenedOnlyClimbs() {
        PartState crippled = PartState.DEFAULT.worsened(Ailment.CRIPPLED);
        assertSame(Ailment.CRIPPLED, crippled.ailment());

        assertSame(Ailment.CRIPPLED, crippled.worsened(Ailment.WOUND).ailment());
        assertSame(Ailment.CRIPPLED, crippled.worsened(Ailment.STRAIN).ailment());
        assertSame(Ailment.RUINED, crippled.worsened(Ailment.RUINED).ailment());
    }

    @Test
    @DisplayName("worsened returns the SAME instance when nothing moved, so no packet is sent")
    void worsenedIsIdentityWhenUnchanged() {
        PartState wound = PartState.DEFAULT.worsened(Ailment.WOUND);
        assertSame(wound, wound.worsened(Ailment.WOUND));
        assertSame(wound, wound.worsened(Ailment.STRAIN));
    }

    @Test
    @DisplayName("a cross-scale grade cannot climb over an existing one")
    void worsenedIgnoresTheOtherScale() {
        PartState blind = PartState.DEFAULT.worsened(Ailment.LOST);
        assertSame(blind, blind.worsened(Ailment.RUINED));
    }

    @Test
    @DisplayName("healing clears the ailment and LEAVES the marks alone")
    void healingKeepsMarks() {
        PartState hurt = new PartState(Ailment.WOUND, Map.of(KEY, new PartMark(10L, 2L)));
        PartState healed = hurt.healed();

        assertEquals(null, healed.ailment());
        assertFalse(healed.hurt());
        assertEquals(10L, healed.mark(KEY).mark());
        assertEquals(2L, healed.mark(KEY).speck());
    }

    @Test
    @DisplayName("healing a part with no marks collapses all the way to DEFAULT")
    void healingWithNoMarksIsDefault() {
        assertSame(PartState.DEFAULT, new PartState(Ailment.WOUND, Map.of()).healed());
    }

    @Test
    @DisplayName("with() does not disturb the ailment")
    void withKeepsTheAilment() {
        PartState hurt = new PartState(Ailment.CRIPPLED, Map.of());
        assertSame(Ailment.CRIPPLED, hurt.with(KEY, new PartMark(1L, 0L)).ailment());
    }
}
