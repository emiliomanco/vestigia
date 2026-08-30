package com.emiliomanco.vestigia.client.animation;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.util.Mth;

public final class PinnedDownAnimation {
    private PinnedDownAnimation() {}

    private static final float[] TIMES = {0.0F, 5.0F, 10.0F, 15.0F};

    public static final float LENGTH_TICKS = 15.0F;

    private static final float[][] HEAD = {
            {37.5F, 0.0F, 0.0F},
            {33.3622F, -21.4895F, -17.5836F},
            {29.0356F, 38.2109F, 21.077F},
            {37.5F, 0.0F, 0.0F},
    };

    private static final float[][] RIGHT_ARM = {
            {0.0F, 0.0F, 25.0F},
            {-154.8407F, 21.2842F, -12.6979F},
            {-24.8407F, 21.2842F, -12.6979F},
            {0.0F, 0.0F, 25.0F},
    };

    private static final float[][] LEFT_ARM = {
            {0.0F, 0.0F, -22.5F},
            {-139.2915F, -21.6346F, 12.0027F},
            {-51.7915F, -21.6346F, 12.0027F},
            {0.0F, 0.0F, -22.5F},
    };

    private static final float[][] RIGHT_LEG = {
            {0.0F, 0.0F, 7.5F},
            {-10.0F, 0.0F, 7.5F},
            {7.5F, 0.0F, 7.5F},
            {0.0F, 0.0F, 7.5F},
    };

    private static final float[][] LEFT_LEG = {
            {0.0F, 0.0F, -5.0F},
            {-2.5F, 0.0F, -5.0F},
            {-15.0F, 0.0F, -5.0F},
            {0.0F, 0.0F, -5.0F},
    };

    public static final float BODY_PITCH_DEGREES = -90.0F;

    public static final float BODY_OFFSET_BLOCKS = -10.0F / 16.0F;

    public static void apply(PlayerModel model, float ageInTicks) {
        float time = ageInTicks % LENGTH_TICKS;

        applyRotation(model.head, HEAD, time);
        applyRotation(model.rightArm, RIGHT_ARM, time);
        applyRotation(model.leftArm, LEFT_ARM, time);
        applyRotation(model.rightLeg, RIGHT_LEG, time);
        applyRotation(model.leftLeg, LEFT_LEG, time);

    }

    private static void applyRotation(ModelPart part, float[][] track, float time) {
        int next = 1;
        while (next < TIMES.length - 1 && TIMES[next] < time) {
            next++;
        }
        int previous = next - 1;
        float span = TIMES[next] - TIMES[previous];
        float progress = span <= 0.0F ? 0.0F : Mth.clamp((time - TIMES[previous]) / span, 0.0F, 1.0F);

        part.xRot = radians(Mth.lerp(progress, track[previous][0], track[next][0]));
        part.yRot = radians(Mth.lerp(progress, track[previous][1], track[next][1]));
        part.zRot = radians(Mth.lerp(progress, track[previous][2], track[next][2]));
    }

    private static float radians(float degrees) {
        return degrees * ((float) Math.PI / 180.0F);
    }
}
