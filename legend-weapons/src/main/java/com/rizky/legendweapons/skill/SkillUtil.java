package com.rizky.legendweapons.skill;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.List;

/**
 * Helper untuk pencarian target dan damage yang dipakai semua skill.
 */
public final class SkillUtil {
	private SkillUtil() {
	}

	/**
	 * Mengambil musuh yang valid di sekitar sebuah titik. Pemain sendiri, pet, anggota tim,
	 * spectator, dan armor stand otomatis dikecualikan.
	 */
	public static List<LivingEntity> targets(ServerWorld world, PlayerEntity player, Vec3d center, double radius) {
		double diameter = radius * 2.0;
		return world.getEntitiesByClass(LivingEntity.class, Box.of(center, diameter, diameter, diameter),
			entity -> entity != player
				&& entity.isAlive()
				&& !entity.isSpectator()
				&& !(entity instanceof ArmorStandEntity)
				&& !player.isTeammate(entity)
				&& (!(entity instanceof PlayerEntity) || world.isPvpEnabled())
				&& !(entity instanceof TameableEntity tameable && tameable.isOwner(player)));
	}

	public static LivingEntity nearest(ServerWorld world, PlayerEntity player, Vec3d center, double radius,
	                                   List<LivingEntity> excluded) {
		LivingEntity nearest = null;
		double bestDistance = Double.MAX_VALUE;

		for (LivingEntity entity : targets(world, player, center, radius)) {
			if (excluded.contains(entity)) {
				continue;
			}

			double distance = entity.getEntityPos().squaredDistanceTo(center);
			if (distance < bestDistance) {
				bestDistance = distance;
				nearest = entity;
			}
		}

		return nearest;
	}

	/**
	 * Memilih target terdekat yang berada di depan pandangan pemain.
	 */
	public static LivingEntity nearestInFront(ServerWorld world, PlayerEntity player, double radius) {
		Vec3d eye = player.getEyePos();
		Vec3d look = player.getRotationVec(1.0F);
		LivingEntity best = null;
		double bestScore = Double.MAX_VALUE;

		for (LivingEntity entity : targets(world, player, eye, radius)) {
			Vec3d delta = entity.getEntityPos().add(0.0, entity.getHeight() * 0.5, 0.0).subtract(eye);
			double length = delta.length();
			if (length < 0.001) {
				continue;
			}

			double dot = delta.multiply(1.0 / length).dotProduct(look);
			if (dot < 0.2) {
				continue;
			}

			double score = delta.lengthSquared() * (2.0 - dot);
			if (score < bestScore) {
				bestScore = score;
				best = entity;
			}
		}

		return best != null ? best : nearest(world, player, eye, radius, List.of());
	}

	public static boolean damage(ServerWorld world, LivingEntity target, PlayerEntity source, float amount) {
		return target.damage(world, world.getDamageSources().playerAttack(source), amount);
	}

	/**
	 * Petir kosmetik: hanya efek visual, damage diatur manual oleh skill.
	 */
	public static void lightning(ServerWorld world, LivingEntity target) {
		LightningEntity bolt = EntityType.LIGHTNING_BOLT.create(world, SpawnReason.TRIGGERED);
		if (bolt != null) {
			bolt.refreshPositionAfterTeleport(target.getX(), target.getY(), target.getZ());
			bolt.setCosmetic(true);
			world.spawnEntity(bolt);
		}
	}
}
