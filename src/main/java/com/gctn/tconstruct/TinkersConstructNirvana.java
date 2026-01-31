package com.gctn.tconstruct;

import com.gctn.tconstruct.debug.DynamicColoredParts;
import com.gctn.tconstruct.library.TinkerRegistry;
import com.gctn.tconstruct.library.tools.TinkerMaterials;
import net.neoforged.neoforge.common.NeoForge;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.ModContainer;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(TinkersConstructNirvana.MODID)
public class TinkersConstructNirvana {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "tconstruct";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    public TinkersConstructNirvana(IEventBus modEventBus, ModContainer modContainer) {
        // Register ourselves for server and other game events we are interested in.
        // Note that this is necessary if and only if we want *this* class (TinkersConstructNirvana) to respond directly to events.
        // Do not add this line if there are no @SubscribeEvent-annotated functions in this class, like onServerStarting() below.
        // NeoForge.EVENT_BUS.register(this);
        TinkerRegistry.register(modEventBus);
        DynamicColoredParts.register(modEventBus);
        new TinkerMaterials();
    }
}
