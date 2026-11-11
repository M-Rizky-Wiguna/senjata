package com.rizky.legendweapons.skill.impl;

import com.rizky.legendweapons.skill.Skill;
import com.rizky.legendweapons.skill.SkillUtil;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.HashSet;
import java.util.Set;

/**
 * Inferno Slash: dash ke depan sambil meninggalkan jejak api dan membakar musuh di jalur.
 */
public final class InfernoSlashSkill implements Skill {
	private static final double RANGE = 10.0;
	private static final double STEP = 0.5;
	private static final double HIT_RADIUS = 1.6;
	private static final float DAMAGE = 1000.0F;
	private static final int FIRE_TICKS = 80;

	@Override
	public boolean cast(ServerWorld world, PlayerEntity player, ItemStack stack) {
		// Arah horizontal supaya pemain tidak melesat ke atas/bawah saat melihat ke langit/lantai.
		double yaw = Math.toRadians(player.getYaw());
		Vec3d direction = new Vec3d(-Math.sin(yaw), 0.0, Math.cos(yaw)).normalize();

		Vec3d start = player.getEntityPos();
		Vec3d safe = start;

		for (double travelled = STEP; travelled <= RANGE; travelled += STEP) {
			Vec3d candidate = start.add(direction.multiply(travelled));
			Box box = player.getBoundingBox().offset(candidate.subtract(start));

			if (!world.isSpaceEmpty(player, box)) {
				break;
			}

			safe = candidate;
		}

		double distance = start.distanceTo(safe);
		Set<LivingEntity> hit = new HashSet<>();

		world.spawnParticles(ParticleTypes.FLAME, start.x, start.y + 1.0, start.z,
			20, 0.4, 0.6, 0.4, 0.02);

		for (double travelled = 0.0; travelled <= distance; travelled += STEP) {
			Vec3d point = start.add(direction.multiply(travelled));

			world.spawnParticles(ParticleTypes.FLAME, point.x, point.y + 0.8, point.z,
				8, 0.25, 0.35, 0.25, 0.01);
			world.spawnParticles(ParticleTypes.SMALL_FLAME, point.x, point.y + 0.8, point.z,
				6, 0.25, 0.35, 0.25, 0.01);

			for (LivingEntity target : SkillUtil.targets(world, player, point, HIT_RADIUS)) {
				if (hit.add(target)) {
					SkillUtil.damage(world, target, player, DAMAGE);
					target.setOnFireForTicks(FIRE_TICKS);
				}
			}
		}

		player.requestTeleport(safe.x, safe.y, safe.z);
		player.velocityDirty = true;

		world.spawnParticles(ParticleTypes.LAVA, safe.x, safe.y + 1.0, safe.z,
			12, 0.3, 0.4, 0.3, 0.0);
		world.playSound(null, player.getBlockPos(), SoundEvents.ENTITY_BLAZE_SHOOT,
			SoundCategory.PLAYERS, 1.0F, 1.0F);

		return true;
	}

	@Override
	public int cooldownTicks() {
		return 12 * 20;
	}

	@Override
	public int durabilityCost() {
		return 8;
	}

	@Override
	public String nameKey() {
		return "skill.legendweapons.inferno_slash";
	}

	@Override
	public String descriptionKey() {
		return "skill.legendweapons.inferno_slash.desc";
	}
}
