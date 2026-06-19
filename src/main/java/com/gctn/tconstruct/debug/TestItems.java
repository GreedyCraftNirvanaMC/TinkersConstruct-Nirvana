package com.gctn.tconstruct.debug;

import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.bus.api.IEventBus;
import com.gctn.tconstruct.TinkersConstructNirvana;

public class TestItems {
    public static final DeferredRegister.Items TEST_ITEMS =
        DeferredRegister.createItems(TinkersConstructNirvana.MODID);
    
    public static void register(IEventBus eventBus) {
        TEST_ITEMS.register(eventBus);
    }
}
