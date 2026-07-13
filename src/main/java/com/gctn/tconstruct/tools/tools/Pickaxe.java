package com.gctn.tconstruct.tools.tools;

import com.gctn.tconstruct.library.TinkerMaterials;
import com.gctn.tconstruct.library.materials.Material;
import com.gctn.tconstruct.library.stats.ExtraMaterialStats;
import com.gctn.tconstruct.library.stats.HandleMaterialStats;
import com.gctn.tconstruct.library.stats.HeadMaterialStats;
import com.gctn.tconstruct.library.utils.HarvestLevels;
import com.gctn.tconstruct.tools.TinkerTools;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.Tool;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;
import net.neoforged.neoforge.registries.DeferredItem;

import java.util.List;

public class Pickaxe extends TinkerTools {
    public static final DeferredItem<Item> PICKAXE = ITEMS.register("pickaxe/pickaxe",
            Pickaxe::new);

    public static void bootstrap() {
    }

    public Pickaxe() {}

    // Material order: handle, head, binding.
    public static ItemStack initPickaxe(Material matHandle, Material matHead, Material matBinding) {
        if (matHandle == null || matHead == null || matBinding == null) {
            return ItemStack.EMPTY;
        }
        HandleMaterialStats handleStats = matHandle.getStats(Material.HANDLE, HandleMaterialStats.class);
        HeadMaterialStats headStats = matHead.getStats(Material.HEAD, HeadMaterialStats.class);
        ExtraMaterialStats bindingStats = matBinding.getStats(Material.EXTRA, ExtraMaterialStats.class);
        if (handleStats == null || headStats == null || bindingStats == null) {
            return ItemStack.EMPTY;
        }

        ItemStack stack = new ItemStack(PICKAXE.get());
        CompoundTag tag = new CompoundTag();
        tag.putString("Handle", matHandle.identifier);
        tag.putString("Head", matHead.identifier);
        tag.putString("Binding", matBinding.identifier);
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
        stack.set(DataComponents.TOOL, getPickaxeInfo(headStats));
        stack.set(DataComponents.ATTRIBUTE_MODIFIERS, getPickaxeAttributes(headStats));
        stack.set(DataComponents.MAX_DAMAGE, getPickaxeDurability(handleStats, headStats, bindingStats));
        stack.set(DataComponents.DAMAGE, 0);
        stack.set(DataComponents.ITEM_NAME, Component.translatable(
                "item.tconstruct.material_name",
                Component.translatable("material." + matHead.identifier + ".name"),
                Component.translatable("item.tconstruct.pickaxe.name")));
        return stack;
    }

    private static int getPickaxeDurability(HandleMaterialStats handleStats, HeadMaterialStats headStats,
                                            ExtraMaterialStats bindingStats) {
        return Math.max(1, Math.round((headStats.durability + bindingStats.durability) * handleStats.modifier)
                + handleStats.durability);
    }

    private static Tool getPickaxeInfo(HeadMaterialStats headStats) {
        return new Tool(List.of(
                Tool.Rule.deniesDrops(HarvestLevels.getIncorrectBlocksForDrops(headStats.harvestLevel)),
                Tool.Rule.minesAndDrops(BlockTags.MINEABLE_WITH_PICKAXE, headStats.miningspeed)
        ), 1.0F, 1);
    }

    private static ItemAttributeModifiers getPickaxeAttributes(HeadMaterialStats headStats) {
        return ItemAttributeModifiers.builder()
                .add(Attributes.ATTACK_DAMAGE,
                        new AttributeModifier(Item.BASE_ATTACK_DAMAGE_ID, headStats.attack,
                                AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.MAINHAND)
                .add(Attributes.ATTACK_SPEED,
                        new AttributeModifier(Item.BASE_ATTACK_SPEED_ID, -2.8,
                                AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.MAINHAND)
                .build();
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        return true;
    }

    @Override
    public boolean canPerformAction(ItemStack stack, ItemAbility itemAbility) {
        return ItemAbilities.DEFAULT_PICKAXE_ACTIONS.contains(itemAbility);
    }

    @Override
    public void postHurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        stack.hurtAndBreak(2, attacker, EquipmentSlot.MAINHAND);
    }

    @Override
    public List<RepairInfo> getRepairInfo(ItemStack stack) {
        Material head = getMaterial(stack, "Head", Material.HEAD);
        if (head == null) {
            return List.of();
        }
        HeadMaterialStats headStats = head.getStats(Material.HEAD, HeadMaterialStats.class);
        return headStats == null ? List.of() : List.of(new RepairInfo(head, 1.0F, headStats.durability));
    }

    private static Material getMaterial(ItemStack stack, String materialKey, String requiredStat) {
        CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
        if (customData == null) {
            return null;
        }

        Material material = TinkerMaterials.getMaterial(customData.getUnsafe().getString(materialKey));
        return material != null && material.hasStats(requiredStat) ? material : null;
    }
}
