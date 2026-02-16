package com.gctn.tconstruct.library;

import com.gctn.tconstruct.TinkersConstructNirvana;
import com.gctn.tconstruct.library.toolparts.ToolRod;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Collection;
import java.util.function.Supplier;

public class TinkerRegistry {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, TinkersConstructNirvana.MODID);

    public static final Supplier<CreativeModeTab> PARTS_TAB =
            CREATIVE_MODE_TABS.register("tinker_tool_parts", () -> CreativeModeTab.builder()
                    .icon(() -> new ItemStack(Items.STICK))
                    .title(Component.translatable("tinker_tool_parts_tab"))
                    .displayItems(((itemDisplayParameters, output) -> {
                        Collection<ItemStack> tempStacks = ToolRod.getColoredParts();
                        int i=0;
                        for (ItemStack itemStack : tempStacks) {
                            output.accept(itemStack);
                        }
                    }))
                    .build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
