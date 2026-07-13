package com.gctn.tconstruct.library.utils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import com.gctn.tconstruct.TinkersConstructNirvana;

public final class ItemRegister {
    public static final DeferredRegister.Items ITEMS =
        DeferredRegister.createItems(TinkersConstructNirvana.MODID);

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }

    private ItemRegister() {
    }
}
