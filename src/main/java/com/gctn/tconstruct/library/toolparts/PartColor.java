package com.gctn.tconstruct.library.toolparts;

import com.gctn.tconstruct.library.TinkerMaterials;
import com.gctn.tconstruct.library.materials.Material;
import com.gctn.tconstruct.utils.Tags;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.component.CustomData;

public class PartColor {
    public static final ItemColor PART_COLOR = (stack, tintIndex) -> {
        CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
        if (customData == null) return 0xFFFFFFFF; // 无NBT时返回默认白色

        CompoundTag tag = customData.copyTag();
        if (!tag.contains(Tags.PART_MATERIAL)) return 0xFFFFFFFF;

        String materialId = tag.getString(Tags.PART_MATERIAL);
        for (Material material : TinkerMaterials.materials) {
            if (material.identifier.equals(materialId)) {
                return material.color;
            }
        }

        return 0xFFFFFFFF;
    };
}
