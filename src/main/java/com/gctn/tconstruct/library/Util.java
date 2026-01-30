package com.gctn.tconstruct.library;

import net.minecraft.network.chat.Component;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class Util {
    public static final DecimalFormat df = new DecimalFormat("#,###,###.##", DecimalFormatSymbols.getInstance(Locale.US));
    public static final DecimalFormat dfPercent = new DecimalFormat("#%");

    public static String sanitizeLocalizationString(String string) {
        return string.toLowerCase(Locale.US).replaceAll(" ", "");
    }

    public static String translate(String key, Object... pars) {
        // translates twice to allow rerouting/alias
        return Component.translatable(Component.translatable(String.format(key, pars)).toString().trim()).toString().trim();

    }
}
