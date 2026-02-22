package com.gctn.tconstruct.tools.toolcolors;

import com.gctn.tconstruct.library.TinkerMaterials;
import com.gctn.tconstruct.library.materials.Material;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.component.CustomData;

public class PickaxeColor {
    public static final ItemColor PICKAXE_COLOR = (stack, tintIndex) -> {
        CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
        if (customData == null) return 0xFFFFFFFF; // 无NBT时返回默认白色

        CompoundTag tag = customData.copyTag();
        if (!tag.contains("Handle") || !tag.contains("Head") || !tag.contains("Binding")) return 0xFFFFFFFF;

        return switch (tintIndex) {
            case 0 -> // layer0 → Handle（手柄）
                    getMaterialColor(tag.getString("Handle"));
            case 1 -> // layer1 → Head（头部）
                    getMaterialColor(tag.getString("Head"));
            case 2 -> // layer2 → Binding（绑定）
                    getMaterialColor(tag.getString("Binding"));
            default -> // 异常索引，返回默认色
                    0xFFFFFFFF;
        };
    };

    /**
     * 方法：根据材质ID获取颜色
     * @param materialId 材质标识符（如"iron"、"diamond"）
     * @return 材质对应的颜色，匹配不到返回默认白色
     */
    private static int getMaterialColor(String materialId) {
        for (Material material : TinkerMaterials.materials) {
            if (material.identifier.equals(materialId)) {
                return material.color;
            }
        }
        // 材质ID匹配不到时返回默认色
        return 0xFFFFFFFF;
    }
}
