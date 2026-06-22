package com.gctn.tconstruct.common.tables;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class ToolStationMenu extends AbstractContainerMenu {
    public static final int CONTAINER_SIZE = 7;

    public static final int DEFAULT_MODE_BUTTON = Mode.DEFAULT.getButtonId();
    public static final int DISASSEMBLE_MODE_BUTTON = Mode.DISASSEMBLE.getButtonId();
    public static final int PICKAXE_MODE_BUTTON = Mode.PICKAXE.getButtonId();
    public static final int SHOVEL_MODE_BUTTON = Mode.SHOVEL.getButtonId();
    public static final int AXE_MODE_BUTTON = Mode.AXE.getButtonId();

    public static final int MOVING_SLOT_INDEX = 0;
    public static final int CONDITIONAL_SLOT_INDEX = 1;
    public static final int RESULT_SLOT_INDEX = 6;

    private static final int TOOL_STATION_SLOT_COUNT = 4;
    private static final int RESULT_MENU_SLOT = 0;
    private static final int PLAYER_INVENTORY_START = TOOL_STATION_SLOT_COUNT;
    private static final int PLAYER_INVENTORY_END = PLAYER_INVENTORY_START + 27;
    private static final int HOTBAR_START = PLAYER_INVENTORY_END;
    private static final int HOTBAR_END = HOTBAR_START + 9;

    private static final int RESULT_SLOT_X = 124;
    private static final int RESULT_SLOT_Y = 38;
    private static final int MOVING_SLOT_EMPTY_X = 104;
    private static final int MOVING_SLOT_EMPTY_Y = 38;
    private static final int MOVING_SLOT_FILLED_X = 84;
    private static final int MOVING_SLOT_FILLED_Y = 38;
    private static final int CONDITIONAL_SLOT_X = 104;
    private static final int CONDITIONAL_SLOT_Y = 38;

    private final Container toolStation;
    private Mode mode = Mode.DEFAULT;

    public ToolStationMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, new SimpleContainer(CONTAINER_SIZE));
    }

    public ToolStationMenu(int containerId, Inventory playerInventory, Container container) {
        super(TableMenuRegistries.TOOL_STATION_MENU.get(), containerId);
        this.toolStation = container;
        container.startOpen(playerInventory.player);

        this.addSlot(new Slot(container, RESULT_SLOT_INDEX, RESULT_SLOT_X, RESULT_SLOT_Y));
        this.addSlot(new ToolStationMenu.SlotWhenToolSlotState(container, MOVING_SLOT_INDEX, MOVING_SLOT_EMPTY_X, MOVING_SLOT_EMPTY_Y, false));
        this.addSlot(new ToolStationMenu.SlotWhenToolSlotState(container, MOVING_SLOT_INDEX, MOVING_SLOT_FILLED_X, MOVING_SLOT_FILLED_Y, true));
        this.addSlot(new ToolStationMenu.SlotWhenToolSlotState(container, CONDITIONAL_SLOT_INDEX, CONDITIONAL_SLOT_X, CONDITIONAL_SLOT_Y, true));

        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                this.addSlot(new Slot(playerInventory, column + row * 9 + 9, 8 + column * 18, 92 + row * 18));
            }
        }

        for (int column = 0; column < 9; column++) {
            this.addSlot(new Slot(playerInventory, column, 8 + column * 18, 150));
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
                if (!this.moveItemStackTo(rawStack, 1, TOOL_STATION_SLOT_COUNT, false)) {
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

    private class SlotWhenToolSlotState extends Slot {
        private final boolean activeWhenToolSlotHasItem;

        SlotWhenToolSlotState(Container container, int slot, int x, int y, boolean activeWhenToolSlotHasItem) {
            super(container, slot, x, y);
            this.activeWhenToolSlotHasItem = activeWhenToolSlotHasItem;
        }

        @Override
        public boolean isActive() {
            return ToolStationMenu.this.hasToolSlotItem() == this.activeWhenToolSlotHasItem;
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return this.isActive() && super.mayPlace(stack);
        }

        @Override
        public boolean mayPickup(Player player) {
            return this.isActive() && super.mayPickup(player);
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
