package com.gctn.tconstruct.tools.toolcolors;

import com.gctn.tconstruct.library.TinkerMaterials;
import com.gctn.tconstruct.library.materials.Material;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.component.CustomData;

public final class PickaxeColor {
    private static final int DEFAULT_COLOR = 0xFFFFFFFF;

    private PickaxeColor() {
    }

    public static final ItemColor PICKAXE_COLOR = (stack, tintIndex) -> {
        CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
        if (customData == null) {
            return DEFAULT_COLOR;
        }

        CompoundTag tag = customData.getUnsafe();
        return switch (tintIndex) {
            case 0 -> getMaterialColor(tag.getString("Handle"));
            case 1 -> getMaterialColor(tag.getString("Head"));
            case 2 -> getMaterialColor(tag.getString("Binding"));
            default -> DEFAULT_COLOR;
        };
    };

    private static int getMaterialColor(String materialId) {
        Material material = TinkerMaterials.getMaterial(materialId);
        return material == null ? DEFAULT_COLOR : material.color;
    }
}
