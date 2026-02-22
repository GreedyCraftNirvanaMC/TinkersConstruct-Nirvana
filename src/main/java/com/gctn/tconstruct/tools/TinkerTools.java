package com.gctn.tconstruct.tools;

import net.minecraft.world.item.Item;

public abstract class TinkerTools extends Item {
    public TinkerTools() {
        super(new Item.Properties().stacksTo(1));
    }
}
