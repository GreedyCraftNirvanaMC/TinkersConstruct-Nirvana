package com.gctn.tconstruct.library.utils;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import static com.gctn.tconstruct.TinkersConstructNirvana.MODID;

public final class Util {
    public static final DecimalFormat df = new DecimalFormat("#,###,###.##", DecimalFormatSymbols.getInstance(Locale.US));
    public static final DecimalFormat dfPercent = new DecimalFormat("#%");

    private Util() {
    }

    public static Logger getLogger(String type) {
        String log = MODID;

        return LoggerFactory.getLogger(log + "-" + type);
    }

    public static String sanitizeLocalizationString(String string) {
        return string.toLowerCase(Locale.ROOT).replace(" ", "");
    }

    public static String translate(String key, Object... pars) {
        // translates twice to allow rerouting/alias
        String translatedKey = Component.translatable(String.format(key, pars)).getString().trim();
        return Component.translatable(translatedKey).getString().trim();
    }

    /**
     * Returns the actual color value for a chatformatting
     */
    public static int enumChatFormattingToColor(ChatFormatting color) {
        Integer rgb = color.getColor();
        if (rgb == null) {
            throw new IllegalArgumentException(color + " is not a color");
        }
        return rgb;
    }
}
