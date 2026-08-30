package com.emiliomanco.vestigia.entity.guardian;

import com.emiliomanco.vestigia.registry.ModSounds;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import org.jspecify.annotations.Nullable;

public final class MayaVoice {
    private MayaVoice() {}

    public static final int SPEECH_INTERVAL = 200;

    public static SoundEvent idle(@Nullable LivingEntity target) {
        return target == null ? ModSounds.MAYA_FLUTE.get() : ModSounds.MAYA_TAUNT.get();
    }

    public static SoundEvent battlecry() {
        return ModSounds.MAYA_BATTLECRY.get();
    }

    public static @Nullable SoundEvent hurt() {
        return null;
    }

    public static @Nullable SoundEvent death() {
        return null;
    }
}
