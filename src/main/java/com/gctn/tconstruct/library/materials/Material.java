package com.gctn.tconstruct.library.materials;

import com.gctn.tconstruct.library.stats.AbstractMaterialStats;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

public class Material {
    private static final Pattern VALID_IDENTIFIER = Pattern.compile("[a-z0-9_]+");
    public static final String HEAD = "Head";
    public static final String HANDLE = "Handle";
    public static final String EXTRA = "Extra";
    public final String identifier;
    public final int color;
    public final boolean hidden;

    private final Map<String, AbstractMaterialStats> stats = new LinkedHashMap<>();
    private final Map<String, AbstractMaterialStats> statsView = Collections.unmodifiableMap(stats);

    // 是否可以直接使用部件加工台(默认false)
    protected boolean craftable = false;

    // 是否可以使用浇筑方式合成(默认false)
    protected boolean castable = false;

    // 参数不全的默认初始化
    public Material(String identifier, int color) { this(identifier, color, false); }
    public Material(String identifier) { this(identifier, 0xFFFFFF); }

    public Material(String identifier, int color, boolean hidden) {
        this.identifier = Objects.requireNonNull(identifier, "identifier");
        if (!VALID_IDENTIFIER.matcher(identifier).matches()) {
            throw new IllegalArgumentException("Invalid material identifier: " + identifier);
        }
        this.color = (color & 0xFF000000) == 0 ? color | 0xFF000000 : color;
        this.hidden = hidden;
    }

    // 设置合成方式
    public Material setCraftable(boolean craftable) {
        this.craftable = craftable;
        return this;
    }
    public Material setCastable(boolean castable) {
        this.castable = castable;
        return this;
    }

    public boolean isHidden() {
        return hidden;
    }

    public boolean isCraftable() {
        return craftable;
    }

    public boolean isCastable() {
        return castable;
    }

    // 添加材料属性
    public Material addMaterialStats(AbstractMaterialStats... stats) {
        for (AbstractMaterialStats stat : stats) {
            Objects.requireNonNull(stat, "stat");
            this.stats.put(stat.identifier, stat);
        }
        return this;
    }

    // 获取材料属性
    public Map<String, AbstractMaterialStats> getStats() {
        return statsView;
    }

    public boolean hasStats(String identifier) {
        return stats.containsKey(identifier);
    }

    public <T extends AbstractMaterialStats> T getStats(String identifier, Class<T> statClass) {
        AbstractMaterialStats stat = stats.get(identifier);
        return statClass.isInstance(stat) ? statClass.cast(stat) : null;
    }

    public int getColor() { return color; }

}
