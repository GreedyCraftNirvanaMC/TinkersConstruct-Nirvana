package com.gctn.tconstruct.library;

import com.gctn.tconstruct.library.materials.Material;
import com.google.common.collect.Lists;

import java.util.List;

public class TinkerMaterials {
    public static final List<Material> materials = Lists.newArrayList();

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
    public static final Material xu;

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

    public static Material mat(String name, int color) {
        // make materials show by default, integration will make them invisible
        Material mat = new Material(name, color, false);
        materials.add(mat);
        return mat;
    }

    static {
        xu = new Material("unstable", 0xFFFFFFFF, true);
    }
}
