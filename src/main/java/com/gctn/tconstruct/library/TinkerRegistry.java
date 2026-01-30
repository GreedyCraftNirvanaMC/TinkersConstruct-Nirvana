package com.gctn.tconstruct.library;

import com.gctn.tconstruct.TinkersConstructNirvana;
import com.gctn.tconstruct.library.materials.IMaterialStats;
import com.gctn.tconstruct.library.materials.Material;
import com.gctn.tconstruct.library.traits.ITrait;
import com.google.common.base.CharMatcher;
import com.google.common.collect.ImmutableList;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.slf4j.Logger;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public final class TinkerRegistry {
    // the logger for the library
    public static final Logger log = Util.getLogger("API");

    private TinkerRegistry() {}

    /*---------------------------------------------------------------------------
    | CREATIVE TABS                                                             |
    ---------------------------------------------------------------------------*/
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, TinkersConstructNirvana.MODID);

    public static final Supplier<CreativeModeTab> GENERAL_TAB =
            CREATIVE_MODE_TABS.register("tinker_general", () -> CreativeModeTab.builder()
                    .icon(() -> new ItemStack(Items.SLIME_BALL))
                    .title(Component.translatable("tinker_general_tab"))
                    .build());
    public static final Supplier<CreativeModeTab> TOOLS_TAB =
            CREATIVE_MODE_TABS.register("tinker_tools", () -> CreativeModeTab.builder()
                    .icon(() -> new ItemStack(Items.IRON_PICKAXE))
                    .title(Component.translatable("tinker_tools_tab"))
                    .build());
    public static final Supplier<CreativeModeTab> PARTS_TAB =
            CREATIVE_MODE_TABS.register("tinker_tool_parts", () -> CreativeModeTab.builder()
                    .icon(() -> new ItemStack(Items.STICK))
                    .title(Component.translatable("tinker_tool_parts_tab"))
                    .build());
    public static final Supplier<CreativeModeTab> SMELTERY_TAB =
            CREATIVE_MODE_TABS.register("tinker_smeltery", () -> CreativeModeTab.builder()
                    .icon(() -> new ItemStack(Blocks.STONE_BRICKS))
                    .title(Component.translatable("tinker_smeltery_tab"))
                    .build());
    public static final Supplier<CreativeModeTab> WORLD_TAB =
            CREATIVE_MODE_TABS.register("tinker_world", () -> CreativeModeTab.builder()
                    .icon(() -> new ItemStack(Blocks.SLIME_BLOCK))
                    .title(Component.translatable("tinker_world_tab"))
                    .build());
    public static final Supplier<CreativeModeTab> GADGETS_TAB =
            CREATIVE_MODE_TABS.register("tinker_gadgets", () -> CreativeModeTab.builder()
                    .icon(() -> new ItemStack(Blocks.TNT))
                    .title(Component.translatable("tinker_gadgets_tab"))
                    .build());


    /*---------------------------------------------------------------------------
    | MATERIALS                                                                 |
    ---------------------------------------------------------------------------*/

    // Identifier to Material mapping. Hashmap so we can look it up directly without iterating
    private static final Map<String, Material> materials = new ConcurrentHashMap<>();
    private static final Map<String, ITrait> traits = new ConcurrentHashMap<>();
    // traceability information who registered what. Used to find errors.
    private static final Map<String, ModContainer> materialRegisteredByMod = new Object2ObjectOpenHashMap<>();
    private static final Map<String, Map<String, ModContainer>> statRegisteredByMod = new Object2ObjectOpenHashMap<>();
    private static final Map<String, Map<String, ModContainer>> traitRegisteredByMod = new Object2ObjectOpenHashMap<>();

    // contains all cancelled materials, allows us to eat calls regarding the material silently
    private static final Set<String> cancelledMaterials = new ObjectOpenHashSet<>();

    public static void addMaterial(Material material, IMaterialStats stats, ITrait trait) {
        addMaterial(material, stats);
        addMaterialTrait(material.identifier, trait, null);
    }

    public static void addMaterial(Material material, ITrait trait) {
        addMaterial(material);
        addMaterialTrait(material.identifier, trait, null);
    }

    public static void addMaterial(Material material, IMaterialStats stats) {
        addMaterial(material);
        addMaterialStats(material.identifier, stats);
    }

    /**
     * Registers a material. The materials identifier has to be lowercase and not contain any spaces.
     * Identifiers have to be globally unique!
     */
    public static void addMaterial(Material material) {
        // TODO 处理材料注册
        /*
        // ensure material identifiers are safe
        if(CharMatcher.whitespace().matchesAnyOf(material.getIdentifier())) {
            log.error("Could not register material \"{}\": Material identifier must not contain any spaces.", material.identifier);
            return;
        }
        if(CharMatcher.javaUpperCase().matchesAnyOf(material.getIdentifier())) {
            log.error("Could not register material \"{}\": Material identifier must be completely lowercase.", material.identifier);
            return;
        }

        // duplicate material
        if(materials.containsKey(material.identifier)) {
            ModContainer currentMod = Loader.instance().activeModContainer();
            String currentModId = currentMod != null ? currentMod.getModId() : "unknown";
            ModContainer registeredMod = getTrace(material);
            String registeredModId = registeredMod != null ? registeredMod.getModId() : "unknown";
            // compare priorities based on config
            int currentPriority = getModPriority(currentModId);
            int registeredPriority = getModPriority(registeredModId);
            if(currentPriority < registeredPriority) {
                // current mod has higher priority (lower index), replace existing material
                log.warn("Replacing material \"{}\" with material from {} ({})",
                        material.identifier, currentMod.getName(), currentModId);
                // remove existing material
                materials.remove(material.identifier);
                materialRegisteredByMod.remove(material.identifier);
            } else {
                // existing mod has higher priority, reject new material
                log.error("Could not register material \"{}\" from {} ({}): It was already registered by {} ({})",
                        material.identifier, currentMod.getName(), currentModId, registeredMod.getName(), registeredModId);
                return;
            }
        }

        MaterialEvent.MaterialRegisterEvent event = new MaterialEvent.MaterialRegisterEvent(material);

        if(NeoForge.EVENT_BUS.post(event).booleanValue()) {
            // event cancelled
            log.trace("Addition of material \"{}\" cancelled by event", material.getIdentifier());
            cancelledMaterials.add(material.getIdentifier());
            return;
        }

        // ignored material
        if(Arrays.stream(Config.materialIgnore).anyMatch(mat -> mat.equals(material.getIdentifier()))) {
            log.trace("Addition of material \"{}\" ignored by config", material.getIdentifier());
            cancelledMaterials.add(material.getIdentifier());
            return;
        }

        // register material
        materials.put(material.identifier, material);
        putMaterialTrace(material.identifier);

         */
    }

    public static Material getMaterial(String identifier) {
        return materials.getOrDefault(identifier, Material.UNKNOWN);
    }

    public static Collection<Material> getAllMaterials() {
        return ImmutableList.copyOf(materials.values());
    }

    /**
     * Called by TinkerIntegrtion at the end of postInit to remove any materials that are still hidden (unused)
     * For internal use, should not need to be called by other mods
     */
    public static void removeHiddenMaterials() {
        materials.entrySet().removeIf(entry->entry.getValue().isHidden());
    }

    public static Collection<Material> getAllMaterialsWithStats(String statType) {
        ImmutableList.Builder<Material> mats = ImmutableList.builder();
        for(Material material : materials.values()) {
            if(material.hasStats(statType)) {
                mats.add(material);
            }
        }

        return mats.build();
    }

    /*---------------------------------------------------------------------------
    | TRAITS & STATS                                                            |
    ---------------------------------------------------------------------------*/

    public static void addTrait(ITrait trait) {
        // TODO 处理材料特性注册
        /*
        // Trait might already have been registered since modifiers and materials share traits
        if(traits.containsKey(trait.getIdentifier())) {
            return;
        }

        traits.put(trait.getIdentifier(), trait);

        ModContainer activeMod = Loader.instance().activeModContainer();
        putTraitTrace(trait.getIdentifier(), trait, activeMod);

         */
    }

    public static void addMaterialStats(String materialIdentifier, IMaterialStats stats) {
        if(cancelledMaterials.contains(materialIdentifier)) {
            return;
        }
        if(!materials.containsKey(materialIdentifier)) {
            log.error("Could not add Stats \"{}\" to \"{}\": Unknown Material", stats.getIdentifier(), materialIdentifier);
            return;
        }

        Material material = materials.get(materialIdentifier);
        addMaterialStats(material, stats);
    }

    public static void addMaterialStats(Material material, IMaterialStats stats, IMaterialStats... stats2) {
        addMaterialStats(material, stats);
        for(IMaterialStats stat : stats2) {
            addMaterialStats(material, stat);
        }
    }

    public static void addMaterialStats(Material material, IMaterialStats stats) {
        // TODO 处理材料数据注册
        /*
        if(material == null) {
            log.error("Could not add Stats \"{}\": Material is null", stats.getIdentifier());
            return;
        }
        if(cancelledMaterials.contains(material.identifier)) {
            return;
        }

        String identifier = material.identifier;
        // duplicate stats
        if(material.getStats(stats.getIdentifier()) != null) {
            String registeredBy = "Unknown";
            Map<String, ModContainer> matReg = statRegisteredByMod.get(identifier);
            if(matReg != null) {
                registeredBy = matReg.get(stats.getIdentifier()).getName();
            }

            log.fatal("Could not add Stats to \"{}\": Stats of type \"{}\" were already registered by {}. Use the events to modify stats.", identifier, stats.getIdentifier(), registeredBy);
            return;
        }

        // ensure there are default stats present
        if(Material.UNKNOWN.getStats(stats.getIdentifier()) == null) {
            log.fatal("Could not add Stat of type \"{}\": Default Material does not have default stats for said type. Please add default-values to the default material \"unknown\" first.", stats
                    .getIdentifier());
            return;
        }

        MaterialEvent.StatRegisterEvent<?> event = new MaterialEvent.StatRegisterEvent<>(material, stats);
        MinecraftForge.EVENT_BUS.post(event);

        // overridden stats from event
        if(event.getResult() == Event.Result.ALLOW) {
            stats = event.newStats;
        }

        material.addStats(stats);

        ModContainer activeMod = Loader.instance().activeModContainer();
        putStatTrace(identifier, stats, activeMod);

        if(Objects.equals(stats.getIdentifier(), MaterialTypes.HEAD) && !material.hasStats(MaterialTypes.PROJECTILE)) {
            addMaterialStats(material, new ProjectileMaterialStats());
        }

         */
    }

    public static void addMaterialTrait(String materialIdentifier, ITrait trait, String stats) {
        if(cancelledMaterials.contains(materialIdentifier)) {
            return;
        }
        if(!materials.containsKey(materialIdentifier)) {
            log.error("Could not add Trait \"{}\" to \"{}\": Unknown Material", trait.getIdentifier(), materialIdentifier);
            return;
        }

        Material material = materials.get(materialIdentifier);
        addMaterialTrait(material, trait, stats);
    }

    public static void addMaterialTrait(Material material, ITrait trait, String stats) {
        if(checkMaterialTrait(material, trait, stats)) {
            material.addTrait(trait);
        }
    }

    /**
     * Call before adding a trait to a material. Checks consistency and takes care everything is in a consistent state.
     * Registers the trait if it's not registered, takes events into account.
     */
    public static boolean checkMaterialTrait(Material material, ITrait trait, String stats) {
        // TODO 不知道啥功能，暂时默认返回true
        /*
        if(material == null) {
            log.error("Could not add Trait \"{}\": Material is null", trait.getIdentifier());
            return false;
        }
        if(cancelledMaterials.contains(material.identifier)) {
            return false;
        }

        String identifier = material.identifier;
        // duplicate traits
        if(material.hasTrait(trait.getIdentifier(), stats)) {
            String registeredBy = "Unknown";
            Map<String, ModContainer> matReg = traitRegisteredByMod.get(identifier);
            if(matReg != null) {
                registeredBy = matReg.get(trait.getIdentifier()).getName();
            }

            log.fatal("Could not add Trait to \"{}\": Trait \"{}\" was already registered by {}", identifier, trait.getIdentifier(), registeredBy);
            return false;
        }

        MaterialEvent.TraitRegisterEvent<?> event = new MaterialEvent.TraitRegisterEvent<>(material, trait);
        if(MinecraftForge.EVENT_BUS.post(event)) {
            // cancelled
            log.trace("Trait {} on {} cancelled by event", trait.getIdentifier(), material.getIdentifier());
            return false;
        }

        addTrait(trait);

         */

        return true;

    }

    public static ITrait getTrait(String identifier) {
        return traits.get(identifier);
    }

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }

}
