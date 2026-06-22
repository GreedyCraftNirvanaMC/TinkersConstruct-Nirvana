package com.gctn.tconstruct.common.tables.client;

import com.gctn.tconstruct.TinkersConstructNirvana;
import com.gctn.tconstruct.common.tables.ToolStationMenu;
import com.gctn.tconstruct.common.tables.ToolStationMenu.Mode;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@OnlyIn(Dist.CLIENT)
public class ToolStationScreen extends AbstractContainerScreen<ToolStationMenu> {
    private static final ResourceLocation BACKGROUND_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(TinkersConstructNirvana.MODID, "textures/gui/toolstation.png");
    private static final ResourceLocation ICON_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(TinkersConstructNirvana.MODID, "textures/gui/icons.png");

    private static final int MODE_BUTTON_X = -108;
    private static final int MODE_BUTTON_Y = 10;
    private static final int MODE_BUTTON_SPACING= 22;
    private static final int MODE_BUTTON_SIZE = 18;
    private static final int MODE_BUTTON_TEXTURE_X = 180;
    private static final int ACTIVE_MODE_BUTTON_TEXTURE_X = 144;
    private static final int MODE_BUTTON_TEXTURE_Y = 180;

    private final List<ModeButton> modeButtons = new ArrayList<>();

    public ToolStationScreen(ToolStationMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 176;
        this.imageHeight = 174;
        this.titleLabelX = 8;
        this.titleLabelY = 6;
        this.inventoryLabelX = 8;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    protected void init() {
        super.init();
        this.modeButtons.clear();
        this.addModeButton(Mode.DEFAULT, ICON_TEXTURE, 55, 1, 16, 16, 0);
        this.addModeButton(Mode.DISASSEMBLE, new ItemStack(Items.SHEARS), 1);
        this.addModeButton(Mode.PICKAXE, new ItemStack(Items.WOODEN_PICKAXE), 2);
        this.addModeButton(Mode.SHOVEL, new ItemStack(Items.WOODEN_SHOVEL), 3);
        this.addModeButton(Mode.AXE, new ItemStack(Items.WOODEN_AXE), 4);
        this.addModeButton(Mode.AXE, new ItemStack(Items.WOODEN_AXE), 5);
        this.addModeButton(Mode.AXE, new ItemStack(Items.WOODEN_AXE), 6);
        this.addModeButton(Mode.AXE, new ItemStack(Items.WOODEN_AXE), 7);
        this.addModeButton(Mode.AXE, new ItemStack(Items.WOODEN_AXE), 8);
        this.updateModeButtons();
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.updateModeButtons();
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        guiGraphics.blit(BACKGROUND_TEXTURE, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
        guiGraphics.blit(BACKGROUND_TEXTURE, this.leftPos - 110, this.topPos, 0, 180, 108, 7);
        guiGraphics.blit(BACKGROUND_TEXTURE, this.leftPos - 2, this.topPos, 131, 180, 2, 7);
        guiGraphics.blit(BACKGROUND_TEXTURE, this.leftPos + this.imageWidth, this.topPos, 0, 180, 133, 7);
        renderItemSlot(guiGraphics, 32, 41);
        renderItemSlot(guiGraphics, 14, 61);
        renderItemSlot(guiGraphics, 50, 61);
        renderItemSlot(guiGraphics, 10, 36);
        renderItemSlot(guiGraphics, 54, 36);
        renderItemSlot(guiGraphics, 32, 18);
        if (this.menu.hasToolSlotItem()) {
            guiGraphics.blit(BACKGROUND_TEXTURE, this.leftPos + 104, this.topPos + 38, 0, 241, 8, 15);
        }
    }

    private void renderItemSlot(GuiGraphics guiGraphics, int x, int y) {
        guiGraphics.blit(ICON_TEXTURE, this.leftPos + x, this.topPos + y, 144, 216, 18, 18);
    }

    private void addModeButton(Mode mode, ItemStack icon, int index) {
        int x = this.leftPos + MODE_BUTTON_X + index % 5 * MODE_BUTTON_SPACING;
        int y = this.topPos + MODE_BUTTON_Y + (index / 5) * MODE_BUTTON_SPACING;
        ModeButton button = new ModeButton(x, y, mode, icon);
        this.modeButtons.add(this.addRenderableWidget(button));
    }

    private void addModeButton(Mode mode, ResourceLocation resourceLocation, int uOffset, int vOffset, int uWidth, int vHeight, int index) {
        int x = this.leftPos + MODE_BUTTON_X + index % 5 * MODE_BUTTON_SPACING;
        int y = this.topPos + MODE_BUTTON_Y + (index / 5) * MODE_BUTTON_SPACING;
        ModeButton button = new ModeButton(x, y, mode, resourceLocation, uOffset, vOffset, uWidth, vHeight);
        this.modeButtons.add(this.addRenderableWidget(button));
    }

    private void setMode(int buttonId) {
        if (this.minecraft == null || this.minecraft.gameMode == null || this.minecraft.player == null) {
            return;
        }
        this.menu.clickMenuButton(this.minecraft.player, buttonId);
        this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, buttonId);
        this.updateModeButtons();
    }

    private void updateModeButtons() {
        for (ModeButton button : this.modeButtons) {
            button.active = !this.menu.isMode(button.mode);
        }
    }

    private class ModeButton extends AbstractButton {
        private final Mode mode;
        private final ItemStack icon;
        private final ResourceLocation iconTexture;
        private final int iconU;
        private final int iconV;
        private final int iconWidth;
        private final int iconHeight;

        ModeButton(int x, int y, Mode mode, ItemStack icon) {
            super(x, y, MODE_BUTTON_SIZE, MODE_BUTTON_SIZE, Component.translatable(
                    "container.tconstruct.toolstation.mode." + mode.name().toLowerCase(Locale.ROOT)));
            this.mode = mode;
            this.icon = icon;
            this.iconTexture = null;
            this.iconU = 0;
            this.iconV = 0;
            this.iconWidth = 0;
            this.iconHeight = 0;
        }

        ModeButton(int x, int y, Mode mode, ResourceLocation iconTexture, int iconU, int iconV, int iconWidth, int iconHeight) {
            super(x, y, MODE_BUTTON_SIZE, MODE_BUTTON_SIZE, Component.translatable(
                    "container.tconstruct.toolstation.mode." + mode.name().toLowerCase(Locale.ROOT)));
            this.mode = mode;
            this.icon = ItemStack.EMPTY;
            this.iconTexture = iconTexture;
            this.iconU = iconU;
            this.iconV = iconV;
            this.iconWidth = iconWidth;
            this.iconHeight = iconHeight;
        }

        @Override
        public void onPress() {
            ToolStationScreen.this.setMode(this.mode.getButtonId());
        }

        @Override
        protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
            int textureX = ToolStationScreen.this.menu.isMode(this.mode)
                    ? ACTIVE_MODE_BUTTON_TEXTURE_X
                    : MODE_BUTTON_TEXTURE_X;
            guiGraphics.blit(ICON_TEXTURE, this.getX(), this.getY(),
                    textureX, MODE_BUTTON_TEXTURE_Y, MODE_BUTTON_SIZE, MODE_BUTTON_SIZE);
            guiGraphics.blit(BACKGROUND_TEXTURE, this.getX() + 2, this.getY() - 4, 20, 174, 14, 4);
            if (this.iconTexture != null) {
                int iconX = this.getX() + (MODE_BUTTON_SIZE - this.iconWidth) / 2;
                int iconY = this.getY() + (MODE_BUTTON_SIZE - this.iconHeight) / 2;
                guiGraphics.blit(this.iconTexture, iconX, iconY, this.iconU, this.iconV, this.iconWidth, this.iconHeight);
            } else {
                guiGraphics.renderItem(this.icon, this.getX() + 1, this.getY() + 1);
            }
        }

        @Override
        protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
            this.defaultButtonNarrationText(narrationElementOutput);
        }
    }
}
