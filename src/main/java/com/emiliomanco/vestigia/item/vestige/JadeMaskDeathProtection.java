package com.emiliomanco.vestigia.item.vestige;

import com.emiliomanco.vestigia.Vestigia;
import com.emiliomanco.vestigia.network.ItemActivationPayload;
import com.emiliomanco.vestigia.registry.ModItems;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;

@EventBusSubscriber(modid = Vestigia.MODID)
public final class JadeMaskDeathProtection {
    private JadeMaskDeathProtection() {}

    @SubscribeEvent
    static void onDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }
        if (event.getSource().is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            return;
        }
        ItemStack worn = player.getItemBySlot(EquipmentSlot.HEAD);
        if (!worn.is(ModItems.JADE_MASK.get())) {
            return;
        }

        ItemStack shown = worn.copyWithCount(1);

        event.setCanceled(true);
        worn.shrink(1);
        player.setHealth(player.getMaxHealth());
        player.removeAllEffects();
        player.clearFire();

        if (player.level() instanceof ServerLevel level) {
            level.sendParticles(ParticleTypes.TOTEM_OF_UNDYING,
                    player.getX(), player.getY() + player.getBbHeight() * 0.5D, player.getZ(),
                    60, 0.5D, 0.6D, 0.5D, 0.35D);
        }
        player.level().playSound(null, player.blockPosition(), SoundEvents.TOTEM_USE,
                SoundSource.PLAYERS, 1.0F, 0.8F);
        if (player instanceof ServerPlayer saved) {
            PacketDistributor.sendToPlayer(saved, new ItemActivationPayload(shown));
        }
    }
}
