package com.gctn.tconstruct.tables.menu;

import com.gctn.tconstruct.tables.data.Mode;
import com.gctn.tconstruct.tables.data.ModeAwareInputSlot;
import com.gctn.tconstruct.tables.data.ToolStationSlotPositions;
import com.gctn.tconstruct.tables.data.ToolStationSlotPositions.SlotPosition;
import com.gctn.tconstruct.tables.recipe.ToolAssemblyRecipe;
import com.gctn.tconstruct.tables.recipe.ToolStationDefaultRecipes;
import com.gctn.tconstruct.tables.recipe.ToolStationAssemblyRecipes;
import com.gctn.tconstruct.tables.recipe.ToolStationDisassemblyRecipes;
import com.gctn.tconstruct.tables.recipe.ToolStationDisassemblyRecipes.PartList;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
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
    public static final int DEBUG_MODE_BUTTON = Mode.DEBUG.getButtonId();

    private static final int TOOL_STATION_SLOT_COUNT = 1 + Mode.values().length * INPUT_SLOT_COUNT;
    private static final int RESULT_MENU_SLOT = 0;
    private static final int INPUT_MENU_SLOT_START = 1;
    private static final int MAX_VISIBLE_SPECIAL_MODE_INPUTS = 3;
    private static final int HIDDEN_SLOT_X = -9999;
    private static final int HIDDEN_SLOT_Y = -9999;

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
    private final SimpleContainer resultContainer = new SimpleContainer(1);
    private Mode mode = Mode.DEFAULT;
    private boolean disassemblyPending;

    public ToolStationMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, new SimpleContainer(CONTAINER_SIZE));
    }

    public ToolStationMenu(int containerId, Inventory playerInventory, Container container) {
        super(TableMenuRegistries.TOOL_STATION_MENU.get(), containerId);
        this.toolStation = container;
        container.startOpen(playerInventory.player);

        this.addToolStationSlots(container);
        this.addPlayerInventorySlots(playerInventory);
        this.updateResult();
    }

    private void addToolStationSlots(Container container) {
        this.addSlot(new ToolStationResultSlot(this, this.resultContainer, 0, RESULT_SLOT_X, RESULT_SLOT_Y));

        for (Mode eachMode : Mode.values()) {
            for (int slotIndex = 0; slotIndex < INPUT_SLOT_COUNT; slotIndex++) {
                SlotPosition position = ToolStationSlotPositions.getInputSlotPosition(eachMode, slotIndex);
                int x = position == null ? HIDDEN_SLOT_X : position.slotX();
                int y = position == null ? HIDDEN_SLOT_Y : position.slotY();
                this.addSlot(new ModeAwareInputSlot(this, container, slotIndex, x, y, eachMode, slotIndex));
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
                if (this.mode == Mode.DISASSEMBLE
                        && this.moveItemStackTo(rawStack, RESULT_MENU_SLOT, RESULT_MENU_SLOT + 1, false)) {
                    // Moved a full tool into the disassembly source slot.
                } else if (!this.moveItemStackTo(rawStack, INPUT_MENU_SLOT_START, TOOL_STATION_SLOT_COUNT, false)) {
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
        this.cancelDisassemblyAndReturnTool(player);
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
        if (mode != null && this.isModeButtonVisible(mode)) {
            if (this.mode == Mode.DISASSEMBLE && mode != Mode.DISASSEMBLE) {
                this.cancelDisassemblyAndReturnTool(player);
            }
            this.mode = mode;
            this.updateResult();
            return true;
        }
        return false;
    }

    public void inputsChanged() {
        this.updateResult();
    }

    public boolean isMode(Mode mode) {
        return this.mode == mode;
    }

    public boolean isModeButtonVisible(Mode mode) {
        return mode == Mode.DEFAULT
                || mode == Mode.DISASSEMBLE
                || ToolStationSlotPositions.getActiveInputSlotCount(mode) <= MAX_VISIBLE_SPECIAL_MODE_INPUTS;
    }

    public boolean isInputSlotActive(int inputSlot) {
        return ToolStationSlotPositions.isInputSlotActive(this.mode, inputSlot);
    }

    public SlotPosition getInputSlotPosition(int inputSlot) {
        return ToolStationSlotPositions.getInputSlotPosition(this.mode, inputSlot);
    }

    public ItemStack getInputSlotIcon(int inputSlot) {
        Item requiredItem = this.getRequiredInputItem(this.mode, inputSlot);
        return requiredItem == null ? ItemStack.EMPTY : new ItemStack(requiredItem);
    }

    public Item getRequiredInputItem(Mode mode, int inputSlot) {
        if (!ToolStationSlotPositions.isInputSlotActive(mode, inputSlot)) {
            return null;
        }

        ToolAssemblyRecipe recipe = ToolStationAssemblyRecipes.get(mode);
        return recipe == null ? null : recipe.getRequiredItem(inputSlot);
    }

    public boolean canStartDisassembly(ItemStack stack) {
        return this.mode == Mode.DISASSEMBLE
                && !this.disassemblyPending
                && this.resultContainer.getItem(0).isEmpty()
                && this.areInputSlotsEmpty()
                && ToolStationDisassemblyRecipes.canDisassemble(stack);
    }

    public void startDisassembly(ItemStack sourceTool) {
        PartList parts = ToolStationDisassemblyRecipes.createParts(sourceTool);
        if (parts.isEmpty() || !this.areInputSlotsEmpty()) {
            this.resultContainer.setItem(0, ItemStack.EMPTY);
            this.broadcastChanges();
            return;
        }

        this.disassemblyPending = true;
        ItemStack[] partStacks = parts.parts();
        for (int inputSlot = 0; inputSlot < partStacks.length; inputSlot++) {
            this.toolStation.setItem(inputSlot, partStacks[inputSlot].copy());
        }
        this.toolStation.setChanged();
        this.broadcastChanges();
    }

    public void disassemblyPartTaken() {
        if (!this.disassemblyPending) {
            return;
        }

        this.disassemblyPending = false;
        this.resultContainer.setItem(0, ItemStack.EMPTY);
        this.resultContainer.setChanged();
        this.toolStation.setChanged();
        this.broadcastChanges();
    }

    private void updateResult() {
        if (this.mode == Mode.DISASSEMBLE) {
            if (!this.disassemblyPending && !this.resultContainer.getItem(0).isEmpty()) {
                this.resultContainer.setItem(0, ItemStack.EMPTY);
            }
            this.broadcastChanges();
            return;
        }

        ItemStack result = this.createResult();
        this.resultContainer.setItem(0, result);
        this.broadcastChanges();
    }

    private ItemStack createResult() {
        if (this.mode == Mode.DEFAULT) {
            return ToolStationDefaultRecipes.createResult(this.toolStation);
        }

        ToolAssemblyRecipe recipe = ToolStationAssemblyRecipes.get(this.mode);
        if (recipe == null) {
            return ItemStack.EMPTY;
        }

        return recipe.createResult(this.toolStation);
    }

    private boolean hasCraftingResult() {
        return !this.createResult().isEmpty();
    }

    private void takeCraftingResult(Player player) {
        if (this.mode == Mode.DEFAULT) {
            if (!this.hasCraftingResult()) {
                this.updateResult();
                return;
            }

            ToolStationDefaultRecipes.consumeInputs(this.toolStation);
            this.toolStation.setChanged();
            this.updateResult();
            return;
        }

        ToolAssemblyRecipe recipe = ToolStationAssemblyRecipes.get(this.mode);
        if (recipe == null || !this.hasCraftingResult()) {
            this.updateResult();
            return;
        }

        recipe.consumeInputs(this.toolStation);
        this.toolStation.setChanged();
        this.updateResult();
    }

    private boolean areInputSlotsEmpty() {
        for (int inputSlot = 0; inputSlot < INPUT_SLOT_COUNT; inputSlot++) {
            if (!this.toolStation.getItem(inputSlot).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private void cancelDisassemblyPreview() {
        if (!this.disassemblyPending) {
            return;
        }

        this.disassemblyPending = false;
        for (int inputSlot = 0; inputSlot < INPUT_SLOT_COUNT; inputSlot++) {
            this.toolStation.setItem(inputSlot, ItemStack.EMPTY);
        }
        this.toolStation.setChanged();
        this.broadcastChanges();
    }

    private void cancelDisassemblyAndReturnTool(Player player) {
        if (!this.disassemblyPending) {
            return;
        }

        ItemStack sourceTool = this.resultContainer.getItem(0);
        this.resultContainer.setItem(0, ItemStack.EMPTY);
        this.cancelDisassemblyPreview();
        if (!sourceTool.isEmpty()) {
            player.getInventory().placeItemBackInInventory(sourceTool);
        }
    }

    private static class ToolStationResultSlot extends Slot {
        private final ToolStationMenu menu;

        ToolStationResultSlot(ToolStationMenu menu, Container container, int slot, int x, int y) {
            super(container, slot, x, y);
            this.menu = menu;
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return this.menu.canStartDisassembly(stack);
        }

        @Override
        public boolean mayPickup(Player player) {
            if (this.menu.isMode(Mode.DISASSEMBLE)) {
                return this.menu.disassemblyPending && this.hasItem();
            }
            return this.menu.hasCraftingResult();
        }

        @Override
        public int getMaxStackSize() {
            return 1;
        }

        @Override
        public void setByPlayer(ItemStack newStack, ItemStack oldStack) {
            super.setByPlayer(newStack, oldStack);
            if (!this.menu.isMode(Mode.DISASSEMBLE)) {
                return;
            }

            if (newStack.isEmpty()) {
                this.menu.cancelDisassemblyPreview();
            } else if (!this.menu.disassemblyPending) {
                this.menu.startDisassembly(newStack);
            }
        }

        @Override
        public void onTake(Player player, ItemStack stack) {
            super.onTake(player, stack);
            if (this.menu.isMode(Mode.DISASSEMBLE)) {
                this.menu.cancelDisassemblyPreview();
            } else {
                this.menu.takeCraftingResult(player);
            }
        }
    }

}
