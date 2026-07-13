package com.gctn.tconstruct.tables.block.entity;

import com.gctn.tconstruct.tables.menu.ToolStationMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class ToolStationBlockEntity extends BaseContainerBlockEntity {
    private static final int CONTAINER_SIZE = ToolStationMenu.CONTAINER_SIZE;
    private static final String DISASSEMBLY_SOURCE_TAG = "DisassemblySource";
    private static final String DISASSEMBLY_OWNER_TAG = "DisassemblyOwner";

    private NonNullList<ItemStack> items = NonNullList.withSize(CONTAINER_SIZE, ItemStack.EMPTY);
    private ItemStack disassemblySource = ItemStack.EMPTY;
    private UUID disassemblyOwner;
    private ServerPlayer currentUser;

    public ToolStationBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(TableEntityRegistries.TOOL_STATION_BLOCK_ENTITY.get(), blockPos, blockState);
    }

    @Override
    public int getContainerSize() {
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
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(tag, this.items, registries);
        this.disassemblySource = ItemStack.parseOptional(registries, tag.getCompound(DISASSEMBLY_SOURCE_TAG));
        this.disassemblyOwner = tag.hasUUID(DISASSEMBLY_OWNER_TAG) ? tag.getUUID(DISASSEMBLY_OWNER_TAG) : null;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        ContainerHelper.saveAllItems(tag, this.items, registries);
        if (!this.disassemblySource.isEmpty()) {
            tag.put(DISASSEMBLY_SOURCE_TAG, this.disassemblySource.saveOptional(registries));
            if (this.disassemblyOwner != null) {
                tag.putUUID(DISASSEMBLY_OWNER_TAG, this.disassemblyOwner);
            }
        }
    }

    @Override
    protected AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
        return new ToolStationMenu(containerId, inventory, this);
    }

    @Override
    @Nullable
    public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
        if (!(player instanceof ServerPlayer serverPlayer) || !this.canOpen(player)
                || this.isInUseByAnotherPlayer(player)) {
            return null;
        }
        this.recoverAbandonedDisassembly(serverPlayer);
        this.currentUser = serverPlayer;
        return new ToolStationMenu(containerId, inventory, this);
    }

    public boolean isInUseByAnotherPlayer(Player player) {
        if (this.currentUser != null && (this.currentUser.hasDisconnected() || !this.currentUser.isAlive()
                || !(this.currentUser.containerMenu instanceof ToolStationMenu menu)
                || !menu.isBoundTo(this))) {
            this.currentUser = null;
        }
        return this.currentUser != null && this.currentUser != player;
    }

    public boolean beginDisassembly(ItemStack source, Player owner) {
        if (!this.disassemblySource.isEmpty()) {
            return false;
        }
        this.disassemblySource = source;
        this.disassemblyOwner = owner.getUUID();
        this.setChanged();
        return true;
    }

    public void storeDisassemblyParts(ItemStack[] parts) {
        for (int slot = 0; slot < this.items.size(); slot++) {
            this.items.set(slot, slot < parts.length ? parts[slot].copy() : ItemStack.EMPTY);
        }
        this.setChanged();
    }

    public boolean isDisassembling() {
        return !this.disassemblySource.isEmpty();
    }

    public void finishDisassembly() {
        this.disassemblySource = ItemStack.EMPTY;
        this.disassemblyOwner = null;
        this.setChanged();
    }

    public ItemStack cancelDisassembly() {
        ItemStack source = this.disassemblySource;
        this.disassemblySource = ItemStack.EMPTY;
        this.disassemblyOwner = null;
        this.setChanged();
        return source;
    }

    private void recoverAbandonedDisassembly(ServerPlayer player) {
        if (this.disassemblySource.isEmpty()) {
            return;
        }
        UUID owner = this.disassemblyOwner;
        ItemStack source = this.cancelDisassembly();
        this.clearContent();
        this.setChanged();
        if (owner != null && owner.equals(player.getUUID())) {
            player.getInventory().placeItemBackInInventory(source);
        } else if (this.level != null) {
            net.minecraft.world.Containers.dropItemStack(
                    this.level, this.worldPosition.getX(), this.worldPosition.getY(), this.worldPosition.getZ(), source);
        }
    }

    public void rollbackDisassemblyTo(Player player) {
        ItemStack source = this.cancelDisassembly();
        if (!source.isEmpty()) {
            player.getInventory().placeItemBackInInventory(source);
        }
    }

    public void rollbackDisassemblyToWorld() {
        ItemStack source = this.cancelDisassembly();
        if (!source.isEmpty() && this.level != null && !this.level.isClientSide) {
            net.minecraft.world.Containers.dropItemStack(
                    this.level, this.worldPosition.getX(), this.worldPosition.getY(), this.worldPosition.getZ(), source);
        }
    }

    public void clearDisassemblyParts() {
        for (int slot = 0; slot < this.items.size(); slot++) {
            this.items.set(slot, ItemStack.EMPTY);
        }
        this.setChanged();
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        ItemStack removed = super.removeItem(slot, amount);
        if (this.isDisassembling() && !removed.isEmpty()) {
            this.finishDisassembly();
        }
        return removed;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        ItemStack removed = super.removeItemNoUpdate(slot);
        if (this.isDisassembling() && !removed.isEmpty()) {
            this.finishDisassembly();
        }
        return removed;
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        return !this.isDisassembling();
    }

    @Override
    public boolean canTakeItem(Container target, int slot, ItemStack stack) {
        return !this.isDisassembling();
    }

    @Override
    public void stopOpen(Player player) {
        if (this.currentUser == player) {
            this.currentUser = null;
        }
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("container.tconstruct.toolstation.name");
    }
}
