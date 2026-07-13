package com.gctn.tconstruct.tables.client;

import com.gctn.tconstruct.TinkersConstructNirvana;
import com.gctn.tconstruct.tables.menu.TableMenuRegistries;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@EventBusSubscriber(modid = TinkersConstructNirvana.MODID, value = Dist.CLIENT)
public final class TableClientEvents {
    @SubscribeEvent
    private static void registerTableScreens(RegisterMenuScreensEvent event) {
        event.register(TableMenuRegistries.TOOL_STATION_MENU.get(), ToolStationScreen::new);
    }

    private TableClientEvents() {
    }
}
