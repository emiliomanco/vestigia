package com.emiliomanco.vestigia.client.render;

import com.emiliomanco.vestigia.entity.animal.Jaguar;
import com.emiliomanco.vestigia.registry.ModEntities;
import com.geckolib.model.DefaultedEntityGeoModel;
import com.geckolib.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;

public class JaguarRenderer extends GeoEntityRenderer<Jaguar, LivingEntityRenderState> {

    public JaguarRenderer(EntityRendererProvider.Context context) {
        super(context, new DefaultedEntityGeoModel<>(ModEntities.JAGUAR.get()));
        this.shadowRadius = 0.7F;
    }
}
