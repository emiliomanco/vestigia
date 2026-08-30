package com.emiliomanco.vestigia.client.render;

import com.emiliomanco.vestigia.Vestigia;
import com.emiliomanco.vestigia.block.entity.VestigeTableBlockEntity;
import com.emiliomanco.vestigia.item.VestigeTableBlockItem;
import com.geckolib.animatable.GeoAnimatable;
import com.geckolib.model.GeoModel;
import com.geckolib.renderer.GeoBlockRenderer;
import com.geckolib.renderer.GeoItemRenderer;
import com.geckolib.renderer.base.GeoRenderState;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.resources.Identifier;

public final class VestigeTableRenderers {
    private VestigeTableRenderers() {}

    private static final Identifier MODEL = Vestigia.id("block/vestige_table");
    private static final Identifier TEXTURE = Vestigia.id("textures/block/vestige_table.png");
    private static final Identifier ANIMATION = Vestigia.id("block/vestige_table");

    public static GeoBlockRenderer<VestigeTableBlockEntity, BlockEntityRenderState> placed(
            BlockEntityRendererProvider.Context context) {
        return new GeoBlockRenderer<>(context, new TableModel<VestigeTableBlockEntity>());
    }

    public static GeoItemRenderer<VestigeTableBlockItem> held() {
        return new GeoItemRenderer<>(new TableModel<VestigeTableBlockItem>());
    }

    private static final class TableModel<T extends GeoAnimatable> extends GeoModel<T> {
        @Override
        public Identifier getModelResource(GeoRenderState state) {
            return MODEL;
        }

        @Override
        public Identifier getTextureResource(GeoRenderState state) {
            return TEXTURE;
        }

        @Override
        public Identifier getAnimationResource(T animatable) {
            return ANIMATION;
        }
    }
}
