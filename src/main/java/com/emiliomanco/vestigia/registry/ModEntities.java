package com.emiliomanco.vestigia.registry;

import com.emiliomanco.vestigia.Vestigia;
import com.emiliomanco.vestigia.entity.AirScooter;
import com.emiliomanco.vestigia.entity.RaisedEarth;
import com.emiliomanco.vestigia.entity.animal.Jaguar;
import com.emiliomanco.vestigia.entity.projectile.ElementalBolt;
import com.emiliomanco.vestigia.entity.guardian.MayanWarrior;
import com.emiliomanco.vestigia.entity.guardian.IncanApuskipay;
import com.emiliomanco.vestigia.entity.guardian.IncanPriest;
import com.emiliomanco.vestigia.entity.guardian.IncanWarrior;
import com.emiliomanco.vestigia.entity.guardian.MayanZombie;
import com.emiliomanco.vestigia.entity.guardian.MayanShaman;
import com.emiliomanco.vestigia.entity.guardian.MayanNacom;
import com.emiliomanco.vestigia.entity.projectile.ObsidianSpearProjectile;
import com.emiliomanco.vestigia.entity.projectile.CurareDartProjectile;
import com.emiliomanco.vestigia.entity.projectile.ThrownSunDisc;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModEntities {
    private ModEntities() {}

    public static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(Registries.ENTITY_TYPE, Vestigia.MODID);

    public static final DeferredHolder<EntityType<?>, EntityType<ObsidianSpearProjectile>> OBSIDIAN_SPEAR =
            ENTITIES.register("obsidian_spear", id -> EntityType.Builder
                    .<ObsidianSpearProjectile>of(ObsidianSpearProjectile::new, MobCategory.MISC)
                    .noLootTable()
                    .sized(0.5F, 0.5F)
                    .clientTrackingRange(4)
                    .updateInterval(20)
                    .build(ResourceKey.create(Registries.ENTITY_TYPE, id)));

    public static final DeferredHolder<EntityType<?>, EntityType<CurareDartProjectile>> DART =
            ENTITIES.register("dart", id -> EntityType.Builder
                    .<CurareDartProjectile>of(CurareDartProjectile::new, MobCategory.MISC)
                    .noLootTable()
                    .sized(0.2F, 0.2F)
                    .clientTrackingRange(4)
                    .updateInterval(20)
                    .build(ResourceKey.create(Registries.ENTITY_TYPE, id)));

    public static final DeferredHolder<EntityType<?>, EntityType<ThrownSunDisc>> SUN_DISC =
            ENTITIES.register("sun_disc", id -> EntityType.Builder
                    .<ThrownSunDisc>of(ThrownSunDisc::new, MobCategory.MISC)
                    .noLootTable()
                    .fireImmune()
                    .sized(0.6F, 0.6F)
                    .clientTrackingRange(8)
                    .updateInterval(1)
                    .build(ResourceKey.create(Registries.ENTITY_TYPE, id)));

    public static final DeferredHolder<EntityType<?>, EntityType<ElementalBolt>> ICE_SPIKE =
            ENTITIES.register("ice_spike", id -> EntityType.Builder
                    .<ElementalBolt>of(ElementalBolt::new, MobCategory.MISC)
                    .noLootTable()
                    .sized(0.5F, 0.5F)
                    .clientTrackingRange(6)
                    .updateInterval(1)
                    .build(ResourceKey.create(Registries.ENTITY_TYPE, id)));

    public static final DeferredHolder<EntityType<?>, EntityType<RaisedEarth>> BLOCK_PROJECTILE =
            ENTITIES.register("block_projectile", id -> EntityType.Builder
                    .<RaisedEarth>of(RaisedEarth::new, MobCategory.MISC)
                    .noLootTable()
                    .sized(0.98F, 0.98F)
                    .clientTrackingRange(10)
                    .updateInterval(1)
                    .build(ResourceKey.create(Registries.ENTITY_TYPE, id)));

    public static final DeferredHolder<EntityType<?>, EntityType<AirScooter>> AIR_SCOOTER =
            ENTITIES.register("air_scooter", id -> EntityType.Builder
                    .<AirScooter>of(AirScooter::new, MobCategory.MISC)
                    .noLootTable()
                    .sized(0.8F, 0.8F)
                    .clientTrackingRange(10)
                    .updateInterval(1)
                    .build(ResourceKey.create(Registries.ENTITY_TYPE, id)));

    public static final DeferredHolder<EntityType<?>, EntityType<Jaguar>> JAGUAR =
            ENTITIES.register("jaguar", id -> EntityType.Builder
                    .of(Jaguar::new, MobCategory.CREATURE)
                    .sized(1.0F, 1.25F)
                    .eyeHeight(1.0F)
                    .clientTrackingRange(10)
                    .build(ResourceKey.create(Registries.ENTITY_TYPE, id)));

    public static final DeferredHolder<EntityType<?>, EntityType<MayanWarrior>> MAYAN_WARRIOR =
            ENTITIES.register("mayan_warrior", id -> EntityType.Builder
                    .of(MayanWarrior::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.9F)
                    .eyeHeight(1.7F)
                    .clientTrackingRange(10)
                    .build(ResourceKey.create(Registries.ENTITY_TYPE, id)));

    public static final DeferredHolder<EntityType<?>, EntityType<MayanZombie>> MAYAN_ZOMBIE =
            ENTITIES.register("mayan_zombie", id -> EntityType.Builder
                    .of(MayanZombie::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.9F)
                    .eyeHeight(1.7F)
                    .clientTrackingRange(10)
                    .build(ResourceKey.create(Registries.ENTITY_TYPE, id)));

    public static final DeferredHolder<EntityType<?>, EntityType<MayanShaman>> MAYAN_SHAMAN =
            ENTITIES.register("mayan_shaman", id -> EntityType.Builder
                    .of(MayanShaman::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.9F)
                    .eyeHeight(1.7F)
                    .clientTrackingRange(10)
                    .build(ResourceKey.create(Registries.ENTITY_TYPE, id)));

    public static final DeferredHolder<EntityType<?>, EntityType<MayanNacom>> MAYAN_NACOM =
            ENTITIES.register("mayan_nacom", id -> EntityType.Builder
                    .of(MayanNacom::new, MobCategory.MONSTER)
                    .sized(1.5F, 4.0F)
                    .eyeHeight(3.6F)
                    .clientTrackingRange(16)
                    .build(ResourceKey.create(Registries.ENTITY_TYPE, id)));

    public static final DeferredHolder<EntityType<?>, EntityType<IncanWarrior>> INCAN_WARRIOR =
            ENTITIES.register("incan_warrior", id -> EntityType.Builder
                    .of(IncanWarrior::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.9F)
                    .eyeHeight(1.7F)
                    .clientTrackingRange(10)
                    .build(ResourceKey.create(Registries.ENTITY_TYPE, id)));

    public static final DeferredHolder<EntityType<?>, EntityType<IncanPriest>> INCAN_PRIEST =
            ENTITIES.register("incan_priest", id -> EntityType.Builder
                    .of(IncanPriest::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.9F)
                    .eyeHeight(1.7F)
                    .clientTrackingRange(10)
                    .build(ResourceKey.create(Registries.ENTITY_TYPE, id)));

    public static final DeferredHolder<EntityType<?>, EntityType<IncanApuskipay>> INCAN_APUSKIPAY =
            ENTITIES.register("incan_apuskipay", id -> EntityType.Builder
                    .of(IncanApuskipay::new, MobCategory.MONSTER)
                    .sized(1.5F, 4.0F)
                    .eyeHeight(3.6F)
                    .clientTrackingRange(16)
                    .build(ResourceKey.create(Registries.ENTITY_TYPE, id)));

    public static void register(IEventBus modEventBus) {
        ENTITIES.register(modEventBus);
    }

    public static void onCreateAttributes(EntityAttributeCreationEvent event) {
        event.put(MAYAN_WARRIOR.get(), MayanWarrior.createAttributes().build());
        event.put(MAYAN_ZOMBIE.get(), MayanZombie.createAttributes().build());
        event.put(MAYAN_SHAMAN.get(), MayanShaman.createAttributes().build());
        event.put(MAYAN_NACOM.get(), MayanNacom.createAttributes().build());
        event.put(INCAN_WARRIOR.get(), IncanWarrior.createAttributes().build());
        event.put(INCAN_PRIEST.get(), IncanPriest.createAttributes().build());
        event.put(INCAN_APUSKIPAY.get(), IncanApuskipay.createAttributes().build());
        event.put(JAGUAR.get(), Jaguar.createAttributes().build());
    }
}
