package com.gctn.tconstruct.common.tables;

import com.gctn.tconstruct.common.tables.entity.TableEntityRegistries;
import net.neoforged.bus.api.IEventBus;

public class TableRegistryBus {
    public static void register(IEventBus eventBus) {
        TableMenuRegistries.register(eventBus);
        TableEntityRegistries.register(eventBus);
    }
}
