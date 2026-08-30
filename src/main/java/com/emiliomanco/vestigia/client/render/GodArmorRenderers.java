package com.emiliomanco.vestigia.client.render;

import com.emiliomanco.vestigia.Vestigia;
import com.geckolib.animatable.GeoAnimatable;
import com.geckolib.model.GeoModel;
import com.geckolib.renderer.GeoArmorRenderer;
import com.geckolib.renderer.GeoItemRenderer;
import com.geckolib.renderer.base.GeoRenderState;
import net.minecraft.resources.Identifier;

public final class GodArmorRenderers {
    private GodArmorRenderers() {}

    public static GeoArmorRenderer<?, ?> crownOfPachamama() {
        return new GeoArmorRenderer<>(new ArmorModel<>("corona_pachamama"));
    }

    public static GeoArmorRenderer<?, ?> mantleOfKukulkan() {
        return new GeoArmorRenderer<>(new ArmorModel<>("manto_kukulkan"));
    }

    public static GeoItemRenderer<?> crownOfPachamamaInHand() {
        return new GeoItemRenderer<>(new HeldModel<>("corona_pachamama"));
    }

    public static GeoItemRenderer<?> mantleOfKukulkanInHand() {
        return new GeoItemRenderer<>(new HeldModel<>("manto_kukulkan"));
    }

    public static GeoArmorRenderer<?, ?> jadeMask() {
        return new GeoArmorRenderer<>(new ArmorModel<>("jade_mask", JADE));
    }

    public static GeoItemRenderer<?> jadeMaskInHand() {
        return new GeoItemRenderer<>(new HeldModel<>("jade_mask", JADE));
    }

    private static final Identifier JADE = Vestigia.id("textures/item/jademask.png");

    public static GeoArmorRenderer<?, ?> otorongoHelm() {
        return new GeoArmorRenderer<>(new ArmorModel<>("otorongo_helm", "otorongohelm"));
    }

    public static GeoItemRenderer<?> otorongoHelmInHand() {
        return new GeoItemRenderer<>(new HeldModel<>("otorongo_helm", OTORONGO_HIDE));
    }

    private static final Identifier OTORONGO_HIDE =
            Vestigia.id("textures/entity/equipment/humanoid/otorongohelm.png");

    private static final class ArmorModel<T extends GeoAnimatable> extends GeoModel<T> {
        private final Identifier model;
        private final Identifier texture;

        ArmorModel(String name) {
            this(name, name);
        }

        ArmorModel(String name, Identifier texture) {
            this.model = Vestigia.id("armor/" + name);
            this.texture = texture;
        }

        ArmorModel(String name, String textureName) {
            this.model = Vestigia.id("armor/" + name);
            this.texture = Vestigia.id("textures/entity/equipment/humanoid/" + textureName + ".png");
        }

        @Override
        public Identifier getModelResource(GeoRenderState state) {
            return model;
        }

        @Override
        public Identifier getTextureResource(GeoRenderState state) {
            return texture;
        }

        @Override
        public Identifier getAnimationResource(T animatable) {
            return model;
        }
    }

    private static final class HeldModel<T extends GeoAnimatable> extends GeoModel<T> {
        private final Identifier model;
        private final Identifier texture;

        HeldModel(String name) {
            this(name, Vestigia.id("textures/item/" + name + ".png"));
        }

        HeldModel(String name, Identifier texture) {
            this.model = Vestigia.id("item/" + name + "_item");
            this.texture = texture;
        }

        @Override
        public Identifier getModelResource(GeoRenderState state) {
            return model;
        }

        @Override
        public Identifier getTextureResource(GeoRenderState state) {
            return texture;
        }

        @Override
        public Identifier getAnimationResource(T animatable) {
            return model;
        }
    }
}
