package com.emiliomanco.vestigia.entity.guardian;

import com.emiliomanco.vestigia.Civilization;
import com.emiliomanco.vestigia.registry.ModItems;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class IncanWarrior extends TempleSoldier {

    public IncanWarrior(EntityType<? extends IncanWarrior> type, Level level) {
        super(type, level, Civilization.INCA);
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
                : new ItemStack(ModItems.INCAN_CLUB.get());
    }
}
