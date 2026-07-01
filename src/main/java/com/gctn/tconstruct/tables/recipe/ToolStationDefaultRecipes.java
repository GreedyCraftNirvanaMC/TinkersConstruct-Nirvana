package com.gctn.tconstruct.tables.recipe;

import com.gctn.tconstruct.library.MaterialList;
import com.gctn.tconstruct.library.materials.Material;
import com.gctn.tconstruct.tools.TinkerTools;
import com.gctn.tconstruct.tools.TinkerTools.RepairMaterial;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

        RepairPlan repairPlan = createRepairPlan(container);
        if (repairPlan == null) {
            return;
        }

        container.setItem(TOOL_SLOT, ItemStack.EMPTY);
        for (RepairMatch repairMatch : repairPlan.matches()) {
            container.removeItem(repairMatch.slot(), 1);
        }
    }

    private static ItemStack createEnhancementResult(Container container) {
        return ItemStack.EMPTY;
    }

    private static void consumeEnhancementInputs(Container container) {
    }

    private static ItemStack createRepairResult(Container container) {
        ItemStack tool = container.getItem(TOOL_SLOT);
        RepairPlan repairPlan = createRepairPlan(container);
        if (repairPlan == null) {
            return ItemStack.EMPTY;
        }

        ItemStack repairedTool = tool.copy();
        repairedTool.setCount(1);
        repairedTool.setDamageValue(Math.max(0, tool.getDamageValue() - repairPlan.repairAmount()));
        return repairedTool;
    }

    private static RepairPlan createRepairPlan(Container container) {
        ItemStack tool = container.getItem(TOOL_SLOT);
        if (!isRepairableTool(tool)) {
            return null;
        }

        Collection<RepairMatch> repairMatches = findRepairMatches(container, tool);
        if (repairMatches.isEmpty()) {
            return null;
        }

        int remainingDamage = tool.getDamageValue();
        int totalRepairAmount = 0;
        List<RepairMatch> usedMatches = new ArrayList<>();
        for (RepairMatch repairMatch : repairMatches) {
            if (remainingDamage <= 0) {
                break;
            }

            int repairAmount = getRepairAmount(tool, repairMatch.coefficient());
            usedMatches.add(repairMatch);
            totalRepairAmount += repairAmount;
            remainingDamage -= repairAmount;
        }

        if (usedMatches.isEmpty()) {
            return null;
        }

        return new RepairPlan(List.copyOf(usedMatches), totalRepairAmount);
    }

    private static Collection<RepairMatch> findRepairMatches(Container container, ItemStack tool) {
        List<RepairMatch> repairMatches = new ArrayList<>();
        TinkerTools tinkerTool = (TinkerTools) tool.getItem();
        Set<String> checkedMaterialIds = new HashSet<>();
        Set<Integer> usedSlots = new HashSet<>();

        for (RepairMaterial repairMaterial : tinkerTool.getRepairMaterials(tool)) {
            Material material = repairMaterial.material();
            if (material == null || material.identifier == null || repairMaterial.coefficient() <= 0.0F) {
                continue;
            }
            if (!checkedMaterialIds.add(material.identifier)) {
                continue;
            }

            RepairMatch repairMatch = findInputMatch(container, material, repairMaterial.coefficient(), usedSlots);
            if (repairMatch != null) {
                repairMatches.add(repairMatch);
                usedSlots.add(repairMatch.slot());
            }
        }

        return repairMatches;
    }

    private static RepairMatch findInputMatch(Container container, Material material, float coefficient, Set<Integer> usedSlots) {
        ensureMaterialListInitialized();

        for (int slot = FIRST_MATERIAL_SLOT; slot <= LAST_MATERIAL_SLOT; slot++) {
            if (usedSlots.contains(slot)) {
                continue;
            }

            ItemStack materialStack = container.getItem(slot);
            if (materialStack.isEmpty()) {
                continue;
            }

            String inputItemId = BuiltInRegistries.ITEM.getKey(materialStack.getItem()).toString();
            if (materialMatchesInput(material, inputItemId)) {
                return new RepairMatch(slot, materialStack, coefficient);
            }
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

    private static void ensureMaterialListInitialized() {
        if (MaterialList.MATERIAL_MAP.isEmpty()) {
            MaterialList.init();
        }
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

    private static int getRepairAmount(ItemStack tool, float coefficient) {
        return Math.max(1, (int) Math.ceil(tool.getMaxDamage() * REPAIR_FRACTION_PER_MATERIAL * coefficient));
    }

    private record RepairPlan(Collection<RepairMatch> matches, int repairAmount) {
    }

    private record RepairMatch(int slot, ItemStack materialStack, float coefficient) {
    }
}
