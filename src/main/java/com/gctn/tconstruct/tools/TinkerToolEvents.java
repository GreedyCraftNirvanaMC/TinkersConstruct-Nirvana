package com.gctn.tconstruct.tools;

import com.gctn.tconstruct.TinkersNirvana;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;

@EventBusSubscriber(modid = TinkersNirvana.MODID)
public final class TinkerToolEvents {
    private TinkerToolEvents() {
    }

    @SubscribeEvent
    public static void removeBrokenToolAttributes(ItemAttributeModifierEvent event) {
        ItemStack stack = event.getItemStack();
        if (stack.getItem() instanceof TinkerTools && TinkerTools.isBroken(stack)) {
            event.clearModifiers();
        }
    }
}
