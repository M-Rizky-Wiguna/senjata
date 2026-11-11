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
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;

/**
 * Chain Lightning: sambaran petir berantai ke beberapa musuh terdekat.
 */
public final class ChainLightningSkill implements Skill {
	private static final int MAX_TARGETS = 4;
	private static final double SEARCH_RADIUS = 10.0;
	private static final double CHAIN_RADIUS = 6.0;
	private static final float BASE_DAMAGE = 6000.0F;
	private static final float MIN_DAMAGE =1000.0F;
	private static final ChainLightningSkill INSTANCE = new ChainLightningSkill();

	public static ChainLightningSkill getInstance() {
		return INSTANCE;
		
	}

	@Override
	public boolean cast(ServerWorld world, PlayerEntity player, ItemStack stack) {
		List<LivingEntity> chain = new ArrayList<>();
		LivingEntity current = SkillUtil.nearestInFront(world, player, SEARCH_RADIUS);

		if (current == null) {
			player.sendMessage(Text.translatable("message.legendweapons.no_target").formatted(Formatting.RED), true);
			return false;
		}

		Vec3d from = player.getEyePos();
		float damage = BASE_DAMAGE;

		while (current != null && chain.size() < MAX_TARGETS) {
			Vec3d center = current.getEntityPos().add(0.0, current.getHeight() * 0.5, 0.0);

			SkillUtil.lightning(world, current);
			spawnBeam(world, from, center);
			SkillUtil.damage(world, current, player, damage);

			chain.add(current);
			from = center;
			damage = Math.max(MIN_DAMAGE, damage - 1.0F);
			current = SkillUtil.nearest(world, player, center, CHAIN_RADIUS, chain);
		}

		world.playSound(null, player.getBlockPos(), SoundEvents.ENTITY_LIGHTNING_BOLT_THUNDER,
			SoundCategory.PLAYERS, 1.0F, 1.2F);

		return true;
	}

	private void spawnBeam(ServerWorld world, Vec3d from, Vec3d to) {
		double distance = from.distanceTo(to);
		int steps = Math.max(1, (int) Math.ceil(distance * 3.0));

		for (int i = 0; i <= steps; i++) {
			Vec3d point = from.lerp(to, (double) i / steps);
			world.spawnParticles(ParticleTypes.ELECTRIC_SPARK, point.x, point.y, point.z,
				2, 0.1, 0.1, 0.1, 0.02);
		}
	}

	@Override
	public int cooldownTicks() {
		return 15 * 20;
	}

	@Override
	public int durabilityCost() {
		return 10;
	}

	@Override
	public String nameKey() {
		return "skill.legendweapons.chain_lightning";
	}

	@Override
	public String descriptionKey() {
		return "skill.legendweapons.chain_lightning.desc";
	}
}
