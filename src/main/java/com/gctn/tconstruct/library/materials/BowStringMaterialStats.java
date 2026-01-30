package com.gctn.tconstruct.library.materials;

import com.gctn.tconstruct.library.Util;
import com.google.common.collect.Lists;

import java.util.List;

public class BowStringMaterialStats extends AbstractMaterialStats {

    public final static String LOC_Multiplier = "stat.bowstring.modifier.name";

    public final static String LOC_MultiplierDesc = "stat.bowstring.modifier.desc";

    public final static String COLOR_Modifier = HandleMaterialStats.COLOR_Modifier;

    public final float modifier; // around 1.0

    public BowStringMaterialStats(float modifier) {
        super(MaterialTypes.BOWSTRING);
        this.modifier = modifier;
    }

    @Override
    public List<String> getLocalizedInfo() {
        List<String> info = Lists.newArrayList();

        if(modifier != 0) info.add(formatModifier(modifier));

        return info;
    }

    @Override
    public List<String> getLocalizedDesc() {
        List<String> info = Lists.newArrayList();

        if(modifier != 0) info.add(Util.translate(LOC_MultiplierDesc));

        return info;
    }


    public static String formatModifier(float quality) {
        return formatNumber(LOC_Multiplier, COLOR_Modifier, quality);
    }
}
