package com.rizky.legendweapons.skill.impl;

import com.rizky.legendweapons.skill.Skill;
import com.rizky.legendweapons.skill.SkillUtil;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Vec3d;

/**
 * Earthshatter: hantam tanah, lempar dan perlambat musuh di sekitar.
 */
public final class EarthshatterSkill implements Skill {
	private static final double RADIUS = 5.0;
	private static final float DAMAGE = 6.0F;
	private static final double KNOCK_UP = 0.65;
	private static final int SLOWNESS_TICKS = 100;

	@Override
	public boolean cast(ServerWorld world, PlayerEntity player, ItemStack stack) {
		if (!player.isOnGround()) {
			player.sendMessage(Text.translatable("message.legendweapons.earthshatter_air")
				.formatted(Formatting.RED), true);
			return false;
		}

		Vec3d center = player.getEntityPos();

		for (LivingEntity target : SkillUtil.targets(world, player, center, RADIUS)) {
			SkillUtil.damage(world, target, player, DAMAGE);
			target.setVelocity(target.getVelocity().add(0.0, KNOCK_UP, 0.0));
			target.velocityDirty = true;
			target.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, SLOWNESS_TICKS, 1));
		}

		for (int i = 0; i < 60; i++) {
			double angle = Math.PI * 2.0 * i / 60.0;
			double x = center.x + Math.cos(angle) * RADIUS;
			double z = center.z + Math.sin(angle) * RADIUS;

			world.spawnParticles(ParticleTypes.POOF, x, center.y + 0.2, z,
				2, 0.05, 0.1, 0.05, 0.01);
			world.spawnParticles(ParticleTypes.LARGE_SMOKE, x, center.y + 0.2, z,
				1, 0.05, 0.1, 0.05, 0.01);
		}

		world.spawnParticles(ParticleTypes.EXPLOSION, center.x, center.y + 0.5, center.z,
			4, 1.5, 0.3, 1.5, 0.0);
		world.playSound(null, player.getBlockPos(), SoundEvents.BLOCK_ANVIL_LAND,
			SoundCategory.PLAYERS, 1.2F, 0.7F);
		world.playSound(null, center.x, center.y, center.z, SoundEvents.ENTITY_GENERIC_EXPLODE,
			SoundCategory.PLAYERS, 0.8F, 0.6F);

		return true;
	}

	@Override
	public int cooldownTicks() {
		return 18 * 20;
	}

	@Override
	public int durabilityCost() {
		return 12;
	}

	@Override
	public String nameKey() {
		return "skill.legendweapons.earthshatter";
	}

	@Override
	public String descriptionKey() {
		return "skill.legendweapons.earthshatter.desc";
	}
}
