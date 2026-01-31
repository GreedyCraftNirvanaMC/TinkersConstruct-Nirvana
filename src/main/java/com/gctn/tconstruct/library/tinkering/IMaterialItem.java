package com.gctn.tconstruct.library.tinkering;

import com.gctn.tconstruct.library.materials.Material;
import net.minecraft.world.item.ItemStack;

/**
 * Items implementing this interface contain a material
 */
public interface IMaterialItem {

    /**
     * Returns the material identifier of the material of the part this itemstack holds.
     *
     * @return Identifier of a material or "Unknown", null or empty if invalid.
     */
    String getMaterialID(ItemStack stack);

    /**
     * Returns the material of the part this itemstack holds.
     *
     * @return Material or Material.UNKNOWN if invalid
     */
    Material getMaterial(ItemStack stack);

    /**
     * Returns the item with the given material
     */
    ItemStack getItemstackWithMaterial(Material material);
}
