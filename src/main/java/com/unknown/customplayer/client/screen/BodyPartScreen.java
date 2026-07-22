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
//  Each region is tinted by what is wrong with that part, and a hairline connects it to its slot.
public class BodyPartScreen extends AbstractContainerScreen<BodyPartMenu> {

    private record Box(int x1, int y1, int x2, int y2) {}

    //  ⚠ BRAIN is drawn as the SKULL. There is no HEAD part, and inventing a decorative one just to have
    //  something to hang the senses on would put a region on screen that no data backs.
    private static final Map<BodyPart, Box> FIGURE = new EnumMap<>(BodyPart.class);

    static {
        FIGURE.put(BodyPart.BRAIN,     new Box(79, 16,  97,  34));
        FIGURE.put(BodyPart.TORSO,     new Box(76, 38, 100,  72));
        FIGURE.put(BodyPart.ARM_LEFT,  new Box(66, 38,  74,  68));
        FIGURE.put(BodyPart.ARM_RIGHT, new Box(102, 38, 110, 68));
        FIGURE.put(BodyPart.LEG_LEFT,  new Box(79, 76,  87, 108));
        FIGURE.put(BodyPart.LEG_RIGHT, new Box(89, 76,  97, 108));
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

    //  ⚠ Whole-body parts get a labelled square each, NOT a place on the figure. Drawing "bone" at some
    //  spot would lie about what it means -- it is the state of every bone, not a break in one place.
    private static final BodyPart[] WHOLE_BODY = {
            BodyPart.BONE, BodyPart.SKIN, BodyPart.MUSCLE, BodyPart.SINEW,
    };

    private static final int WHOLE_Y = 116;
    private static final int WHOLE_SIZE = 10;
    private static final int WHOLE_STEP = 34;
    private static final int WHOLE_LEFT = 30;

    private static final int PANEL_FILL = 0xBF000000;
    private static final int BORDER = 0x66FFFFFF;
    private static final int SLOT_FILL = 0x33FFFFFF;
    private static final int TEXT = 0xFFFFFFFF;
    private static final int MUTED = 0xFFA0A0A0;
    private static final int LINE = 0x33FFFFFF;
    private static final int ACCENT = 0xFF7FB2E5;

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

        renderConnectors(g, x, y);
        renderFigure(g, x, y);
        renderWholeBody(g, x, y);
        renderSlots(g, x, y);
    }

    //  ⚠ Drawn FIRST, so the figure and the slots both sit on top of their own hairlines.
    private void renderConnectors(GuiGraphics g, int x, int y) {
        for (int i = 0; i < BodyPartMenu.PART_SLOTS; i++) {
            Box box = FIGURE.get(menu.part(i));
            if (box == null) continue;

            int slotX = BodyPartMenu.SLOT_X[i];
            int slotY = BodyPartMenu.SLOT_Y[i] + 8;
            int partX = (box.x1() + box.x2()) / 2;
            int partY = (box.y1() + box.y2()) / 2;

            //  An L, never a diagonal: g.fill draws rectangles, and a stepped line would look ragged.
            int fromX = slotX < partX ? slotX + 16 : slotX;
            g.fill(x + Math.min(fromX, partX), y + slotY, x + Math.max(fromX, partX), y + slotY + 1, LINE);
            g.fill(x + partX, y + Math.min(slotY, partY), x + partX + 1, y + Math.max(slotY, partY), LINE);
        }
    }

    //  Skull, trunk and limbs first, then the senses on top -- they sit inside the skull box.
    private void renderFigure(GuiGraphics g, int x, int y) {
        for (int i = HIT_ORDER.length - 1; i >= 0; i--) drawPart(g, x, y, HIT_ORDER[i]);
    }

    private void drawPart(GuiGraphics g, int x, int y, BodyPart part) {
        Box box = FIGURE.get(part);
        g.fill(x + box.x1(), y + box.y1(), x + box.x2(), y + box.y2(), tint(part));
        g.renderOutline(x + box.x1(), y + box.y1(),
                box.x2() - box.x1(), box.y2() - box.y1(), OUTLINE);
    }

    //  Bone / skin / muscle / sinew: a square and its name, under the figure.
    private void renderWholeBody(GuiGraphics g, int x, int y) {
        for (int i = 0; i < WHOLE_BODY.length; i++) {
            BodyPart part = WHOLE_BODY[i];
            int px = x + WHOLE_LEFT + i * WHOLE_STEP;
            g.fill(px, y + WHOLE_Y, px + WHOLE_SIZE, y + WHOLE_Y + WHOLE_SIZE, tint(part));
            g.renderOutline(px, y + WHOLE_Y, WHOLE_SIZE, WHOLE_SIZE, OUTLINE);

            Component name = Component.translatable(part.getTranslationKey());
            g.drawString(font, name, px + WHOLE_SIZE + 3, y + WHOLE_Y + 1, MUTED, false);
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

    private void renderSlots(GuiGraphics g, int x, int y) {
        for (int i = 0; i < BodyPartMenu.PART_SLOTS; i++) {
            int sx = x + BodyPartMenu.SLOT_X[i];
            int sy = y + BodyPartMenu.SLOT_Y[i];
            g.fill(sx, sy, sx + 16, sy + 16, SLOT_FILL);
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

    //  Hovering a region names the part and says what is wrong with it.
    //  ⚠ Runs BEFORE renderTooltip, so a hovered slot's item tooltip still wins -- an item under the
    //  cursor is the more specific answer to "what am I pointing at".
    private void renderPartTooltip(GuiGraphics g, int mouseX, int mouseY) {
        if (hoveredSlot != null && hoveredSlot.hasItem()) return;

        BodyPart part = hovered(mouseX - leftPos, mouseY - topPos);
        if (part == null) return;

        PartState state = BodyPartService.state(minecraft.player, part);
        List<Component> lines = new ArrayList<>();
        lines.add(Component.translatable(part.getTranslationKey()));
        if (state.ailment() != null) {
            lines.add(Component.translatable(state.ailment().getTranslationKey(part)));
        }
        g.renderComponentTooltip(font, lines, mouseX, mouseY);
    }

    private @Nullable BodyPart hovered(int x, int y) {
        for (BodyPart part : HIT_ORDER) {
            if (inBox(FIGURE.get(part), x, y)) return part;
        }
        for (int i = 0; i < WHOLE_BODY.length; i++) {
            int px = WHOLE_LEFT + i * WHOLE_STEP;
            if (inBox(new Box(px, WHOLE_Y, px + WHOLE_SIZE, WHOLE_Y + WHOLE_SIZE), x, y)) {
                return WHOLE_BODY[i];
            }
        }
        return null;
    }

    private static boolean inBox(Box box, int x, int y) {
        return x >= box.x1() && x < box.x2() && y >= box.y1() && y < box.y2();
    }
}
