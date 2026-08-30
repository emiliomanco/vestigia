package com.emiliomanco.vestigia.item.vestige;

import com.emiliomanco.vestigia.Vestigia;
import com.emiliomanco.vestigia.config.VestigiaConfig;
import com.emiliomanco.vestigia.registry.ModItems;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = Vestigia.MODID)
public final class VestigePassives {
    private VestigePassives() {}

    private static final int SCAN_INTERVAL_TICKS = 10;

    private static final Identifier PUNCHAO_ARMOR_ID = Vestigia.id("punchao_sunlit");

    @SubscribeEvent
    static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player)
                || !VestigiaConfig.ENABLE_VESTIGE_PASSIVES.get()
                || player.tickCount % SCAN_INTERVAL_TICKS != 0) {
            return;
        }

        applyPunchao(player, carries(player, ModItems.PUNCHAO.get()));
    }

    private static void applyPunchao(ServerPlayer player, boolean carrying) {
        AttributeInstance armor = player.getAttribute(Attributes.ARMOR);
        if (armor == null) {
            return;
        }
        long dayTime = Math.floorMod(player.level().getOverworldClockTime(), 24000L);
        boolean shouldApply = carrying
                && dayTime < 12000L
                && player.level().canSeeSky(player.blockPosition());

        if (shouldApply) {
            if (armor.getModifier(PUNCHAO_ARMOR_ID) == null) {
                armor.addTransientModifier(new AttributeModifier(
                        PUNCHAO_ARMOR_ID,
                        VestigiaConfig.PUNCHAO_SUNLIT_ARMOR.get(),
                        AttributeModifier.Operation.ADD_VALUE));
            }
        } else {
            armor.removeModifier(PUNCHAO_ARMOR_ID);
        }
    }

    private static void scatterNearbyHostiles(ServerPlayer player) {
        double radius = VestigiaConfig.LANZON_PANIC_RADIUS.get();
        double chance = VestigiaConfig.LANZON_PANIC_CHANCE.get() * (SCAN_INTERVAL_TICKS / 20.0D);
        int ticks = VestigiaConfig.LANZON_PANIC_TICKS.get();

        for (Mob mob : player.level().getEntitiesOfClass(Mob.class,
                player.getBoundingBox().inflate(radius),
                candidate -> candidate instanceof Enemy)) {
            if (player.getRandom().nextDouble() >= chance) {
                continue;
            }
            mob.setTarget(null);
            mob.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                    MobEffects.SPEED, ticks, 1, false, false));
            fleeFrom(mob, player, ticks);
        }
    }

    private static void fleeFrom(Mob mob, Player from, int ticks) {
        var away = mob.position().subtract(from.position()).normalize().scale(8.0D);
        var target = mob.position().add(away);
        mob.getNavigation().moveTo(target.x, target.y, target.z, 1.4D);
        mob.setNoActionTime(0);
        mob.getBrain().clearMemories();
    }

    private static void floatOnWater(ServerPlayer player) {
        if (!player.isInWater() || player.isCrouching()) {
            return;
        }
        var motion = player.getDeltaMovement();
        if (motion.y < 0.0D) {
            player.setDeltaMovement(motion.x, Math.min(motion.y + 0.06D, 0.0D), motion.z);
            player.hurtMarked = true;
        }
    }

    public static boolean carries(Player player, Item vestige) {
        for (ItemStack stack : player.getInventory()) {
            if (stack.is(vestige)) {
                return true;
            }
        }
        return false;
    }

}
