package com.gctn.tconstruct.library.stats;

import com.gctn.tconstruct.library.materials.Material;

public class ExtraMaterialStats extends AbstractMaterialStats {
    public final int durability;
    public ExtraMaterialStats(int durability) {
        super(Material.EXTRA);
        this.durability = durability;
    }
}
