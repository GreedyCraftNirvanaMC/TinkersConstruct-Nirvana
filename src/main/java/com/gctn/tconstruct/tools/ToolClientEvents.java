package com.gctn.tconstruct.tools;

import com.gctn.tconstruct.TinkersConstructNirvana;
import com.gctn.tconstruct.library.toolparts.Binding;
import com.gctn.tconstruct.library.toolparts.PartColor;
import com.gctn.tconstruct.library.toolparts.PickaxeHead;
import com.gctn.tconstruct.library.toolparts.ToolRod;
import com.gctn.tconstruct.tools.toolcolors.PickaxeColor;
import com.gctn.tconstruct.tools.tools.Pickaxe;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;

@EventBusSubscriber(modid = TinkersConstructNirvana.MODID, value = Dist.CLIENT)
public final class ToolClientEvents {
    private ToolClientEvents() {
    }

    @SubscribeEvent
    private static void registerItemColors(RegisterColorHandlersEvent.Item event) {
        event.register(PartColor.PART_COLOR, Binding.BINDING.get(), PickaxeHead.PICKAXEHEAD.get(), ToolRod.TOOL_ROD.get());
        event.register(PickaxeColor.PICKAXE_COLOR, Pickaxe.PICKAXE.get());
    }
}
