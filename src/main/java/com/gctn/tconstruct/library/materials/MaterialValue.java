package com.gctn.tconstruct.library.materials;

public class MaterialValue {

    // 各种类型的材料值，以mB计，物品注册会转换
    public static final int VALUE_Ingot = 90;
    public static final int VALUE_Nugget = VALUE_Ingot / 9;
    public static final int VALUE_Fragment = VALUE_Ingot / 4;
    public static final int VALUE_Shard = VALUE_Ingot / 2;

    public static final int VALUE_Gem = 100; // divisible by 3!
    public static final int VALUE_Block = VALUE_Ingot * 9;

    public static final int VALUE_SearedBlock = VALUE_Ingot * 2;
    public static final int VALUE_SearedMaterial = VALUE_Ingot / 2;
    public static final int VALUE_Glass = 1000;

    public static final int VALUE_BrickBlock = VALUE_Ingot * 4;

    public static final int VALUE_SlimeBall = 250;

}
