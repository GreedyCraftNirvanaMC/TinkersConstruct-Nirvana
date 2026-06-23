package com.gctn.tconstruct.tables;

public enum Mode {
    DEFAULT,
    DISASSEMBLE,
    PICKAXE,
    SHOVEL,
    AXE,
    DEBUG;

    public int getButtonId() {
        return this.ordinal();
    }

    public static Mode byButtonId(int id) {
        Mode[] modes = values();
        if (id < 0 || id >= modes.length) {
            return null;
        }
        return modes[id];
    }
}
