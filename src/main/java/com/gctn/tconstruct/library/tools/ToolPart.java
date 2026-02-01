package com.gctn.tconstruct.library.tools;

import com.gctn.tconstruct.library.TinkerRegistry;
import com.gctn.tconstruct.library.Util;
import com.gctn.tconstruct.library.materials.IMaterialStats;
import com.gctn.tconstruct.library.materials.IToolPart;
import com.gctn.tconstruct.library.materials.Material;
import com.gctn.tconstruct.library.tinkering.MaterialItem;
import com.gctn.tconstruct.library.traits.ITrait;
import com.gctn.tconstruct.utils.TagUtil;
import com.gctn.tconstruct.utils.Tags;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

public class ToolPart extends MaterialItem implements IToolPart {
    // TODO 一堆注释
    protected int cost;

    public ToolPart(int cost) {
        this.cost = cost;
    }


    @Override
    public int getCost() {
        return cost;
    }

    @Override
    public boolean canUseMaterial(Material mat) {
        /*
        for(ToolCore tool : TinkerRegistry.getTools()) {
            for(PartMaterialType pmt : tool.getRequiredComponents()) {
                if(pmt.isValid(this, mat)) {
                    return true;
                }
            }
        }
        */

        return false;
    }


    @SubscribeEvent
    public void onItemTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();

        if (stack.getItem() instanceof ToolPart toolPart) {
            Material material = getMaterial(stack);
            if (material == null) return;

            List<Component> tooltip = event.getToolTip();

            // Material traits/info
            boolean shift = Screen.hasShiftDown();

            if(!checkMissingMaterialTooltip(stack, tooltip)) {
                tooltip.addAll(getTooltipTraitInfo(material));
            }
            // Stats
            if(true/* Config.extraTooltips */) {
                if(!shift) {
                    // info tooltip for detailed and component info
                    tooltip.add(Component.empty());
                    tooltip.add(Component.nullToEmpty(Util.translate("tooltip.tool.holdShift")));
                }
                else {
                    tooltip.addAll(getTooltipStatsInfo(material));
                }
                tooltip.addAll(getAddedByInfo(material));
            }

        }



    }


    public List<Component> getTooltipTraitInfo(Material material) {
        // We build a map with Stat -> Traits mappings that allows us to group or not group depending on what's available
        Map<String, List<ITrait>> mapping = Maps.newConcurrentMap();

        // go through all stats of the material, and check if they have a use, build the map from them
        for(IMaterialStats stat : material.getAllStats()) {
            if(hasUseForStat(stat.getIdentifier())) {
                List<ITrait> traits = material.getAllTraitsForStats(stat.getIdentifier());
                if(!traits.isEmpty()) {
                    boolean unified = false;
                    for(Map.Entry<String, List<ITrait>> entry : mapping.entrySet()) {
                        // group together if identical
                        if(entry.getValue().equals(traits)) {
                            mapping.put(entry.getKey() + ", " + stat.getLocalizedName(), entry.getValue());
                            mapping.remove(entry.getKey());
                            unified = true;
                            break;
                        }
                    }

                    if(!unified) {
                        mapping.put(stat.getLocalizedName(), traits);
                    }
                }
            }
        }

        List<Component> tooltips = Lists.newLinkedList();
        boolean withType = mapping.size() > 1;

        // convert the entries into tooltips
        for(Map.Entry<String, List<ITrait>> entry : mapping.entrySet()) {
            // add the traits in "Stattype: Trait1, Trait2,..." style
            StringBuilder sb = new StringBuilder();
            if(withType) {
                sb.append(ChatFormatting.ITALIC.toString());
                sb.append(entry.getKey());
                sb.append(": ");
                sb.append(ChatFormatting.RESET.toString());
            }
            sb.append(material.getTextColor());
            List<ITrait> traits = entry.getValue();
            if(!traits.isEmpty()) {
                ListIterator<ITrait> iter = traits.listIterator();

                sb.append(iter.next().getLocalizedName());
                while(iter.hasNext()) {
                    sb.append(", ").append(iter.next().getLocalizedName());
                }

                tooltips.add(Component.nullToEmpty(sb.toString()));
            }
        }

        return tooltips;
    }

    public List<Component> getTooltipStatsInfo(Material material) {
        ImmutableList.Builder<Component> builder = ImmutableList.builder();

        for(IMaterialStats stat : material.getAllStats()) {
            if(hasUseForStat(stat.getIdentifier())) {
                List<String> text = stat.getLocalizedInfo();
                if(!text.isEmpty()) {
                    builder.add(Component.empty());
                    // builder.add(ChatFormatting.WHITE.toString() + ChatFormatting.UNDERLINE + stat.getLocalizedName());
                    // builder.addAll(stat.getLocalizedInfo());
                }
            }
        }

        return builder.build();
    }


    public List<Component> getAddedByInfo(Material material) {
        ImmutableList.Builder<Component> builder = ImmutableList.builder();
        /*
        if(TinkerRegistry.getTrace(material) != null) {
            String materialInfo = Component.translatable("tooltip.part.material_added_by",
                    TinkerRegistry.getTrace(material).getName());
            builder.add(Component.empty());
            builder.add(Component.nullToEmpty(materialInfo));
        }
         */
        return builder.build();
    }

    /*
    @Nonnull
    @Override
    public String getItemStackDisplayName(@Nonnull ItemStack stack) {
        Material material = getMaterial(stack);

        String locString = getUnlocalizedName() + "." + material.getIdentifier();

        // custom name?
        if(I18n.canTranslate(locString)) {
            return Util.translate(locString);
        }

        // no, create the default name combo
        return material.getLocalizedItemName(super.getItemStackDisplayName(stack));
    }

    @Nonnull
    @OnlyIn(Dist.CLIENT)
    @Override
    public FontRenderer getFontRenderer(ItemStack stack) {
        return ClientProxy.fontRenderer;
    }

    */
    @Override
    public boolean hasUseForStat(String stat) {
        /*
        for(ToolCore tool : TinkerRegistry.getTools()) {
            for(PartMaterialType pmt : tool.getRequiredComponents()) {
                if(pmt.isValidItem(this) && pmt.usesStat(stat)) {
                    return true;
                }
            }
        }

         */
        return false;
    }

    public boolean checkMissingMaterialTooltip(ItemStack stack, List<Component> tooltip) {
        return checkMissingMaterialTooltip(stack, tooltip, null);
    }

    public boolean checkMissingMaterialTooltip(ItemStack stack, List<Component> tooltip, String statIdentifier) {
        Material material = getMaterial(stack);

        if(material == Material.UNKNOWN) {
            CompoundTag tag = TagUtil.getTagSafe(stack);
            String materialID = tag.getString(Tags.PART_MATERIAL);

            String error;
            if(!materialID.isEmpty()) {
                error = Component.translatable("tooltip.part.missing_material", materialID).toString();
            }
            else {
                error = Component.translatable("tooltip.part.missing_info").toString();
            }
            // tooltip.addAll(LocUtils.getTooltips(error));
            return true;
        }
        else if(statIdentifier != null && material.getStats(statIdentifier) == null) {
            // tooltip.addAll(LocUtils.getTooltips(Util.translateFormatted("tooltip.part.missing_stats", material.getLocalizedName(), statIdentifier)));
            return true;
        }

        return false;
    }

}
