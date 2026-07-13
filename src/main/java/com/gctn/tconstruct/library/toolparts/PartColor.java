package com.gctn.tconstruct.library.toolparts;

import com.gctn.tconstruct.library.TinkerMaterials;
import com.gctn.tconstruct.library.materials.Material;
import com.gctn.tconstruct.library.utils.Tags;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.CustomData;

public final class PartColor {
    private static final int DEFAULT_COLOR = 0xFFFFFFFF;

    private PartColor() {
    }

    public static final ItemColor PART_COLOR = (stack, tintIndex) -> {
        CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
        if (customData == null) {
            return DEFAULT_COLOR;
        }

        Material material = TinkerMaterials.getMaterial(customData.getUnsafe().getString(Tags.PART_MATERIAL));
        return material == null ? DEFAULT_COLOR : material.color;
    };
}
