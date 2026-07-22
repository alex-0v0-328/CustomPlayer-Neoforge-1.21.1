package com.unknown.customplayer.menu;

import com.unknown.customplayer.attachment.service.body.InjuryService;
import com.unknown.customplayer.attachment.service.body.PartStorageService;
import com.unknown.customplayer.custom.enums.body.BodyPart;
import com.unknown.customplayer.registry.ModMenus;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

//  One slot per installable body part, laid out ON the figure the screen draws -- the coordinates are
//  anatomy, not a grid. HEAD hosts nothing, so nine slots, not ten.
public class BodyPartMenu extends AbstractContainerMenu {

    //  ⚠ Slot order IS this array's order, and quickMoveStack's ranges depend on it. The screen reads
    //  the same array for its connector lines, so the two can never disagree about which slot is which.
    //  All fourteen: every part hosts. ⚠ Order is the layout -- senses down the left, trunk and limbs
    //  down the right, the four whole-body tissues along the bottom.
    public static final BodyPart[] SLOTS = {
            BodyPart.EYES, BodyPart.EARS, BodyPart.NOSE, BodyPart.MOUTH, BodyPart.BRAIN,
            BodyPart.TORSO, BodyPart.ARM_LEFT, BodyPart.ARM_RIGHT,
            BodyPart.LEG_LEFT, BodyPart.LEG_RIGHT,
            BodyPart.BONE, BodyPart.SKIN, BodyPart.MUSCLE, BodyPart.SINEW,
    };

    public static final int PART_SLOTS = SLOTS.length;

    //  Two columns flanking the figure, then a row underneath. Indexed to match SLOTS; the screen reads
    //  these same arrays, so a slot and what the screen says about it can never drift apart.
    //  ⚠ The panel is 222 tall for a reason: at 720p with GUI scale 3 only 240 scaled pixels exist.
    //  An earlier layout came to 248 and would have run off the bottom of the screen.
    //  ⚠⚠ There are NO connector lines. Fourteen of them were spaghetti whatever their weight; the
    //  screen highlights one pair on hover instead, which is stronger exactly when it is wanted.
    public static final int[] SLOT_X = {
            8, 8, 8, 8, 8,
            152, 152, 152, 152, 152,
            26, 62, 98, 134,
    };
    public static final int[] SLOT_Y = {
            16, 34, 52, 70, 88,
            16, 34, 52, 70, 88,
            110, 110, 110, 110,
    };

    private static final int INVENTORY_X = 8;
    private static final int INVENTORY_Y = 140;
    private static final int HOTBAR_Y = 198;
    private static final int SLOT = 18;

    private final Player player;
    private final SimpleContainer parts = new SimpleContainer(PART_SLOTS);

    //  ⚠ Seeding the container fires the listener; without this the load would immediately save itself
    //  back, and on the client that would write nothing while still costing a rebuild.
    private boolean loading;

    public BodyPartMenu(int id, Inventory inventory) {
        super(ModMenus.BODY_PART_MENU.get(), id);
        this.player = inventory.player;

        for (int i = 0; i < PART_SLOTS; i++) {
            addSlot(new PartSlot(i, SLOT_X[i], SLOT_Y[i]));
        }
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new Slot(inventory, col + row * 9 + 9,
                        INVENTORY_X + col * SLOT, INVENTORY_Y + row * SLOT));
            }
        }
        for (int col = 0; col < 9; col++) {
            addSlot(new Slot(inventory, col, INVENTORY_X + col * SLOT, HOTBAR_Y));
        }

        //  ⚠⚠ THE save trigger. Overriding slotsChanged does NOTHING: SimpleContainer.setChanged only
        //  notifies registered listeners, and AbstractContainerMenu is not a ContainerListener -- so
        //  that override is never called, and a logout with the menu open eats the deposit.
        parts.addListener(container -> save());
        load();
    }

    public BodyPart part(int index) {return SLOTS[index];}

    private void load() {
        loading = true;
        for (int i = 0; i < PART_SLOTS; i++) {
            parts.setItem(i, PartStorageService.installed(player, SLOTS[i]));
        }
        loading = false;
    }

    //  Writes on every change, not only on close -- a crash mid-session must not eat what was installed.
    private void save() {
        if (loading || !(player instanceof ServerPlayer server)) return;
        for (int i = 0; i < PART_SLOTS; i++) {
            PartStorageService.install(server, SLOTS[i], parts.getItem(i));
        }
    }

    @Override
    public void removed(@NotNull Player who) {
        save();
        super.removed(who);
    }

    //  Shift-click moves between the body and the pack, and never into a slot that would refuse it.
    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player who, int index) {
        Slot slot = slots.get(index);
        if (!slot.hasItem()) return ItemStack.EMPTY;

        ItemStack stack = slot.getItem();
        ItemStack original = stack.copy();

        if (index < PART_SLOTS) {
            if (!moveItemStackTo(stack, PART_SLOTS, slots.size(), true)) return ItemStack.EMPTY;
        } else if (!moveToBody(stack)) {
            return ItemStack.EMPTY;
        }

        if (stack.isEmpty()) {
            slot.set(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }
        return original;
    }

    //  ⚠ One part at a time, and only where it is allowed: moveItemStackTo would happily drop an eye
    //  implant into a leg, because it never consults mayPlace for a single-item slot.
    private boolean moveToBody(ItemStack stack) {
        List<Integer> targets = new ArrayList<>(PART_SLOTS);
        for (int i = 0; i < PART_SLOTS; i++) targets.add(i);

        for (int i : targets) {
            Slot target = slots.get(i);
            if (!target.hasItem() && target.mayPlace(stack)) {
                target.set(stack.split(1));
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean stillValid(@NotNull Player who) {return who == player && who.isAlive();}

    //  ⚠ TWO questions, deliberately separate: the tag says whether this ITEM belongs in this part, the
    //  injury check says whether the part is fit to hold anything right now. Folding them into one
    //  predicate is how "you cannot install into a blind eye" would quietly become "this item is wrong".
    private class PartSlot extends Slot {
        private final BodyPart part;

        PartSlot(int index, int x, int y) {
            super(parts, index, x, y);
            this.part = SLOTS[index];
        }

        @Override
        public boolean mayPlace(@NotNull ItemStack stack) {
            return stack.is(part.installTag()) && InjuryService.healthyEnoughToInstall(player, part);
        }

        @Override
        public int getMaxStackSize() {return 1;}
    }
}
