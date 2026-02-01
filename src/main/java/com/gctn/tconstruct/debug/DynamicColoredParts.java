package com.gctn.tconstruct.debug;

import com.gctn.tconstruct.TinkersConstructNirvana;
import com.gctn.tconstruct.library.TinkerRegistry;
import com.gctn.tconstruct.library.Util;
import com.gctn.tconstruct.library.materials.Material;
import com.gctn.tconstruct.library.tinkering.MaterialItem;
import com.gctn.tconstruct.library.tools.TinkerMaterials;
import com.gctn.tconstruct.library.tools.ToolPart;
import com.gctn.tconstruct.utils.TagUtil;
import com.gctn.tconstruct.utils.Tags;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.gctn.tconstruct.library.TinkerRegistry.getMaterial;

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

    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();

        if (!stack.is(TOOL_ROD.get())) {
            return; // 非目标物品，直接退出，不执行后续逻辑
        }
        List<Component> tooltip = event.getToolTip();

        CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
        if (customData == null) return; // 无自定义NBT，返回
        CompoundTag nbtTag = customData.copyTag();
        if (!nbtTag.contains(Tags.PART_MATERIAL)) return; // 无材料标识，返回

        String materialId = nbtTag.getString(Tags.PART_MATERIAL);
        Material material = TinkerMaterials.wood;//TinkerRegistry.getMaterial(materialId);
        if (material == null) return; // 材料不存在，返回

        // 国际化获取材料名称（与createColoredParts中的命名逻辑一致）
        Component materialName = Component.translatable("material." + material.identifier + ".name");
        // 整数颜色（materialTextColor）转TextColor（直接用fromRgb，高效无风险）
        TextColor materialTextColor = TextColor.fromRgb(material.materialTextColor);

        Component coloredMaterialTip = Component.literal("§7材料：") // 灰色前缀，MC通用辅助色
                .append(materialName.copy().withStyle(style -> style.withColor(materialTextColor)));

        tooltip.add(0, coloredMaterialTip);
    }

    public static Material getMaterial(ItemStack stack) {
        CompoundTag tag = TagUtil.getTagSafe(stack);

        return TinkerRegistry.getMaterial(tag.getString(Tags.PART_MATERIAL));
    }

    private static void registerItemColors(RegisterColorHandlersEvent.Item event) {
        event.register(PART_COLOR, TOOL_ROD.get());
    }

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
        eventBus.addListener(DynamicColoredParts::registerItemColors);
    }
}
