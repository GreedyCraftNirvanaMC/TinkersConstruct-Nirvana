package com.gctn.tconstruct.library;

import com.gctn.tconstruct.library.materials.Material;
import com.gctn.tconstruct.library.stats.ExtraMaterialStats;
import com.gctn.tconstruct.library.stats.HandleMaterialStats;
import com.gctn.tconstruct.library.stats.HeadMaterialStats;
import com.gctn.tconstruct.library.utils.HarvestLevels;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class TinkerMaterials {
    private static final List<Material> MUTABLE_MATERIALS = new ArrayList<>();
    private static final Map<String, Material> MATERIALS_BY_ID = new LinkedHashMap<>();
    public static final List<Material> materials = Collections.unmodifiableList(MUTABLE_MATERIALS);

    private TinkerMaterials() {
    }

    // natural resources/blocks
    public static final Material wood       = mat("wood", 0xff8e661b);
    public static final Material stone      = mat("stone", 0xff999999);
    public static final Material flint      = mat("flint", 0xff696969);
    public static final Material cactus     = mat("cactus", 0xff00a10f);
    public static final Material bone       = mat("bone", 0xffede6bf);
    public static final Material obsidian   = mat("obsidian", 0xff601cc4);
    public static final Material prismarine = mat("prismarine", 0xff7edebc);
    public static final Material endstone   = mat("endstone", 0xffe0d890);
    public static final Material paper      = mat("paper", 0xffffffff);
    public static final Material sponge     = mat("sponge", 0xffcacc4e);
    public static final Material firewood   = mat("firewood", 0xffcc5300);

    // Slime
    public static final Material knightslime= mat("knightslime", 0xfff18ff0);
    public static final Material slime      = mat("slime", 0xff82c873);
    public static final Material blueslime  = mat("blueslime", 0xff74c8c7);
    public static final Material magmaslime = mat("magmaslime", 0xffff960d);

    // Metals
    public static final Material iron       = mat("iron", 0xffcacaca);
    public static final Material pigiron    = mat("pigiron", 0xffef9e9b);

    // Nether Materials
    public static final Material netherrack = mat("netherrack", 0xffb84f4f);
    public static final Material ardite     = mat("ardite", 0xffd14210);
    public static final Material cobalt     = mat("cobalt", 0xff2882d4);
    public static final Material manyullyn  = mat("manyullyn", 0xffa15cf8);

    // Special Bone Materials
    public static final Material bloodbone  = mat("bloodbone", 0xffc70000);

    // Common Metals + Alloys
    public static final Material copper     = mat("copper", 0xffed9f07);
    public static final Material bronze     = mat("bronze", 0xffe3bd68);
    public static final Material lead       = mat("lead", 0xff4d4968);
    public static final Material silver     = mat("silver", 0xffd1ecf6);
    public static final Material electrum   = mat("electrum", 0xffe8db49);
    public static final Material steel      = mat("steel", 0xffa7a7a7);
    public static final Material alubrass   = mat("alubrass", 0xfff0d467);
    public static final Material alumite    = mat("alumite", 0xffffa7e9);

    // specul
    public static final Material xu = hiddenMat("unstable", 0xFFFFFFFF);

    // unknown
    public static final Material unknown = hiddenMat("unknown", 0xFFFFFFFF);

    // bowstring materials 弓弦
    public static final Material string    = mat("string", 0xffeeeeee);
    public static final Material vine      = mat("vine", 0xff40a10f);
    public static final Material slimevine_blue   = mat("slimevine_blue", 0xff74c8c7);
    //public static final Material slimevine_orange = mat("slimevine_orange", 0xffff960d);
    public static final Material slimevine_purple = mat("slimevine_purple", 0xffc873c8);

    // additional arrow shaft 额外箭杆
    public static final Material blaze     = mat("blaze", 0xffffc100);
    public static final Material reed      = mat("reed", 0xffaadb74);
    public static final Material ice       = mat("ice", 0xff97d7e0);
    public static final Material endrod    = mat("endrod", 0xffe8ffd6);

    // fletching 箭羽
    public static final Material feather   = mat("feather", 0xffeeeeee);
    public static final Material leaf      = mat("leaf", 0xff1d730c);
    public static final Material slimeleaf_blue   = mat("slimeleaf_blue", 0xff74c8c7);
    public static final Material slimeleaf_orange = mat("slimeleaf_orange", 0xffff960d);
    public static final Material slimeleaf_purple = mat("slimeleaf_purple", 0xffc873c8);

    private static Material mat(String name, int color) {
        Material mat = new Material(name, color, false);
        register(mat);
        return mat;
    }

    private static Material hiddenMat(String name, int color) {
        Material mat = new Material(name, color, true);
        register(mat);
        return mat;
    }

    private static void register(Material mat) {
        String name = mat.identifier;
        if (MATERIALS_BY_ID.putIfAbsent(name, mat) != null) {
            throw new IllegalArgumentException("Duplicate material identifier: " + name);
        }
        MUTABLE_MATERIALS.add(mat);
    }

    public static Material getMaterial(String identifier) {
        return MATERIALS_BY_ID.get(identifier);
    }

    public static List<Material> getCraftableMaterials() {
        return materials.stream().filter(Material::isCraftable).toList();
    }

    public static List<Material> getCastableMaterials() {
        return materials.stream().filter(Material::isCastable).toList();
    }

    /** Explicit class-initialization hook for the mod constructor. */
    public static void bootstrap() {
    }

    private static void setCraftableMaterials(Material... matList) {
        for (Material mat : matList) {
            mat.setCraftable(true);
        }
    }

    private static void setCastableMaterials(Material... matList) {
        for (Material mat : matList) {
            mat.setCastable(true);
        }
    }

    // 注册材料属性
    private static void registerToolMaterialStats() {
        wood.addMaterialStats(new HeadMaterialStats(35, 2.00F, 2.00F, HarvestLevels.STONE),
                new HandleMaterialStats(1, 25),
                new ExtraMaterialStats(15));
        stone.addMaterialStats(new HeadMaterialStats(120, 4.00F, 3.00F, HarvestLevels.IRON),
                new HandleMaterialStats(0.50F, -50),
                new ExtraMaterialStats(20));
    }

    // 设定合成方式
    static {
        setCraftableMaterials(wood, stone, flint, cactus, bone, obsidian, prismarine, endstone, paper, sponge, firewood,
                knightslime, slime, blueslime, magmaslime);
        setCastableMaterials(iron, pigiron);

        registerToolMaterialStats();
    }

}
