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
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Vec3d;

/**
 * Gempa Penghancur (ultimate Kapak Penghancur): gempa besar yang melontar dan
 * melumpuhkan semua musuh di sekitar.
 */
public final class TitanQuakeSkill implements UltimateSkill {
	private static final double RADIUS = 10.0;
	private static final float DAMAGE = 300.0F;
	private static final double KNOCK_UP = 6.0;
	private static final int EFFECT_TICKS = 160;

	@Override
	public boolean canCast(ServerWorld world, PlayerEntity player, ItemStack stack) {
		if (!player.isOnGround()) {
			player.sendMessage(Text.translatable("message.legendweapons.earthshatter_air")
				.formatted(Formatting.RED), true);
			return false;
		}

		return true;
	}

	@Override
	public void impact(ServerWorld world, PlayerEntity player, ItemStack stack) {
		Vec3d center = player.getEntityPos();

		for (LivingEntity target : SkillUtil.targets(world, player, center, RADIUS)) {
			SkillUtil.damage(world, target, player, DAMAGE);
			target.setVelocity(target.getVelocity().add(0.0, KNOCK_UP, 0.0));
			target.velocityDirty = true;
			target.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, EFFECT_TICKS, 3));
			target.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, EFFECT_TICKS, 2));
		}

		// Retakan tanah mengembang dari pusat gempa.
		for (int ring = 1; ring <= 5; ring++) {
			double ringRadius = RADIUS * ring / 5.0;
			int points = 14 + ring * 10;

			for (int i = 0; i < points; i++) {
				double angle = Math.PI * 2.0 * i / points;
				double x = center.x + Math.cos(angle) * ringRadius;
				double z = center.z + Math.sin(angle) * ringRadius;

				world.spawnParticles(ParticleTypes.POOF, x, center.y + 0.2, z,
					3, 0.15, 0.15, 0.15, 0.02);
				world.spawnParticles(ParticleTypes.LARGE_SMOKE, x, center.y + 0.3, z,
					2, 0.1, 0.2, 0.1, 0.01);
			}
		}

		// Pecahan tanah meledak ke atas.
		for (int i = 0; i < 50; i++) {
			double angle = player.getRandom().nextDouble() * Math.PI * 2.0;
			double dist = player.getRandom().nextDouble() * RADIUS;
			double x = center.x + Math.cos(angle) * dist;
			double z = center.z + Math.sin(angle) * dist;

			world.spawnParticles(ParticleTypes.EXPLOSION, x, center.y + 1.0, z,
				1, 0.0, 0.0, 0.0, 0.0);
			world.spawnParticles(ParticleTypes.DUST_PLUME, x, center.y + 0.5, z,
				4, 0.3, 0.5, 0.3, 0.05);
		}

		world.spawnParticles(ParticleTypes.EXPLOSION_EMITTER, center.x, center.y + 0.8, center.z,
			4, 2.0, 0.5, 2.0, 0.0);

		world.playSound(null, player.getBlockPos(), SoundEvents.ENTITY_WITHER_SPAWN,
			SoundCategory.PLAYERS, 1.2F, 0.5F);
		world.playSound(null, player.getBlockPos(), SoundEvents.BLOCK_ANVIL_LAND,
			SoundCategory.PLAYERS, 1.5F, 0.5F);
		world.playSound(null, center.x, center.y, center.z, SoundEvents.ENTITY_GENERIC_EXPLODE,
			SoundCategory.PLAYERS, 1.5F, 0.4F);
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
		return 25;
	}

	@Override
	public int overlayColor() {
		return 0xCC8844;
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
		return "skill.legendweapons.titan_quake";
	}

	@Override
	public String descriptionKey() {
		return "skill.legendweapons.titan_quake.desc";
	}
}
