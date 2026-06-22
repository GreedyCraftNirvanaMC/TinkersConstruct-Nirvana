package com.gctn.tconstruct.common.tables;

import com.gctn.tconstruct.common.tables.ToolStationMenu.Mode;

public final class ToolStationSlotPositions {
    public static final int INPUT_SLOT_COUNT = 6;

    private static final int SLOT_ITEM_OFFSET = 1;

    private static final SlotPosition[] ALL_INPUT_SLOTS = {
            new SlotPosition(32, 41),
            new SlotPosition(14, 61),
            new SlotPosition(10, 36),
            new SlotPosition(32, 18),
            new SlotPosition(54, 36),
            new SlotPosition(50, 61)
    };

    private static final SlotPosition[] PICKAXE_INPUT_SLOTS = {
            new SlotPosition(14, 59),
            new SlotPosition(52, 21),
            new SlotPosition(32, 41),
            null,
            null,
            null
    };

    private static final SlotPosition[] SHOVEL_INPUT_SLOTS = {
            new SlotPosition(32, 41),
            new SlotPosition(50, 23),
            new SlotPosition(12, 61),
            null,
            null,
            null
    };

    private static final SlotPosition[] AXE_INPUT_SLOTS = {
            new SlotPosition(22, 51),
            new SlotPosition(30, 20),
            new SlotPosition(50, 32),
            null,
            null,
            null
    };

    private ToolStationSlotPositions() {
    }

    public static boolean isInputSlotActive(Mode mode, int inputSlot) {
        return getInputSlotPosition(mode, inputSlot) != null;
    }

    public static SlotPosition getInputSlotPosition(Mode mode, int inputSlot) {
        validateInputSlot(inputSlot);
        return getInputSlots(mode)[inputSlot];
    }

    public static SlotPosition getInputSlotPositionOrFallback(Mode mode, int inputSlot) {
        SlotPosition position = getInputSlotPosition(mode, inputSlot);
        return position != null ? position : getFallbackInputSlotPosition(inputSlot);
    }

    public static SlotPosition getFallbackInputSlotPosition(int inputSlot) {
        validateInputSlot(inputSlot);
        return ALL_INPUT_SLOTS[inputSlot];
    }

    private static SlotPosition[] getInputSlots(Mode mode) {
        return switch (mode) {
            case DEFAULT, DISASSEMBLE -> ALL_INPUT_SLOTS;
            case PICKAXE -> PICKAXE_INPUT_SLOTS;
            case SHOVEL -> SHOVEL_INPUT_SLOTS;
            case AXE -> AXE_INPUT_SLOTS;
        };
    }

    private static void validateInputSlot(int inputSlot) {
        if (inputSlot < 0 || inputSlot >= INPUT_SLOT_COUNT) {
            throw new IndexOutOfBoundsException("Invalid tool station input slot: " + inputSlot);
        }
    }

    public record SlotPosition(int x, int y) {
        int slotX() {
            return this.x + SLOT_ITEM_OFFSET;
        }

        int slotY() {
            return this.y + SLOT_ITEM_OFFSET;
        }
    }
}
