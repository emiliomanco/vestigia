package com.emiliomanco.vestigia.client.screen;

import com.emiliomanco.vestigia.Vestigia;
import com.emiliomanco.vestigia.menu.VestigeTableMenu;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

public class VestigeTableScreen extends AbstractContainerScreen<VestigeTableMenu> {

    private static final Identifier TEXTURE = Vestigia.id("textures/gui/container/vestige_table.png");

    private static final int PANEL_WIDTH = 176;
    private static final int PANEL_HEIGHT = 186;
    private static final int SHEET_SIZE = 256;

    private static final int RITUAL_TEXT_X = 11;
    private static final int RITUAL_TEXT_Y = 73;

    private static final int COLOR_LABEL = 0xFF404040;
    private static final int COLOR_RECOGNISED = 0xFF3F7A3F;
    private static final int COLOR_IDLE = 0xFF6A6A6A;

    public VestigeTableScreen(VestigeTableMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title, PANEL_WIDTH, PANEL_HEIGHT);
    }

    @Override
    protected void init() {
        super.init();
        titleLabelX = (imageWidth - font.width(title)) / 2;
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractBackground(graphics, mouseX, mouseY, partialTick);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, x, y, 0.0F, 0.0F,
                imageWidth, imageHeight, SHEET_SIZE, SHEET_SIZE);
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        super.extractLabels(graphics, mouseX, mouseY);
        graphics.text(font, ritualLine(), RITUAL_TEXT_X, RITUAL_TEXT_Y, ritualColor(), false);
    }

    private Component ritualLine() {
        return menu.hasRitual()
                ? Component.translatable("container.vestigia.vestige_table.recognised")
                : Component.translatable("container.vestigia.vestige_table.no_ritual")
                        .withStyle(ChatFormatting.ITALIC);
    }

    private int ritualColor() {
        return menu.hasRitual() ? COLOR_RECOGNISED : COLOR_IDLE;
    }
}
