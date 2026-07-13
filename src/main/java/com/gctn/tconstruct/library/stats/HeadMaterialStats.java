package com.gctn.tconstruct.library.stats;

import com.gctn.tconstruct.library.materials.Material;

public class HeadMaterialStats extends AbstractMaterialStats {
    public final int durability; // usually between 1 and 1000
    public final int harvestLevel; // see HarvestLevels class
    public final float attack; // usually between 0 and 10 (in 1/2 hearts, so divide by 2 for damage in hearts)
    public final float miningspeed;

    public HeadMaterialStats(int durability, float miningspeed, float attack, int harvestLevel) {
        super(Material.HEAD);
        this.durability = durability;
        this.miningspeed = miningspeed;
        this.attack = attack;
        this.harvestLevel = harvestLevel;
    }
}
