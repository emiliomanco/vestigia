package com.emiliomanco.vestigia.registry;

import com.emiliomanco.vestigia.Vestigia;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModSounds {
    private ModSounds() {}

    public static final DeferredRegister<SoundEvent> SOUNDS =
            DeferredRegister.create(Registries.SOUND_EVENT, Vestigia.MODID);

    public static final DeferredHolder<SoundEvent, SoundEvent> JAGUAR_CHASE = register("entity.jaguar.chase");
    public static final DeferredHolder<SoundEvent, SoundEvent> JAGUAR_ATTACK = register("entity.jaguar.attack");
    public static final DeferredHolder<SoundEvent, SoundEvent> JAGUAR_POUNCE = register("entity.jaguar.pounce");

    public static final DeferredHolder<SoundEvent, SoundEvent> BOSS_STOMP = register("entity.mayan_nacom.stomp");

    public static final DeferredHolder<SoundEvent, SoundEvent> MAYA_BATTLECRY = register("entity.mayan_warrior.battlecry");
    public static final DeferredHolder<SoundEvent, SoundEvent> MAYA_TAUNT = register("entity.mayan_warrior.taunt");
    public static final DeferredHolder<SoundEvent, SoundEvent> MAYA_FLUTE = register("entity.mayan_warrior.flute");

    public static final DeferredHolder<SoundEvent, SoundEvent> SHAMAN_CURSE = register("entity.mayan_shaman.curse");

    public static final DeferredHolder<SoundEvent, SoundEvent> BLOWGUN_SHOOT = register("item.blowgun.shoot");

    public static final DeferredHolder<SoundEvent, SoundEvent> SPEAR_THROW = register("item.obsidian_spear.throw");
    public static final DeferredHolder<SoundEvent, SoundEvent> SPEAR_IMPACT = register("item.obsidian_spear.impact");

    public static final DeferredHolder<SoundEvent, SoundEvent> SUN_DISC_THROW = register("item.sun_disc.throw");
    public static final DeferredHolder<SoundEvent, SoundEvent> SUN_DISC_CATCH = register("item.sun_disc.catch");

    public static final DeferredHolder<SoundEvent, SoundEvent> LUNAR_MIRROR_USE = register("item.lunar_mirror.use");

    public static final DeferredHolder<SoundEvent, SoundEvent> VIRACOCHA_STOP_TIME = register("item.viracocha_staff.stop_time");
    public static final DeferredHolder<SoundEvent, SoundEvent> VIRACOCHA_WHILE_TIME_STOPPED = register("item.viracocha_staff.while_time_stopped");
    public static final DeferredHolder<SoundEvent, SoundEvent> VIRACOCHA_RESUME_TIME = register("item.viracocha_staff.resume_time");

    public static final DeferredHolder<SoundEvent, SoundEvent> ANTARA_CALM = register("item.antara.calm");
    public static final DeferredHolder<SoundEvent, SoundEvent> ANTARA_RAIN = register("item.antara.rain");
    public static final DeferredHolder<SoundEvent, SoundEvent> ANTARA_REVEAL = register("item.antara.reveal");
    public static final DeferredHolder<SoundEvent, SoundEvent> ANTARA_BREATH = register("item.antara.breath");
    public static final DeferredHolder<SoundEvent, SoundEvent> ANTARA_MOURNING = register("item.antara.mourning");

    public static final DeferredHolder<SoundEvent, SoundEvent> VESTIGE_TABLE_READY = register("block.vestige_table.ready");
    public static final DeferredHolder<SoundEvent, SoundEvent> VESTIGE_TABLE_CRAFT_ANIMAL = register("block.vestige_table.craft_animal");
    public static final DeferredHolder<SoundEvent, SoundEvent> VESTIGE_TABLE_CRAFT_GOD = register("block.vestige_table.craft_god");

    public static final DeferredHolder<SoundEvent, SoundEvent> ECLIPSE_BEGIN = register("event.eclipse.begin");
    public static final DeferredHolder<SoundEvent, SoundEvent> ECLIPSE_END = register("event.eclipse.end");
    public static final DeferredHolder<SoundEvent, SoundEvent> WAKA_NIGHT_BEGIN = register("event.waka_night.begin");

    private static DeferredHolder<SoundEvent, SoundEvent> register(String path) {
        return SOUNDS.register(path, () -> SoundEvent.createVariableRangeEvent(Vestigia.id(path)));
    }

    public static void register(IEventBus modEventBus) {
        SOUNDS.register(modEventBus);
    }
}
