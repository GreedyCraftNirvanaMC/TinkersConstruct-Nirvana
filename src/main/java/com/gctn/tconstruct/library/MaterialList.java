package com.gctn.tconstruct.library;

import com.gctn.tconstruct.library.materials.Material;

import java.util.HashMap;
import java.util.Map;

public class MaterialList {
    public static final Map<Material, String[]> MATERIAL_MAP = new HashMap<>();

    public static void init(){
        MATERIAL_MAP.put(TinkerMaterials.wood, new String[] {
                "minecraft:oak_planks",
        });

        MATERIAL_MAP.put(TinkerMaterials.stone, new String[] {
                "minecraft:cobblestone",
        });
    }
}
