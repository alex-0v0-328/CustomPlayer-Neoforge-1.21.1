package com.unknown.customplayer.client.screen;

import com.unknown.customplayer.attachment.data.body.PartState;
import com.unknown.customplayer.attachment.service.body.BodyPartService;
import com.unknown.customplayer.custom.enums.body.Ailment;
import com.unknown.customplayer.custom.enums.body.BodyPart;
import com.unknown.customplayer.menu.BodyPartMenu;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

//  A blocky figure drawn entirely with g.fill -- no texture anywhere, so it is sharp at every GUI scale.
//  ⚠⚠ NO connector lines. Fourteen slots pointing at fourteen targets is spaghetti at any line weight;
//  hovering either a slot or a region highlights the pair instead. Nothing is drawn until it is wanted,
//  and then exactly one thing is.
public class BodyPartScreen extends AbstractContainerScreen<BodyPartMenu> {

    private record Box(int x1, int y1, int x2, int y2) {}

    //  ⚠ BRAIN is drawn as the SKULL. There is no HEAD part, and inventing a decorative one just to have
    //  something to hang the senses on would put a region on screen that no data backs.
    private static final Map<BodyPart, Box> FIGURE = new EnumMap<>(BodyPart.class);

    static {
        FIGURE.put(BodyPart.BRAIN,     new Box(79, 16,  97,  34));
        FIGURE.put(BodyPart.TORSO,     new Box(76, 38, 100,  70));
        FIGURE.put(BodyPart.ARM_LEFT,  new Box(66, 38,  74,  66));
        FIGURE.put(BodyPart.ARM_RIGHT, new Box(102, 38, 110, 66));
        FIGURE.put(BodyPart.LEG_LEFT,  new Box(79, 74,  87, 104));
        FIGURE.put(BodyPart.LEG_RIGHT, new Box(89, 74,  97, 104));
        //  The four senses are marks ON the skull, not limbs.
        FIGURE.put(BodyPart.EARS,      new Box(77, 22,  99,  24));
        FIGURE.put(BodyPart.EYES,      new Box(82, 26,  94,  28));
        FIGURE.put(BodyPart.NOSE,      new Box(87, 29,  89,  31));
        FIGURE.put(BodyPart.MOUTH,     new Box(84, 32,  92,  33));
    }

    //  ⚠⚠ Hit-testing order is the REVERSE of drawing order: the senses are painted on top of the skull,
    //  so they must be asked first. Using BodyPart.values() would answer "brain" for every pixel of an eye.
    private static final BodyPart[] HIT_ORDER = {
            BodyPart.EARS, BodyPart.EYES, BodyPart.NOSE, BodyPart.MOUTH,
            BodyPart.BRAIN, BodyPart.TORSO, BodyPart.ARM_LEFT, BodyPart.ARM_RIGHT,
            BodyPart.LEG_LEFT, BodyPart.LEG_RIGHT,
    };

    //  ⚠ Bone, skin, muscle and sinew are everywhere, so they get a tint bar under their own slot rather
    //  than a spot on the figure. Pinning "bone" to one place would misdescribe what it is.
    private static final int TINT_BAR_H = 3;
    private static final int TINT_BAR_GAP = 2;

    private static final int PANEL_FILL = 0xBF000000;
    private static final int BORDER = 0x66FFFFFF;
    private static final int SLOT_FILL = 0x33FFFFFF;
    private static final int TEXT = 0xFFFFFFFF;
    private static final int ACCENT = 0xFF7FB2E5;
    private static final int HIGHLIGHT = 0xFFFFFFFF;

    //  ⚠ Status colours are the IDENTITY of a condition, not a judgement of a number -- there are no
    //  numbers here. Healthy is the accent; each grade darkens or reddens what it owns.
    private static final int HEALTHY = 0x807FB2E5;
    private static final int OUTLINE = 0x40FFFFFF;

    public BodyPartScreen(BodyPartMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageWidth = 176;
        this.imageHeight = 222;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    protected void renderBg(@NotNull GuiGraphics g, float partialTick, int mouseX, int mouseY) {
        int x = leftPos;
        int y = topPos;
        g.fill(x, y, x + imageWidth, y + imageHeight, PANEL_FILL);
        g.renderOutline(x, y, imageWidth, imageHeight, BORDER);
        g.fill(x + 7, y + 13, x + imageWidth - 7, y + 14, ACCENT);

        BodyPart focus = focused(mouseX, mouseY);
        renderFigure(g, x, y, focus);
        renderSlots(g, x, y, focus);
    }

    //  Skull, trunk and limbs first, then the senses on top -- they sit inside the skull box.
    private void renderFigure(GuiGraphics g, int x, int y, @Nullable BodyPart focus) {
        for (int i = HIT_ORDER.length - 1; i >= 0; i--) {
            BodyPart part = HIT_ORDER[i];
            Box box = FIGURE.get(part);
            g.fill(x + box.x1(), y + box.y1(), x + box.x2(), y + box.y2(), tint(part));
            g.renderOutline(x + box.x1(), y + box.y1(),
                    box.x2() - box.x1(), box.y2() - box.y1(), part == focus ? HIGHLIGHT : OUTLINE);
        }
    }

    private void renderSlots(GuiGraphics g, int x, int y, @Nullable BodyPart focus) {
        for (int i = 0; i < BodyPartMenu.PART_SLOTS; i++) {
            BodyPart part = menu.part(i);
            int sx = x + BodyPartMenu.SLOT_X[i];
            int sy = y + BodyPartMenu.SLOT_Y[i];
            g.fill(sx, sy, sx + 16, sy + 16, SLOT_FILL);
            if (part == focus) g.renderOutline(sx - 1, sy - 1, 18, 18, HIGHLIGHT);

            //  Only the placeless ones carry their own status bar; the rest are tinted on the figure.
            if (!part.regional()) {
                int by = sy + 16 + TINT_BAR_GAP;
                g.fill(sx, by, sx + 16, by + TINT_BAR_H, tint(part));
            }
        }
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                int sx = x + 8 + col * 18;
                int sy = y + 140 + row * 18;
                g.fill(sx, sy, sx + 16, sy + 16, SLOT_FILL);
            }
        }
        for (int col = 0; col < 9; col++) {
            int sx = x + 8 + col * 18;
            g.fill(sx, y + 198, sx + 16, y + 198 + 16, SLOT_FILL);
        }
    }

    //  ⚠ One region can only be one colour, so this ordinal-to-shade mapping is where severity is read.
    //  The two scales share the ramp: worse is darker and redder, whichever ladder it came from.
    private int tint(BodyPart part) {
        Ailment ailment = BodyPartService.state(minecraft.player, part).ailment();
        if (ailment == null) return HEALTHY;
        return switch (ailment) {
            case STRAIN -> 0x90C8C07A;
            case WOUND -> 0xB0E5A03C;
            case CRIPPLED -> 0xC0E5603C;
            case RUINED -> 0xD0993030;
            case LOST -> 0x90707070;
            case DESTROYED -> 0xD0402020;
        };
    }

    //  Which part the cursor means, from EITHER side -- a slot or a region. ⚠ The slot is asked first:
    //  a whole-body slot has no region at all, and the figure must not answer for it.
    private @Nullable BodyPart focused(int mouseX, int mouseY) {
        for (int i = 0; i < BodyPartMenu.PART_SLOTS; i++) {
            int sx = BodyPartMenu.SLOT_X[i];
            int sy = BodyPartMenu.SLOT_Y[i];
            if (inBox(new Box(sx, sy, sx + 16, sy + 16), mouseX - leftPos, mouseY - topPos)) {
                return menu.part(i);
            }
        }
        for (BodyPart part : HIT_ORDER) {
            if (inBox(FIGURE.get(part), mouseX - leftPos, mouseY - topPos)) return part;
        }
        return null;
    }

    @Override
    protected void renderLabels(@NotNull GuiGraphics g, int mouseX, int mouseY) {
        g.drawString(font, title, titleLabelX, titleLabelY, ACCENT, false);
        g.drawString(font, playerInventoryTitle, inventoryLabelX, inventoryLabelY, TEXT, false);
    }

    @Override
    public void render(@NotNull GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        super.render(g, mouseX, mouseY, partialTick);
        renderPartTooltip(g, mouseX, mouseY);
        renderTooltip(g, mouseX, mouseY);
    }

    //  Hovering either side names the part and says what is wrong with it.
    //  ⚠ Runs BEFORE renderTooltip, so a hovered slot's item tooltip still wins -- an item under the
    //  cursor is the more specific answer to "what am I pointing at".
    private void renderPartTooltip(GuiGraphics g, int mouseX, int mouseY) {
        if (hoveredSlot != null && hoveredSlot.hasItem()) return;

        BodyPart part = focused(mouseX, mouseY);
        if (part == null) return;

        PartState state = BodyPartService.state(minecraft.player, part);
        List<Component> lines = new ArrayList<>();
        lines.add(Component.translatable(part.getTranslationKey()));
        if (state.ailment() != null) {
            lines.add(Component.translatable(state.ailment().getTranslationKey(part)));
        }
        g.renderComponentTooltip(font, lines, mouseX, mouseY);
    }

    private static boolean inBox(Box box, int x, int y) {
        return x >= box.x1() && x < box.x2() && y >= box.y1() && y < box.y2();
    }
}
