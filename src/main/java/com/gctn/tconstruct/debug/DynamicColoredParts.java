package com.gctn.tconstruct.debug;

import com.gctn.tconstruct.TinkersConstructNirvana;
import com.gctn.tconstruct.library.materials.Material;
import com.gctn.tconstruct.library.tools.TinkerMaterials;
import com.gctn.tconstruct.utils.Tags;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DynamicColoredParts {
    public static final DeferredRegister.Items ITEMS =
            DeferredRegister.createItems(TinkersConstructNirvana.MODID);

    public static final DeferredItem<Item> TOOL_ROD = ITEMS.register("tool_rod",
            () -> new Item(new Item.Properties()));

    private static final ItemColor PART_COLOR = (stack, tintIndex) -> {
        CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
        if (customData == null) return 0xFFFFFF; // 无NBT时返回默认白色

        CompoundTag tag = customData.copyTag();
        if (!tag.contains(Tags.PART_MATERIAL)) return 0xFFFFFF;

        String materialId = tag.getString(Tags.PART_MATERIAL);
        for (Material material : TinkerMaterials.materials) {
            if (material.identifier.equals(materialId)) {
                return material.materialTextColor;
            }
        }

        return 0xFFFFFF;
    };

    public static Collection<ItemStack> createColoredParts() {
        Collection<ItemStack> coloredParts = new ArrayList<>();
        List<Material> materials = TinkerMaterials.materials;
        for (Material material : materials) {
            ItemStack stack = new ItemStack(TOOL_ROD.get());
            CompoundTag tag = new CompoundTag();
            tag.putString(Tags.PART_MATERIAL, material.identifier);
            stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
            stack.set(DataComponents.ITEM_NAME, Component.literal(
                    Component.translatable("material."+material.identifier+".name").getString()
                    +" "
                    +Component.translatable("item.tconstruct.tool_rod.name").getString()
            ));
            coloredParts.add(stack);
        }
        return coloredParts;
    }

    private static void registerItemColors(RegisterColorHandlersEvent.Item event) {
        event.register(PART_COLOR, TOOL_ROD.get());
    }

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
        eventBus.addListener(DynamicColoredParts::registerItemColors);
    }
}
