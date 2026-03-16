package com.gctn.tconstruct.tools;

import com.gctn.tconstruct.TinkersConstructNirvana;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public abstract class TinkerTools extends Item {
    public static final DeferredRegister.Items ITEMS =
            DeferredRegister.createItems(TinkersConstructNirvana.MODID);

    public TinkerTools() {
        super(new Item.Properties().stacksTo(1));
    }

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
