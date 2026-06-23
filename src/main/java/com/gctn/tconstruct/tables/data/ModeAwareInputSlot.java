package com.gctn.tconstruct.tables.data;

import com.gctn.tconstruct.tables.menu.ToolStationMenu;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class ModeAwareInputSlot extends Slot {
    private final ToolStationMenu menu;
    private final Mode slotMode;
    private final int slotIndex;

    public ModeAwareInputSlot(ToolStationMenu menu, Container container, int slot, int x, int y, Mode slotMode, int slotIndex) {
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

        if (this.slotMode == Mode.DISASSEMBLE) {
            return false;
        }

        Item requiredItem = this.menu.getRequiredInputItem(this.slotMode, this.slotIndex);
        return requiredItem == null || stack.is(requiredItem);
    }

    @Override
    public void onTake(net.minecraft.world.entity.player.Player player, ItemStack stack) {
        super.onTake(player, stack);
        if (this.slotMode == Mode.DISASSEMBLE) {
            this.menu.disassemblyPartTaken();
        }
    }

    @Override
    public void setChanged() {
        super.setChanged();
        this.menu.inputsChanged();
    }
}
