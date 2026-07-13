package com.gctn.tconstruct.library.toolparts;

import com.gctn.tconstruct.library.TinkerMaterials;
import com.gctn.tconstruct.library.materials.Material;
import com.gctn.tconstruct.library.materials.MaterialValue;
import com.gctn.tconstruct.library.utils.Tags;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.neoforged.neoforge.registries.DeferredItem;

import java.util.Collection;

public class Binding extends ToolPart {
    public Binding(int cost) {
        super(cost);
    }

    public static final DeferredItem<Item> BINDING = ITEMS.register("parts/binding",
            () -> new Binding(MaterialValue.VALUE_Ingot));

    public static void bootstrap() {
    }

    public static void getAllColoredParts(Collection<ItemStack> coloredParts) {
        for (Material material : TinkerMaterials.materials) {
            if (material.hasStats(Material.EXTRA) && !material.isHidden()) {
                coloredParts.add(getColoredPart(material));
            }
        }
    }

    public static ItemStack getColoredPart(Material material) {
        ItemStack stack = new ItemStack(BINDING.get());
        CompoundTag tag = new CompoundTag();
        tag.putString(Tags.PART_MATERIAL, material.identifier);
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
        stack.set(DataComponents.ITEM_NAME, Component.translatable(
                "item.tconstruct.material_name",
                Component.translatable("material." + material.identifier + ".name"),
                Component.translatable("item.tconstruct.binding.name")));
        return stack;
    }

}
