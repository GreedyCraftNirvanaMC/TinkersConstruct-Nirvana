package com.gctn.tconstruct.init;

import com.gctn.tconstruct.tables.block.entity.TableEntityRegistries;
import com.gctn.tconstruct.tables.menu.TableMenuRegistries;
import net.neoforged.bus.api.IEventBus;

public class TableRegistryBus {
    public static void register(IEventBus eventBus) {
        TableMenuRegistries.register(eventBus);
        TableEntityRegistries.register(eventBus);
    }
}
