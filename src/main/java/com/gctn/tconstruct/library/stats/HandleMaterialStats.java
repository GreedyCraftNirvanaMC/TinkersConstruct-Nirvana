package com.gctn.tconstruct.library.stats;

import com.gctn.tconstruct.library.materials.Material;

public class HandleMaterialStats extends AbstractMaterialStats {
    public final int durability;
    public final float modifier;

    public HandleMaterialStats(float modifier, int durability) {
        super(Material.HANDLE);
        this.modifier = modifier;
        this.durability = durability;
    }
}
