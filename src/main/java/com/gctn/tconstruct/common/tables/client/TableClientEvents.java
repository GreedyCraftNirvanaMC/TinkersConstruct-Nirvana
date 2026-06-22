package com.gctn.tconstruct.common.tables.client;

import com.gctn.tconstruct.TinkersConstructNirvana;
import com.gctn.tconstruct.common.tables.TableMenuRegistries;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@EventBusSubscriber(modid = TinkersConstructNirvana.MODID, value = Dist.CLIENT)
public class TableClientEvents {
    @SubscribeEvent
    private static void registerTableScreens(RegisterMenuScreensEvent event) {
        event.register(TableMenuRegistries.TOOL_STATION_MENU.get(), ToolStationScreen::new);
    }
}
