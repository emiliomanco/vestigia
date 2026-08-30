package com.emiliomanco.vestigia.entity.guardian;

import com.emiliomanco.vestigia.Civilization;
import com.emiliomanco.vestigia.registry.ModItems;
import net.minecraft.world.BossEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class MayanNacom extends TempleLord {

    public MayanNacom(EntityType<? extends MayanNacom> type, Level level) {
        super(type, level, Civilization.MAYA, BossEvent.BossBarColor.YELLOW);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return lordAttributes();
    }

    @Override
    protected ItemStack weapon() {
        return new ItemStack(ModItems.MACUAHUITL.get());
    }
}
