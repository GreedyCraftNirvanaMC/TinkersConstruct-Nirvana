package com.gctn.tconstruct.library.tinkering;

import com.gctn.tconstruct.library.TinkerRegistry;
import com.gctn.tconstruct.library.materials.Material;
import com.gctn.tconstruct.utils.TagUtil;
import com.gctn.tconstruct.utils.Tags;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

/**
 * Represents an item that has a Material associated with it. The metadata of an itemstack identifies which material the
 * itemstack of this item has.
 */
public class MaterialItem extends Item implements IMaterialItem {
    public MaterialItem() {
        super(new Item.Properties());
    }

    @Override
    public String getMaterialID(ItemStack stack) {
        return getMaterial(stack).identifier;
    }

    @Override
    public Material getMaterial(ItemStack stack) {
        CompoundTag tag = TagUtil.getTagSafe(stack);

        return TinkerRegistry.getMaterial(tag.getString(Tags.PART_MATERIAL));
    }


    @Override
    public ItemStack getItemstackWithMaterial(Material material) {
        ItemStack stack = new ItemStack(this);
        CompoundTag tag = new CompoundTag();
        tag.putString(Tags.PART_MATERIAL, material.identifier);
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));

        return stack;
    }

}
