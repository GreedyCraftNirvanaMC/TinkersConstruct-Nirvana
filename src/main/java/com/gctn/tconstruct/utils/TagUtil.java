package com.gctn.tconstruct.utils;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

public final class TagUtil {

    // vpublic static int TAG_TYPE_STRING = (new NBTTagString()).getId();
    // public static int TAG_TYPE_COMPOUND = (new CompoundTag()).getId();

    private TagUtil() {
    }

    /* Generic Tag Operations */
    public static CompoundTag getTagSafe(ItemStack stack) {
        // yes, the null checks aren't needed anymore, but they don't hurt either.
        // After all the whole purpose of this function is safety/processing possibly invalid input ;)
        if(stack == null || stack.getItem() == null || stack.isEmpty()) {
            return new CompoundTag();
        }
        // 检查是否有 CustomData 组件
        if (!stack.has(DataComponents.CUSTOM_DATA)) {
            return new CompoundTag();
        }

        CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
       /*
        // 使用一个临时变量来捕获 Tag
        final Tag[] result = new Tag[1];

        // 读取 Tag，检查是否是 CompoundTag
        customData.read(tag -> {
            if (tag instanceof CompoundTag) {
                result[0] = tag;
            } else {
                // 如果不是 CompoundTag，返回空的 CompoundTag
                result[0] = new CompoundTag();
            }
        });

        // 如果没有读取到（理论上不会发生），返回空
        if (result[0] == null) {
            return new CompoundTag();
        }
        */

        if (customData != null) {
            return customData.copyTag();
        }else{
            return new CompoundTag();
        }
    }

    public static CompoundTag getTagSafe(CompoundTag tag, String key) {
        if(tag == null) {
            return new CompoundTag();
        }

        return tag.getCompound(key);
    }
    
    /*
    public static NBTTagList getTagListSafe(CompoundTag tag, String key, int type) {
        if(tag == null) {
            return new NBTTagList();
        }

        return tag.getTagList(key, type);
    }
    
     */

    /* Operations concerning the base-data of the tool */
    /*
    public static CompoundTag getBaseTag(ItemStack stack) {
        return getBaseTag(getTagSafe(stack));
    }

    public static CompoundTag getBaseTag(CompoundTag root) {
        return getTagSafe(root, Tags.BASE_DATA);
    }

    public static void setBaseTag(ItemStack stack, CompoundTag tag) {
        CompoundTag root = TagUtil.getTagSafe(stack);
        setBaseTag(root, tag);

        stack.setTagCompound(root);
    }

    public static void setBaseTag(CompoundTag root, CompoundTag tag) {
        if(root != null) {
            root.setTag(Tags.BASE_DATA, tag);
        }
    }


    public static NBTTagList getBaseModifiersTagList(ItemStack stack) {
        return getBaseModifiersTagList(getTagSafe(stack));
    }

    public static NBTTagList getBaseModifiersTagList(CompoundTag root) {
        return getTagListSafe(getBaseTag(root), Tags.BASE_MODIFIERS, TAG_TYPE_STRING);
    }

    public static void setBaseModifiersTagList(ItemStack stack, NBTTagList tagList) {
        CompoundTag root = TagUtil.getTagSafe(stack);
        setBaseModifiersTagList(root, tagList);

        stack.setTagCompound(root);
    }

    public static void setBaseModifiersTagList(CompoundTag root, NBTTagList tagList) {
        CompoundTag baseTag = getBaseTag(root);
        baseTag.setTag(Tags.BASE_MODIFIERS, tagList);
        setBaseTag(root, baseTag);
    }

    public static NBTTagList getBaseMaterialsTagList(ItemStack stack) {
        return getBaseMaterialsTagList(getTagSafe(stack));
    }

    public static NBTTagList getBaseMaterialsTagList(CompoundTag root) {
        return getTagListSafe(getBaseTag(root), Tags.BASE_MATERIALS, TAG_TYPE_STRING);
    }

    public static void setBaseMaterialsTagList(ItemStack stack, NBTTagList tagList) {
        CompoundTag root = TagUtil.getTagSafe(stack);
        setBaseMaterialsTagList(root, tagList);

        stack.setTagCompound(root);
    }

    public static void setBaseMaterialsTagList(CompoundTag root, NBTTagList tagList) {
        getBaseTag(root).setTag(Tags.BASE_MATERIALS, tagList);
    }

    public static int getBaseModifiersUsed(CompoundTag root) {
        return getBaseTag(root).getInteger(Tags.BASE_USED_MODIFIERS);
    }

    public static void setBaseModifiersUsed(CompoundTag root, int count) {
        getBaseTag(root).setInteger(Tags.BASE_USED_MODIFIERS, count);
    }
    
     */

    /* Operations concerning the calculated tool data */
    /*
    public static CompoundTag getToolTag(ItemStack stack) {
        return getToolTag(getTagSafe(stack));
    }

    public static CompoundTag getToolTag(CompoundTag root) {
        return getTagSafe(root, Tags.TOOL_DATA);
    }

    public static void setToolTag(ItemStack stack, CompoundTag tag) {
        CompoundTag root = TagUtil.getTagSafe(stack);
        setToolTag(root, tag);

        stack.setTagCompound(root);
    }

    public static void setToolTag(CompoundTag root, CompoundTag tag) {
        if(root != null) {
            root.setTag(Tags.TOOL_DATA, tag);
        }
    }
    
     */

    /* Operations concerning the data of modifiers */
    /*
    public static NBTTagList getModifiersTagList(ItemStack stack) {
        return getModifiersTagList(getTagSafe(stack));
    }

    public static NBTTagList getModifiersTagList(CompoundTag root) {
        return getTagListSafe(root, Tags.TOOL_MODIFIERS, TAG_TYPE_COMPOUND);
    }

    public static void setModifiersTagList(ItemStack stack, NBTTagList tagList) {
        CompoundTag root = TagUtil.getTagSafe(stack);
        setModifiersTagList(root, tagList);

        stack.setTagCompound(root);
    }

    public static void setModifiersTagList(CompoundTag root, NBTTagList tagList) {
        if(root != null) {
            root.setTag(Tags.TOOL_MODIFIERS, tagList);
        }
    }
    
     */

    /* Operations concerning the list of traits present on the tool */
    /*
    public static NBTTagList getTraitsTagList(ItemStack stack) {
        return getTraitsTagList(getTagSafe(stack));
    }

    public static NBTTagList getTraitsTagList(CompoundTag root) {
        return getTagListSafe(root, Tags.TOOL_TRAITS, TAG_TYPE_STRING);
    }

    public static void setTraitsTagList(ItemStack stack, NBTTagList tagList) {
        CompoundTag root = TagUtil.getTagSafe(stack);
        setTraitsTagList(root, tagList);

        stack.setTagCompound(root);
    }

    public static void setTraitsTagList(CompoundTag root, NBTTagList tagList) {
        if(root != null) {
            root.setTag(Tags.TOOL_TRAITS, tagList);
        }
    }
    
     */

    /* Tool stats */
    /*
    public static ToolNBT getToolStats(ItemStack stack) {
        return getToolStats(getTagSafe(stack));
    }

    public static ToolNBT getToolStats(CompoundTag root) {
        return new ToolNBT(getToolTag(root));
    }

    public static ToolNBT getOriginalToolStats(ItemStack stack) {
        return getOriginalToolStats(getTagSafe(stack));
    }

    public static ToolNBT getOriginalToolStats(CompoundTag root) {
        return new ToolNBT(getTagSafe(root, Tags.TOOL_DATA_ORIG));
    }
    
     */
    
    /* Extra data */
    /*
    public static CompoundTag getExtraTag(ItemStack stack) {
        return getExtraTag(getTagSafe(stack));
    }

    public static CompoundTag getExtraTag(CompoundTag root) {
        return getTagSafe(root, Tags.TINKER_EXTRA);
    }

    public static void setExtraTag(ItemStack stack, CompoundTag tag) {
        CompoundTag root = getTagSafe(stack);
        setExtraTag(root, tag);
        stack.setTagCompound(root);
    }

    public static void setExtraTag(CompoundTag root, CompoundTag tag) {
        root.setTag(Tags.TINKER_EXTRA, tag);
    }

    public static Category[] getCategories(CompoundTag root) {
        NBTTagList categories = getTagListSafe(getExtraTag(root), Tags.EXTRA_CATEGORIES, 8);
        Category[] out = new Category[categories.tagCount()];
        for(int i = 0; i < out.length; i++) {
            out[i] = Category.categories.get(categories.getStringTagAt(i));
        }

        return out;
    }

    public static void setCategories(ItemStack stack, Category[] categories) {
        CompoundTag root = getTagSafe(stack);
        setCategories(root, categories);
        stack.setTagCompound(root);
    }

    public static void setCategories(CompoundTag root, Category[] categories) {
        NBTTagList list = new NBTTagList();
        for(Category category : categories) {
            list.appendTag(new NBTTagString(category.name));
        }

        CompoundTag extra = getExtraTag(root);
        extra.setTag(Tags.EXTRA_CATEGORIES, list);
        setExtraTag(root, extra);
    }

    public static void setEnchantEffect(ItemStack stack, boolean active) {
        CompoundTag root = getTagSafe(stack);
        setEnchantEffect(root, active);
        stack.setTagCompound(root);
    }

    public static void setEnchantEffect(CompoundTag root, boolean active) {
        if(active) {
            root.setBoolean(Tags.ENCHANT_EFFECT, true);
        }
        else {
            root.removeTag(Tags.ENCHANT_EFFECT);
        }
    }

    public static boolean hasEnchantEffect(ItemStack stack) {
        return hasEnchantEffect(getTagSafe(stack));
    }

    public static boolean hasEnchantEffect(CompoundTag root) {
        return root.getBoolean(Tags.ENCHANT_EFFECT);
    }

    public static void setResetFlag(ItemStack stack, boolean active) {
        CompoundTag root = getTagSafe(stack);
        root.setBoolean(Tags.RESET_FLAG, active);
        stack.setTagCompound(root);
    }

    public static boolean getResetFlag(ItemStack stack) {
        return getTagSafe(stack).getBoolean(Tags.RESET_FLAG);
    }

    public static void setNoRenameFlag(ItemStack stack, boolean active) {
        CompoundTag root = getTagSafe(stack);
        setNoRenameFlag(root, active);
        stack.setTagCompound(root);
    }

    public static void setNoRenameFlag(CompoundTag root, boolean active) {
        CompoundTag displayTag = root.getCompoundTag("display");
        if(displayTag.hasKey("Name")) {
            displayTag.setBoolean(Tags.NO_RENAME, active);
            root.setTag("display", displayTag);
        }
    }

    public static boolean getNoRenameFlag(ItemStack stack) {
        CompoundTag root = getTagSafe(stack);
        CompoundTag displayTag = root.getCompoundTag("display");
        return displayTag.getBoolean(Tags.NO_RENAME);
    }
    
     */

    /* Helper functions */
    /*
    public static CompoundTag writePos(BlockPos pos) {
        CompoundTag tag = new CompoundTag();
        if(pos != null) {
            tag.setInteger("X", pos.getX());
            tag.setInteger("Y", pos.getY());
            tag.setInteger("Z", pos.getZ());
        }
        return tag;
    }

    public static BlockPos readPos(CompoundTag tag) {
        if(tag != null) {
            return new BlockPos(tag.getInteger("X"), tag.getInteger("Y"), tag.getInteger("Z"));
        }
        return null;
    }
    
     */
}