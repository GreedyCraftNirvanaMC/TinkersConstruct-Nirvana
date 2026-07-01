package com.gctn.tconstruct.library;

import com.gctn.tconstruct.library.materials.Material;

import java.util.HashMap;
import java.util.Map;

public class MaterialList {
    public static final Map<Material, Map<String, Double>> MATERIAL_MAP = new HashMap<>();

    public static void init(){
        MATERIAL_MAP.put(TinkerMaterials.wood, Map.ofEntries(
                Map.entry("minecraft:oak_planks", 0.05)
        ));

        MATERIAL_MAP.put(TinkerMaterials.stone, Map.ofEntries(
                Map.entry("minecraft:cobblestone", 0.1)
        ));
    }
}
