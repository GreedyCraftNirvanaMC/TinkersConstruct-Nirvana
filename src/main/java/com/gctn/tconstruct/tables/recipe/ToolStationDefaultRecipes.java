package com.gctn.tconstruct.tables.recipe;

import com.gctn.tconstruct.library.MaterialList;
import com.gctn.tconstruct.library.materials.Material;
import com.gctn.tconstruct.tools.TinkerTools;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

public final class ToolStationDefaultRecipes {
    private static final int TOOL_SLOT = 0;
    private static final int FIRST_MATERIAL_SLOT = 1;
    private static final int LAST_MATERIAL_SLOT = 5;
    private static final float REPAIR_FRACTION_PER_MATERIAL = 0.3F;

    private ToolStationDefaultRecipes() {
    }

    public static ItemStack createResult(Container container) {
        ItemStack tool = container.getItem(TOOL_SLOT);
        if (!isTinkerTool(tool)) {
            return ItemStack.EMPTY;
        }

        ItemStack enhancedTool = createEnhancementResult(container);
        if (!enhancedTool.isEmpty()) {
            return enhancedTool;
        }

        return createRepairResult(container);
    }

    public static void consumeInputs(Container container) {
        ItemStack tool = container.getItem(TOOL_SLOT);
        if (!isTinkerTool(tool)) {
            return;
        }

        ItemStack enhancedTool = createEnhancementResult(container);
        if (!enhancedTool.isEmpty()) {
            consumeEnhancementInputs(container);
            return;
        }

        RepairMatch repairMatch = findRepairMatch(container);
        if (repairMatch == null) {
            return;
        }

        int materialCount = getMaterialCountToRepair(tool, repairMatch.materialStack());
        if (materialCount <= 0) {
            return;
        }

        container.setItem(TOOL_SLOT, ItemStack.EMPTY);
        container.removeItem(repairMatch.slot(), materialCount);
    }

    private static ItemStack createEnhancementResult(Container container) {
        return ItemStack.EMPTY;
    }

    private static void consumeEnhancementInputs(Container container) {
    }

    private static ItemStack createRepairResult(Container container) {
        ItemStack tool = container.getItem(TOOL_SLOT);
        RepairMatch repairMatch = findRepairMatch(container);
        if (repairMatch == null) {
            return ItemStack.EMPTY;
        }

        int materialCount = getMaterialCountToRepair(tool, repairMatch.materialStack());
        if (materialCount <= 0) {
            return ItemStack.EMPTY;
        }

        ItemStack repairedTool = tool.copy();
        repairedTool.setCount(1);
        repairedTool.setDamageValue(Math.max(0, tool.getDamageValue() - materialCount * getRepairAmount(tool)));
        return repairedTool;
    }

    private static RepairMatch findRepairMatch(Container container) {
        ItemStack tool = container.getItem(TOOL_SLOT);
        if (!isRepairableTool(tool)) {
            return null;
        }

        for (int slot = FIRST_MATERIAL_SLOT; slot <= LAST_MATERIAL_SLOT; slot++) {
            ItemStack materialStack = container.getItem(slot);
            if (materialStack.isEmpty()) {
                continue;
            }

            return isMatchingHeadMaterial(tool, materialStack) ? new RepairMatch(slot, materialStack) : null;
        }
        return null;
    }

    private static boolean isTinkerTool(ItemStack stack) {
        return stack.getItem() instanceof TinkerTools;
    }

    private static boolean isRepairableTool(ItemStack stack) {
        return isTinkerTool(stack)
                && stack.isDamageableItem()
                && stack.getMaxDamage() > 0
                && stack.getDamageValue() > 0;
    }

    private static boolean isMatchingHeadMaterial(ItemStack tool, ItemStack materialStack) {
        ensureMaterialListInitialized();

        String inputItemId = BuiltInRegistries.ITEM.getKey(materialStack.getItem()).toString();
        CompoundTag toolTag = getCustomDataTag(tool);
        if (toolTag == null) {
            return false;
        }

        for (String key : toolTag.getAllKeys()) {
            if (!isHeadMaterialKey(key)) {
                continue;
            }

            Material headMaterial = getListedMaterial(toolTag.getString(key));
            if (headMaterial != null && materialMatchesInput(headMaterial, inputItemId)) {
                return true;
            }
        }
        return false;
    }

    private static void ensureMaterialListInitialized() {
        if (MaterialList.MATERIAL_MAP.isEmpty()) {
            MaterialList.init();
        }
    }

    private static Material getListedMaterial(String materialId) {
        for (Material material : MaterialList.MATERIAL_MAP.keySet()) {
            if (material.identifier.equals(materialId)) {
                return material;
            }
        }
        return null;
    }

    private static boolean isHeadMaterialKey(String key) {
        return key.equals("Head") || key.startsWith("Head") || key.endsWith("Head");
    }

    private static boolean materialMatchesInput(Material material, String inputItemId) {
        String[] itemIds = MaterialList.MATERIAL_MAP.get(material);
        if (itemIds == null) {
            return false;
        }

        for (String itemId : itemIds) {
            if (inputItemId.equals(itemId)) {
                return true;
            }
        }
        return false;
    }

    private static CompoundTag getCustomDataTag(ItemStack stack) {
        CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
        if (customData == null) {
            return null;
        }

        return customData.copyTag();
    }

    private static int getMaterialCountToRepair(ItemStack tool, ItemStack materialStack) {
        int repairAmount = getRepairAmount(tool);
        int missingDurability = tool.getDamageValue();
        int materialsNeeded = (missingDurability + repairAmount - 1) / repairAmount;
        return Math.min(materialStack.getCount(), materialsNeeded);
    }

    private static int getRepairAmount(ItemStack tool) {
        return Math.max(1, (int) Math.ceil(tool.getMaxDamage() * REPAIR_FRACTION_PER_MATERIAL));
    }

    private record RepairMatch(int slot, ItemStack materialStack) {
    }
}
