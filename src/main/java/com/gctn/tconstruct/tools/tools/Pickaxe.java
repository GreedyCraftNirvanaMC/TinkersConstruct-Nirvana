package com.gctn.tconstruct.tools.tools;

import com.gctn.tconstruct.library.materials.Material;
import com.gctn.tconstruct.library.stats.HeadMaterialStats;
import com.gctn.tconstruct.tools.TinkerTools;
import com.gctn.tconstruct.utils.HarvestLevels;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.Tool;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.registries.DeferredItem;

import java.util.List;

import static com.gctn.tconstruct.tools.toolcolors.PickaxeColor.PICKAXE_COLOR;


public class Pickaxe extends TinkerTools {
    public static final DeferredItem<Item> PICKAXE = ITEMS.register("pickaxe/pickaxe",
            () -> new Pickaxe());

    public Pickaxe() {}

    // 材料顺序 手柄-镐头-绑定结
    public static ItemStack getColoredPickaxe(Material matHandle, Material matHead, Material matBinding) {
        ItemStack stack = new ItemStack(PICKAXE.get());
        CompoundTag tag = new CompoundTag();
        tag.putString("Handle", matHandle.identifier);
        tag.putString("Head", matHead.identifier);
        tag.putString("Binding", matBinding.identifier);
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
        HeadMaterialStats headStats = (HeadMaterialStats) matHead.getStats().get("Head");
        if (headStats != null) {
            stack.set(DataComponents.TOOL, getPickaxeInfo(headStats));
        }
        stack.set(DataComponents.ITEM_NAME, Component.literal(
                        Component.translatable(
                                "material."+matHead.identifier+".name").getString()
                                +" "
                                +Component.translatable("item.tconstruct.pickaxe.name").getString()
                )
        );
        return stack;
    }

    private static Tool getPickaxeInfo(HeadMaterialStats headStats) {
        return new Tool(List.of(
                Tool.Rule.deniesDrops(HarvestLevels.getIncorrectBlocksForDrops(headStats.harvestLevel)),
                Tool.Rule.minesAndDrops(BlockTags.MINEABLE_WITH_PICKAXE, headStats.miningspeed)
        ), 1.0F, 1);
    }

    private static void registerPickaxeColors(RegisterColorHandlersEvent.Item event) {
        event.register(PICKAXE_COLOR, PICKAXE.get());
    }

    public static void register(IEventBus eventBus) {
        eventBus.addListener(Pickaxe::registerPickaxeColors);
    }
}
