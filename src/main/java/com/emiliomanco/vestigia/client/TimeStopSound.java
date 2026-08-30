package com.emiliomanco.vestigia.client;

import com.emiliomanco.vestigia.Vestigia;
import com.emiliomanco.vestigia.network.TimeStopSoundPayload;
import com.emiliomanco.vestigia.registry.ModSounds;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import org.jspecify.annotations.Nullable;

@EventBusSubscriber(modid = Vestigia.MODID, value = Dist.CLIENT)
public final class TimeStopSound {
    private TimeStopSound() {}

    private static @Nullable Instance current;

    public static void accept(TimeStopSoundPayload payload) {
        if (payload.active()) {
            start();
        } else {
            stopNow();
        }
    }

    private static void start() {
        if (current != null && !current.isStopped()) {
            return;
        }
        current = new Instance();
        Minecraft.getInstance().getSoundManager().play(current);
    }

    private static void stopNow() {
        if (current != null) {
            current.end();
            current = null;
        }
    }

    @SubscribeEvent
    static void onLoggingOut(ClientPlayerNetworkEvent.LoggingOut event) {
        stopNow();
    }

    private static final class Instance extends AbstractTickableSoundInstance {

        Instance() {
            super(ModSounds.VIRACOCHA_WHILE_TIME_STOPPED.get(), SoundSource.PLAYERS,
                    RandomSource.create());
            this.looping = true;
            this.delay = 0;
            this.volume = 0.56F;
            this.pitch = 1.0F;
            this.relative = true;
            this.x = 0.0D;
            this.y = 0.0D;
            this.z = 0.0D;
        }

        @Override
        public void tick() {
            if (Minecraft.getInstance().player == null) {
                end();
            }
        }

        void end() {
            stop();
        }
    }
}
