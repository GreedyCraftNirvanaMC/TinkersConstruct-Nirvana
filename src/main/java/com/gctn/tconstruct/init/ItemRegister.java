package com.gctn.tconstruct.init;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import com.gctn.tconstruct.TinkersNirvana;

public class ItemRegister {
    public static final DeferredRegister.Items ITEMS =
        DeferredRegister.createItems(TinkersNirvana.MODID);

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
