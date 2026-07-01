package com.gctn.tconstruct.tables.recipe;

import com.gctn.tconstruct.library.TinkerMaterials;
import com.gctn.tconstruct.library.materials.Material;
import com.gctn.tconstruct.library.toolparts.Binding;
import com.gctn.tconstruct.library.toolparts.PickaxeHead;
import com.gctn.tconstruct.library.toolparts.ToolRod;
import com.gctn.tconstruct.tools.tools.Pickaxe;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

public final class ToolStationDisassemblyRecipes {
    private ToolStationDisassemblyRecipes() {
    }

    public static boolean canDisassemble(ItemStack stack) {
        return !createParts(stack).isEmpty();
    }

    public static PartList createParts(ItemStack stack) {
        if (!isFullDurabilityTool(stack)) {
            return PartList.EMPTY;
        }

        return switch (stack.getItem()) {
            case net.minecraft.world.item.Item item when item == Pickaxe.PICKAXE.get() -> createPickaxeParts(stack);
            default -> PartList.EMPTY;
        };
    }

    private static boolean isFullDurabilityTool(ItemStack stack) {
        return stack.isDamageableItem() && stack.getDamageValue() == 0;
    }

    private static PartList createPickaxeParts(ItemStack stack) {
        CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
        if (customData == null) {
            return PartList.EMPTY;
        }

        CompoundTag tag = customData.copyTag();
        Material handle = getMaterial(tag, "Handle", "Handle");
        Material head = getMaterial(tag, "Head", "Head");
        Material binding = getMaterial(tag, "Binding", "Extra");
        if (handle == null || head == null || binding == null) {
            return PartList.EMPTY;
        }

        return new PartList(new ItemStack[] {
                ToolRod.getPart(handle),
                PickaxeHead.getColoredPart(head),
                Binding.getColoredPart(binding)
        });
    }

    private static Material getMaterial(CompoundTag tag, String materialKey, String requiredStat) {
        String materialId = tag.getString(materialKey);
        for (Material material : TinkerMaterials.materials) {
            if (material.identifier.equals(materialId)
                    && material.getStats() != null
                    && material.getStats().containsKey(requiredStat)) {
                return material;
            }
        }
        return null;
    }

    public record PartList(ItemStack[] parts) {
        public static final PartList EMPTY = new PartList(new ItemStack[0]);

        public boolean isEmpty() {
            return this.parts.length == 0;
        }
    }
}
