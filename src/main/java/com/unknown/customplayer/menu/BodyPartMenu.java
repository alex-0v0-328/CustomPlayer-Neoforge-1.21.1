package com.unknown.customplayer.menu;

import com.unknown.customplayer.attachment.service.body.InjuryService;
import com.unknown.customplayer.attachment.service.body.PartStorageService;
import com.unknown.customplayer.custom.enums.body.BodyPart;
import com.unknown.customplayer.registry.ModMenus;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * The container behind the body screen: one slot per part.
 *
 * <p>⚠ Saving hangs off a container listener rather than an override of the changed hook -- a menu is
 * not a container listener, so that override never runs and a logout would eat the deposit.
 *
 * @author Alex
 * @since 1.0.0
 */
public class BodyPartMenu extends AbstractContainerMenu {

    public static final BodyPart[] SLOTS = {
            BodyPart.EYES, BodyPart.EARS, BodyPart.NOSE, BodyPart.MOUTH, BodyPart.BRAIN,
            BodyPart.TORSO, BodyPart.ARM_LEFT, BodyPart.ARM_RIGHT,
            BodyPart.LEG_LEFT, BodyPart.LEG_RIGHT,
            BodyPart.BONE, BodyPart.SKIN, BodyPart.MUSCLE, BodyPart.SINEW,
    };

    public static final int PART_SLOTS = SLOTS.length;

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

    private boolean moveToBody(ItemStack stack) {
        for (int i = 0; i < PART_SLOTS; i++) {
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
