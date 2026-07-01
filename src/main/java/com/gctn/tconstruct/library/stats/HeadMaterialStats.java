package com.gctn.tconstruct.library.stats;

public class HeadMaterialStats extends AbstractMaterialStats {
    public final int durability; // usually between 1 and 1000
    public final int harvestLevel; // see HarvestLevels class
    public final float attack; // usually between 0 and 10 (in 1/2 hearts, so divide by 2 for damage in hearts)
    public final float miningspeed;

    public HeadMaterialStats(int durability, int harvestLevel, float attack,  float miningspeed) {
        super("Head");
        this.durability = durability;
        this.harvestLevel = harvestLevel;
        this.attack= attack;
        this.miningspeed = miningspeed;
    }

    public int getDurability() {
        return durability;
    }
}
