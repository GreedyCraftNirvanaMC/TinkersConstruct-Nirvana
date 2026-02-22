package com.gctn.tconstruct.library.stats;

public class ExtraMaterialStats extends AbstractMaterialStats {
    public final int durability;
    public ExtraMaterialStats(int durability) {
        super("Extra");
        this.durability = durability;
    }
}
