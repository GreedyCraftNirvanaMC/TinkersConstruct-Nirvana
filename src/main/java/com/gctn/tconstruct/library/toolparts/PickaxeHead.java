package com.gctn.tconstruct.library.toolparts;

import com.gctn.tconstruct.library.TinkerMaterials;
import com.gctn.tconstruct.library.materials.Material;
import com.gctn.tconstruct.utils.Tags;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.registries.DeferredItem;

import java.util.Collection;
import java.util.List;

import static com.gctn.tconstruct.library.toolparts.PartColor.PART_COLOR;

public class PickaxeHead extends ToolPart {
    public PickaxeHead(int cost) {
        super(cost);
    }

    public static final DeferredItem<Item> PICKAXEHEAD = ITEMS.register("parts/pickaxe_head",
            () -> new PickaxeHead(144));

    public static void getAllColoredParts(Collection<ItemStack> coloredParts) {
        List<Material> materials = TinkerMaterials.materials;
        for (Material material : materials) {
            if (material.getStats() == null || !material.getStats().containsKey("Head")) { continue; }
            ItemStack stack = getColoredPart(material);
            coloredParts.add(stack);
        }
    }

    public static ItemStack getColoredPart(Material material) {
        ItemStack stack = new ItemStack(PICKAXEHEAD.get());
        CompoundTag tag = new CompoundTag();
        tag.putString(Tags.PART_MATERIAL, material.identifier);
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
        stack.set(DataComponents.ITEM_NAME, Component.literal(
                        Component.translatable(
                                "material."+material.identifier+".name").getString()
                                +" "
                                +Component.translatable("item.tconstruct.pickaxe_head.name").getString()
                )
        );
        return stack;
    }

    private static void registerItemColors(RegisterColorHandlersEvent.Item event) {
        event.register(PART_COLOR, PICKAXEHEAD.get());
    }

    public static void register(IEventBus eventBus) {
        eventBus.addListener(PickaxeHead::registerItemColors);
    }
}
