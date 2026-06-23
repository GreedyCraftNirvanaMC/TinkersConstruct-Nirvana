package com.gctn.tconstruct;

import com.gctn.tconstruct.tables.TableRegistryBus;
import com.gctn.tconstruct.library.TinkerMaterials;
import com.gctn.tconstruct.library.TinkerRegistry;
import com.gctn.tconstruct.library.utils.BlockRegister;
import com.gctn.tconstruct.library.utils.ItemRegister;
import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(TinkersConstructNirvana.MODID)
public class TinkersConstructNirvana {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "tconstruct";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    public TinkersConstructNirvana(IEventBus modEventBus, ModContainer modContainer) {
        TinkerRegistry.register(modEventBus);
        BlockRegister.register(modEventBus);
        TableRegistryBus.register(modEventBus);
        ItemRegister.register(modEventBus);
        new TinkerMaterials();
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }
    
}
