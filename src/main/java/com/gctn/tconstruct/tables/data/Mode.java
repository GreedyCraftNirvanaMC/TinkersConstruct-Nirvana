package com.gctn.tconstruct.tables.data;

public enum Mode {
    DEFAULT,
    DISASSEMBLE,
    PICKAXE,
    SHOVEL,
    AXE,
    DEBUG;

    private static final Mode[] VALUES = values();

    public int getButtonId() {
        return this.ordinal();
    }

    public static Mode byButtonId(int id) {
        if (id < 0 || id >= VALUES.length) {
            return null;
        }
        return VALUES[id];
    }

    public static int count() {
        return VALUES.length;
    }
}
