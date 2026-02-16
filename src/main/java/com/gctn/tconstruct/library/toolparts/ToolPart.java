package com.gctn.tconstruct.library.toolparts;

import net.minecraft.world.item.Item;

public abstract class ToolPart extends Item {
    private final int cost;

    public ToolPart(int cost) {
        super(new Item.Properties());
        this.cost = cost;
    }

}
