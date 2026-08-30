package com.emiliomanco.vestigia.client.render;

import com.emiliomanco.vestigia.Vestigia;
import com.emiliomanco.vestigia.client.ClientStasis;
import com.emiliomanco.vestigia.entity.PosedHumanoid;
import com.emiliomanco.vestigia.entity.guardian.TempleLord;
import com.geckolib.constant.DataTickets;
import com.geckolib.constant.dataticket.DataTicket;
import com.geckolib.model.GeoModel;
import com.geckolib.renderer.GeoEntityRenderer;
import com.geckolib.renderer.base.BoneSnapshots;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.renderer.base.GeoRenderer;
import com.geckolib.renderer.base.RenderPassInfo;
import com.geckolib.cache.model.GeoBone;
import com.geckolib.renderer.layer.GeoRenderLayer;
import com.geckolib.renderer.layer.builtin.ItemInHandGeoLayer;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemDisplayContext;

public class AncestralHumanoidRenderer<T extends Mob & PosedHumanoid>
        extends GeoEntityRenderer<T, LivingEntityRenderState> {

    public static <T extends Mob & PosedHumanoid> AncestralHumanoidRenderer<T> soldier(
            EntityRendererProvider.Context context) {
        return new AncestralHumanoidRenderer<>(context, Vestigia.id("entity/mayan_soldier"),
                Vestigia.id("textures/entity/mayan_soldier.png"),
                Vestigia.id("entity/playerspear"), 1.0F, HUMAN_HAND, HUMAN_WEAPON);
    }

    public static <T extends Mob & PosedHumanoid> AncestralHumanoidRenderer<T> zombie(
            EntityRendererProvider.Context context) {
        return new AncestralHumanoidRenderer<>(context, Vestigia.id("entity/mayan_zombie"),
                Vestigia.id("textures/entity/mayan_zombie.png"),
                Vestigia.id("entity/mayan_zombie"), 1.0F, HUMAN_HAND, HUMAN_WEAPON);
    }

    public static <T extends Mob & PosedHumanoid> AncestralHumanoidRenderer<T> shaman(
            EntityRendererProvider.Context context) {
        return new AncestralHumanoidRenderer<>(context, Vestigia.id("entity/mayan_chaman"),
                Vestigia.id("textures/entity/mayan_chaman.png"),
                Vestigia.id("entity/mayan_chaman"), 1.0F, HUMAN_HAND, HUMAN_WEAPON);
    }

    public static <T extends Mob & PosedHumanoid> AncestralHumanoidRenderer<T> boss(
            EntityRendererProvider.Context context) {
        return new AncestralHumanoidRenderer<>(context, Vestigia.id("entity/mayan_boss"),
                Vestigia.id("textures/entity/mayan_boss.png"),
                Vestigia.id("entity/mayan_boss"), 1.2F, LORD_HAND, LORD_WEAPON);
    }

    public static <T extends Mob & PosedHumanoid> AncestralHumanoidRenderer<T> incaSoldier(
            EntityRendererProvider.Context context) {
        return new AncestralHumanoidRenderer<>(context, Vestigia.id("entity/incan_warrior"),
                Vestigia.id("textures/entity/incan_warrior.png"),
                Vestigia.id("entity/playerspear"), 1.0F, HUMAN_HAND, HUMAN_WEAPON);
    }

    public static <T extends Mob & PosedHumanoid> AncestralHumanoidRenderer<T> incaPriest(
            EntityRendererProvider.Context context) {
        return new AncestralHumanoidRenderer<>(context, Vestigia.id("entity/incan_priest"),
                Vestigia.id("textures/entity/incan_priest.png"),
                Vestigia.id("entity/incan_priest"), 1.0F, HUMAN_HAND, HUMAN_WEAPON);
    }

    public static <T extends Mob & PosedHumanoid> AncestralHumanoidRenderer<T> incaBoss(
            EntityRendererProvider.Context context) {
        return new AncestralHumanoidRenderer<>(context, Vestigia.id("entity/incan_boss"),
                Vestigia.id("textures/entity/incan_boss.png"),
                Vestigia.id("entity/mayan_boss"), 1.2F, LORD_HAND, LORD_WEAPON);
    }

    private static final float HUMAN_HAND = 9.0F / 16.0F;
    private static final float LORD_HAND = 13.5F / 16.0F;

    private static final float HUMAN_WEAPON = 1.0F;
    private static final float LORD_WEAPON = 2.0F;

    @SuppressWarnings({"rawtypes", "unchecked"})
    private AncestralHumanoidRenderer(EntityRendererProvider.Context context, Identifier model,
                                 Identifier texture, Identifier animation, float scale,
                                 float handDrop, float weaponScale) {
        super(context, new AncestralModel<>(model, texture, animation));
        this.shadowRadius = 0.5F * scale;
        withScale(scale);
        withRenderLayer((GeoRenderLayer) new WeaponInHand(context, (GeoRenderer) this, handDrop, weaponScale));
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static final class WeaponInHand extends ItemInHandGeoLayer {
        private final float handDrop;
        private final float weaponScale;

        WeaponInHand(EntityRendererProvider.Context context, GeoRenderer renderer, float handDrop,
                     float weaponScale) {
            super(context, renderer, "right_arm", "left_arm");
            this.handDrop = handDrop;
            this.weaponScale = weaponScale;
        }

        @Override
        protected void submitItemStackRender(PoseStack poseStack, GeoBone bone,
                                             ItemStackRenderState item, ItemDisplayContext context,
                                             GeoRenderState state, SubmitNodeCollector collector,
                                             int light) {
            poseStack.pushPose();
            poseStack.translate(0.0F, -handDrop, 0.0F);
            poseStack.scale(weaponScale, weaponScale, weaponScale);
            super.submitItemStackRender(poseStack, bone, item, context, state, collector, light);
            poseStack.popPose();
        }
    }

    private record Pose(float headYaw, float headPitch, float walkPos, float walkSpeed,
                        float swing, float age, boolean armsDriven, boolean bodyDriven) {}

    private static final DataTicket<Pose> POSE = DataTicket.create("vestigia_soldier_pose", Pose.class);

    @Override
    public void addRenderData(T soldier, Void unused,
                              LivingEntityRenderState state, float partialTick) {
        super.addRenderData(soldier, unused, state, partialTick);
        float bodyYaw = Mth.rotLerp(partialTick, soldier.yBodyRotO, soldier.yBodyRot);
        float headYaw = Mth.rotLerp(partialTick, soldier.yHeadRotO, soldier.yHeadRot);

        ClientStasis.Snapshot frozen = ClientStasis.snapshotFor(soldier);
        float walkPos = frozen != null ? frozen.walkPos() : soldier.walkAnimation.position(partialTick);
        float walkSpeed = frozen != null ? frozen.walkSpeed() : soldier.walkAnimation.speed(partialTick);
        float age = frozen != null ? frozen.ageInTicks() : soldier.tickCount + partialTick;

        if (frozen != null) {
            ((GeoRenderState) state).addGeckolibData(DataTickets.TICK, (double) age);
        }

        Pose pose = new Pose(
                Mth.wrapDegrees(headYaw - bodyYaw),
                soldier.getXRot(partialTick),
                walkPos,
                walkSpeed,
                soldier.getAttackAnim(partialTick),
                age,
                soldier.armsDriven(),
                soldier.bodyDriven());
        ((GeoRenderState) state).addGeckolibData(POSE, pose);
        if (soldier instanceof TempleLord lord) {
            ((GeoRenderState) state).addGeckolibData(BossWarningIcon.WARNING, lord.warning());
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public void adjustModelBonesForRender(RenderPassInfo pass, BoneSnapshots bones) {
        super.adjustModelBonesForRender(pass, bones);
        Pose pose = ((GeoRenderState) pass.renderState()).getGeckolibData(POSE);
        if (pose == null) {
            return;
        }
        if (pose.bodyDriven()) {
            return;
        }

        bones.ifPresent("head", head -> head.setRotation(
                -pose.headPitch() * Mth.DEG_TO_RAD, -pose.headYaw() * Mth.DEG_TO_RAD, 0.0F));

        float phase = pose.walkPos() * 0.6662F;
        float speed = pose.walkSpeed();
        float rightArmX = Mth.cos(phase + Mth.PI) * 2.0F * speed * 0.5F;
        float leftArmX = Mth.cos(phase) * 2.0F * speed * 0.5F;

        bones.ifPresent("right_leg", leg -> leg.setRotation(
                -Mth.cos(phase) * 1.4F * speed, 0.0F, 0.0F));
        bones.ifPresent("left_leg", leg -> leg.setRotation(
                -Mth.cos(phase + Mth.PI) * 1.4F * speed, 0.0F, 0.0F));

        if (pose.armsDriven()) {
            return;
        }

        float swing = pose.swing();
        float torsoYaw = swing <= 0.0F ? 0.0F : Mth.sin(Mth.sqrt(swing) * Mth.TWO_PI) * 0.2F;
        bones.ifPresent("torso", torso -> torso.setRotation(0.0F, -torsoYaw, 0.0F));

        float rightArm = rightArmX;
        float leftArm = leftArmX;
        if (swing > 0.0F) {
            float eased = 1.0F - (1.0F - swing) * (1.0F - swing) * (1.0F - swing) * (1.0F - swing);
            float arc = Mth.sin(eased * Mth.PI) * 1.2F
                    + Mth.sin(swing * Mth.PI) * (pose.headPitch() * Mth.DEG_TO_RAD + 0.7F) * 0.75F;
            rightArm -= arc;
        }

        float armYaw = -torsoYaw;
        float finalRightArm = rightArm;
        float finalLeftArm = leftArm;
        bones.ifPresent("right_arm", arm -> arm.setRotation(-finalRightArm, armYaw, 0.0F));
        bones.ifPresent("left_arm", arm -> arm.setRotation(-finalLeftArm, armYaw, 0.0F));
    }

    private static final class AncestralModel<T extends PosedHumanoid> extends GeoModel<T> {
        private static final Identifier[] FALLBACKS = {
                Vestigia.id("entity/playerspear"), Vestigia.id("entity/blowgun")};

        private final Identifier model;
        private final Identifier texture;
        private final Identifier animation;

        AncestralModel(Identifier model, Identifier texture, Identifier animation) {
            this.model = model;
            this.texture = texture;
            this.animation = animation;
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
            return animation;
        }

        @Override
        public Identifier[] getAnimationResourceFallbacks(T animatable) {
            return FALLBACKS;
        }
    }
}
