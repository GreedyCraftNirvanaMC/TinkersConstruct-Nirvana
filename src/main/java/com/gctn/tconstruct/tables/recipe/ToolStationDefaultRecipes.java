package com.gctn.tconstruct.tables.recipe;

import com.gctn.tconstruct.library.MaterialList;
import com.gctn.tconstruct.library.materials.Material;
import com.gctn.tconstruct.tools.TinkerTools;
import com.gctn.tconstruct.tools.TinkerTools.RepairInfo;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class ToolStationDefaultRecipes {
    private static final int TOOL_SLOT = 0;
    private static final int FIRST_MATERIAL_SLOT = 1;
    private static final int LAST_MATERIAL_SLOT = 5;
    private static final int MATERIAL_SLOT_COUNT = LAST_MATERIAL_SLOT - FIRST_MATERIAL_SLOT + 1;

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
        if (repairPlan == null || repairPlan.isEmpty()) {
            return;
        }

        container.setItem(TOOL_SLOT, ItemStack.EMPTY);
        for (int i = 0; i < MATERIAL_SLOT_COUNT; i++) {
            if (repairPlan.repairCount[i] > 0) {
                container.removeItem(FIRST_MATERIAL_SLOT + i, repairPlan.repairCount[i]);
            }
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
        if (repairPlan == null || repairPlan.isEmpty()) {
            return ItemStack.EMPTY;
        }

        ItemStack repairedTool = tool.copy();
        repairedTool.setCount(1);
        repairedTool.setDamageValue(repairPlan.remainingDamage);
        return repairedTool;
    }

    private static RepairPlan createRepairPlan(Container container) {
        ItemStack tool = container.getItem(TOOL_SLOT);
        if (!isRepairableTool(tool)) {
            return null;
        }
        int remainingDamage = tool.getDamageValue();
        int[] totalRepairAmount = new int[MATERIAL_SLOT_COUNT];

        while (remainingDamage > 0) {
            List<RepairMatch> repairMatches = findRepairMatches(container, tool, totalRepairAmount);
            if (repairMatches.isEmpty()) {
                break;
            }

            double matchEfficiency = 1 + (repairMatches.size() - 1) / 9.0;
            for (RepairMatch repairMatch : repairMatches) {
                if (remainingDamage == 0) {
                    break;
                }
                int repairAmount = getRepairAmount(repairMatch, matchEfficiency);

                int slot = repairMatch.slot() - FIRST_MATERIAL_SLOT;
                totalRepairAmount[slot] += 1;
                remainingDamage = Math.max(0, remainingDamage - repairAmount);
            }
        }

        return areAllMaterialInputsValid(container, tool)
                ? new RepairPlan(totalRepairAmount, remainingDamage)
                : null;
    }

    private static boolean areAllMaterialInputsValid(Container container, ItemStack tool) {
        List<RepairInfo> repairInfos = ((TinkerTools) tool.getItem()).getRepairInfo(tool);
        for (int slot = FIRST_MATERIAL_SLOT; slot <= LAST_MATERIAL_SLOT; slot++) {
            ItemStack input = container.getItem(slot);
            if (!input.isEmpty() && repairInfos.stream().noneMatch(info -> matchesRepairInput(info, input))) {
                return false;
            }
        }
        return true;
    }

    private static boolean matchesRepairInput(RepairInfo repairInfo, ItemStack input) {
        return repairInfo.material() != null
                && repairInfo.partEfficient() > 0.0F
                && repairInfo.durability() > 0
                && materialMatchesInput(repairInfo.material(), BuiltInRegistries.ITEM.getKey(input.getItem()).toString()) > 0.0;
    }

    private static List<RepairMatch> findRepairMatches(Container container, ItemStack tool, int[] totalRepairAmount) {
        List<RepairMatch> repairMatches = new ArrayList<>();
        TinkerTools tinkerTool = (TinkerTools) tool.getItem();
        Set<String> checkedMaterialIds = new HashSet<>();
        List<RepairInfo> repairInfos = tinkerTool.getRepairInfo(tool);

        for (RepairInfo repairInfo : repairInfos) {
            Material material = repairInfo.material();
            if (material == null || repairInfo.partEfficient() <= 0.0F || repairInfo.durability() <= 0) {
                continue;
            }
            // 每种材料只计算一次
            if (!checkedMaterialIds.add(material.identifier)) {
                continue;
            }

            RepairMatch repairMatch = findInputMatch(container, repairInfo, totalRepairAmount);
            if (repairMatch != null) {
                repairMatches.add(repairMatch);
            }
        }

        return repairMatches;
    }

    private static RepairMatch findInputMatch(Container container, RepairInfo repairInfo, int[] totalRepairAmount) {
        Material material = repairInfo.material();
        float partEfficient = repairInfo.partEfficient();

        for (int slot = FIRST_MATERIAL_SLOT; slot <= LAST_MATERIAL_SLOT; slot++) {
            ItemStack materialStack = container.getItem(slot);
            if (materialStack.isEmpty()) {
                continue;
            }
            if(materialStack.getCount() <= totalRepairAmount[slot - FIRST_MATERIAL_SLOT]) {
                continue;
            }
            String inputItemId = BuiltInRegistries.ITEM.getKey(materialStack.getItem()).toString();
            double matEfficient = materialMatchesInput(material, inputItemId);
            if (matEfficient > 0.0) {
                return new RepairMatch(slot, partEfficient, repairInfo.durability(), matEfficient);
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

    private static double materialMatchesInput(Material material, String inputItemId) {
        Map<String, Double> efficiencies = MaterialList.MATERIAL_MAP.get(material);
        return efficiencies == null ? 0.0 : efficiencies.getOrDefault(inputItemId, 0.0);
    }

    private static int getRepairAmount(RepairMatch match, double matchEfficiency) {
        return Math.max(1, (int) Math.ceil(
                match.durability * match.partEfficient * match.matEfficient * matchEfficiency));
    }

    private record RepairPlan(int[] repairCount, int remainingDamage) {
        boolean isEmpty() {
            for (int count : repairCount) {
                if (count > 0) {
                    return false;
                }
            }
            return true;
        }
    }

    private record RepairMatch(int slot, float partEfficient, int durability, double matEfficient) {
    }
}
