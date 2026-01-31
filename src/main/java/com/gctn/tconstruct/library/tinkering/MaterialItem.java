package com.gctn.tconstruct.library.tinkering;

import com.gctn.tconstruct.library.TinkerRegistry;
import com.gctn.tconstruct.library.materials.Material;
import com.gctn.tconstruct.utils.Tags;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/**
 * Represents an item that has a Material associated with it. The metadata of an itemstack identifies which material the
 * itemstack of this item has.
 */
public class MaterialItem extends Item /* implements IMaterialItem */ {
    // TODO 一堆报错

    public MaterialItem() {
        super(new Item.Properties());
        //this.setHasSubtypes(true);
    }
    /*
    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> subItems) {
        if(this.isInCreativeTab(tab)) {
            // this adds a variant of each material to the creative menu
            for(Material mat : TinkerRegistry.getAllMaterials()) {
                subItems.add(getItemstackWithMaterial(mat));
                if(!Config.listAllPartMaterials) {
                    break;
                }
            }
        }
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
        tag.setString(Tags.PART_MATERIAL, material.identifier);
        stack.setTagCompound(tag);

        return stack;
    }

     */

}
