package com.gctn.tconstruct.common.block;

import com.gctn.tconstruct.utils.BlockRegister;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;

import java.util.Collection;
import java.util.List;

public class StationBlocks {
    public static final DeferredBlock<Block> TOOL_STATION =
        BlockRegister.registerBlockWithItem("toolstation", "blockitem/toolstation", () -> new Block(
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
