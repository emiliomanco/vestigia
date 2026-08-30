package com.emiliomanco.vestigia.client.screen;

import com.emiliomanco.vestigia.client.ElementIcons;
import com.emiliomanco.vestigia.item.god.pachamama.BendingBranch;
import com.emiliomanco.vestigia.network.BendingPayloads;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

public class BranchChoiceScreen extends Screen {

    private static final int PADDING = 6;
    private static final int CELL = ElementIcons.SIZE + PADDING * 2;
    private static final int GAP = 8;

    public BranchChoiceScreen() {
        super(Component.translatable("screen.vestigia.branch_choice"));
    }

    @Override
    protected void init() {
        BendingBranch[] branches = BendingBranch.values();
        int total = branches.length * CELL + (branches.length - 1) * GAP;
        int left = width / 2 - total / 2;
        int top = height / 2 - CELL / 2;

        for (int index = 0; index < branches.length; index++) {
            BendingBranch branch = branches[index];
            addRenderableWidget(new ElementButton(
                    left + index * (CELL + GAP), top, branch, () -> choose(branch)));
        }
    }

    private void choose(BendingBranch branch) {
        ClientPacketDistributor.sendToServer(new BendingPayloads.ChooseBranch(branch));
        onClose();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private static final class ElementButton extends Button {

        private final BendingBranch branch;

        ElementButton(int x, int y, BendingBranch branch, Runnable onPress) {
            super(x, y, CELL, CELL,
                    Component.translatable(branch.nameKey()).withStyle(branch.colour()),
                    button -> onPress.run(), DEFAULT_NARRATION);
            this.branch = branch;
            setTooltip(Tooltip.create(Component.empty()
                    .append(Component.translatable(branch.nameKey()).withStyle(branch.colour()))
                    .append(Component.literal("\n"))
                    .append(Component.translatable(branch.descriptionKey()))));
        }

        @Override
        protected void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY,
                                       float partialTick) {
            int iconX = getX() + PADDING;
            int iconY = getY() + PADDING;
            ElementIcons.draw(graphics, ElementIcons.of(branch), iconX, iconY, ElementIcons.SIZE);
            if (isHoveredOrFocused()) {
                ElementIcons.draw(graphics, ElementIcons.SELECTED, iconX, iconY, ElementIcons.SIZE);
            }
        }
    }
}
