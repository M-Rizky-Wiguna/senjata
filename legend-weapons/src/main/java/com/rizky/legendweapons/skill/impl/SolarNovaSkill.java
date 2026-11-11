package com.rizky.legendweapons.skill.impl;

import com.rizky.legendweapons.skill.SkillUtil;
import com.rizky.legendweapons.skill.UltimateSkill;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;

/**
 * Nova Matahari (ultimate Pedang Api Abadi): ledakan matahari besar di sekitar pemain.
 */
public final class SolarNovaSkill implements UltimateSkill {
	private static final double RADIUS = 8.0;
	private static final float DAMAGE = 2000.0F;
	private static final int FIRE_TICKS = 200;
	private static final double KNOCKBACK = 1.4;

	@Override
	public boolean canCast(ServerWorld world, PlayerEntity player, ItemStack stack) {
		return true;
	}

	@Override
	public void impact(ServerWorld world, PlayerEntity player, ItemStack stack) {
		Vec3d center = player.getEntityPos();

		for (LivingEntity target : SkillUtil.targets(world, player, center, RADIUS)) {
			SkillUtil.damage(world, target, player, DAMAGE);
			target.setOnFireForTicks(FIRE_TICKS);

			Vec3d delta = target.getEntityPos().subtract(center);
			double dx = delta.x;
			double dz = delta.z;
			double length = Math.sqrt(dx * dx + dz * dz);
			if (length < 0.01) {
				dx = 0.0;
				dz = 0.0;
				length = 1.0;
			}

			target.setVelocity(target.getVelocity().add(
				dx / length * KNOCKBACK, 0.5, dz / length * KNOCKBACK));
			target.velocityDirty = true;
		}

		// Gelombang api mengembang beberapa lapis.
		for (int ring = 1; ring <= 4; ring++) {
			double ringRadius = RADIUS * ring / 4.0;
			int points = 12 + ring * 12;

			for (int i = 0; i < points; i++) {
				double angle = Math.PI * 2.0 * i / points;
				double x = center.x + Math.cos(angle) * ringRadius;
				double z = center.z + Math.sin(angle) * ringRadius;

				world.spawnParticles(ParticleTypes.FLAME, x, center.y + 0.3, z,
					3, 0.15, 0.4, 0.15, 0.03);
				world.spawnParticles(ParticleTypes.LAVA, x, center.y + 0.6, z,
					1, 0.1, 0.2, 0.1, 0.0);
			}
		}

		world.spawnParticles(ParticleTypes.EXPLOSION_EMITTER, center.x, center.y + 1.0, center.z,
			3, 0.5, 0.5, 0.5, 0.0);
		world.spawnParticles(ParticleTypes.FLAME, center.x, center.y + 1.0, center.z,
			120, 3.5, 1.5, 3.5, 0.05);

		world.playSound(null, player.getBlockPos(), SoundEvents.ENTITY_ENDER_DRAGON_GROWL,
			SoundCategory.PLAYERS, 1.4F, 0.8F);
		world.playSound(null, center.x, center.y, center.z, SoundEvents.ENTITY_GENERIC_EXPLODE,
			SoundCategory.PLAYERS, 1.2F, 0.9F);
	}

	@Override
	public boolean cast(ServerWorld world, PlayerEntity player, ItemStack stack) {
		if (!canCast(world, player, stack)) {
			return false;
		}

		impact(world, player, stack);
		return true;
	}

	@Override
	public int castTimeTicks() {
		return 20;
	}

	@Override
	public int overlayColor() {
		return 0xFF6A00;
	}

	@Override
	public int cooldownTicks() {
		return 90 * 20;
	}

	@Override
	public int durabilityCost() {
		return 40;
	}

	@Override
	public String nameKey() {
		return "skill.legendweapons.solar_nova";
	}

	@Override
	public String descriptionKey() {
		return "skill.legendweapons.solar_nova.desc";
	}
}
