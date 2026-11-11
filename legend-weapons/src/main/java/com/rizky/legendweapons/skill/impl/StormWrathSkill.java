package com.rizky.legendweapons.skill.impl;

import com.rizky.legendweapons.skill.SkillUtil;
import com.rizky.legendweapons.skill.UltimateSkill;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;

import java.util.List;

/**
 * Amarah Badai (ultimate Pedang Petir): badai petir menyambar semua musuh di sekitar.
 */
public final class StormWrathSkill implements UltimateSkill {
	private static final double RADIUS = 12.0;
	private static final float DAMAGE = 3000.0F;
	private static final int SLOWNESS_TICKS = 120;

	@Override
	public boolean canCast(ServerWorld world, PlayerEntity player, ItemStack stack) {
		return true;
	}

	@Override
	public void impact(ServerWorld world, PlayerEntity player, ItemStack stack) {
		Vec3d center = player.getEntityPos();
		List<LivingEntity> targets = SkillUtil.targets(world, player, center, RADIUS);

		for (LivingEntity target : targets) {
			Vec3d pos = target.getEntityPos();

			SkillUtil.lightning(world, target);
			SkillUtil.damage(world, target, player, DAMAGE);
			target.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, SLOWNESS_TICKS, 1));

			// Percikan listrik memancar dari tubuh target.
			world.spawnParticles(ParticleTypes.ELECTRIC_SPARK,
				pos.x, pos.y + target.getHeight() * 0.5, pos.z,
				25, 0.4, 0.6, 0.4, 0.15);
		}

		// Kolom percikan listrik turun dari langit di area skill.
		for (int i = 0; i < 40; i++) {
			double angle = Math.PI * 2.0 * i / 40.0;
			double ringRadius = 2.0 + (i % 4) * (RADIUS - 2.0) / 3.0;
			double x = center.x + Math.cos(angle) * ringRadius;
			double z = center.z + Math.sin(angle) * ringRadius;

			world.spawnParticles(ParticleTypes.ELECTRIC_SPARK, x, center.y + 6.0, z,
				6, 0.3, 2.5, 0.3, 0.02);
			world.spawnParticles(ParticleTypes.END_ROD, x, center.y + 3.0, z,
				2, 0.3, 1.5, 0.3, 0.01);
		}

		world.playSound(null, player.getBlockPos(), SoundEvents.ENTITY_LIGHTNING_BOLT_THUNDER,
			SoundCategory.PLAYERS, 2.0F, 0.6F);
		world.playSound(null, center.x, center.y, center.z, SoundEvents.ENTITY_LIGHTNING_BOLT_IMPACT,
			SoundCategory.PLAYERS, 1.5F, 1.2F);
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
		return 0xFFF050;
	}

	@Override
	public int cooldownTicks() {
		return 75 * 20;
	}

	@Override
	public int durabilityCost() {
		return 35;
	}

	@Override
	public String nameKey() {
		return "skill.legendweapons.storm_wrath";
	}

	@Override
	public String descriptionKey() {
		return "skill.legendweapons.storm_wrath.desc";
	}
}
