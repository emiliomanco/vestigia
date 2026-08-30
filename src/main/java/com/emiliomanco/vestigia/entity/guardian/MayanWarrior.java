package com.emiliomanco.vestigia.entity.guardian;

import com.emiliomanco.vestigia.Civilization;
import com.emiliomanco.vestigia.registry.ModEffects;
import com.emiliomanco.vestigia.registry.ModItems;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class MayanWarrior extends TempleSoldier {

    private static final int BLEED_DURATION = 60;

    public MayanWarrior(EntityType<? extends MayanWarrior> type, Level level) {
        super(type, level, Civilization.MAYA);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return baseAttributes()
                .add(Attributes.MAX_HEALTH, 20.0D)
                .add(Attributes.ATTACK_DAMAGE, 7.0D)
                .add(Attributes.ARMOR, 3.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.32D)
                .add(Attributes.STEP_HEIGHT, 0.6D);
    }

    @Override
    protected ItemStack chooseWeapon(RandomSource random) {
        int roll = random.nextInt(10);
        return roll < 2 ? new ItemStack(Items.BOW)
                : roll < 4 ? new ItemStack(ModItems.BLOWGUN.get())
                : roll < 7 ? new ItemStack(ModItems.OBSIDIAN_SPEAR.get())
                : new ItemStack(ModItems.MACUAHUITL.get());
    }

    @Override
    protected void onMeleeHit(ServerLevel level, LivingEntity target) {
        target.addEffect(new MobEffectInstance(ModEffects.BLEED, BLEED_DURATION, 0), this);
    }

}
