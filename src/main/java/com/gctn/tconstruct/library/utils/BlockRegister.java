package com.gctn.tconstruct.library.utils;

import com.gctn.tconstruct.TinkersConstructNirvana;
import com.gctn.tconstruct.common.block.OreBlocks;
import com.gctn.tconstruct.tables.block.TableBlocks;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Collection;
import java.util.function.Supplier;

public final class BlockRegister {
    public static final DeferredRegister.Blocks BLOCKS =
        DeferredRegister.createBlocks(TinkersConstructNirvana.MODID);

    public static <T extends Block> DeferredBlock<T> registerBlockWithItem(String name, Supplier<T> block) {
        return registerBlockWithItem(name, name, block);
    }

    public static <T extends Block> DeferredBlock<T> registerBlockWithItem(String blockName, String itemName, Supplier<T> block) {
        DeferredBlock<T> registeredBlock = BLOCKS.register(blockName, block);
        ItemRegister.ITEMS.register(itemName, () -> new BlockItem(registeredBlock.get(), new Item.Properties()));
        return registeredBlock;
    }

    public static void register(IEventBus eventBus) {
        entries();
        simpleEntries();
        BLOCKS.register(eventBus);
    }

    public static Collection<DeferredBlock<?>> entries() {
        // Calling each category's entries initializes its static registrations.
        return TableBlocks.entries();
    }

    public static Collection<DeferredBlock<?>> simpleEntries() {
        return OreBlocks.entries();
    }

    private BlockRegister() {
    }
}
