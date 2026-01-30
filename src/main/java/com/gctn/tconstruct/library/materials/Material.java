package com.gctn.tconstruct.library.materials;

import com.gctn.tconstruct.library.Util;
import com.gctn.tconstruct.library.traits.ITrait;
// import net.minecraft.world.level.material.Fluid;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Material {
    public static final int VALUE_Ingot = 144;
    public static final int VALUE_Nugget = VALUE_Ingot / 9;
    public static final int VALUE_Fragment = VALUE_Ingot / 4;
    public static final int VALUE_Shard = VALUE_Ingot / 2;

    public static final int VALUE_Gem = 666; // divisible by 3!
    public static final int VALUE_Block = VALUE_Ingot * 9;

    public static final int VALUE_SearedBlock = VALUE_Ingot * 2;
    public static final int VALUE_SearedMaterial = VALUE_Ingot / 2;
    public static final int VALUE_Glass = 1000;

    public static final int VALUE_BrickBlock = VALUE_Ingot * 4;

    public static final int VALUE_SlimeBall = 250;

    /**
     * This String uniquely identifies a material.
     */
    public final String identifier;

    //TODO 关联流体
    /* The fluid associated with this material, can be null
      protected Fluid fluid;
     */

    /** Material can be crafted into parts in the PartBuilder */
    protected boolean craftable;

    /** Material can be cast into parts using the Smeltery and a Cast. Fluid must be NON-NULL */
    protected boolean castable;

    /**
     * If true, the material is not shown
     */
    private boolean hidden;

    // we use a specific map for 2 reasons:
    // * A Map so we can obtain the stats we want quickly
    // * the linked map to ensure the order when iterating
    protected final Map<String, IMaterialStats> stats = new LinkedHashMap<>();
    /** Stat-ID -> Traits */
    protected final Map<String, List<ITrait>> traits = new LinkedHashMap<>();

    public Material(String identifier) { this(identifier, 0x000000, false); }
    public Material(String identifier, int color) { this(identifier, color, false); }
    public Material(String identifier, int color, boolean hidden) {
        this.identifier = Util.sanitizeLocalizationString(identifier);
    }

}
