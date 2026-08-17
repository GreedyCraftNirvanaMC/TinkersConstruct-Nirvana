package com.gctn.tconstruct.tools.tools;

import com.gctn.tconstruct.TinkersNirvana;
import com.gctn.tconstruct.library.TinkerMaterials;
import com.gctn.tconstruct.library.materials.Material;
import com.gctn.tconstruct.library.stats.ExtraMaterialStats;
import com.gctn.tconstruct.library.stats.HandleMaterialStats;
import com.gctn.tconstruct.library.stats.HeadMaterialStats;
import com.gctn.tconstruct.library.utils.HarvestLevels;
import com.gctn.tconstruct.library.utils.ToolHelper;
import com.gctn.tconstruct.library.utils.Util;
import com.gctn.tconstruct.tools.TinkerTools;
import com.mojang.datafixers.util.Either;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RenderTooltipEvent;
import net.neoforged.neoforge.registries.DeferredItem;

import java.util.ArrayList;
import java.util.List;

import static com.gctn.tconstruct.tools.toolcolors.PickaxeColor.PICKAXE_COLOR;

@EventBusSubscriber(modid = TinkersNirvana.MODID)
public class Pickaxe extends TinkerTools {
    public static final DeferredItem<Item> PICKAXE = ITEMS.register("pickaxe/pickaxe",
            Pickaxe::new);

    public Pickaxe() {}

    // Material order: handle, head, binding.
    public static ItemStack initPickaxe(Material matHandle, Material matHead, Material matBinding) {
        ItemStack stack = new ItemStack(PICKAXE.get());
        CompoundTag tag = new CompoundTag();
        tag.putString("Handle", matHandle.identifier);
        tag.putString("Head", matHead.identifier);
        tag.putString("Binding", matBinding.identifier);
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));

        HeadMaterialStats headStats = (HeadMaterialStats) matHead.getStats().get("Head");
        if (headStats != null) {
            stack.set(DataComponents.TOOL, getPickaxeInfo(headStats));
            stack.set(DataComponents.ATTRIBUTE_MODIFIERS, getPickaxeAttibute(headStats));
            stack.set(DataComponents.MAX_DAMAGE, getPickaxeDurability(matHandle, matHead, matBinding));
            stack.set(DataComponents.DAMAGE, 0);
        }

        stack.set(DataComponents.ITEM_NAME, Component.literal(
                        Component.translatable(
                                "material." + matHead.identifier + ".name").getString()
                                + " "
                                + Component.translatable("item.tconstruct.pickaxe.name").getString()
                )
        );
        return stack;
    }

    private static int getPickaxeDurability(Material matHandle, Material matHead, Material matBinding) {
        HandleMaterialStats handleStats = (HandleMaterialStats) matHandle.getStats().get("Handle");
        ExtraMaterialStats bindingStats = (ExtraMaterialStats) matBinding.getStats().get("Extra");
        HeadMaterialStats headStats = (HeadMaterialStats) matHead.getStats().get("Head");

        float handleModifier = handleStats != null ? handleStats.modifer : 1.0F;
        int handleDurability = handleStats != null ? handleStats.durability : 0;
        int bindingDurability = bindingStats != null ? bindingStats.durability : 0;

        return Math.max(1, Math.round((headStats.durability + bindingDurability) * handleModifier) + handleDurability);
    }

    private static Tool getPickaxeInfo(HeadMaterialStats headStats) {
        return new Tool(List.of(
                Tool.Rule.deniesDrops(HarvestLevels.getIncorrectBlocksForDrops(headStats.harvestLevel)),
                Tool.Rule.minesAndDrops(BlockTags.MINEABLE_WITH_PICKAXE, headStats.miningspeed)
        ), 1.0F, 1);
    }

    private static ItemAttributeModifiers getPickaxeAttibute(HeadMaterialStats headStats) {
        double attackSpeed = 1.2;
        return ItemAttributeModifiers.builder()
                .add(
                        Attributes.ATTACK_DAMAGE,
                        new AttributeModifier(
                                Item.BASE_ATTACK_DAMAGE_ID,
                                headStats.attack,
                                AttributeModifier.Operation.ADD_VALUE
                        ),
                        EquipmentSlotGroup.MAINHAND
                )
                .add(
                        Attributes.ATTACK_SPEED,
                        new AttributeModifier(
                                Item.BASE_ATTACK_SPEED_ID,
                                attackSpeed - 4.0,
                                AttributeModifier.Operation.ADD_VALUE
                        ),
                        EquipmentSlotGroup.MAINHAND
                )
                .build();
    }

    @Override
    public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity miningEntity) {
        if (!level.isClientSide && state.getDestroySpeed(level, pos) != 0.0F) {
            stack.hurtAndBreak(1, miningEntity, EquipmentSlot.MAINHAND);
        }
        return true;
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        return true;
    }

    @Override
    public void postHurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        stack.hurtAndBreak(2, attacker, EquipmentSlot.MAINHAND);
    }

    @Override
    public List<RepairInfo> getRepairInfo(ItemStack stack) {
        List<RepairInfo> repairInfos = new ArrayList<>();
        Material head = getMaterial(stack, "Head", "Head");
        Material handle = getMaterial(stack, "Handle", "Handle");
        if (head == null) {
            return List.of();
        }
        int headDurability = ((HeadMaterialStats) head.getStats().get("Head")).durability;
        int handleDurability = ((HandleMaterialStats) handle.getStats().get("Handle")).durability;
        repairInfos.add(new RepairInfo(head, 1.0F, headDurability));
        repairInfos.add(new RepairInfo(handle, 0.8F, handleDurability));

        return repairInfos;
    }

    private static Material getMaterial(ItemStack stack, String materialKey, String requiredStat) {
        CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
        if (customData == null) {
            return null;
        }

        String materialId = customData.copyTag().getString(materialKey);
        for (Material material : TinkerMaterials.materials) {
            if (material.identifier.equals(materialId)
                    && material.getStats() != null
                    && material.getStats().containsKey(requiredStat)) {
                return material;
            }
        }
        return null;
    }

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
        Component headTraitsTip = Component.literal("词条").withColor(headMaterial.color);
        Component handleTraitsTip = Component.literal("词条").withColor(handleMaterial.color);
        Component bindingTraitsTip = Component.literal("词条").withColor(bindingMaterial.color);

        Component shiftTip = Component.translatable(
                "tooltip.tconstruct.hold_for_stats",
                Component.literal("Shift").withColor(0xFFFFFF55)
        ).withColor(0xFFAAAAAA);
        Component controlTip = Component.translatable(
                "tooltip.tconstruct.hold_for_more",
                Component.literal("Ctrl").withColor(0xFF55FFFF)
        ).withColor(0xFFAAAAAA);

        List<Either<FormattedText, TooltipComponent>> upperTips = new ArrayList<>();
        upperTips.add(Either.left(headTraitsTip));
        upperTips.add(Either.left(handleTraitsTip));
        upperTips.add(Either.left(bindingTraitsTip));
        upperTips.add(Either.left(Component.empty()));

        int maxDurability = stack.getMaxDamage();
        int remainingDurability = Math.max(0, maxDurability - stack.getDamageValue());
        Component durabilityTip = Component.translatable(
                "tooltip.tconstruct.tool.durability",
                Component.literal(String.valueOf(remainingDurability)).withColor(ToolHelper.getDurabilityColor(stack)),
                Component.literal(String.valueOf(maxDurability)).withColor(0xFF47CC47)
        ).withColor(0xFFAAAAAA);

        if (Screen.hasShiftDown()) {
            // TODO 按下Shift时显示数据
            upperTips.add(Either.left(durabilityTip));
        } else if (Screen.hasControlDown()) {
            // TODO 按下Ctrl时显示详细信息
        } else if (Screen.hasAltDown()) {
            // TODO 按下Alt时显示词条简介
        } else {
            upperTips.add(Either.left(shiftTip));
            upperTips.add(Either.left(controlTip));
        }
        if (ToolHelper.hasCreativeTabTip(tooltip, stack)) {
            upperTips.add(Either.left(Component.empty()));
        }
        tooltip.addAll(1, upperTips);
    }

    private static void registerPickaxeColors(RegisterColorHandlersEvent.Item event) {
        event.register(PICKAXE_COLOR, PICKAXE.get());
    }

    public static void register(IEventBus eventBus) {
        eventBus.addListener(Pickaxe::registerPickaxeColors);
    }
}
