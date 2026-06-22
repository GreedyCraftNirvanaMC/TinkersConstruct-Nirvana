package com.gctn.tconstruct.common.tables.entity;

import com.gctn.tconstruct.common.tables.ToolStationMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class ToolStationBlockEntity extends BaseContainerBlockEntity {
    private static final int CONTAINER_SIZE = ToolStationMenu.CONTAINER_SIZE;

    private NonNullList<ItemStack> items = NonNullList.withSize(CONTAINER_SIZE, ItemStack.EMPTY);

    public ToolStationBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(TableEntityRegistries.TOOL_STATION_BLOCK_ENTITY.get(), blockPos, blockState);
    }

    @Override
    public int  getContainerSize() {
        return CONTAINER_SIZE;
    }

    @Override
    public NonNullList<ItemStack> getItems() {
        return this.items;
    }

    @Override
    public void setItems(NonNullList<ItemStack> items) {
        this.items = items;
    }

    @Override
    protected AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
        return new ToolStationMenu(containerId, inventory, this);
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("container.tconstruct.toolstation.name");
    }
}
