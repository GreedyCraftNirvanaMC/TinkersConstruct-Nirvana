package com.gctn.tconstruct.common.tables;

import com.gctn.tconstruct.common.tables.ToolStationSlotPositions.SlotPosition;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class ToolStationMenu extends AbstractContainerMenu {
    public static final int CONTAINER_SIZE = 7;

    public static final int MOVING_SLOT_INDEX = 0;
    public static final int CONDITIONAL_SLOT_INDEX = 1;
    public static final int RESULT_SLOT_INDEX = 6;
    public static final int INPUT_SLOT_COUNT = ToolStationSlotPositions.INPUT_SLOT_COUNT;

    public static final int DEFAULT_MODE_BUTTON = Mode.DEFAULT.getButtonId();
    public static final int DISASSEMBLE_MODE_BUTTON = Mode.DISASSEMBLE.getButtonId();
    public static final int PICKAXE_MODE_BUTTON = Mode.PICKAXE.getButtonId();
    public static final int SHOVEL_MODE_BUTTON = Mode.SHOVEL.getButtonId();
    public static final int AXE_MODE_BUTTON = Mode.AXE.getButtonId();

    private static final int TOOL_STATION_SLOT_COUNT = 1 + Mode.values().length * INPUT_SLOT_COUNT;
    private static final int RESULT_MENU_SLOT = 0;
    private static final int INPUT_MENU_SLOT_START = 1;

    private static final int PLAYER_INVENTORY_COLUMNS = 9;
    private static final int PLAYER_INVENTORY_ROWS = 3;
    private static final int PLAYER_INVENTORY_START = TOOL_STATION_SLOT_COUNT;
    private static final int PLAYER_INVENTORY_END = PLAYER_INVENTORY_START + PLAYER_INVENTORY_COLUMNS * PLAYER_INVENTORY_ROWS;
    private static final int HOTBAR_START = PLAYER_INVENTORY_END;
    private static final int HOTBAR_END = HOTBAR_START + PLAYER_INVENTORY_COLUMNS;

    private static final int RESULT_SLOT_X = 124;
    private static final int RESULT_SLOT_Y = 38;
    private static final int PLAYER_INVENTORY_X = 8;
    private static final int PLAYER_INVENTORY_Y = 92;
    private static final int HOTBAR_Y = 150;
    private static final int SLOT_SPACING = 18;

    private final Container toolStation;
    private Mode mode = Mode.DEFAULT;

    public ToolStationMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, new SimpleContainer(CONTAINER_SIZE));
    }

    public ToolStationMenu(int containerId, Inventory playerInventory, Container container) {
        super(TableMenuRegistries.TOOL_STATION_MENU.get(), containerId);
        this.toolStation = container;
        container.startOpen(playerInventory.player);

        this.addToolStationSlots(container);
        this.addPlayerInventorySlots(playerInventory);
    }

    private void addToolStationSlots(Container container) {
        this.addSlot(new Slot(container, RESULT_SLOT_INDEX, RESULT_SLOT_X, RESULT_SLOT_Y));
        for (Mode slotMode : Mode.values()) {
            for (int inputSlot = 0; inputSlot < INPUT_SLOT_COUNT; inputSlot++) {
                SlotPosition position = ToolStationSlotPositions.getInputSlotPositionOrFallback(slotMode, inputSlot);
                this.addSlot(new ModeAwareInputSlot(container, inputSlot, position.slotX(), position.slotY(), slotMode, inputSlot));
            }
        }
    }

    private void addPlayerInventorySlots(Inventory playerInventory) {
        for (int row = 0; row < PLAYER_INVENTORY_ROWS; row++) {
            for (int column = 0; column < PLAYER_INVENTORY_COLUMNS; column++) {
                int inventorySlot = column + row * PLAYER_INVENTORY_COLUMNS + PLAYER_INVENTORY_COLUMNS;
                int x = PLAYER_INVENTORY_X + column * SLOT_SPACING;
                int y = PLAYER_INVENTORY_Y + row * SLOT_SPACING;
                this.addSlot(new Slot(playerInventory, inventorySlot, x, y));
            }
        }

        for (int column = 0; column < PLAYER_INVENTORY_COLUMNS; column++) {
            int x = PLAYER_INVENTORY_X + column * SLOT_SPACING;
            this.addSlot(new Slot(playerInventory, column, x, HOTBAR_Y));
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int quickMovedSlotIndex) {
        ItemStack quickMovedStack = ItemStack.EMPTY;
        Slot quickMovedSlot = this.slots.get(quickMovedSlotIndex);
        if (quickMovedSlot != null && quickMovedSlot.hasItem()) {
            ItemStack rawStack = quickMovedSlot.getItem();
            quickMovedStack = rawStack.copy();

            if (quickMovedSlotIndex == RESULT_MENU_SLOT) {
                if (!this.moveItemStackTo(rawStack, PLAYER_INVENTORY_START, HOTBAR_END, true)) {
                    return ItemStack.EMPTY;
                }
                quickMovedSlot.onQuickCraft(rawStack, quickMovedStack);
            } else if (quickMovedSlotIndex >= PLAYER_INVENTORY_START && quickMovedSlotIndex < HOTBAR_END) {
                if (!this.moveItemStackTo(rawStack, INPUT_MENU_SLOT_START, TOOL_STATION_SLOT_COUNT, false)) {
                    if (quickMovedSlotIndex < PLAYER_INVENTORY_END) {
                        if (!this.moveItemStackTo(rawStack, HOTBAR_START, HOTBAR_END, false)) {
                            return ItemStack.EMPTY;
                        }
                    } else if (!this.moveItemStackTo(rawStack, PLAYER_INVENTORY_START, PLAYER_INVENTORY_END, false)) {
                        return ItemStack.EMPTY;
                    }
                }
            } else if (!this.moveItemStackTo(rawStack, PLAYER_INVENTORY_START, HOTBAR_END, false)) {
                return ItemStack.EMPTY;
            }

            if (rawStack.isEmpty()) {
                quickMovedSlot.setByPlayer(ItemStack.EMPTY);
            } else {
                quickMovedSlot.setChanged();
            }

            if (rawStack.getCount() == quickMovedStack.getCount()) {
                return ItemStack.EMPTY;
            }

            quickMovedSlot.onTake(player, rawStack);
        }

        return quickMovedStack;
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        this.toolStation.stopOpen(player);
    }

    @Override
    public boolean stillValid(Player player) {
        return this.toolStation.stillValid(player);
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        Mode mode = Mode.byButtonId(id);
        if (mode != null) {
            this.mode = mode;
            return true;
        }
        return false;
    }

    public boolean isMode(Mode mode) {
        return this.mode == mode;
    }

    public boolean hasToolSlotItem() {
        return !this.toolStation.getItem(RESULT_SLOT_INDEX).isEmpty();
    }

    public boolean isInputSlotActive(int inputSlot) {
        return ToolStationSlotPositions.isInputSlotActive(this.mode, inputSlot);
    }

    public SlotPosition getInputSlotPosition(int inputSlot) {
        return ToolStationSlotPositions.getInputSlotPosition(this.mode, inputSlot);
    }

    private class ModeAwareInputSlot extends Slot {
        private final Mode slotMode;
        private final int inputSlot;

        ModeAwareInputSlot(Container container, int slot, int x, int y, Mode slotMode, int inputSlot) {
            super(container, slot, x, y);
            this.slotMode = slotMode;
            this.inputSlot = inputSlot;
        }

        @Override
        public boolean isActive() {
            return ToolStationMenu.this.mode == this.slotMode
                    && ToolStationMenu.this.isInputSlotActive(this.inputSlot);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return this.isActive() && super.mayPlace(stack);
        }
    }

    public enum Mode {
        DEFAULT,
        DISASSEMBLE,
        PICKAXE,
        SHOVEL,
        AXE;

        public int getButtonId() {
            return this.ordinal();
        }

        public static Mode byButtonId(int id) {
            Mode[] modes = values();
            if (id < 0 || id >= modes.length) {
                return null;
            }
            return modes[id];
        }
    }

}
