package com.gctn.tconstruct.library.toolparts;

import com.gctn.tconstruct.TinkersConstructNirvana;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public abstract class ToolPart extends Item {
    public static final DeferredRegister.Items ITEMS =
            DeferredRegister.createItems(TinkersConstructNirvana.MODID);

    private final int cost;

    public ToolPart(int cost) {
        super(new Item.Properties());
        this.cost = cost;
    }

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }

}
