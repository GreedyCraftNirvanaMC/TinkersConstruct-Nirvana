package com.gctn.tconstruct.library.materials;

import net.minecraft.ChatFormatting;

public class Material {
    public final String identifier;
    public final int color;
    public final boolean hidden;

    // 是否可以直接使用部件加工台
    protected boolean craftable;

    // 是否可以使用浇筑方式合成
    protected boolean castable;

    private boolean isHidden;

    // 参数不全的默认初始化
    public Material(String identifier, int color) { this(identifier, color, false); }
    public Material(String identifier) { this(identifier, 0xFFFFFF); }

    public Material(String identifier, int color, boolean hidden) {
        this.identifier = identifier;
        this.color = color;
        this.hidden = hidden;
    }

    public int getColor() { return color; }

}
