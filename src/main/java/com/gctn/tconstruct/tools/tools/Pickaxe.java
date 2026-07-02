package com.gctn.tconstruct.tools.tools;

import com.gctn.tconstruct.library.TinkerMaterials;
import com.gctn.tconstruct.library.materials.Material;
import com.gctn.tconstruct.library.stats.ExtraMaterialStats;
import com.gctn.tconstruct.library.stats.HandleMaterialStats;
import com.gctn.tconstruct.library.stats.HeadMaterialStats;
import com.gctn.tconstruct.library.utils.HarvestLevels;
import com.gctn.tconstruct.tools.TinkerTools;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.registries.DeferredItem;

import java.util.ArrayList;
import java.util.List;

import static com.gctn.tconstruct.tools.toolcolors.PickaxeColor.PICKAXE_COLOR;

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

    @Override
    public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity miningEntity) {
        if (!level.isClientSide && state.getDestroySpeed(level, pos) != 0.0F) {
            stack.hurtAndBreak(1, miningEntity, EquipmentSlot.MAINHAND);
        }
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

    private static void registerPickaxeColors(RegisterColorHandlersEvent.Item event) {
        event.register(PICKAXE_COLOR, PICKAXE.get());
    }

    public static void register(IEventBus eventBus) {
        eventBus.addListener(Pickaxe::registerPickaxeColors);
    }
}
