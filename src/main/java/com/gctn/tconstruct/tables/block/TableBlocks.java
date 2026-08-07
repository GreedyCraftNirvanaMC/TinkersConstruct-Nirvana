package com.gctn.tconstruct.tables.block;

import com.gctn.tconstruct.library.utils.BlockRegister;

import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;

import java.util.Collection;
import java.util.List;

public class TableBlocks {
    public static final DeferredBlock<ToolStationBlock> TOOL_STATION =
        BlockRegister.registerBlockWithItem("toolstation", "blockitem/toolstation", () -> new ToolStationBlock(
            BlockBehaviour.Properties.of()
                .strength(1.0F, 5.0F)
                .sound(SoundType.WOOD)
                .noOcclusion()
        ));

    public static Collection<DeferredBlock<?>> entries() {
        return List.<DeferredBlock<?>>of(TOOL_STATION);
    }
}
