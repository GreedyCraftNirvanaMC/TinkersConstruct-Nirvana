package com.gctn.tconstruct.library.materials;

import com.gctn.tconstruct.library.TinkerRegistry;
import com.gctn.tconstruct.library.Util;
import com.gctn.tconstruct.library.client.CustomFontColor;
import com.gctn.tconstruct.library.client.MaterialRenderInfo;
import com.gctn.tconstruct.library.traits.ITrait;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import net.minecraft.ChatFormatting;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
// import net.minecraft.world.level.material.Fluid;

import java.util.*;

public class Material {
    public static final Material UNKNOWN = new Material("unknown", ChatFormatting.WHITE);

    // How much the different items are "worth"
    // the values are used for both liquid conversion as well as part crafting
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

    /**
     * Client-Information
     * How the material will be rendered on tinker tools etc.
     */
    @OnlyIn(Dist.CLIENT)
    public MaterialRenderInfo renderInfo;// = new MaterialRenderInfo.Default(0xffffff);
    public int materialTextColor = 0xffffff; // used in tooltips and other text. Saved in NBT.

    // we use a specific map for 2 reasons:
    // * A Map so we can obtain the stats we want quickly
    // * the linked map to ensure the order when iterating
    protected final Map<String, IMaterialStats> stats = new LinkedHashMap<>();
    /** Stat-ID -> Traits */
    protected final Map<String, List<ITrait>> traits = new LinkedHashMap<>();

    public Material(String identifier) { this(identifier, 0x000000, false); }
    public Material(String identifier, ChatFormatting textColor) { this(identifier, Util.enumChatFormattingToColor(textColor)); }
    public Material(String identifier, int color) { this(identifier, color, false); }
    public Material(String identifier, int color, boolean hidden) {
        this.identifier = Util.sanitizeLocalizationString(identifier);
        this.hidden = hidden;

        // if invisible, make it fully opaque.
        if(((color >> 24) & 0xFF) == 0) {
            color |= 0xFF << 24;
        }

        this.materialTextColor = color;
    }

    public String getTextColor() {
        return CustomFontColor.encodeColor(materialTextColor);
    }

    /*** If true the material will not be displayed to the user anywhere. Used for special or internal materials. */
    public boolean isHidden() {
        return hidden;
    }

    /**
     * Returns the given type of stats if the material has them. Returns null Otherwise.
     */
    private IMaterialStats getStatsSafe(String identifier) {
        if(identifier == null || identifier.isEmpty()) {
            return null;
        }

        for(IMaterialStats stat : stats.values()) {
            if(identifier.equals(stat.getIdentifier())) {
                return stat;
            }
        }

        return null;
    }

    /**
     * Returns the material stats of the given type of this material.
     *
     * @param identifier Identifier of the material.
     * @param <T>        Type of the Stats are determined by return value. Use the correct
     * @return The stats found or null if none present.
     */
    @SuppressWarnings("unchecked")
    public <T extends IMaterialStats> T getStats(String identifier) {
        return (T) getStatsSafe(identifier);
    }

    @SuppressWarnings("unchecked")
    public <T extends IMaterialStats> T getStatsOrUnknown(String identifier) {
        T stats = (T) getStatsSafe(identifier);
        if(stats == null && this != UNKNOWN) {
            return UNKNOWN.getStats(identifier);
        }
        return stats;
    }

    public Collection<IMaterialStats> getAllStats() {
        return stats.values();
    }

    public boolean hasStats(String identifier) {
        return getStats(identifier) != null;
    }

    /**
     * Adds the trait as the default trait, will be used if no more specific one is present time.
     */
    public Material addTrait(ITrait materialTrait) {
        return addTrait(materialTrait, null);
    }

    /**
     * Adds the trait to be added if the specified stats are used.
     */
    public Material addTrait(ITrait materialTrait, String dependency) {
        // register unregistered traits
        if(TinkerRegistry.checkMaterialTrait(this, materialTrait, dependency)) {
            getStatTraits(dependency).add(materialTrait);
        }

        return this;
    }

    /** Obtains the list of traits for the given stat, creates it if it doesn't exist yet. */
    protected List<ITrait> getStatTraits(String id) {
        if(!this.traits.containsKey(id)) {
            this.traits.put(id, new LinkedList<>());
        }
        return this.traits.get(id);
    }

    /**
     * Returns whether the material has a trait with that identifier.
     */
    public boolean hasTrait(String identifier, String stats) {
        if(identifier == null || identifier.isEmpty()) {
            return false;
        }

        for(ITrait trait : getStatTraits(stats)) {
            if(trait.getIdentifier().equals(identifier)) {
                return true;
            }
        }
        return false;
    }

    public List<ITrait> getDefaultTraits() {
        return ImmutableList.copyOf(getStatTraits(null));
    }

    public List<ITrait> getAllTraitsForStats(String stats) {
        if(this.traits.containsKey(stats)) {
            return ImmutableList.copyOf(this.traits.get(stats));
        }
        else if(this.traits.containsKey(null)) {
            return ImmutableList.copyOf(this.traits.get(null));
        }
        return ImmutableList.of();
    }

    public Collection<ITrait> getAllTraits() {
        ImmutableSet.Builder<ITrait> builder = ImmutableSet.builder();
        for(List<ITrait> traitlist : traits.values()) {
            builder.addAll(traitlist);
        }
        return builder.build();
    }

    /**
     * The display information for the Material. You should totally set this if you want your material to be visible.
     *
     * @param renderInfo How the textures for the material are generated
     */
    @OnlyIn(Dist.CLIENT)
    public void setRenderInfo(MaterialRenderInfo renderInfo) {
        this.renderInfo = renderInfo;
    }

    public String getIdentifier() {
        return identifier;
    }

    /**
     * Do not use this function directly stats. Use TinkerRegistry.addMaterialStats instead.
     */
    public Material addStats(IMaterialStats materialStats) {
        this.stats.put(materialStats.getIdentifier(), materialStats);
        return this;
    }

    // TODO 大量功能仍未迁移
}
