package com.emiliomanco.vestigia.client.data;

import com.emiliomanco.vestigia.Vestigia;
import com.emiliomanco.vestigia.registry.ModSounds;
import net.minecraft.data.PackOutput;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.common.data.SoundDefinition;
import net.neoforged.neoforge.common.data.SoundDefinitionsProvider;
import net.neoforged.neoforge.registries.DeferredHolder;

public final class ModSoundDefinitionsProvider extends SoundDefinitionsProvider {

    public ModSoundDefinitionsProvider(PackOutput output) {
        super(output, Vestigia.MODID);
    }

    @Override
    public void registerSounds() {
        pool(ModSounds.BOSS_STOMP, "boss/stomp_boss");

        pool(ModSounds.JAGUAR_CHASE, "jaguar/jaguargrowl_chase");
        pool(ModSounds.JAGUAR_ATTACK, "jaguar/jaguargrowl_attack");
        pool(ModSounds.JAGUAR_POUNCE, "jaguar/jaguargrowl_jump");

        pool(ModSounds.MAYA_BATTLECRY,
                "maya/battlecry1_maya", "maya/battlecry2_maya", "maya/battlecry3_maya");
        pool(ModSounds.MAYA_TAUNT,
                "maya/verybrave_maya1", "maya/mypeople_maya2",
                "maya/mebigwarrior_maya3", "maya/verystrong_maya4");
        pool(ModSounds.MAYA_FLUTE,
                "maya/flute1_maya", "maya/flute2_maya", "maya/flute3_maya");
        pool(ModSounds.SHAMAN_CURSE, "chaman/chaman_curse");

        pool(ModSounds.BLOWGUN_SHOOT,
                "items/blowgun/blowgun1", "items/blowgun/blowgun2", "items/blowgun/blowgun3");
        pool(ModSounds.SPEAR_THROW,
                "items/spear/spear_throw1", "items/spear/spear_throw2", "items/spear/spear_throw3");
        pool(ModSounds.SPEAR_IMPACT, "items/spear/spear_impact");

        pool(ModSounds.SUN_DISC_THROW, "items/sundisc/disc_throw");
        pool(ModSounds.SUN_DISC_CATCH, "items/sundisc/catch_disc");
        pool(ModSounds.LUNAR_MIRROR_USE,
                "items/lunarmirror/lunarmirrorvariant1", "items/lunarmirror/lunarmirrorvariant2",
                "items/lunarmirror/lunarmirrorvariant3", "items/lunarmirror/lunarmirrorvariant4");
        pool(ModSounds.VIRACOCHA_STOP_TIME, "items/viracocha_staff/stop_time");
        pool(ModSounds.VIRACOCHA_RESUME_TIME, "items/viracocha_staff/resume_time");
        add(ModSounds.VIRACOCHA_WHILE_TIME_STOPPED.get(), definition()
                .subtitle(ModLanguageProvider.subtitleKey(
                        ModSounds.VIRACOCHA_WHILE_TIME_STOPPED.getId().getPath()))
                .with(sound(Vestigia.id("items/viracocha_staff/while_time_stopped"))
                        .stream(false)));

        simple(ModSounds.ANTARA_CALM, "antara_calm");
        simple(ModSounds.ANTARA_RAIN, "antara_rain");
        simple(ModSounds.ANTARA_REVEAL, "antara_reveal");
        simple(ModSounds.ANTARA_BREATH, "antara_breath");
        simple(ModSounds.ANTARA_MOURNING, "antara_mourning");

        simple(ModSounds.VESTIGE_TABLE_READY, "vestigestable/vestige_table_ready");
        simple(ModSounds.VESTIGE_TABLE_CRAFT_ANIMAL, "vestigestable/vestige_table_craft_animal");
        simple(ModSounds.VESTIGE_TABLE_CRAFT_GOD, "vestigestable/vestige_table_craft_god");

        simple(ModSounds.ECLIPSE_BEGIN, "eclipse_begin");
        simple(ModSounds.ECLIPSE_END, "eclipse_end");
        simple(ModSounds.WAKA_NIGHT_BEGIN, "waka_night_begin");
    }

    private void pool(DeferredHolder<SoundEvent, SoundEvent> event, String... files) {
        SoundDefinition definition = definition()
                .subtitle(ModLanguageProvider.subtitleKey(event.getId().getPath()));
        for (String file : files) {
            definition.with(sound(Vestigia.id(file)));
        }
        add(event.get(), definition);
    }

    private void simple(DeferredHolder<SoundEvent, SoundEvent> event, String file) {
        add(event.get(), definition()
                .subtitle(ModLanguageProvider.subtitleKey(event.getId().getPath()))
                .with(sound(Vestigia.id(file))));
    }
}
