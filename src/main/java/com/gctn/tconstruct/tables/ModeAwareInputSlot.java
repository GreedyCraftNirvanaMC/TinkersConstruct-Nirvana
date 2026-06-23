package com.gctn.tconstruct.tables;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

class ModeAwareInputSlot extends Slot {
    private final ToolStationMenu menu;
    private final Mode slotMode;
    private final int slotIndex;

    ModeAwareInputSlot(ToolStationMenu menu, Container container, int slot, int x, int y, Mode slotMode, int slotIndex) {
        super(container, slot, x, y);
        this.menu = menu;
        this.slotMode = slotMode;
        this.slotIndex = slotIndex;
    }

    @Override
    public boolean isActive() {
        return this.menu.isMode(this.slotMode)
                && this.menu.isInputSlotActive(this.slotIndex);
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        if (!this.isActive() || !super.mayPlace(stack)) {
            return false;
        }

        Item requiredItem = this.menu.getRequiredInputItem(this.slotMode, this.slotIndex);
        return requiredItem == null || stack.is(requiredItem);
    }

    @Override
    public void setChanged() {
        super.setChanged();
        this.menu.inputsChanged();
    }
}
