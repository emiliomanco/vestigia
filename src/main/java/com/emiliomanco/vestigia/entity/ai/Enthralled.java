package com.emiliomanco.vestigia.entity.ai;

import com.emiliomanco.vestigia.Vestigia;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import org.jspecify.annotations.Nullable;

@EventBusSubscriber(modid = Vestigia.MODID)
public final class Enthralled {
    private Enthralled() {}

    private static final float BARE_HANDED_DAMAGE = 2.0F;
    private static final double REACH_SQR = 4.0D;
    private static final int SWING_COOLDOWN_TICKS = 20;
    private static final double CHASE_SPEED = 1.2D;

    private record Thrall(UUID victim, int ticksLeft, int swingCooldown) {}

    private static final Map<UUID, Thrall> ACTIVE = new HashMap<>();

    public static void bind(Mob mob, LivingEntity victim, int ticks) {
        ACTIVE.put(mob.getUUID(), new Thrall(victim.getUUID(), ticks, 0));
    }

    public static boolean isEnthralled(Entity entity) {
        return ACTIVE.containsKey(entity.getUUID());
    }

    @SubscribeEvent
    static void onEntityTick(EntityTickEvent.Post event) {
        if (ACTIVE.isEmpty() || !(event.getEntity() instanceof Mob mob)
                || !(mob.level() instanceof ServerLevel level)) {
            return;
        }
        Thrall thrall = ACTIVE.get(mob.getUUID());
        if (thrall == null) {
            return;
        }

        LivingEntity victim = resolve(level, thrall.victim());
        if (thrall.ticksLeft() <= 0 || victim == null || !victim.isAlive() || !mob.isAlive()) {
            release(mob);
            return;
        }

        mob.setTarget(victim);
        mob.getLookControl().setLookAt(victim, 30.0F, 30.0F);

        int cooldown = thrall.swingCooldown();
        if (mob.distanceToSqr(victim) > REACH_SQR) {
            mob.getNavigation().moveTo(victim, CHASE_SPEED);
        } else if (cooldown <= 0) {
            strike(level, mob, victim);
            cooldown = SWING_COOLDOWN_TICKS;
        }

        ACTIVE.put(mob.getUUID(), new Thrall(thrall.victim(), thrall.ticksLeft() - 1, Math.max(0, cooldown - 1)));
    }

    private static void strike(ServerLevel level, Mob mob, LivingEntity victim) {
        mob.swing(net.minecraft.world.InteractionHand.MAIN_HAND);
        double damage = mob.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE)
                ? mob.getAttributeValue(Attributes.ATTACK_DAMAGE)
                : 0.0D;
        if (damage > 0.0D) {
            mob.doHurtTarget(level, victim);
            return;
        }
        victim.hurtServer(level, mob.damageSources().mobAttack(mob), BARE_HANDED_DAMAGE);
    }

    private static void release(Mob mob) {
        ACTIVE.remove(mob.getUUID());
        if (mob.isAlive()) {
            mob.setTarget(null);
            mob.getNavigation().stop();
        }
    }

    private static @Nullable LivingEntity resolve(ServerLevel level, UUID id) {
        return level.getEntity(id) instanceof LivingEntity living ? living : null;
    }

    public static void clear() {
        ACTIVE.clear();
    }

    @SubscribeEvent
    static void onServerTick(net.neoforged.neoforge.event.tick.ServerTickEvent.Post event) {
        if (ACTIVE.isEmpty() || event.getServer().getTickCount() % 200 != 0) {
            return;
        }
        Iterator<Map.Entry<UUID, Thrall>> entries = ACTIVE.entrySet().iterator();
        while (entries.hasNext()) {
            UUID id = entries.next().getKey();
            boolean present = false;
            for (ServerLevel level : event.getServer().getAllLevels()) {
                if (level.getEntity(id) != null) {
                    present = true;
                    break;
                }
            }
            if (!present) {
                entries.remove();
            }
        }
    }
}
