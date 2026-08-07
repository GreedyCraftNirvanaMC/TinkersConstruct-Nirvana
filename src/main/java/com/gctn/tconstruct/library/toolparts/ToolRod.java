package com.gctn.tconstruct.library.toolparts;

import com.mojang.datafixers.util.Either;
import com.gctn.tconstruct.TinkersNirvana;
import com.gctn.tconstruct.library.TinkerMaterials;
import com.gctn.tconstruct.library.materials.Material;
import com.gctn.tconstruct.library.materials.MaterialValue;
import com.gctn.tconstruct.library.stats.HandleMaterialStats;
import com.gctn.tconstruct.library.utils.Tags;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RenderTooltipEvent;
import net.neoforged.neoforge.registries.DeferredItem;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.gctn.tconstruct.library.toolparts.PartColor.PART_COLOR;

@EventBusSubscriber(modid = TinkersNirvana.MODID)
public class ToolRod extends ToolPart {
    private final String materialTag = "";

    public ToolRod(int cost) {
        super(cost);
    }

    public static final DeferredItem<Item> TOOL_ROD = ITEMS.register("parts/tool_rod",
            () -> new ToolRod(MaterialValue.VALUE_Ingot));

    public static void getAllParts(Collection<ItemStack> coloredParts) {
        List<Material> materials = TinkerMaterials.materials;
        for (Material material : materials) {
            if (material.getStats() == null || !material.getStats().containsKey("Handle")) { continue; }
            ItemStack stack = getPart(material);
            coloredParts.add(stack);
        }
    }

    public static ItemStack getPart(Material material) {
        ItemStack stack = new ItemStack(TOOL_ROD.get());
        CompoundTag tag = new CompoundTag();
        tag.putString(Tags.PART_MATERIAL, material.identifier);
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
        stack.set(DataComponents.ITEM_NAME, Component.literal(
                        Component.translatable("material."+material.identifier+".name").getString()
                        + " "
                        + Component.translatable("item.tconstruct.tool_rod.name").getString()
                )
        );
        return stack;
    }

    @SubscribeEvent
    public static void onGatherTooltipComponents(RenderTooltipEvent.GatherComponents event) {
        ItemStack stack = event.getItemStack();
        if (!stack.is(TOOL_ROD.get())) { return; }
        List<Either<FormattedText, TooltipComponent>> tooltip = event.getTooltipElements();

        CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
        if (customData == null) { return; }
        CompoundTag nbtTag = customData.copyTag();
        if (!nbtTag.contains(Tags.PART_MATERIAL)) { return; }

        String materialId = nbtTag.getString(Tags.PART_MATERIAL);
        Material material = TinkerMaterials.getMaterial(materialId);
        if (material == null) { return; }
        HandleMaterialStats handleStats = material.getStats("Handle", HandleMaterialStats.class);
        if (handleStats == null) { return; }
        // TODO 用获取词条本地化代替测试文案
        Component traitsTip = Component.literal("词条").withColor(material.color);

        Component shiftTip = Component.translatable(
                "tooltip.tconstruct.hold_for_stats",
                Component.literal("Shift").withColor(0xFFFFFF55)
        ).withColor(0xFFAAAAAA);
        Component handleTip = Component.translatable("tooltip.tconstruct.handle")
                .withColor(0xFFFFFFFF)
                .withStyle(ChatFormatting.UNDERLINE);
        Component modifierTip = Component.translatable(
                "tooltip.tconstruct.handle.modifier",
                Component.literal(String.valueOf(handleStats.modifer)).withColor(0xFFB9B95A)
        ).withColor(0xFFAAAAAA);
        Component durabilityTip = Component.translatable(
                "tooltip.tconstruct.uni.durability",
                Component.literal(String.valueOf(handleStats.durability)).withColor(0xFF47CC47)
        ).withColor(0xFFAAAAAA);

        List<Either<FormattedText, TooltipComponent>> addedTips = new ArrayList<>(List.of(
                Either.left(traitsTip),
                Either.left(Component.empty())
        ));
        if (Screen.hasShiftDown()) {
            addedTips.add(Either.left(handleTip));
            addedTips.add(Either.left(modifierTip));
            addedTips.add(Either.left(durabilityTip));
        } else if (Screen.hasControlDown()) {
            // TODO 按下Ctrl时显示词条简介
        } else {
            addedTips.add(Either.left(shiftTip));
        }
        if (hasCreativeTabTip(tooltip, stack)) {
            addedTips.add(Either.left(Component.empty()));
        }
        tooltip.addAll(1, addedTips);
    }

    private static boolean hasCreativeTabTip(
            List<Either<FormattedText, TooltipComponent>> tooltip, ItemStack stack
    ) {
        return CreativeModeTabs.tabs().stream()
                .filter(tab -> !tab.hasSearchBar() && tab.contains(stack))
                .map(tab -> tab.getDisplayName().getString())
                .anyMatch(tabName -> tooltip.stream().anyMatch(element -> element.left()
                        .map(text -> text.getString().equals(tabName))
                        .orElse(false)));
    }

    private static void registerItemColors(RegisterColorHandlersEvent.Item event) {
        event.register(PART_COLOR, TOOL_ROD.get());
    }

    public static void register(IEventBus eventBus) {
        eventBus.addListener(ToolRod::registerItemColors);
    }
}
