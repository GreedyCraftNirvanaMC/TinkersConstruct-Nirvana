package com.gctn.tconstruct.library.stats;

public class HandleMaterialStats extends AbstractMaterialStats {
    public final int durability;
    public final float modifer;
    public HandleMaterialStats(float modifer, int durability) {
        super("Handle");
        this.modifer = modifer;
        this.durability = durability;
    }
}
