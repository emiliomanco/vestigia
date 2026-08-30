package com.emiliomanco.vestigia.client.data;

import com.emiliomanco.vestigia.Civilization;
import com.emiliomanco.vestigia.Vestigia;
import com.emiliomanco.vestigia.registry.ModBlocks;
import com.emiliomanco.vestigia.registry.ModEntities;
import com.emiliomanco.vestigia.registry.ModItems;
import com.emiliomanco.vestigia.registry.ModSounds;
import com.emiliomanco.vestigia.item.artifact.CurareDart;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;

public abstract class ModLanguageProvider extends LanguageProvider {

    protected ModLanguageProvider(PackOutput output, String locale) {
        super(output, Vestigia.MODID, locale);
    }

    public static String subtitleKey(String soundPath) {
        return "subtitles." + Vestigia.MODID + "." + soundPath;
    }

    protected static String subtitle(String soundPath) {
        return subtitleKey(soundPath);
    }

    public static final class EnglishUs extends ModLanguageProvider {
        public EnglishUs(PackOutput output) {
            super(output, "en_us");
        }

        @Override
        protected void addTranslations() {
            add("itemGroup.vestigia", "Vestigia");

            add(Civilization.CHAVIN.translationKey(), "Chavín");
            add(Civilization.NAZCA.translationKey(), "Nazca");
            add(Civilization.MAYA.translationKey(), "Maya");
            add(Civilization.MARAJOARA.translationKey(), "Marajoara");
            add(Civilization.MUISCA.translationKey(), "Muisca");
            add(Civilization.INCA.translationKey(), "Inca");

            add(ModItems.LINE_TABLET.get(), "Tablet of the Lines");
            add(ModItems.MACUAHUITL.get(), "Macuahuitl");
            add(ModItems.INCAN_CLUB.get(), "Incan Club");
            add(ModItems.JADE_MASK.get(), "Jade Mask");
            add(ModBlocks.VESTIGE_TABLE.get(), "Vestiges Table");

            add("container.vestigia.vestige_table", "Vestiges Table");
            add("container.vestigia.vestige_table.no_ritual", "No ritual recognised");
            add("container.vestigia.vestige_table.recognised", "Ritual recognised");

            add(ModItems.OFFERING.get(), "Offering");

            add("item.vestigia.lunar_mirror.veiled", "The moon covers you");
            add("item.vestigia.viracocha_staff.stasis", "Time stops. %s held.");
            add("item.vestigia.viracocha_staff.stasis_end", "Time resumes.");
            add("item.vestigia.viracocha_staff.no_offering", "You have nothing to offer him.");
            add("item.vestigia.viracocha_staff.restored", "%s stones return to their courses");
            add("item.vestigia.viracocha_staff.nothing_to_restore", "Nothing here was taken.");
            add("item.vestigia.viracocha_staff.rebirth", "Viracocha makes you again.");
            add(ModItems.PUNCHAO.get(), "Punchao of Inti");
            add(ModItems.SUN_DISC_OF_INTI.get(), "Sun Disc of Inti");
            add(ModItems.BLOWGUN.get(), "Blowgun");
            add(ModItems.OBSIDIAN_SPEAR.get(), "Obsidian Spear");
            add(ModItems.curareDart(CurareDart.POISON).get(), "Poison Dart");
            add(ModItems.curareDart(CurareDart.PARALYTIC).get(), "Paralytic Dart");
            add(ModItems.curareDart(CurareDart.SLEEP).get(), "Sleep Dart");
            add(ModItems.curareDart(CurareDart.MARKER).get(), "Marker Dart");
            add(ModEntities.DART.get(), "Curare Dart");
            add(ModEntities.JAGUAR.get(), "Jaguar");
            add("effect.vestigia.bleed", "Bleeding");
            add("effect.vestigia.paralysis", "Paralysis");

            add(ModItems.LUNAR_MIRROR.get(), "Lunar Silver Mirror");
            add(ModItems.VIRACOCHA_STAFF.get(), "Staff of Viracocha");
            add(ModItems.OTORONGO_FANG.get(), "Otorongo Fang");
            add(ModItems.OBSIDIAN_FRAGMENT.get(), "Obsidian Fragment");
            add(ModItems.OTORONGO_HELM.get(), "Helm of the Otorongo");

            add(ModItems.CROWN_OF_PACHAMAMA.get(), "Crown of Pachamama");

            add(ModItems.MANTLE_OF_KUKULKAN.get(), "Mantle of Kukulkan");

            add(ModEntities.ICE_SPIKE.get(), "Bent Element");
            add("key.categories.vestigia", "Vestigia");
            add("key.vestigia.ability_one", "God ability I");
            add("key.vestigia.ability_two", "God ability II");
            add("screen.vestigia.branch_choice", "Pachamama is listening. Choose once.");
            add("item.vestigia.corona_pachamama.unbound", "The crown has not been given an element yet");
            add("item.vestigia.corona_pachamama.bound", "The crown answers to %s");
            add("item.vestigia.corona_pachamama.too_dry", "There is no water in this air");
            add("item.vestigia.corona_pachamama.named", "%s Crown of Pachamama");
            add("key.vestigia.element_previous", "Previous element");
            add("key.vestigia.element_next", "Next element");
            add("item.vestigia.supreme_crown", "Supreme Crown of Pachamama");
            add("item.vestigia.corona_pachamama.no_ground", "There is no earth here to raise");
            add("item.vestigia.corona_pachamama.boulder_ready", "Strike the stone to throw it");
            add("item.vestigia.corona_pachamama.shards_ready", "Ten shards ready - each swing throws one");
            add(ModItems.JAGUAR_SPAWN_EGG.get(), "Jaguar Spawn Egg");
            add(ModItems.MAYAN_WARRIOR_SPAWN_EGG.get(), "Mayan Warrior Spawn Egg");
            add(ModItems.MAYAN_ZOMBIE_SPAWN_EGG.get(), "Undead Mayan Warrior Spawn Egg");
            add(ModItems.MAYAN_SHAMAN_SPAWN_EGG.get(), "Maya Shaman Spawn Egg");
            add(ModItems.MAYAN_NACOM_SPAWN_EGG.get(), "Nacom Spawn Egg");
            add(ModItems.INCAN_WARRIOR_SPAWN_EGG.get(), "Incan Warrior Spawn Egg");
            add(ModItems.INCAN_PRIEST_SPAWN_EGG.get(), "Incan Priest Spawn Egg");
            add(ModItems.INCAN_APUSKIPAY_SPAWN_EGG.get(), "Apuskipay Spawn Egg");
            add(ModEntities.AIR_SCOOTER.get(), "Air Scooter");
            add("item.vestigia.corona_pachamama.branch.earth", "Earth");
            add("item.vestigia.corona_pachamama.branch.earth.desc",
                    "Raise a wall out of the ground you stand on. Tear a boulder loose and throw it.");
            add("item.vestigia.corona_pachamama.branch.water", "Water");
            add("item.vestigia.corona_pachamama.branch.water.desc",
                    "Hold ten shards of ice and throw one with every swing. Breathe underwater, and mend while in it.");
            add("item.vestigia.corona_pachamama.branch.air", "Air");
            add("item.vestigia.corona_pachamama.branch.air.desc",
                    "Throw everything near you away. Ride a ball of air over ground and sky.");
            add("item.vestigia.corona_pachamama.branch.fire", "Fire");
            add("item.vestigia.corona_pachamama.branch.fire.desc",
                    "Throw fire. Call lightning down on what you are looking at.");

            add(ModEntities.MAYAN_WARRIOR.get(), "Mayan Warrior");
            add(ModEntities.MAYAN_ZOMBIE.get(), "Undead Mayan Warrior");
            add(ModEntities.MAYAN_SHAMAN.get(), "Maya Shaman");
            add(ModEntities.MAYAN_NACOM.get(), "Nacom");
            add(ModEntities.INCAN_WARRIOR.get(), "Incan Warrior");
            add(ModEntities.INCAN_PRIEST.get(), "Incan Priest");
            add(ModEntities.INCAN_APUSKIPAY.get(), "Apuskipay");
            add("entity.vestigia.mayan_nacom.echoes",
                    "He splits into echoes - strike them in the calendar's order");
            add("entity.vestigia.mayan_nacom.correct", "The glyph darkens. The next one waits.");
            add("entity.vestigia.mayan_nacom.wrong_order",
                    "Out of order - the sequence closes and he mends");
            add("entity.vestigia.mayan_nacom.opened", "The last glyph fades. He is open.");
            add("entity.vestigia.mother_of_urns.exposed",
                    "The last urn breaks. She has nothing left to hide behind.");
            add("entity.vestigia.settler.refuses", "The %s will not deal with you.");
            add("entity.vestigia.settler.standing", "Your standing with the %s: %s");

            add("item.vestigia.line_tablet.header", "The tablet reads:");
            add("item.vestigia.line_tablet.entry", "  %s - %s blocks %s");
            add("item.vestigia.line_tablet.nothing", "Nothing of ours lies within reach.");

            add("structure.vestigia.ciudadela_del_sol", "the Citadel of the Sun");
            add("structure.vestigia.maya_acropolis", "the Jungle Acropolis");
            add("structure.vestigia.maya_pyramid", "the Maya Pyramid");
            add("structure.vestigia.incan_lunar_stone", "the Lunar Stone");
            add("structure.vestigia.marajoara_teso", "the River Teso");
            add("structure.vestigia.chavin_labyrinth", "the Underground Labyrinth");
            add("structure.vestigia.ayllu_del_valle", "the Ayllu of the Valley");

            add("entity.vestigia.sapa_guardian.summon", "The Sapa Guardian calls his sentinels");
            add("entity.vestigia.sapa_guardian.intihuatana_opens",
                    "The Intihuatana opens - strike while the sun is hidden");

            add("item.vestigia.khipu.tied", "Knotted %s at %s, %s, %s");
            add("item.vestigia.khipu.displaced", "The khipu has no free cord left; the oldest knot was untied.");
            add("item.vestigia.khipu.empty", "This khipu has no knots yet.");
            add("item.vestigia.khipu.header", "The khipu records:");
            add("item.vestigia.khipu.entry", "  %s - %s blocks %s");
            add("item.vestigia.khipu.entry_other_dimension", "  %s - in %s");

            add(subtitle("entity.mayan_nacom.stomp"), "The lord strikes the ground");
            add(subtitle("entity.jaguar.chase"), "Jaguar growls");
            add(subtitle("entity.jaguar.attack"), "Jaguar bites");
            add(subtitle("entity.jaguar.pounce"), "Jaguar pounces");
            add(subtitle("entity.mayan_warrior.battlecry"), "Mayan warrior cries out");
            add(subtitle("entity.mayan_warrior.taunt"), "Mayan warrior boasts");
            add(subtitle("entity.mayan_warrior.flute"), "A flute plays");
            add(subtitle("entity.mayan_shaman.curse"), "The shaman calls out");
            add(subtitle("item.blowgun.shoot"), "Blowgun breathes");
            add(subtitle("item.obsidian_spear.throw"), "Spear thrown");
            add(subtitle("item.obsidian_spear.impact"), "Spear strikes");
            add(subtitle("item.sun_disc.throw"), "Punchao flies");
            add(subtitle("item.sun_disc.catch"), "Punchao returns");
            add(subtitle("item.lunar_mirror.use"), "The mirror turns the tide");
            add(subtitle("item.viracocha_staff.stop_time"), "Time stops");
            add(subtitle("item.viracocha_staff.while_time_stopped"), "Time is held");
            add(subtitle("item.viracocha_staff.resume_time"), "Time resumes");
            add(subtitle("item.pututu.blow"), "Pututu sounds");
            add(subtitle("item.pututu.echo"), "The galleries answer");
            add(subtitle("item.antara.calm"), "Antara plays a calming melody");
            add(subtitle("item.antara.rain"), "Antara calls the rain");
            add(subtitle("item.antara.reveal"), "Antara traces the lines");
            add(subtitle("item.antara.breath"), "Antara quickens the breath");
            add(subtitle("item.antara.mourning"), "Antara plays a mourning melody");
            add(subtitle("block.vestige_table.ready"), "The Vestiges Table stirs");
            add(subtitle("block.vestige_table.craft_animal"), "A beast wakes");
            add(subtitle("block.vestige_table.craft_god"), "A god wakes");
            add(subtitle("event.eclipse.begin"), "The sun is swallowed");
            add(subtitle("event.eclipse.end"), "The sun returns");
            add(subtitle("event.waka_night.begin"), "The wak'as light up");

            addConfig("Vestigia Configuration", "Guardians", "World Generation", "Events", "Reputation");
        }
    }

    public static final class SpanishEs extends ModLanguageProvider {
        public SpanishEs(PackOutput output) {
            super(output, "es_es");
        }

        @Override
        protected void addTranslations() {
            add("itemGroup.vestigia", "Vestigia");

            add(Civilization.CHAVIN.translationKey(), "Chavín");
            add(Civilization.NAZCA.translationKey(), "Nazca");
            add(Civilization.MAYA.translationKey(), "Maya");
            add(Civilization.MARAJOARA.translationKey(), "Marajoara");
            add(Civilization.MUISCA.translationKey(), "Muisca");
            add(Civilization.INCA.translationKey(), "Inca");

            add(ModItems.LINE_TABLET.get(), "Tablilla de las Líneas");
            add(ModItems.MACUAHUITL.get(), "Macuahuitl");
            add(ModItems.INCAN_CLUB.get(), "Club Inca");
            add(ModItems.JADE_MASK.get(), "Máscara de Jade");
            add(ModBlocks.VESTIGE_TABLE.get(), "Mesa de los Vestigios");

            add("container.vestigia.vestige_table", "Mesa de los Vestigios");
            add("container.vestigia.vestige_table.no_ritual", "Ningún ritual reconocido");
            add("container.vestigia.vestige_table.recognised", "Ritual reconocido");

            add(ModItems.OFFERING.get(), "Ofrenda");

            add("item.vestigia.lunar_mirror.veiled", "La luna te cubre");
            add("item.vestigia.viracocha_staff.stasis", "El tiempo se detiene. %s retenidos.");
            add("item.vestigia.viracocha_staff.stasis_end", "El tiempo vuelve a correr.");
            add("item.vestigia.viracocha_staff.no_offering", "No tienes nada que ofrecerle.");
            add("item.vestigia.viracocha_staff.restored", "%s piedras vuelven a su hilada");
            add("item.vestigia.viracocha_staff.nothing_to_restore", "Aquí no se llevaron nada.");
            add("item.vestigia.viracocha_staff.rebirth", "Viracocha te hace de nuevo.");
            add(ModItems.PUNCHAO.get(), "Punchao de Inti");
            add(ModItems.SUN_DISC_OF_INTI.get(), "Disco Solar de Inti");
            add(ModItems.BLOWGUN.get(), "Cerbatana");
            add(ModItems.OBSIDIAN_SPEAR.get(), "Lanza de Obsidiana");
            add(ModItems.curareDart(CurareDart.POISON).get(), "Dardo de veneno");
            add(ModItems.curareDart(CurareDart.PARALYTIC).get(), "Dardo paralizante");
            add(ModItems.curareDart(CurareDart.SLEEP).get(), "Dardo de sueño");
            add(ModItems.curareDart(CurareDart.MARKER).get(), "Dardo marcador");
            add(ModEntities.DART.get(), "Dardo de curare");
            add(ModEntities.JAGUAR.get(), "Jaguar");
            add("effect.vestigia.bleed", "Sangrado");
            add("effect.vestigia.paralysis", "Parálisis");

            add(ModItems.LUNAR_MIRROR.get(), "Espejo de Plata Lunar");
            add(ModItems.VIRACOCHA_STAFF.get(), "Vara de Viracocha");
            add(ModItems.OTORONGO_FANG.get(), "Colmillo de Otorongo");
            add(ModItems.OBSIDIAN_FRAGMENT.get(), "Fragmento de Obsidiana");
            add(ModItems.OTORONGO_HELM.get(), "Yelmo del Otorongo");

            add(ModItems.CROWN_OF_PACHAMAMA.get(), "Corona de Pachamama");

            add(ModItems.MANTLE_OF_KUKULKAN.get(), "Manto de Kukulkán");

            add(ModEntities.ICE_SPIKE.get(), "Elemento moldeado");
            add("key.categories.vestigia", "Vestigia");
            add("key.vestigia.ability_one", "Habilidad de dios I");
            add("key.vestigia.ability_two", "Habilidad de dios II");
            add("screen.vestigia.branch_choice", "Pachamama escucha. Se elige una sola vez.");
            add("item.vestigia.corona_pachamama.unbound", "La corona todavía no tiene elemento");
            add("item.vestigia.corona_pachamama.bound", "La corona responde a %s");
            add("item.vestigia.corona_pachamama.too_dry", "No hay agua en este aire");
            add("item.vestigia.corona_pachamama.named", "Corona de Pachamama de %s");
            add("key.vestigia.element_previous", "Elemento anterior");
            add("key.vestigia.element_next", "Elemento siguiente");
            add("item.vestigia.supreme_crown", "Corona Suprema de Pachamama");
            add("item.vestigia.corona_pachamama.no_ground", "Aquí no hay tierra que levantar");
            add("item.vestigia.corona_pachamama.boulder_ready", "Golpea la roca para lanzarla");
            add("item.vestigia.corona_pachamama.shards_ready", "Diez fragmentos listos - cada golpe lanza uno");
            add(ModItems.JAGUAR_SPAWN_EGG.get(), "Huevo generador de jaguar");
            add(ModItems.MAYAN_WARRIOR_SPAWN_EGG.get(), "Huevo generador de guerrero maya");
            add(ModItems.MAYAN_ZOMBIE_SPAWN_EGG.get(), "Huevo generador de guerrero maya no muerto");
            add(ModItems.MAYAN_SHAMAN_SPAWN_EGG.get(), "Huevo generador de chamán maya");
            add(ModItems.MAYAN_NACOM_SPAWN_EGG.get(), "Huevo generador de Nacom");
            add(ModItems.INCAN_WARRIOR_SPAWN_EGG.get(), "Huevo generador de guerrero inca");
            add(ModItems.INCAN_PRIEST_SPAWN_EGG.get(), "Huevo generador de sacerdote inca");
            add(ModItems.INCAN_APUSKIPAY_SPAWN_EGG.get(), "Huevo generador de Apuskipay");
            add(ModEntities.AIR_SCOOTER.get(), "Bola de aire");
            add("item.vestigia.corona_pachamama.branch.earth", "Tierra");
            add("item.vestigia.corona_pachamama.branch.earth.desc",
                    "Levanta un muro del suelo que pisas. Arranca una roca y lánzala.");
            add("item.vestigia.corona_pachamama.branch.water", "Agua");
            add("item.vestigia.corona_pachamama.branch.water.desc",
                    "Sostiene diez fragmentos de hielo y lanza uno con cada golpe. Respira bajo el agua, y sana dentro de ella.");
            add("item.vestigia.corona_pachamama.branch.air", "Aire");
            add("item.vestigia.corona_pachamama.branch.air.desc",
                    "Lanza lejos todo lo que tengas cerca. Móntate en una bola de aire por tierra y cielo.");
            add("item.vestigia.corona_pachamama.branch.fire", "Fuego");
            add("item.vestigia.corona_pachamama.branch.fire.desc",
                    "Lanza fuego. Llama al rayo sobre lo que estés mirando.");

            add(ModEntities.MAYAN_WARRIOR.get(), "Guerrero maya");
            add(ModEntities.MAYAN_ZOMBIE.get(), "Guerrero maya no muerto");
            add(ModEntities.MAYAN_SHAMAN.get(), "Chamán maya");
            add(ModEntities.MAYAN_NACOM.get(), "Nacom");
            add(ModEntities.INCAN_WARRIOR.get(), "Guerrero inca");
            add(ModEntities.INCAN_PRIEST.get(), "Sacerdote inca");
            add(ModEntities.INCAN_APUSKIPAY.get(), "Apuskipay");
            add("entity.vestigia.mayan_nacom.echoes",
                    "Se divide en ecos - gólpealos en el orden del calendario");
            add("entity.vestigia.mayan_nacom.correct", "El glifo se apaga. El siguiente espera.");
            add("entity.vestigia.mayan_nacom.wrong_order",
                    "Fuera de orden - la secuencia se cierra y él se cura");
            add("entity.vestigia.mayan_nacom.opened", "El último glifo se desvanece. Está abierto.");
            add("entity.vestigia.mother_of_urns.exposed",
                    "La última urna se rompe. Ya no tiene tras qué esconderse.");
            add("entity.vestigia.settler.refuses", "Los %s no tratan contigo.");
            add("entity.vestigia.settler.standing", "Tu reputación con los %s: %s");

            add("item.vestigia.line_tablet.header", "La tablilla dice:");
            add("item.vestigia.line_tablet.entry", "  %s - a %s bloques, %s");
            add("item.vestigia.line_tablet.nothing", "Nada nuestro queda al alcance.");

            add("structure.vestigia.ciudadela_del_sol", "la Ciudadela del Sol");
            add("structure.vestigia.maya_acropolis", "la Acrópolis de la Selva");
            add("structure.vestigia.maya_pyramid", "la Pirámide Maya");
            add("structure.vestigia.incan_lunar_stone", "la Piedra Lunar");
            add("structure.vestigia.marajoara_teso", "el Teso del Río");
            add("structure.vestigia.chavin_labyrinth", "el Laberinto Subterráneo");
            add("structure.vestigia.ayllu_del_valle", "el Ayllu del Valle");

            add("entity.vestigia.sapa_guardian.summon", "El Sapa Guardián llama a sus sentinelas");
            add("entity.vestigia.sapa_guardian.intihuatana_opens",
                    "El Intihuatana se abre - golpea mientras el sol está oculto");

            add("item.vestigia.khipu.tied", "Nudo %s anudado en %s, %s, %s");
            add("item.vestigia.khipu.displaced", "Al khipu no le queda cuerda libre; se deshizo el nudo más antiguo.");
            add("item.vestigia.khipu.empty", "Este khipu todavía no tiene nudos.");
            add("item.vestigia.khipu.header", "El khipu registra:");
            add("item.vestigia.khipu.entry", "  %s - a %s bloques, %s");
            add("item.vestigia.khipu.entry_other_dimension", "  %s - en %s");

            add(subtitle("entity.mayan_nacom.stomp"), "El señor golpea el suelo");
            add(subtitle("entity.jaguar.chase"), "El jaguar gruñe");
            add(subtitle("entity.jaguar.attack"), "El jaguar muerde");
            add(subtitle("entity.jaguar.pounce"), "El jaguar se abalanza");
            add(subtitle("entity.mayan_warrior.battlecry"), "Un guerrero maya grita");
            add(subtitle("entity.mayan_warrior.taunt"), "Un guerrero maya se jacta");
            add(subtitle("entity.mayan_warrior.flute"), "Suena una flauta");
            add(subtitle("entity.mayan_shaman.curse"), "El chamán invoca");
            add(subtitle("item.blowgun.shoot"), "La cerbatana sopla");
            add(subtitle("item.obsidian_spear.throw"), "Lanza arrojada");
            add(subtitle("item.obsidian_spear.impact"), "La lanza golpea");
            add(subtitle("item.sun_disc.throw"), "El Punchao vuela");
            add(subtitle("item.sun_disc.catch"), "El Punchao regresa");
            add(subtitle("item.lunar_mirror.use"), "El espejo cambia la marea");
            add(subtitle("item.viracocha_staff.stop_time"), "El tiempo se detiene");
            add(subtitle("item.viracocha_staff.while_time_stopped"), "El tiempo está detenido");
            add(subtitle("item.viracocha_staff.resume_time"), "El tiempo se reanuda");
            add(subtitle("item.pututu.blow"), "Suena el pututu");
            add(subtitle("item.pututu.echo"), "Las galerías responden");
            add(subtitle("item.antara.calm"), "La antara toca una melodía serena");
            add(subtitle("item.antara.rain"), "La antara llama a la lluvia");
            add(subtitle("item.antara.reveal"), "La antara traza las líneas");
            add(subtitle("item.antara.breath"), "La antara aviva el aliento");
            add(subtitle("item.antara.mourning"), "La antara toca una melodía de duelo");
            add(subtitle("block.vestige_table.ready"), "La Mesa de los Vestigios se agita");
            add(subtitle("block.vestige_table.craft_animal"), "Despierta una bestia");
            add(subtitle("block.vestige_table.craft_god"), "Despierta un dios");
            add(subtitle("event.eclipse.begin"), "El sol es devorado");
            add(subtitle("event.eclipse.end"), "El sol regresa");
            add(subtitle("event.waka_night.begin"), "Las wak'as se encienden");

            addConfig("Configuración de Vestigia", "Guardianes", "Generación del mundo", "Eventos", "Reputación");
        }
    }

    protected void addConfig(String title, String guardians, String worldgen, String events, String reputation) {
        add("vestigia.configuration.title", title);
        addSection("guardians", guardians);
        addSection("worldgen", worldgen);
        addSection("events", events);
        addSection("reputation", reputation);
    }

    protected void addSection(String key, String name) {
        add("vestigia.configuration.section." + key, name);
        add("vestigia.configuration.section." + key + ".title", name);
    }
}
