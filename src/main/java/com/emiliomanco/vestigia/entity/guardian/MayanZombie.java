package com.emiliomanco.vestigia.entity.guardian;

import com.emiliomanco.vestigia.Civilization;
import com.emiliomanco.vestigia.entity.ai.SpearRetrieveGoal;
import com.emiliomanco.vestigia.entity.ai.SpearThrowGoal;
import com.emiliomanco.vestigia.registry.ModItems;
import com.geckolib.animation.AnimationController;
import com.geckolib.animation.RawAnimation;
import com.geckolib.animation.object.PlayState;
import com.geckolib.animatable.manager.AnimatableManager;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class MayanZombie extends AncestralGuardian {

    public static final int RAISED_LIFESPAN_TICKS = 1200;
    private static final int PERMANENT = -1;

    private static final int EMERGING_TICKS = 40;

    private int remainingLife = PERMANENT;
    private int emerging;
    private boolean announceRise;

    public MayanZombie(EntityType<? extends MayanZombie> type, Level level) {
        super(type, level, Civilization.MAYA);
    }

    public void raiseTemporarily() {
        remainingLife = RAISED_LIFESPAN_TICKS;
        emerging = EMERGING_TICKS;
        announceRise = true;
        setClipRunning(true);
    }

    public boolean isRaised() {
        return remainingLife != PERMANENT;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return baseAttributes()
                .add(Attributes.MAX_HEALTH, 20.0D)
                .add(Attributes.ATTACK_DAMAGE, 5.0D)
                .add(Attributes.ARMOR, 2.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.25D);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        goalSelector.addGoal(1, new SpearThrowGoal(this));
        goalSelector.addGoal(1, new SpearRetrieveGoal(this));
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty,
                                        EntitySpawnReason reason, SpawnGroupData data) {
        SpawnGroupData result = super.finalizeSpawn(level, difficulty, reason, data);
        RandomSource random = level.getRandom();
        ItemStack weapon = random.nextInt(2) == 0
                ? new ItemStack(ModItems.OBSIDIAN_SPEAR.get())
                : new ItemStack(ModItems.MACUAHUITL.get());
        setItemSlot(EquipmentSlot.MAINHAND, weapon);
        setDropChance(EquipmentSlot.MAINHAND, 0.05F);
        return result;
    }

    @Override
    protected boolean isImmobile() {
        return super.isImmobile() || emerging > 0;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (level().isClientSide()) {
            return;
        }
        if (announceRise && tickCount > 2) {
            announceRise = false;
            triggerAnim(RISING, "rise");
        }
        if (emerging > 0) {
            getNavigation().stop();
            if (--emerging == 0) {
                setClipRunning(false);
            }
        }
        if (!isRaised()) {
            return;
        }
        if (--remainingLife <= 0) {
            discard();
        }
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return getTarget() == null ? SoundEvents.ZOMBIE_AMBIENT : MayaVoice.idle(getTarget());
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ZOMBIE_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ZOMBIE_DEATH;
    }

    private static final RawAnimation RISE = RawAnimation.begin().thenPlay("spawn_mayan_zombie");
    private static final String RISING = "rising";

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        registerSpearControllers(controllers);
        controllers.add(new AnimationController<MayanZombie>(RISING, 0, state -> PlayState.STOP)
                .triggerableAnim("rise", RISE));
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        remainingLife = input.getIntOr("RemainingLife", PERMANENT);
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putInt("RemainingLife", remainingLife);
    }
}
