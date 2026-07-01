package com.gctn.tconstruct.tables.recipe;

import com.gctn.tconstruct.TinkersConstructNirvana;
import com.gctn.tconstruct.library.MaterialList;
import com.gctn.tconstruct.library.materials.Material;
import com.gctn.tconstruct.tools.TinkerTools;
import com.gctn.tconstruct.tools.TinkerTools.RepairInfo;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;

import java.util.*;

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
        for (int i = 0; i <= 4; i++) {
            container.removeItem(i + 1, repairPlan.repairCount[i]);
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
        if (repairPlan == null || Arrays.equals(repairPlan.repairCount, new int[]{0, 0, 0, 0, 0})) {
            return ItemStack.EMPTY;
        }

        ItemStack repairedTool = tool.copy();
        repairedTool.setCount(1);
        repairedTool.setDamageValue(Math.max(0, repairPlan.remainingDamage));
        return repairedTool;
    }

    private static RepairPlan createRepairPlan(Container container) {
        ItemStack tool = container.getItem(TOOL_SLOT);
        if (!isRepairableTool(tool)) {
            return null;
        }
        int remainingDamage = tool.getDamageValue();
        int[] totalRepairAmount = {0, 0, 0, 0, 0};

        while (remainingDamage > 0) {
            Collection<RepairMatch> repairMatches = findRepairMatches(container, tool, totalRepairAmount);
            if (repairMatches.isEmpty()) {
                break;
            }

            for (RepairMatch repairMatch : repairMatches) {
                if (remainingDamage <= 0) {
                    break;
                }
                double matchEfficient = 1 + (repairMatches.size() - 1) / 9.0;
                int repairAmount = getRepairAmount(tool, repairMatch.partEfficient, repairMatch.matEfficient, matchEfficient);
                TinkersConstructNirvana.LOGGER.debug(String.valueOf(repairMatch.partEfficient()));
                TinkersConstructNirvana.LOGGER.debug(String.valueOf(repairMatch.matEfficient()));
                TinkersConstructNirvana.LOGGER.debug(String.valueOf(matchEfficient));
                int slot = repairMatch.slot() - 1;
                totalRepairAmount[slot] += 1;
                remainingDamage -= repairAmount;
            }
        }

        return new RepairPlan(totalRepairAmount, remainingDamage);
    }

    private static Collection<RepairMatch> findRepairMatches(Container container, ItemStack tool, int[] totalRepairAmount) {
        List<RepairMatch> repairMatches = new ArrayList<>();
        TinkerTools tinkerTool = (TinkerTools) tool.getItem();
        Set<String> checkedMaterialIds = new HashSet<>();

        for (RepairInfo repairInfo : tinkerTool.getRepairInfo(tool)) {
            Material material = repairInfo.material();
            if (material == null || material.identifier == null || repairInfo.partEfficient() <= 0.0F) {
                continue;
            }
            if (!checkedMaterialIds.add(material.identifier)) {
                continue;
            }

            Collection<RepairMatch> repairMatch = findInputMatch(container, repairInfo, totalRepairAmount);
            if (!repairMatch.isEmpty()) {
                for(RepairMatch match : repairMatch) {
                    repairMatches.add(match);
                }
            }
        }

        return repairMatches;
    }

    private static Collection<RepairMatch> findInputMatch(Container container, RepairInfo repairInfo, int[] totalRepairAmount) {
        ensureMaterialListInitialized();

        Material material = repairInfo.material();
        float partEfficient = repairInfo.partEfficient();

        Collection<RepairMatch> repairMatches = new ArrayList<>();
        for (int slot = FIRST_MATERIAL_SLOT; slot <= LAST_MATERIAL_SLOT; slot++) {
            ItemStack materialStack = container.getItem(slot);
            if (materialStack.isEmpty()) {
                continue;
            }
            if(materialStack.getCount() <= totalRepairAmount[slot- 1 ]) {
                continue;
            }
            String inputItemId = BuiltInRegistries.ITEM.getKey(materialStack.getItem()).toString();
            double matEfficient = materialMatchesInput(material, inputItemId);
            if (matEfficient != 0) {
                repairMatches.add(new RepairMatch(slot, materialStack, partEfficient, matEfficient));
            }
        }

        return repairMatches;
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

    private static double materialMatchesInput(Material material, String inputItemId) {
        double efficient = 0.0;
        Map<String, Double> matEfficient = MaterialList.MATERIAL_MAP.get(material);
        if (matEfficient == null) {
            return efficient;
        }
        efficient = matEfficient.getOrDefault(inputItemId, 0.0);
        return efficient;

    }

    private static int getRepairAmount(ItemStack tool, float partEfficient, double repairEfficient, double matchEfficient) {
        return Math.max(1, (int) Math.ceil(tool.getMaxDamage() * partEfficient * repairEfficient * matchEfficient));
    }

    private record RepairPlan(int[] repairCount, int remainingDamage) {
    }

    private record RepairMatch(int slot, ItemStack materialStack, float partEfficient, double matEfficient) {
    }
}
