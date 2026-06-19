package com.gctn.tconstruct.debug;

import com.gctn.tconstruct.TinkersConstructNirvana;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class TestTable {
    public static final DeferredRegister.Blocks TEST_TABLES =
        DeferredRegister.createBlocks(TinkersConstructNirvana.MODID);

    public static final DeferredBlock<Block> TEST_TABLE =
        registerBlocks("toolstation", () -> new Block(BlockBehaviour.Properties.of()
                    .destroyTime(1.5F)
                    .explosionResistance(1.0F)
                    .strength(1.0F)
                    .noOcclusion()
            ));

    private static <T extends Block> void registerBlockItems(String name, DeferredBlock<T> block) {
        TestItems.TEST_ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }
    
    private static <T extends Block> DeferredBlock<T> registerBlocks(String name, Supplier<T> block) {
        DeferredBlock<T> blocks = TEST_TABLES.register(name, block);
        registerBlockItems(name, blocks);
        return blocks;
    }

    public static void register(IEventBus eventBus) { TEST_TABLES.register(eventBus); }
}
