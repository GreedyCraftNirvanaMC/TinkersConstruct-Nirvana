package com.gctn.tconstruct.library;

import com.gctn.tconstruct.library.materials.Material;

import java.util.Map;

public final class MaterialList {
    private MaterialList() {
    }

    public static final Map<Material, Map<String, Double>> MATERIAL_MAP = Map.of(
            TinkerMaterials.wood, Map.of("minecraft:oak_planks", 1.0),
            TinkerMaterials.stone, Map.of("minecraft:cobblestone", 1.0)
    );
}
