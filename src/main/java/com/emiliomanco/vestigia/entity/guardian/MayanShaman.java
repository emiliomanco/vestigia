package com.emiliomanco.vestigia.entity.guardian;

import com.emiliomanco.vestigia.Civilization;
import com.emiliomanco.vestigia.Vestigia;
import com.emiliomanco.vestigia.registry.ModEntities;
import com.emiliomanco.vestigia.registry.ModSounds;
import com.geckolib.animation.RawAnimation;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;

@EventBusSubscriber(modid = Vestigia.MODID)
public class MayanShaman extends TemplePriest {

    private static final int RAISE_COOLDOWN = 200;

    private static final double RAISE_RANGE = 20.0D;

    private static final RawAnimation SPELL_CLIP = RawAnimation.begin().thenPlay("chaman_spell");
    private static final RawAnimation THUNDER_CLIP = RawAnimation.begin().thenPlay("chaman_call_thunder");
    private static final RawAnimation REVIVE_CLIP = RawAnimation.begin().thenPlay("chaman_revive");

    private static final int SPELL_CLIP_TICKS = 12;
    private static final int THUNDER_CLIP_TICKS = 30;
    private static final int REVIVE_CLIP_TICKS = 30;

    private record Corpse(BlockPos where, long forgetAt) {}

    private final List<Corpse> remembered = new ArrayList<>();
    private static final int CORPSE_MEMORY_TICKS = 1200;

    public MayanShaman(EntityType<? extends MayanShaman> type, Level level) {
        super(type, level, Civilization.MAYA, RAISE_COOLDOWN);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return priestAttributes();
    }

    @Override
    protected RawAnimation spellAnimation() {
        return SPELL_CLIP;
    }

    @Override
    protected RawAnimation thunderAnimation() {
        return THUNDER_CLIP;
    }

    @Override
    protected RawAnimation signatureAnimation() {
        return REVIVE_CLIP;
    }

    @Override
    protected int spellClipTicks() {
        return SPELL_CLIP_TICKS;
    }

    @Override
    protected int thunderClipTicks() {
        return THUNDER_CLIP_TICKS;
    }

    @Override
    protected int signatureClipTicks() {
        return REVIVE_CLIP_TICKS;
    }

    @Override
    protected SoundEvent castSound() {
        return ModSounds.SHAMAN_CURSE.get();
    }

    @Override
    protected SoundEvent quietSound() {
        return ModSounds.MAYA_FLUTE.get();
    }

    @Override
    protected SoundEvent loudSound() {
        return ModSounds.MAYA_BATTLECRY.get();
    }

    @SubscribeEvent
    static void onGuardianDeath(LivingDeathEvent event) {
        if (event.getEntity().getClass() != MayanWarrior.class
                || !(event.getEntity().level() instanceof ServerLevel level)) {
            return;
        }
        MayanWarrior fallen = (MayanWarrior) event.getEntity();
        List<MayanShaman> shamans = level.getEntitiesOfClass(MayanShaman.class,
                fallen.getBoundingBox().inflate(RAISE_RANGE), shaman -> shaman.isAlive());
        if (shamans.isEmpty()) {
            return;
        }
        shamans.getFirst().remembered.add(
                new Corpse(fallen.blockPosition(), level.getGameTime() + CORPSE_MEMORY_TICKS));
    }

    @Override
    protected boolean castSignature(ServerLevel level) {
        if (!ready(SIGNATURE)) {
            return false;
        }
        long now = level.getGameTime();
        remembered.removeIf(corpse -> corpse.forgetAt() < now);

        Corpse grave = null;
        for (Corpse corpse : remembered) {
            double dx = corpse.where().getX() + 0.5D - getX();
            double dz = corpse.where().getZ() + 0.5D - getZ();
            double dy = corpse.where().getY() - getY();
            if (dx * dx + dy * dy + dz * dz <= RAISE_RANGE * RAISE_RANGE) {
                grave = corpse;
                break;
            }
        }
        if (grave == null) {
            return false;
        }
        remembered.remove(grave);

        MayanZombie risen = ModEntities.MAYAN_ZOMBIE.get().create(level, EntitySpawnReason.MOB_SUMMONED);
        if (risen == null) {
            return false;
        }
        BlockPos where = grave.where();
        risen.snapTo(where.getX() + 0.5D, where.getY(), where.getZ() + 0.5D, getYRot(), 0.0F);
        risen.finalizeSpawn(level, level.getCurrentDifficultyAt(where),
                EntitySpawnReason.MOB_SUMMONED, null);
        risen.raiseTemporarily();
        risen.setTarget(getTarget());
        level.addFreshEntity(risen);

        cast(SIGNATURE, SIGNATURE_TRIGGER, signatureClipTicks());
        level.sendParticles(ParticleTypes.SOUL, where.getX() + 0.5D, where.getY() + 1.0D,
                where.getZ() + 0.5D, 30, 0.5D, 0.8D, 0.5D, 0.02D);
        level.playSound(null, where, quietSound(), SoundSource.HOSTILE, 1.2F, 0.7F);
        return true;
    }

}
