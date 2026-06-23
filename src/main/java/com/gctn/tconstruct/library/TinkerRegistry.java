package com.gctn.tconstruct.library;

import com.gctn.tconstruct.TinkersConstructNirvana;
import com.gctn.tconstruct.library.toolparts.Binding;
import com.gctn.tconstruct.library.toolparts.PickaxeHead;
import com.gctn.tconstruct.library.toolparts.ToolPart;
import com.gctn.tconstruct.library.toolparts.ToolRod;
import com.gctn.tconstruct.tools.TinkerTools;
import com.gctn.tconstruct.tools.tools.Pickaxe;
import com.gctn.tconstruct.library.utils.BlockRegister;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Supplier;

import static com.gctn.tconstruct.library.TinkerMaterials.*;

public class TinkerRegistry {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, TinkersConstructNirvana.MODID);

    // 部件
    public static final Supplier<CreativeModeTab> PARTS_TAB =
            CREATIVE_MODE_TABS.register("tinker_tool_parts", () -> CreativeModeTab.builder()
                    .icon(() -> new ItemStack(Items.STICK))
                    .title(Component.translatable("tinker_tool_parts_tab"))
                    .displayItems(((itemDisplayParameters, output) -> {
                        Collection<ItemStack> tempStacks = new ArrayList<>();
                        ToolRod.getAllColoredParts(tempStacks);
                        Binding.getAllColoredParts(tempStacks);
                        PickaxeHead.getAllColoredParts(tempStacks);
                        int i = 0;
                        for (ItemStack itemStack : tempStacks) {
                            output.accept(itemStack);
                        }
                    }))
                    .build());

    // 工具
    public static final Supplier<CreativeModeTab> TOOLS_TAB =
            CREATIVE_MODE_TABS.register("tinker_tools", () -> CreativeModeTab.builder()
                    .icon(() -> new ItemStack(Items.WOODEN_PICKAXE))
                    .title(Component.translatable("tinker_tools_tab"))
                    .displayItems(((itemDisplayParameters, output) -> {
                        output.accept(Pickaxe.initPickaxe(wood, stone, unknown));
                    }))
                    .build());

    // 方块
    public static final Supplier<CreativeModeTab> BLOCKS_TAB =
            CREATIVE_MODE_TABS.register("tinker_blocks", () -> CreativeModeTab.builder()
                    .icon(() -> new ItemStack(Items.COBBLESTONE))
                    .title(Component.translatable("tinker_blocks_tab"))
                    .displayItems(((itemDisplayParameters, output) -> {
                        for (DeferredBlock<?> block : BlockRegister.entries()) {
                            output.accept(block.get());
                        }
                    }))
                    .build());

    public static void register(IEventBus eventBus) {
        // Creative mode tabs
        CREATIVE_MODE_TABS.register(eventBus);

        // Toolparts
        ToolPart.register(eventBus);
        Binding.register(eventBus);
        PickaxeHead.register(eventBus);
        ToolRod.register(eventBus);

        // Tools
        TinkerTools.register(eventBus);
        Pickaxe.register(eventBus);
    }
}
