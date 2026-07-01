package com.gctn.tconstruct.tables.menu;

import com.gctn.tconstruct.tables.data.Mode;
import com.gctn.tconstruct.tables.data.ModeAwareInputSlot;
import com.gctn.tconstruct.tables.data.ToolStationResultSlot;
import com.gctn.tconstruct.tables.data.ToolStationSlotPositions;
import com.gctn.tconstruct.tables.data.ToolStationSlotPositions.SlotPosition;
import com.gctn.tconstruct.tables.recipe.ToolAssemblyRecipe;
import com.gctn.tconstruct.tables.recipe.ToolStationAssemblyRecipes;
import com.gctn.tconstruct.tables.recipe.ToolStationDefaultRecipes;
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
    // 真实输入容器只有 7 格；菜单会为每个模式创建一套可见槽，再用 isActive 控制当前可交互槽。
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

    private static final int RESULT_MENU_SLOT = 0;
    private static final int INPUT_MENU_SLOT_START = 1;
    private static final int TOOL_STATION_SLOT_COUNT = 1 + Mode.values().length * INPUT_SLOT_COUNT;

    private static final int PLAYER_INVENTORY_COLUMNS = 9;
    private static final int PLAYER_INVENTORY_ROWS = 3;
    private static final int PLAYER_INVENTORY_START = TOOL_STATION_SLOT_COUNT;
    private static final int PLAYER_INVENTORY_END = PLAYER_INVENTORY_START + PLAYER_INVENTORY_COLUMNS * PLAYER_INVENTORY_ROWS;
    private static final int HOTBAR_START = PLAYER_INVENTORY_END;
    private static final int HOTBAR_END = HOTBAR_START + PLAYER_INVENTORY_COLUMNS;

    private static final int MAX_VISIBLE_SPECIAL_MODE_INPUTS = 3;
    private static final int HIDDEN_SLOT_X = -9999;
    private static final int HIDDEN_SLOT_Y = -9999;

    private static final int RESULT_SLOT_X = 124;
    private static final int RESULT_SLOT_Y = 38;
    private static final int PLAYER_INVENTORY_X = 8;
    private static final int PLAYER_INVENTORY_Y = 92;
    private static final int HOTBAR_Y = 150;
    private static final int SLOT_SPACING = 18;

    private final Container toolStation;
    private final SimpleContainer resultContainer = new SimpleContainer(1);

    private Mode mode = Mode.DEFAULT;

    // 拆解时结果槽暂存原工具，输入槽展示零件；取走任意零件后原工具被视为已消耗。
    private boolean disassemblyPending;

    // 客户端临时菜单使用空容器初始化。
    public ToolStationMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, new SimpleContainer(CONTAINER_SIZE));
    }

    // 服务端菜单绑定方块实体容器，并初始化工具台槽位、玩家背包和首次结果。
    public ToolStationMenu(int containerId, Inventory playerInventory, Container container) {
        super(TableMenuRegistries.TOOL_STATION_MENU.get(), containerId);
        this.toolStation = container;
        container.startOpen(playerInventory.player);

        this.addToolStationSlots(container);
        this.addPlayerInventorySlots(playerInventory);
        this.updateResult();
    }

    // 处理 Shift 点击转移物品，并保持原版菜单的槽位回调顺序。
    @Override
    public ItemStack quickMoveStack(Player player, int quickMovedSlotIndex) {
        Slot quickMovedSlot = this.slots.get(quickMovedSlotIndex);
        if (quickMovedSlot == null || !quickMovedSlot.hasItem()) {
            return ItemStack.EMPTY;
        }

        ItemStack rawStack = quickMovedSlot.getItem();
        ItemStack quickMovedStack = rawStack.copy();
        if (!this.moveQuickMovedStack(quickMovedSlotIndex, rawStack)) {
            return ItemStack.EMPTY;
        }

        if (quickMovedSlotIndex == RESULT_MENU_SLOT) {
            quickMovedSlot.onQuickCraft(rawStack, quickMovedStack);
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
        return quickMovedStack;
    }

    // 菜单关闭时清理拆解预览，并停止访问工具台容器。
    @Override
    public void removed(Player player) {
        this.cancelDisassemblyAndReturnTool(player);
        super.removed(player);
        this.toolStation.stopOpen(player);
    }

    // 让方块实体容器决定玩家是否仍可使用该菜单。
    @Override
    public boolean stillValid(Player player) {
        return this.toolStation.stillValid(player);
    }

    // 处理模式按钮点击；离开拆解模式时需要返还还未拆解的原工具。
    @Override
    public boolean clickMenuButton(Player player, int id) {
        Mode selectedMode = Mode.byButtonId(id);
        if (selectedMode == null || !this.isModeButtonVisible(selectedMode)) {
            return false;
        }

        if (this.mode == Mode.DISASSEMBLE && selectedMode != Mode.DISASSEMBLE) {
            this.cancelDisassemblyAndReturnTool(player);
        }

        this.mode = selectedMode;
        this.updateResult();
        return true;
    }

    // 输入槽通知菜单重新计算输出结果。
    public void inputsChanged() {
        this.updateResult();
    }

    // 给客户端按钮和槽位判断当前菜单模式。
    public boolean isMode(Mode mode) {
        return this.mode == mode;
    }

    // 控制客户端是否显示指定模式按钮。
    public boolean isModeButtonVisible(Mode mode) {
        return mode == Mode.DEFAULT
                || mode == Mode.DISASSEMBLE
                || ToolStationSlotPositions.getActiveInputSlotCount(mode) <= MAX_VISIBLE_SPECIAL_MODE_INPUTS;
    }

    // 判断当前模式下某个输入槽是否应该显示和可交互。
    public boolean isInputSlotActive(int inputSlot) {
        return ToolStationSlotPositions.isInputSlotActive(this.mode, inputSlot);
    }

    // 获取当前模式下输入槽的绘制位置。
    public SlotPosition getInputSlotPosition(int inputSlot) {
        return ToolStationSlotPositions.getInputSlotPosition(this.mode, inputSlot);
    }

    // 获取当前模式下输入槽的半透明提示图标。
    public ItemStack getInputSlotIcon(int inputSlot) {
        Item requiredItem = this.getRequiredInputItem(this.mode, inputSlot);
        return requiredItem == null ? ItemStack.EMPTY : new ItemStack(requiredItem);
    }

    // 查询指定模式下某个输入槽要求的物品，用于限制放入物和绘制提示图标。
    public Item getRequiredInputItem(Mode targetMode, int inputSlot) {
        if (!ToolStationSlotPositions.isInputSlotActive(targetMode, inputSlot)) {
            return null;
        }

        ToolAssemblyRecipe recipe = ToolStationAssemblyRecipes.get(targetMode);
        return recipe == null ? null : recipe.getRequiredItem(inputSlot);
    }

    // 结果槽询问当前物品是否可以作为拆解源工具放入。
    public boolean canPlaceResultSlot(ItemStack stack) {
        return this.mode == Mode.DISASSEMBLE
                && !this.disassemblyPending
                && this.resultContainer.getItem(0).isEmpty()
                && this.areInputSlotsEmpty()
                && ToolStationDisassemblyRecipes.canDisassemble(stack);
    }

    // 结果槽询问玩家当前是否可以取走输出或拆解源工具。
    public boolean canTakeResultSlot() {
        if (this.mode == Mode.DISASSEMBLE) {
            return this.disassemblyPending && !this.resultContainer.getItem(0).isEmpty();
        }

        return !this.resultContainer.getItem(0).isEmpty();
    }

    // 结果槽内容变化时触发拆解预览或取消拆解预览。
    public void resultSlotChanged(ItemStack newStack) {
        if (this.mode != Mode.DISASSEMBLE) {
            return;
        }

        if (newStack.isEmpty()) {
            this.cancelDisassemblyPreview();
        } else if (!this.disassemblyPending) {
            this.startDisassembly(newStack);
        }
    }

    // 玩家从结果槽取物时，按当前模式分别消费合成材料或取消拆解预览。
    public void resultSlotTaken(Player player, ItemStack stack) {
        if (this.mode == Mode.DISASSEMBLE) {
            this.cancelDisassemblyPreview();
        } else {
            this.takeCraftingResult();
        }
    }

    // 拆解零件被取走时，结束拆解状态并保留剩余零件。
    public void disassemblyPartTaken() {
        this.endDisassembly(false, null);
    }

    // 添加工具台的结果槽和所有模式的输入槽。
    private void addToolStationSlots(Container container) {
        this.addSlot(new ToolStationResultSlot(this, this.resultContainer, 0, RESULT_SLOT_X, RESULT_SLOT_Y));

        for (Mode slotMode : Mode.values()) {
            for (int slotIndex = 0; slotIndex < INPUT_SLOT_COUNT; slotIndex++) {
                this.addModeInputSlot(container, slotMode, slotIndex);
            }
        }
    }

    // 为一个模式创建一个输入槽；隐藏槽放到屏幕外并由 isActive 禁止交互。
    private void addModeInputSlot(Container container, Mode slotMode, int slotIndex) {
        SlotPosition position = ToolStationSlotPositions.getInputSlotPosition(slotMode, slotIndex);
        int x = position == null ? HIDDEN_SLOT_X : position.slotX();
        int y = position == null ? HIDDEN_SLOT_Y : position.slotY();
        this.addSlot(new ModeAwareInputSlot(this, container, slotIndex, x, y, slotMode, slotIndex));
    }

    // 添加玩家背包三行槽位和快捷栏槽位。
    private void addPlayerInventorySlots(Inventory playerInventory) {
        for (int row = 0; row < PLAYER_INVENTORY_ROWS; row++) {
            this.addPlayerInventoryRow(playerInventory, row);
        }

        for (int column = 0; column < PLAYER_INVENTORY_COLUMNS; column++) {
            int x = PLAYER_INVENTORY_X + column * SLOT_SPACING;
            this.addSlot(new Slot(playerInventory, column, x, HOTBAR_Y));
        }
    }

    // 添加玩家背包中的一行普通物品槽。
    private void addPlayerInventoryRow(Inventory playerInventory, int row) {
        for (int column = 0; column < PLAYER_INVENTORY_COLUMNS; column++) {
            int inventorySlot = column + row * PLAYER_INVENTORY_COLUMNS + PLAYER_INVENTORY_COLUMNS;
            int x = PLAYER_INVENTORY_X + column * SLOT_SPACING;
            int y = PLAYER_INVENTORY_Y + row * SLOT_SPACING;
            this.addSlot(new Slot(playerInventory, inventorySlot, x, y));
        }
    }

    // 根据 Shift 点击来源决定目标槽位范围。
    private boolean moveQuickMovedStack(int quickMovedSlotIndex, ItemStack stack) {
        if (quickMovedSlotIndex == RESULT_MENU_SLOT) {
            return this.moveItemStackTo(stack, PLAYER_INVENTORY_START, HOTBAR_END, true);
        }

        if (quickMovedSlotIndex >= PLAYER_INVENTORY_START && quickMovedSlotIndex < HOTBAR_END) {
            return this.movePlayerStack(quickMovedSlotIndex, stack);
        }

        return this.moveItemStackTo(stack, PLAYER_INVENTORY_START, HOTBAR_END, false);
    }

    // 从玩家背包 Shift 点击时，优先放入工具台，失败后在背包和快捷栏之间移动。
    private boolean movePlayerStack(int quickMovedSlotIndex, ItemStack stack) {
        if (this.mode == Mode.DISASSEMBLE
                && this.moveItemStackTo(stack, RESULT_MENU_SLOT, RESULT_MENU_SLOT + 1, false)) {
            return true;
        }

        if (this.moveItemStackTo(stack, INPUT_MENU_SLOT_START, TOOL_STATION_SLOT_COUNT, false)) {
            return true;
        }

        return quickMovedSlotIndex < PLAYER_INVENTORY_END
                ? this.moveItemStackTo(stack, HOTBAR_START, HOTBAR_END, false)
                : this.moveItemStackTo(stack, PLAYER_INVENTORY_START, PLAYER_INVENTORY_END, false);
    }

    // 刷新结果槽；拆解模式下结果槽只负责暂存原工具，不生成合成产物。
    private void updateResult() {
        if (this.mode == Mode.DISASSEMBLE) {
            if (!this.disassemblyPending && !this.resultContainer.getItem(0).isEmpty()) {
                this.resultContainer.setItem(0, ItemStack.EMPTY);
            }
            this.broadcastChanges();
            return;
        }

        this.resultContainer.setItem(0, this.createResult());
        this.broadcastChanges();
    }

    // 按当前模式计算合成输出。
    private ItemStack createResult() {
        if (this.mode == Mode.DEFAULT) {
            return ToolStationDefaultRecipes.createResult(this.toolStation);
        }

        ToolAssemblyRecipe recipe = ToolStationAssemblyRecipes.get(this.mode);
        return recipe == null ? ItemStack.EMPTY : recipe.createResult(this.toolStation);
    }

    // 玩家取走合成结果后，消费当前模式对应的输入材料。
    private void takeCraftingResult() {
        if (this.createResult().isEmpty()) {
            this.updateResult();
            return;
        }

        if (this.mode == Mode.DEFAULT) {
            ToolStationDefaultRecipes.consumeInputs(this.toolStation);
        } else {
            ToolAssemblyRecipe recipe = ToolStationAssemblyRecipes.get(this.mode);
            if (recipe == null) {
                this.updateResult();
                return;
            }
            recipe.consumeInputs(this.toolStation);
        }

        this.toolStation.setChanged();
        this.updateResult();
    }

    // 根据放入结果槽的源工具生成拆解零件预览。
    private void startDisassembly(ItemStack sourceTool) {
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

    // 取消拆解预览并清空已生成但尚未取走的零件。
    private void cancelDisassemblyPreview() {
        this.endDisassembly(true, null);
    }

    // 菜单关闭或切换模式时，取消拆解预览并把源工具返还给玩家。
    private void cancelDisassemblyAndReturnTool(Player player) {
        this.endDisassembly(true, player);
    }

    // 统一结束拆解状态，可选择清空预览零件和返还源工具。
    private void endDisassembly(boolean clearPreviewParts, Player returnToolTo) {
        if (!this.disassemblyPending) {
            return;
        }

        ItemStack sourceTool = this.resultContainer.getItem(0);
        this.disassemblyPending = false;
        this.resultContainer.setItem(0, ItemStack.EMPTY);

        if (clearPreviewParts) {
            for (int inputSlot = 0; inputSlot < INPUT_SLOT_COUNT; inputSlot++) {
                this.toolStation.setItem(inputSlot, ItemStack.EMPTY);
            }
        }

        if (returnToolTo != null && !sourceTool.isEmpty()) {
            returnToolTo.getInventory().placeItemBackInInventory(sourceTool);
        }

        this.resultContainer.setChanged();
        this.toolStation.setChanged();
        this.broadcastChanges();
    }

    // 判断真实输入容器中的所有输入槽是否为空。
    private boolean areInputSlotsEmpty() {
        for (int inputSlot = 0; inputSlot < INPUT_SLOT_COUNT; inputSlot++) {
            if (!this.toolStation.getItem(inputSlot).isEmpty()) {
                return false;
            }
        }
        return true;
    }
}
