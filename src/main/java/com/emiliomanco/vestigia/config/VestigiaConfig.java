package com.emiliomanco.vestigia.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class VestigiaConfig {
    private VestigiaConfig() {}

    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.BooleanValue PACIFIST_MODE;
    public static final ModConfigSpec.DoubleValue GUARDIAN_HEALTH_MULTIPLIER;
    public static final ModConfigSpec.DoubleValue GUARDIAN_DAMAGE_MULTIPLIER;
    public static final ModConfigSpec.IntValue MAX_GUARDIANS_PER_STRUCTURE;

    public static final ModConfigSpec.IntValue STRUCTURE_SPACING;
    public static final ModConfigSpec.IntValue STRUCTURE_SEPARATION;

    public static final ModConfigSpec.BooleanValue ENABLE_ECLIPSE;
    public static final ModConfigSpec.IntValue ECLIPSE_MIN_INTERVAL_DAYS;
    public static final ModConfigSpec.IntValue ECLIPSE_MAX_INTERVAL_DAYS;
    public static final ModConfigSpec.IntValue ECLIPSE_DURATION_SECONDS;
    public static final ModConfigSpec.BooleanValue ENABLE_WAKA_NIGHT;
    public static final ModConfigSpec.DoubleValue WAKA_NIGHT_CHANCE;

    public static final ModConfigSpec.BooleanValue JAGUAR_POUNCE_ENABLED;
    public static final ModConfigSpec.DoubleValue JAGUAR_POUNCE_MIN_RANGE;
    public static final ModConfigSpec.DoubleValue JAGUAR_POUNCE_RANGE;
    public static final ModConfigSpec.IntValue JAGUAR_POUNCE_COOLDOWN_TICKS;
    public static final ModConfigSpec.IntValue JAGUAR_PIN_MAX_TICKS;
    public static final ModConfigSpec.IntValue JAGUAR_PIN_MAUL_INTERVAL_TICKS;
    public static final ModConfigSpec.DoubleValue JAGUAR_PIN_DAMAGE;
    public static final ModConfigSpec.IntValue JAGUAR_PIN_ESCAPE_HITS;

    public static final ModConfigSpec.BooleanValue ENABLE_VESTIGE_PASSIVES;
    public static final ModConfigSpec.DoubleValue PUNCHAO_SUNLIT_ARMOR;
    public static final ModConfigSpec.DoubleValue LANZON_PANIC_RADIUS;
    public static final ModConfigSpec.DoubleValue LANZON_PANIC_CHANCE;
    public static final ModConfigSpec.IntValue LANZON_PANIC_TICKS;
    public static final ModConfigSpec.IntValue LINE_TABLET_RANGE;
    public static final ModConfigSpec.IntValue LINE_TABLET_COOLDOWN_TICKS;

    public static final ModConfigSpec.DoubleValue INTI_AURA_RADIUS;
    public static final ModConfigSpec.DoubleValue INTI_THROW_DAMAGE;
    public static final ModConfigSpec.IntValue INTI_THROW_PIERCE;
    public static final ModConfigSpec.IntValue INTI_THROW_IGNITE_SECONDS;
    public static final ModConfigSpec.IntValue INTI_THROW_COOLDOWN_TICKS;
    public static final ModConfigSpec.DoubleValue INTI_WEAKENED_MULTIPLIER;
    public static final ModConfigSpec.DoubleValue INTI_THROW_SPEED;
    public static final ModConfigSpec.DoubleValue INTI_SPIN_SPEED;
    public static final ModConfigSpec.DoubleValue INTI_BOUNCE_RETENTION;

    public static final ModConfigSpec.IntValue KILLA_VEIL_DELAY_TICKS;
    public static final ModConfigSpec.DoubleValue KILLA_AMBUSH_BONUS;
    public static final ModConfigSpec.IntValue KILLA_TIDE_RADIUS;
    public static final ModConfigSpec.IntValue KILLA_TIDE_TICKS;
    public static final ModConfigSpec.IntValue KILLA_TIDE_COOLDOWN_TICKS;

    public static final ModConfigSpec.IntValue VIRACOCHA_STASIS_RADIUS;
    public static final ModConfigSpec.IntValue VIRACOCHA_STASIS_MAX_TICKS;
    public static final ModConfigSpec.IntValue VIRACOCHA_STASIS_COOLDOWN_TICKS;
    public static final ModConfigSpec.IntValue VIRACOCHA_RESTORE_RADIUS;
    public static final ModConfigSpec.IntValue VIRACOCHA_REBIRTH_COOLDOWN_TICKS;
    public static final ModConfigSpec.BooleanValue VIRACOCHA_STASIS_COSTS_OFFERING;

    public static final ModConfigSpec.IntValue PACHAMAMA_REGEN_INTERVAL_TICKS;
    public static final ModConfigSpec.IntValue PACHAMAMA_REGEN_AMPLIFIER;

    public static final ModConfigSpec.IntValue PACHAMAMA_EARTH_WALL_COOLDOWN;
    public static final ModConfigSpec.IntValue PACHAMAMA_EARTH_BOULDER_COOLDOWN;
    public static final ModConfigSpec.IntValue PACHAMAMA_WATER_SHARDS_COOLDOWN;
    public static final ModConfigSpec.IntValue PACHAMAMA_AIR_SHOCKWAVE_COOLDOWN;
    public static final ModConfigSpec.IntValue PACHAMAMA_AIR_SCOOTER_COOLDOWN;
    public static final ModConfigSpec.IntValue PACHAMAMA_AIR_SCOOTER_DURATION;
    public static final ModConfigSpec.IntValue PACHAMAMA_FIRE_FIREBALL_COOLDOWN;
    public static final ModConfigSpec.IntValue PACHAMAMA_FIRE_LIGHTNING_COOLDOWN;
    public static final ModConfigSpec.DoubleValue PACHAMAMA_BOULDER_DAMAGE;
    public static final ModConfigSpec.DoubleValue PACHAMAMA_SHARD_DAMAGE;
    public static final ModConfigSpec.DoubleValue PACHAMAMA_FIREBALL_DAMAGE;

    public static final ModConfigSpec.DoubleValue KUKULKAN_DASH_SPEED;
    public static final ModConfigSpec.IntValue KUKULKAN_DASH_COOLDOWN_TICKS;
    public static final ModConfigSpec.IntValue KUKULKAN_POISON_TICKS;
    public static final ModConfigSpec.IntValue KUKULKAN_BLOOM_RADIUS;
    public static final ModConfigSpec.IntValue KUKULKAN_BLOOM_DURATION_TICKS;
    public static final ModConfigSpec.DoubleValue KUKULKAN_BLOOM_GROWTH_BONUS;

    static {
        BUILDER.comment("Guardian difficulty and density.").push("guardians");
        PACIFIST_MODE = BUILDER
                .comment("Disable the ancestral guardians entirely.")
                .define("pacifistMode", false);
        GUARDIAN_HEALTH_MULTIPLIER = BUILDER
                .comment("Multiplier applied to every guardian's max health.")
                .defineInRange("healthMultiplier", 1.0D, 0.1D, 10.0D);
        GUARDIAN_DAMAGE_MULTIPLIER = BUILDER
                .comment("Multiplier applied to every guardian's attack damage.")
                .defineInRange("damageMultiplier", 1.0D, 0.1D, 10.0D);
        MAX_GUARDIANS_PER_STRUCTURE = BUILDER
                .comment("How many Maya custodians may be alive around one pyramid at a time.",
                        "Checked when a new one tries to spawn, counting everything within 64",
                        "blocks. A structure spawn override has no cap of its own, so without",
                        "this the temple fills until the global mob cap stops it.")
                .defineInRange("maxPerStructure", 15, 1, 200);
        BUILDER.pop();

        BUILDER.comment("Structure placement.").push("worldgen");
        STRUCTURE_SPACING = BUILDER
                .comment("Average chunk spacing between structures of the same type.",
                        "Larger is rarer. 96 chunks is roughly one every 1500 blocks.")
                .defineInRange("spacing", 96, 8, 4096);
        STRUCTURE_SEPARATION = BUILDER
                .comment("Minimum chunk separation. Must be smaller than spacing.")
                .defineInRange("separation", 40, 0, 4095);
        BUILDER.pop();

        BUILDER.comment("World events.").push("events");
        ENABLE_ECLIPSE = BUILDER.define("enableEclipse", true);
        ECLIPSE_MIN_INTERVAL_DAYS = BUILDER
                .comment("Minimum Minecraft days between eclipses.")
                .defineInRange("eclipseMinIntervalDays", 12, 1, 1000);
        ECLIPSE_MAX_INTERVAL_DAYS = BUILDER
                .comment("Maximum Minecraft days between eclipses.")
                .defineInRange("eclipseMaxIntervalDays", 20, 1, 1000);
        ECLIPSE_DURATION_SECONDS = BUILDER
                .defineInRange("eclipseDurationSeconds", 180, 10, 3600);
        ENABLE_WAKA_NIGHT = BUILDER.define("enableWakaNight", true);
        WAKA_NIGHT_CHANCE = BUILDER
                .comment("Chance per night that every Vestigia structure raises a beacon of light.")
                .defineInRange("wakaNightChance", 0.02D, 0.0D, 1.0D);
        BUILDER.pop();

        BUILDER.comment("The animals of the mod, and what they do that vanilla's do not.")
                .push("fauna");
        BUILDER.comment("The jaguar's takedown: it leaps, drops you and mauls you where you land.",
                        "The whole thing is escapable by fighting back, which is what keeps it a",
                        "fight rather than a cutscene. Turn pounceEnabled off to leave a jaguar",
                        "that only bites.")
                .push("jaguar");
        JAGUAR_POUNCE_ENABLED = BUILDER.define("pounceEnabled", true);
        JAGUAR_POUNCE_MIN_RANGE = BUILDER
                .comment("Closer than this and it simply bites - a pounce needs room to be a pounce.")
                .defineInRange("pounceMinRange", 3.0D, 0.0D, 32.0D);
        JAGUAR_POUNCE_RANGE = BUILDER
                .comment("The furthest it will launch from.")
                .defineInRange("pounceRange", 8.0D, 1.0D, 32.0D);
        JAGUAR_POUNCE_COOLDOWN_TICKS = BUILDER
                .comment("Wait between takedowns, counted from the end of the last one.",
                        "This is the whole pacing dial: between pounces it is an ordinary big cat.")
                .defineInRange("pounceCooldownTicks", 240, 0, 24000);
        JAGUAR_PIN_MAX_TICKS = BUILDER
                .comment("Longest a pin can last before the jaguar lets go of its own accord.",
                        "100 ticks is five seconds, which covers the full jump_attack clip and a",
                        "little of the loop that follows it.")
                .defineInRange("pinMaxTicks", 100, 20, 600);
        JAGUAR_PIN_MAUL_INTERVAL_TICKS = BUILDER
                .comment("Ticks between bites while pinned. Below 20 the extra bites are eaten by",
                        "vanilla's damage-invulnerability window and simply do nothing.")
                .defineInRange("pinMaulIntervalTicks", 20, 5, 200);
        JAGUAR_PIN_DAMAGE = BUILDER
                .comment("Damage per bite while pinned. Deliberately well under its standing attack:",
                        "the pin's threat is the total, not the individual hit.")
                .defineInRange("pinDamage", 3.0D, 0.0D, 100.0D);
        JAGUAR_PIN_ESCAPE_HITS = BUILDER
                .comment("Hits the pinned victim must land on the jaguar to throw it off.",
                        "Set to 0 to make a pin unbreakable, which is not recommended.")
                .defineInRange("pinEscapeHits", 3, 0, 50);
        BUILDER.pop();
        BUILDER.pop();

        BUILDER.comment("The passive each vestige grants while it is in a player's inventory.",
                        "A vestige is also a ritual ingredient; these are what it does before it is spent.")
                .push("vestiges");
        ENABLE_VESTIGE_PASSIVES = BUILDER
                .comment("Turn off every vestige passive, leaving them as pure ritual ingredients.")
                .define("enablePassives", true);
        PUNCHAO_SUNLIT_ARMOR = BUILDER
                .comment("Punchao: extra armour points while standing in direct sunlight.",
                        "2.0 is one armour heart, the value in the design brief.")
                .defineInRange("punchaoSunlitArmor", 2.0D, 0.0D, 20.0D);
        LANZON_PANIC_RADIUS = BUILDER
                .comment("Fragment of the Lanzon: how close a hostile has to be to lose its nerve.")
                .defineInRange("lanzonPanicRadius", 4.0D, 1.0D, 32.0D);
        LANZON_PANIC_CHANCE = BUILDER
                .comment("Chance per second that a hostile inside that radius panics.")
                .defineInRange("lanzonPanicChance", 0.20D, 0.0D, 1.0D);
        LANZON_PANIC_TICKS = BUILDER
                .defineInRange("lanzonPanicTicks", 60, 20, 600);
        LINE_TABLET_RANGE = BUILDER
                .comment("Tablet of the Lines: how far it will look for the mod's structures.")
                .defineInRange("lineTabletRange", 2000, 100, 20000);
        LINE_TABLET_COOLDOWN_TICKS = BUILDER
                .comment("The search scans loaded structure placements, so this is also a rate limit.")
                .defineInRange("lineTabletCooldownTicks", 200, 20, 24000);
        BUILDER.pop();

        BUILDER.comment("The major gods. These sit above the animal totems on purpose -",
                        "a major god should feel categorically stronger, not incrementally.")
                .push("gods");

        BUILDER.comment("Inti - the sun disc. Everything about it is written against the sky.")
                .push("inti");
        INTI_AURA_RADIUS = BUILDER
                .comment("Radius of the solar aura, in blocks.")
                .defineInRange("auraRadius", 8.0D, 1.0D, 64.0D);
        INTI_THROW_DAMAGE = BUILDER
                .comment("Damage of the thrown disc, before the weakened multiplier.",
                        "Well above any vanilla weapon on purpose: this is a major god's weapon,",
                        "it costs a full throw-and-return cycle, and it is the only thing the disc",
                        "does offensively now that the passive aura no longer harms anything.")
                .defineInRange("throwDamage", 28.0D, 0.0D, 200.0D);
        INTI_THROW_PIERCE = BUILDER
                .comment("How many enemies one throw passes through before returning.")
                .defineInRange("throwPierce", 10, 1, 64);
        INTI_THROW_IGNITE_SECONDS = BUILDER
                .defineInRange("throwIgniteSeconds", 6, 0, 120);
        INTI_THROW_COOLDOWN_TICKS = BUILDER
                .comment("The disc cannot be thrown again until it has come back and settled.")
                .defineInRange("throwCooldownTicks", 40, 0, 24000);
        INTI_WEAKENED_MULTIPLIER = BUILDER
                .comment("Power at night or under cover. The design point is that you plan the hour",
                        "of a fight around this, so it should hurt to ignore - but 0.30 made the",
                        "night disc a wet noodle: 20 damage became 6, and a 20-health creeper took",
                        "four throws. At 0.50 a weakened throw is 14, so a creeper dies in two and",
                        "a full-power one dies in one.")
                .defineInRange("weakenedMultiplier", 0.50D, 0.0D, 1.0D);
        INTI_THROW_SPEED = BUILDER
                .comment("Launch speed in blocks per tick. 2.0 is about 40 blocks a second -",
                        "reads as hurled rather than tossed, without outrunning the eye.",
                        "Much above 3.5 and it can tunnel past a thin target between ticks.")
                .defineInRange("throwSpeed", 1.1D, 0.1D, 5.0D);
        INTI_SPIN_SPEED = BUILDER
                .comment("Animation playback rate for the disc in flight, relative to the held disc.",
                        "The clip is a 4-second loop; this is what makes it whirr rather than turn.")
                .defineInRange("spinSpeed", 4.0D, 0.1D, 20.0D);
        INTI_BOUNCE_RETENTION = BUILDER
                .comment("Fraction of speed kept on every bounce, body or block alike.",
                        "0.85 means each impact costs 15%, so a long chain visibly runs the disc",
                        "down and it comes home slower than it left.")
                .defineInRange("bounceRetention", 0.85D, 0.1D, 2.0D);
        BUILDER.pop();

        BUILDER.comment("Mama Killa - the moon. Inti's exact inverse; she wants darkness.")
                .push("mama_killa");
        KILLA_VEIL_DELAY_TICKS = BUILDER
                .comment("Ticks standing still at night before the mirror hides you.")
                .defineInRange("veilDelayTicks", 40, 1, 6000);
        KILLA_AMBUSH_BONUS = BUILDER
                .comment("Extra damage on the first strike out of the veil. 0.5 is +50%.")
                .defineInRange("ambushBonus", 0.50D, 0.0D, 10.0D);
        KILLA_TIDE_RADIUS = BUILDER
                .comment("Radius of the tide, in blocks. Water inside becomes walkable ice.")
                .defineInRange("tideRadius", 10, 1, 32);
        KILLA_TIDE_TICKS = BUILDER
                .defineInRange("tideTicks", 400, 20, 24000);
        KILLA_TIDE_COOLDOWN_TICKS = BUILDER
                .defineInRange("tideCooldownTicks", 300, 0, 24000);
        BUILDER.pop();

        BUILDER.comment("Viracocha - the creator, and the end of the mod.",
                        "Not a damage weapon: every power here is control, and each one costs.")
                .push("viracocha");
        VIRACOCHA_STASIS_RADIUS = BUILDER
                .comment("Radius in which time stops, in blocks.")
                .defineInRange("stasisRadius", 50, 1, 128);
        VIRACOCHA_STASIS_MAX_TICKS = BUILDER
                .comment("Longest a stasis can run before it lapses on its own, in ticks.",
                        "400 is twenty seconds. It is a toggle - right-click again to drop it early.",
                        "Ignored in creative, where it runs until switched off.")
                .defineInRange("stasisMaxTicks", 400, 20, 24000);
        VIRACOCHA_STASIS_COOLDOWN_TICKS = BUILDER
                .comment("Cooldown after a stasis ENDS, however it ended, in ticks. 300 is fifteen",
                        "seconds. Charged on deactivation rather than on cast, so dropping the",
                        "field early costs the same wait as running it to the end.",
                        "Not charged at all in creative.")
                .defineInRange("stasisCooldownTicks", 300, 0, 24000);
        VIRACOCHA_STASIS_COSTS_OFFERING = BUILDER
                .comment("Whether stopping time consumes an Offering. Turning this off makes the",
                        "staff far stronger - it is the only thing rationing its best power.")
                .define("stasisCostsOffering", true);
        VIRACOCHA_RESTORE_RADIUS = BUILDER
                .comment("How far from the targeted block the staff looks for masonry to rebuild.")
                .defineInRange("restoreRadius", 8, 1, 32);
        VIRACOCHA_REBIRTH_COOLDOWN_TICKS = BUILDER
                .comment("Cooldown on dying and coming back. One Minecraft day by default.")
                .defineInRange("rebirthCooldownTicks", 24000, 0, 240000);
        BUILDER.pop();

        BUILDER.comment("Pachamama's crown. The armour is the ability; the condition is where you stand.",
                        "Ability cooldowns run two to five seconds, not the ten to twenty they",
                        "started at - a bender who spends most of a fight waiting is not bending.")
                .push("pachamama");
        PACHAMAMA_REGEN_INTERVAL_TICKS = BUILDER
                .comment("How often the crown checks the ground and re-grants regeneration.")
                .defineInRange("regenIntervalTicks", 40, 5, 600);
        PACHAMAMA_REGEN_AMPLIFIER = BUILDER
                .comment("Regeneration level while standing on natural ground. 0 is Regeneration I.")
                .defineInRange("regenAmplifier", 1, 0, 4);

        PACHAMAMA_EARTH_WALL_COOLDOWN = BUILDER
                .comment("Earth, first key: raise a wall out of whatever you are standing on.")
                .defineInRange("earthWallCooldownTicks", 60, 0, 24000);
        PACHAMAMA_EARTH_BOULDER_COOLDOWN = BUILDER
                .comment("Earth, second key: tear a block out of the ground and throw it.")
                .defineInRange("earthBoulderCooldownTicks", 80, 0, 24000);
        PACHAMAMA_WATER_SHARDS_COOLDOWN = BUILDER
                .comment("Water, first key: a burst of ice shards pulled out of the damp air.")
                .defineInRange("waterShardsCooldownTicks", 60, 0, 24000);
        PACHAMAMA_AIR_SHOCKWAVE_COOLDOWN = BUILDER
                .comment("Air, first key: shove everything nearby away from you.")
                .defineInRange("airShockwaveCooldownTicks", 50, 0, 24000);
        PACHAMAMA_AIR_SCOOTER_COOLDOWN = BUILDER
                .comment("Air, second key: the ball of air you ride.")
                .defineInRange("airScooterCooldownTicks", 100, 0, 24000);
        PACHAMAMA_AIR_SCOOTER_DURATION = BUILDER
                .comment("How long the scooter lasts once it is up.")
                .defineInRange("airScooterDurationTicks", 300, 20, 6000);
        PACHAMAMA_FIRE_FIREBALL_COOLDOWN = BUILDER
                .comment("Fire, first key.")
                .defineInRange("fireballCooldownTicks", 40, 0, 24000);
        PACHAMAMA_FIRE_LIGHTNING_COOLDOWN = BUILDER
                .comment("Fire, second key. The strongest single thing the crown does, so the longest wait.")
                .defineInRange("lightningCooldownTicks", 100, 0, 24000);
        PACHAMAMA_BOULDER_DAMAGE = BUILDER
                .comment("Damage where the thrown boulder lands, to everything within four blocks.",
                        "The heaviest single hit in the crown, and it should be: the throw costs a",
                        "hole in the ground, a wind-up and a second input to release.")
                .defineInRange("boulderDamage", 18.0D, 0.0D, 100.0D);
        PACHAMAMA_SHARD_DAMAGE = BUILDER
                .comment("Per ice shard. Ten are held and each swing throws one, so every shard is",
                        "aimed on its own and most of them land. Worth about a good bow shot each:",
                        "they used to be a spray where landing three was a good volley.")
                .defineInRange("shardDamage", 6.0D, 0.0D, 100.0D);
        PACHAMAMA_FIREBALL_DAMAGE = BUILDER
                .defineInRange("fireballDamage", 7.0D, 0.0D, 100.0D);
        BUILDER.pop();

        BUILDER.comment("Kukulkan's mantle. Bought for movement, not for defence.")
                .push("kukulkan");
        KUKULKAN_DASH_SPEED = BUILDER
                .comment("Blocks per tick the dash sets velocity to. Set outright, not added, so a",
                        "dash is the same length whether you were sprinting or standing still.")
                .defineInRange("dashSpeed", 1.6D, 0.1D, 10.0D);
        KUKULKAN_DASH_COOLDOWN_TICKS = BUILDER
                .comment("Wait between dashes. Creative pays nothing.")
                .defineInRange("dashCooldownTicks", 160, 0, 24000);
        KUKULKAN_POISON_TICKS = BUILDER
                .comment("How long Poison II lasts on whatever the wearer hits in melee.",
                        "Undead and other poison-immune mobs shrug it off, as they do any poison.")
                .defineInRange("poisonTicks", 100, 0, 24000);
        KUKULKAN_BLOOM_RADIUS = BUILDER
                .comment("Radius in blocks of the bloom left behind by sneak-right-clicking.")
                .defineInRange("bloomRadius", 50, 1, 128);
        KUKULKAN_BLOOM_DURATION_TICKS = BUILDER
                .comment("How long a bloom lasts. Using the mantle again moves it and starts it over,",
                        "so this is how long you may walk away for, not a lockout.")
                .defineInRange("bloomDurationTicks", 6000, 20, 432000);
        KUKULKAN_BLOOM_GROWTH_BONUS = BUILDER
                .comment("Extra growth inside a bloom, as a fraction. 0.5 is the stated +50%.",
                        "Applied as additional random ticks on plants only, scaled off the",
                        "randomTickSpeed gamerule -- a server that has turned growth off stays off.")
                .defineInRange("bloomGrowthBonus", 0.5D, 0.0D, 8.0D);
        BUILDER.pop();

        BUILDER.pop();
    }

    public static final ModConfigSpec SPEC = BUILDER.build();
}
