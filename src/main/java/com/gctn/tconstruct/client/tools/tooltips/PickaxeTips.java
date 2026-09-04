package com.gctn.tconstruct.client.tools.tooltips;

import com.gctn.tconstruct.TinkersNirvana;
import com.gctn.tconstruct.library.TinkerMaterials;
import com.gctn.tconstruct.library.materials.Material;
import com.gctn.tconstruct.library.stats.HeadMaterialStats;
import com.gctn.tconstruct.library.utils.HarvestLevels;
import com.gctn.tconstruct.library.utils.ToolHelper;
import com.gctn.tconstruct.tools.TinkerTools;
import com.mojang.datafixers.util.Either;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderTooltipEvent;

import java.util.ArrayList;
import java.util.List;

import static com.gctn.tconstruct.tools.tools.Pickaxe.PICKAXE;

@EventBusSubscriber(modid = TinkersNirvana.MODID, value = Dist.CLIENT)
public class PickaxeTips {
    @SubscribeEvent
    public static void onGatherTooltipComponents(RenderTooltipEvent.GatherComponents event) {
        ItemStack stack = event.getItemStack();
        if (!stack.is(PICKAXE.get())) { return; }
        List<Either<FormattedText, TooltipComponent>> tooltip = event.getTooltipElements();

        CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
        if (customData == null) { return; }
        CompoundTag nbtTag = customData.copyTag();

        // TODO 用获取词条本地化代替测试文案，同时计算词条等级
        String headMaterialId = nbtTag.getString("Head");
        Material headMaterial = TinkerMaterials.getMaterial(headMaterialId);
        String handleMaterialId = nbtTag.getString("Handle");
        Material handleMaterial = TinkerMaterials.getMaterial(handleMaterialId);
        String bindingMaterialId = nbtTag.getString("Binding");
        Material bindingMaterial = TinkerMaterials.getMaterial(bindingMaterialId);

        HeadMaterialStats headStats = headMaterial.getStats("Head", HeadMaterialStats.class);

        Component headTraitsTip = Component.literal("词条").withColor(headMaterial.color);
        Component handleTraitsTip = Component.literal("词条").withColor(handleMaterial.color);
        Component bindingTraitsTip = Component.literal("词条").withColor(bindingMaterial.color);

        List<Either<FormattedText, TooltipComponent>> upperTips = new ArrayList<>();
        upperTips.add(Either.left(headTraitsTip));
        upperTips.add(Either.left(handleTraitsTip));
        upperTips.add(Either.left(bindingTraitsTip));
        upperTips.add(Either.left(Component.empty()));

        if (Screen.hasShiftDown()) {
            Component durabilityTip = TinkerTools.getDurabilityTip(stack);
            Component miningLevelTip = Component.translatable("tooltip.tconstruct.tool.mininglevel",
                    HarvestLevels.getHarvestLevelName(headStats.harvestLevel)
            ).withColor(0xFFAAAAAA);
            Component miningSpeedTip = Component.translatable("tooltip.tconstruct.tool.miningspeed",
                    Component.literal(String.valueOf(headStats.miningspeed)).withColor(0xFF78A0CD)
            ).withColor(0xFFAAAAAA);
            Component attackTip = Component.translatable("tooltip.tconstruct.tool.attack",
                    Component.literal(String.valueOf(headStats.attack)).withColor(0xFFD76464)
            ).withColor(0xFFAAAAAA);
            Component modifiersTip = Component.translatable("tooltip.tconstruct.tool.modifiers",
                    ((TinkerTools) stack.getItem()).getAvailableModifierSlots(stack)
            ).withColor(0xFFAAAAAA);
            upperTips.add(Either.left(durabilityTip));
            upperTips.add(Either.left(miningLevelTip));
            upperTips.add(Either.left(miningSpeedTip));
            upperTips.add(Either.left(attackTip));
            upperTips.add(Either.left(modifiersTip));
        } else if (Screen.hasControlDown()) {
            // TODO 按下Ctrl时显示详细信息
        } else if (Screen.hasAltDown()) {
            // TODO 按下Alt时显示词条简介
        } else {
            upperTips.add(Either.left(TinkerTools.shiftTip));
            upperTips.add(Either.left(TinkerTools.controlTip));
        }
        if (ToolHelper.hasCreativeTabTip(tooltip, stack)) {
            upperTips.add(Either.left(Component.empty()));
        }
        tooltip.addAll(1, upperTips);
    }
}
