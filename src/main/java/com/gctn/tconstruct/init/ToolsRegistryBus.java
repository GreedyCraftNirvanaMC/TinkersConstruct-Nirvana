package com.gctn.tconstruct.init;

import com.gctn.tconstruct.tools.TinkerTools;
import com.gctn.tconstruct.tools.tools.Pickaxe;
import net.neoforged.bus.api.IEventBus;

public class ToolsRegistryBus {
    public static void register(IEventBus eventBus) {
        Pickaxe.init();
        TinkerTools.register(eventBus);
    }
}
