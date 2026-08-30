package com.emiliomanco.vestigia.item.god.pachamama;

import com.emiliomanco.vestigia.config.VestigiaConfig;
import com.emiliomanco.vestigia.entity.AirScooter;
import com.emiliomanco.vestigia.entity.RaisedEarth;
import com.emiliomanco.vestigia.entity.projectile.ElementalBolt;
import com.emiliomanco.vestigia.registry.ModDataComponents;
import com.emiliomanco.vestigia.registry.ModItems;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public final class Bending {
    private Bending() {}

    private static final double REACH = 24.0D;

    private static final int HUMIDITY_RANGE = 8;

    private static final int GROUND_SEARCH_DEPTH = 3;

    private static final int SHARD_COUNT = 10;

    private static final double SHOCKWAVE_STRENGTH = 4.5D;

    public static @Nullable ItemStack worn(LivingEntity entity) {
        ItemStack head = entity.getItemBySlot(EquipmentSlot.HEAD);
        return head.is(ModItems.CROWN_OF_PACHAMAMA.get()) || head.is(ModItems.SUPREME_CROWN.get())
                ? head : null;
    }

    public static @Nullable BendingBranch branchOf(ItemStack stack) {
        return stack.get(ModDataComponents.BENDING_BRANCH.get());
    }

    public static @Nullable BendingBranch branchOf(LivingEntity entity) {
        ItemStack crown = worn(entity);
        return crown == null ? null : branchOf(crown);
    }

    public static void use(ServerPlayer player, int slot) {
        ItemStack crown = worn(player);
        if (crown == null) {
            return;
        }
        BendingBranch branch = branchOf(crown);
        if (branch == null) {
            tell(player, Component.translatable("item.vestigia.corona_pachamama.unbound")
                    .withStyle(ChatFormatting.GRAY));
            return;
        }
        if (!(player.level() instanceof ServerLevel level)) {
            return;
        }

        switch (branch) {
            case EARTH -> {
                if (slot == 0) {
                    raiseWall(level, player);
                } else {
                    throwBoulder(level, player);
                }
            }
            case WATER -> {
                if (slot == 0) {
                    iceShards(level, player);
                }
            }
            case AIR -> {
                if (slot == 0) {
                    shockwave(level, player);
                } else {
                    airScooter(level, player);
                }
            }
            case FIRE -> {
                if (slot == 0) {
                    fireball(level, player);
                } else {
                    lightning(level, player);
                }
            }
        }
    }

    private static void raiseWall(ServerLevel level, ServerPlayer player) {
        if (onCooldown(player, 0)) {
            return;
        }
        BlockState material = groundMaterial(level, player);
        if (material == null) {
            tell(player, Component.translatable("item.vestigia.corona_pachamama.no_ground")
                    .withStyle(ChatFormatting.GRAY));
            return;
        }
        Vec3 facing = player.getLookAngle().multiply(1.0D, 0.0D, 1.0D).normalize();
        if (facing.lengthSqr() < 1.0E-4D) {
            facing = new Vec3(0.0D, 0.0D, 1.0D);
        }
        Vec3 across = new Vec3(-facing.z, 0.0D, facing.x);
        BlockPos origin = BlockPos.containing(player.position().add(facing.scale(2.5D)));

        int placed = 0;
        for (int lateral = -2; lateral <= 2; lateral++) {
            BlockPos column = BlockPos.containing(
                    origin.getX() + across.x * lateral,
                    origin.getY(),
                    origin.getZ() + across.z * lateral);
            BlockPos footing = column;
            for (int drop = 0; drop < 3 && level.getBlockState(footing.below()).isAir(); drop++) {
                footing = footing.below();
            }
            int ceiling = Mth.floor(player.getY()) + 2;
            for (int height = 0; height < 3 && footing.getY() + height <= ceiling; height++) {
                BlockPos target = footing.above(height);
                if (level.getBlockState(target).canBeReplaced()) {
                    level.setBlockAndUpdate(target, material);
                    placed++;
                }
            }
        }
        if (placed == 0) {
            return;
        }
        level.playSound(null, player.blockPosition(), SoundEvents.ROOTED_DIRT_PLACE, SoundSource.PLAYERS, 1.0F, 0.6F);
        setCooldown(player, 0, VestigiaConfig.PACHAMAMA_EARTH_WALL_COOLDOWN.get());
    }

    private static void throwBoulder(ServerLevel level, ServerPlayer player) {
        if (onCooldown(player, 1)) {
            return;
        }
        BlockPos source = sourceBlockAhead(level, player);
        if (source == null) {
            tell(player, Component.translatable("item.vestigia.corona_pachamama.no_ground")
                    .withStyle(ChatFormatting.GRAY));
            return;
        }
        RaisedEarth boulder = RaisedEarth.raise(level, player, source,
                VestigiaConfig.PACHAMAMA_BOULDER_DAMAGE.get().floatValue());
        if (boulder == null) {
            return;
        }
        level.addFreshEntity(boulder);

        tell(player, Component.translatable("item.vestigia.corona_pachamama.boulder_ready")
                .withStyle(ChatFormatting.GRAY));
        setCooldown(player, 1, VestigiaConfig.PACHAMAMA_EARTH_BOULDER_COOLDOWN.get());
    }

    private static @Nullable BlockPos sourceBlockAhead(ServerLevel level, ServerPlayer player) {
        Vec3 ahead = player.position().add(
                player.getLookAngle().multiply(1.0D, 0.0D, 1.0D).normalize().scale(2.5D));
        BlockPos.MutableBlockPos cursor = BlockPos.containing(ahead.x, player.getY(), ahead.z).mutable();
        for (int drop = 0; drop < 8; drop++) {
            BlockState state = level.getBlockState(cursor);
            if (state.isSolid() && state.getDestroySpeed(level, cursor) >= 0.0F && !state.hasBlockEntity()) {
                return cursor.immutable();
            }
            cursor.move(0, -1, 0);
        }
        return null;
    }

    private static @Nullable BlockState groundMaterial(ServerLevel level, ServerPlayer player) {
        BlockPos foot = BlockPos.containing(player.getX(), player.getBoundingBox().minY - 0.1D, player.getZ());
        for (int drop = 0; drop < GROUND_SEARCH_DEPTH; drop++) {
            BlockState under = level.getBlockState(foot.below(drop));
            if (!under.isSolid()) {
                continue;
            }
            if (under.is(BlockTags.TERRACOTTA) || under.is(Blocks.TERRACOTTA)) {
                return under;
            }
            if (under.is(Blocks.DEEPSLATE) || under.is(Blocks.COBBLED_DEEPSLATE)
                    || under.is(Blocks.TUFF) || under.is(BlockTags.DEEPSLATE_ORE_REPLACEABLES)) {
                return Blocks.COBBLED_DEEPSLATE.defaultBlockState();
            }
            if (under.is(BlockTags.BASE_STONE_OVERWORLD) || under.is(Blocks.GRAVEL)) {
                return Blocks.COBBLESTONE.defaultBlockState();
            }
            if (under.is(BlockTags.BASE_STONE_NETHER)) {
                return Blocks.BLACKSTONE.defaultBlockState();
            }
            if (under.is(Blocks.RED_SAND)) {
                return Blocks.RED_SANDSTONE.defaultBlockState();
            }
            if (under.is(BlockTags.SAND)) {
                return Blocks.SANDSTONE.defaultBlockState();
            }
            if (under.is(Blocks.MUD) || under.is(Blocks.CLAY) || under.is(Blocks.MUDDY_MANGROVE_ROOTS)) {
                return Blocks.PACKED_MUD.defaultBlockState();
            }
            if (under.is(BlockTags.SNOW) || under.is(Blocks.SNOW_BLOCK) || under.is(Blocks.POWDER_SNOW)) {
                return Blocks.SNOW_BLOCK.defaultBlockState();
            }
            if (under.is(Blocks.SOUL_SAND) || under.is(Blocks.SOUL_SOIL)) {
                return Blocks.SOUL_SOIL.defaultBlockState();
            }
            return Blocks.COARSE_DIRT.defaultBlockState();
        }
        return null;
    }

    private static void iceShards(ServerLevel level, ServerPlayer player) {
        if (onCooldown(player, 0)) {
            return;
        }
        if (!hasWaterToBend(level, player)) {
            tell(player, Component.translatable("item.vestigia.corona_pachamama.too_dry")
                    .withStyle(ChatFormatting.AQUA));
            return;
        }

        IceVolley.clearHeld(level, player);

        float damage = VestigiaConfig.PACHAMAMA_SHARD_DAMAGE.get().floatValue();
        for (int shard = 0; shard < SHARD_COUNT; shard++) {
            ElementalBolt bolt = new ElementalBolt(level, player, ElementalBolt.Element.ICE, damage);
            bolt.hold(-1, (float) (Math.PI * 2.0D * shard / SHARD_COUNT), 1.9F);
            level.addFreshEntity(bolt);
        }
        tell(player, Component.translatable("item.vestigia.corona_pachamama.shards_ready")
                .withStyle(ChatFormatting.AQUA));
        level.playSound(null, player.blockPosition(), SoundEvents.GLASS_BREAK, SoundSource.PLAYERS, 0.9F, 1.4F);
        setCooldown(player, 0, VestigiaConfig.PACHAMAMA_WATER_SHARDS_COOLDOWN.get());
    }

    private static boolean hasWaterToBend(ServerLevel level, ServerPlayer player) {
        BlockPos at = player.blockPosition();
        if (level.getBiome(at).value().hasPrecipitation()) {
            return true;
        }
        for (BlockPos pos : BlockPos.betweenClosed(at.offset(-HUMIDITY_RANGE, -HUMIDITY_RANGE, -HUMIDITY_RANGE),
                at.offset(HUMIDITY_RANGE, HUMIDITY_RANGE, HUMIDITY_RANGE))) {
            if (level.getFluidState(pos).is(FluidTags.WATER)) {
                return true;
            }
        }
        return false;
    }

    public static void firePassives(ServerLevel level, ServerPlayer player) {
        player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 60, 0, true, false));
    }

    public static void waterPassives(ServerLevel level, ServerPlayer player) {
        player.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, 60, 0, true, false));
        if (player.isInWater()) {
            player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 60, 0, true, false));
        }
    }

    private static void shockwave(ServerLevel level, ServerPlayer player) {
        if (onCooldown(player, 0)) {
            return;
        }
        double radius = 6.0D;
        for (Entity target : level.getEntities(player, new AABB(player.blockPosition()).inflate(radius))) {
            Vec3 away = target.position().subtract(player.position());
            if (away.lengthSqr() < 1.0E-4D || away.length() > radius) {
                continue;
            }
            double strength = SHOCKWAVE_STRENGTH * (1.0D - away.length() / radius);
            Vec3 push = away.normalize().scale(strength).add(0.0D, 0.35D, 0.0D);
            target.setDeltaMovement(target.getDeltaMovement().add(push));
            target.hurtMarked = true;
        }
        for (int step = 0; step < 48; step++) {
            double angle = Math.PI * 2.0D * step / 48.0D;
            level.sendParticles(ParticleTypes.GUST,
                    player.getX() + Math.cos(angle) * radius, player.getY() + 0.3D,
                    player.getZ() + Math.sin(angle) * radius, 1, 0.0D, 0.0D, 0.0D, 0.0D);
            level.sendParticles(ParticleTypes.CLOUD,
                    player.getX() + Math.cos(angle) * radius * 0.55D, player.getY() + 0.2D,
                    player.getZ() + Math.sin(angle) * radius * 0.55D, 1, 0.0D, 0.02D, 0.0D, 0.01D);
        }
        level.playSound(null, player.blockPosition(), SoundEvents.BREEZE_WIND_CHARGE_BURST.value(),
                SoundSource.PLAYERS, 1.0F, 1.0F);
        setCooldown(player, 0, VestigiaConfig.PACHAMAMA_AIR_SHOCKWAVE_COOLDOWN.get());
    }

    private static void airScooter(ServerLevel level, ServerPlayer player) {
        if (onCooldown(player, 1)) {
            return;
        }
        AirScooter scooter = new AirScooter(level, player);
        level.addFreshEntity(scooter);
        player.startRiding(scooter, true, true);

        level.playSound(null, player.blockPosition(), SoundEvents.BREEZE_SHOOT, SoundSource.PLAYERS, 0.9F, 1.5F);
        setCooldown(player, 1, VestigiaConfig.PACHAMAMA_AIR_SCOOTER_COOLDOWN.get());
    }

    private static void fireball(ServerLevel level, ServerPlayer player) {
        if (onCooldown(player, 0)) {
            return;
        }
        ElementalBolt bolt = new ElementalBolt(level, player, ElementalBolt.Element.FIRE,
                VestigiaConfig.PACHAMAMA_FIREBALL_DAMAGE.get().floatValue());
        bolt.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 1.5F, 1.0F);
        level.addFreshEntity(bolt);

        level.playSound(null, player.blockPosition(), SoundEvents.FIRECHARGE_USE, SoundSource.PLAYERS, 1.0F, 1.0F);
        setCooldown(player, 0, VestigiaConfig.PACHAMAMA_FIRE_FIREBALL_COOLDOWN.get());
    }

    private static void lightning(ServerLevel level, ServerPlayer player) {
        if (onCooldown(player, 1)) {
            return;
        }
        Vec3 eye = player.getEyePosition();
        Vec3 end = eye.add(player.getLookAngle().scale(REACH));
        BlockHitResult hit = level.clip(new ClipContext(eye, end,
                ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player));
        Vec3 strike = hit.getType() == HitResult.Type.MISS ? end : hit.getLocation();

        LightningBolt bolt = EntityType.LIGHTNING_BOLT.create(level, net.minecraft.world.entity.EntitySpawnReason.TRIGGERED);
        if (bolt == null) {
            return;
        }
        bolt.snapTo(strike);
        bolt.setCause(player);
        level.addFreshEntity(bolt);

        Vec3 from = player.getEyePosition().subtract(0.0D, 0.3D, 0.0D);
        Vec3 span = strike.subtract(from);
        int steps = Math.max(4, (int) (span.length() * 3.0D));
        for (int step = 0; step <= steps; step++) {
            Vec3 point = from.add(span.scale((double) step / steps));
            level.sendParticles(ParticleTypes.ELECTRIC_SPARK,
                    point.x, point.y, point.z, 2, 0.06D, 0.06D, 0.06D, 0.01D);
        }
        level.playSound(null, player.blockPosition(), SoundEvents.TRIDENT_THUNDER.value(),
                SoundSource.PLAYERS, 0.7F, 1.4F);
        setCooldown(player, 1, VestigiaConfig.PACHAMAMA_FIRE_LIGHTNING_COOLDOWN.get());
    }

    private static final java.util.Map<java.util.UUID, long[]> COOLDOWNS = new java.util.concurrent.ConcurrentHashMap<>();

    private static boolean onCooldown(ServerPlayer player, int slot) {
        long[] expiries = COOLDOWNS.get(player.getUUID());
        return expiries != null && player.level().getGameTime() < expiries[slot];
    }

    private static void setCooldown(ServerPlayer player, int slot, int ticks) {
        if (player.hasInfiniteMaterials()) {
            return;
        }
        COOLDOWNS.computeIfAbsent(player.getUUID(), id -> new long[2])[slot] =
                player.level().getGameTime() + ticks;
    }

    public static void forget(java.util.UUID playerId) {
        COOLDOWNS.remove(playerId);
    }

    private static void tell(ServerPlayer player, Component message) {
        player.sendSystemMessage(message, true);
    }
}
