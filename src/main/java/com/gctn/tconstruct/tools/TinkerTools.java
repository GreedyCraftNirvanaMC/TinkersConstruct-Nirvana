package com.gctn.tconstruct.tools;

import com.gctn.tconstruct.TinkersConstructNirvana;
import com.gctn.tconstruct.library.materials.Material;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Collection;
import java.util.List;

public abstract class TinkerTools extends Item {
    public static final DeferredRegister.Items ITEMS =
            DeferredRegister.createItems(TinkersConstructNirvana.MODID);

    public TinkerTools() {
        super(new Item.Properties().stacksTo(1));
    }

    public Collection<RepairInfo> getRepairInfo(ItemStack stack) {
        return List.of();
    }

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }

    public record RepairInfo(Material material, float partEfficient, int durability) {
    }
}
