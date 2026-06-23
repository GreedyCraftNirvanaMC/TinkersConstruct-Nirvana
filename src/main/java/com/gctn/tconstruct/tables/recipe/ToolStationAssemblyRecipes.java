package com.gctn.tconstruct.tables.recipe;

import com.gctn.tconstruct.library.toolparts.Binding;
import com.gctn.tconstruct.library.toolparts.PickaxeHead;
import com.gctn.tconstruct.library.toolparts.ToolRod;
import com.gctn.tconstruct.tables.Mode;
import com.gctn.tconstruct.tools.tools.Pickaxe;

import java.util.Map;

public final class ToolStationAssemblyRecipes {
    private static final Map<Mode, ToolAssemblyRecipe> RECIPES = Map.of(
            Mode.PICKAXE, new ToolAssemblyRecipe(
                    new ToolAssemblyRecipe.PartRequirement[] {
                            new ToolAssemblyRecipe.PartRequirement(0, ToolRod.TOOL_ROD, "Handle"),
                            new ToolAssemblyRecipe.PartRequirement(1, PickaxeHead.PICKAXEHEAD, "Head"),
                            new ToolAssemblyRecipe.PartRequirement(2, Binding.BINDING, "Extra")
                    },
                    materials -> Pickaxe.getColoredPickaxe(materials[0], materials[1], materials[2]))
    );

    public static ToolAssemblyRecipe get(Mode mode) {
        return RECIPES.get(mode);
    }
}
