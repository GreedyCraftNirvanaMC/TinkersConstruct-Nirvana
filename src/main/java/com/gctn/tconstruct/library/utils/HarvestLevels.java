package com.gctn.tconstruct.library.utils;

import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.Registries;
import net.minecraft.locale.Language;
import net.minecraft.tags.BlockTags;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.tags.TagKey;

import java.util.HashMap;
import java.util.Map;

import static com.gctn.tconstruct.TinkersConstructNirvana.MODID;

public final class HarvestLevels {

    public static final int STONE = 0;
    public static final int IRON = 1;
    public static final int DIAMOND = 2;
    public static final int OBSIDIAN = 3;
    public static final int COBALT = 4;
    private static final TagKey<Block> INCORRECT_FOR_COBALT_TOOL = TagKey.create(
            Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(MODID, "incorrect_for_cobalt_tool"));

    private HarvestLevels() {
    } // non-instantiable

    private static final Map<Integer, String> HARVEST_LEVEL_NAMES = new HashMap<>();

    public static String getHarvestLevelName(int num) {
        return HARVEST_LEVEL_NAMES.getOrDefault(num, String.valueOf(num));
    }

    public static void init() {
        HARVEST_LEVEL_NAMES.clear();
        HARVEST_LEVEL_NAMES.put(STONE, Util.translate("ui.mininglevel.stone"));
        HARVEST_LEVEL_NAMES.put(IRON, Util.translate("ui.mininglevel.iron"));
        HARVEST_LEVEL_NAMES.put(DIAMOND, ChatFormatting.AQUA + Util.translate("ui.mininglevel.diamond"));
        HARVEST_LEVEL_NAMES.put(OBSIDIAN, Util.translate("ui.mininglevel.obsidian"));
        HARVEST_LEVEL_NAMES.put(COBALT, Util.translate("ui.mininglevel.cobalt"));

        // custom names via resource pack.. deprecated
        addTranslatedLevels("gui.mining");

        // and new
        addTranslatedLevels("ui.mininglevel.");
    }

    private static void addTranslatedLevels(String keyPrefix) {
        Language language = Language.getInstance();
        for (int level = 0; ; level++) {
            String key = keyPrefix + level;
            if (!language.has(key)) {
                return;
            }
            HARVEST_LEVEL_NAMES.put(level, language.getOrDefault(key));
        }
    }

    public static TagKey<Block> getIncorrectBlocksForDrops(int harvestLevel) {
        if (harvestLevel <= HarvestLevels.STONE) {
            return BlockTags.INCORRECT_FOR_WOODEN_TOOL;
        }
        if (harvestLevel == HarvestLevels.IRON) {
            return BlockTags.INCORRECT_FOR_STONE_TOOL;
        }
        if (harvestLevel == HarvestLevels.DIAMOND) {
            return BlockTags.INCORRECT_FOR_IRON_TOOL;
        }
        if (harvestLevel == HarvestLevels.OBSIDIAN) {
            return BlockTags.INCORRECT_FOR_DIAMOND_TOOL;
        }
        return INCORRECT_FOR_COBALT_TOOL;
    }
}
