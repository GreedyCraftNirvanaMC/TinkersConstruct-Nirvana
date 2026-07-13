package com.gctn.tconstruct;

import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.List;
import java.util.regex.Pattern;

public final class Config {
    private static final Pattern MOD_ID = Pattern.compile("[a-z][a-z0-9_]{1,63}");
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.ConfigValue<List<? extends String>> MOD_PRIORITIES = BUILDER
            .comment("A list of ModIds to decide priorities")
            .defineList("mod_priorities", List.of("tconstruct"), () -> "minecraft",
                    value -> value instanceof String modId && MOD_ID.matcher(modId).matches());

    static final ModConfigSpec SPEC = BUILDER.build();

    private Config() {
    }
}
