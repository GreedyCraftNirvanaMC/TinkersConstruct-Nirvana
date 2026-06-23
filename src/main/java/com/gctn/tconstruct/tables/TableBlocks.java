package com.gctn.tconstruct.tables;

import com.gctn.tconstruct.utils.BlockRegister;

import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;

import java.util.Collection;
import java.util.List;

public class TableBlocks {
    public static final DeferredBlock<ToolStationBlock> TOOL_STATION =
        BlockRegister.registerBlockWithItem("toolstation", "blockitem/toolstation", () -> new ToolStationBlock(
            BlockBehaviour.Properties.of()
                .destroyTime(1.5F)
                .explosionResistance(1.0F)
                .strength(1.0F)
                .noOcclusion()
        ));

    public static Collection<DeferredBlock<?>> entries() {
        return List.<DeferredBlock<?>>of(TOOL_STATION);
    }
}
