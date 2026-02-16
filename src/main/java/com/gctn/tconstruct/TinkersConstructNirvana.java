package com.gctn.tconstruct;

import com.gctn.tconstruct.library.TinkerMaterials;
import com.gctn.tconstruct.library.TinkerRegistry;
import com.gctn.tconstruct.library.toolparts.ToolRod;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.ModContainer;

import static com.gctn.tconstruct.library.toolparts.PartColor.PART_COLOR;
import static com.gctn.tconstruct.library.toolparts.ToolRod.TOOL_ROD;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(TinkersConstructNirvana.MODID)
public class TinkersConstructNirvana {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "tconstruct";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    public TinkersConstructNirvana(IEventBus modEventBus, ModContainer modContainer) {
        TinkerRegistry.register(modEventBus);
        ToolRod.register(modEventBus);
        new TinkerMaterials();
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }
}
