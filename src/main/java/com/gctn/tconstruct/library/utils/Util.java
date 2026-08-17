package com.gctn.tconstruct.library.utils;

import com.mojang.datafixers.util.Either;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;

import static com.gctn.tconstruct.TinkersNirvana.MODID;

public class Util {
    public static final DecimalFormat df = new DecimalFormat("#,###,###.##", DecimalFormatSymbols.getInstance(Locale.US));
    public static final DecimalFormat dfPercent = new DecimalFormat("#%");

    public static Logger getLogger(String type) {
        String log = MODID;

        return LoggerFactory.getLogger(log + "-" + type);
    }

    public static String sanitizeLocalizationString(String string) {
        return string.toLowerCase(Locale.US).replaceAll(" ", "");
    }

    public static String translate(String key, Object... pars) {
        // translates twice to allow rerouting/alias
        return Component.translatable(Component.translatable(String.format(key, pars)).toString().trim()).toString().trim();

    }

    /**
     * Returns the actual color value for a chatformatting
     */
    public static int enumChatFormattingToColor(ChatFormatting color) {
        // TODO 修改后验证正确性
        int i = color.getColor();
        int j = (i >> 3 & 1) * 85;
        int k = (i >> 2 & 1) * 170 + j;
        int l = (i >> 1 & 1) * 170 + j;
        int i1 = (i >> 0 & 1) * 170 + j;
        if(i == 6) {
            k += 85;
        }
        if(i >= 16) {
            k /= 4;
            l /= 4;
            i1 /= 4;
        }

        return (k & 255) << 16 | (l & 255) << 8 | i1 & 255;
    }

}
