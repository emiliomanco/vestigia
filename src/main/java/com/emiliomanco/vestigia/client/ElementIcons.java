package com.emiliomanco.vestigia.client;

import com.emiliomanco.vestigia.Vestigia;
import com.emiliomanco.vestigia.item.god.pachamama.BendingBranch;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;

public final class ElementIcons {
    private ElementIcons() {}

    public static final int SIZE = 32;

    public static final int HUD_SIZE = 16;

    public static final Identifier SELECTED = Vestigia.id("textures/gui/element/element_selected_outline.png");

    public static Identifier of(BendingBranch branch) {
        return Vestigia.id("textures/gui/element/" + branch.id() + ".png");
    }

    public static void draw(GuiGraphicsExtractor graphics, Identifier texture, int x, int y, int size) {
        graphics.blit(RenderPipelines.GUI_TEXTURED, texture, x, y, 0.0F, 0.0F,
                size, size, SIZE, SIZE, SIZE, SIZE);
    }
}
