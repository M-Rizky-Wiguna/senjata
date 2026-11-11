package com.rizky.legendweapons.item.custom;

import com.rizky.legendweapons.skill.Skill;
import com.rizky.legendweapons.skill.SkillHandler;
import com.rizky.legendweapons.skill.SkillHolder;
import com.rizky.legendweapons.skill.SkillTooltip;
import com.rizky.legendweapons.skill.UltimateHolder;
import com.rizky.legendweapons.skill.impl.ChainLightningSkill;
import com.rizky.legendweapons.skill.impl.StormWrathSkill;
import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

import java.util.function.Consumer;

/**
 * Pedang Petir: peluang 25% menyambar musuh dengan petir.
 * Skill aktif: Chain Lightning. Ultimate: Storm Wrath.
 */
public class ThunderSwordItem extends Item implements SkillHolder, UltimateHolder {
	private static final float LIGHTNING_CHANCE = 0.25F;
	private static final Skill SKILL = new ChainLightningSkill();
	private static final StormWrathSkill ULTIMATE = new StormWrathSkill();

	public ThunderSwordItem(Settings settings) {
		super(settings);
	}

	@Override
	public ActionResult use(World world, PlayerEntity user, Hand hand) {
		if (user.isSneaking()) {
			return SkillHandler.tryCastUltimate(world, user, hand);
		}

		return SkillHandler.tryCast(world, user, hand);
	}

	@Override
	public Skill getSkill() {
		return SKILL;
	}

	@Override
	public StormWrathSkill getUltimate() {
		return ULTIMATE;
	}

	@Override
	public void postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
		if (target.getEntityWorld() instanceof ServerWorld world
			&& attacker.getRandom().nextFloat() < LIGHTNING_CHANCE) {
			LightningEntity bolt = EntityType.LIGHTNING_BOLT.create(world, SpawnReason.TRIGGERED);
			if (bolt != null) {
				bolt.refreshPositionAfterTeleport(target.getX(), target.getY(), target.getZ());
				world.spawnEntity(bolt);
			}
		}
		super.postHit(stack, target, attacker);
	}

	@Override
	public void appendTooltip(ItemStack stack, TooltipContext context, TooltipDisplayComponent display,
	                          Consumer<Text> tooltip, TooltipType type) {
		SkillTooltip.append(stack, context, display, tooltip, type, SKILL);
		SkillTooltip.appendUltimate(stack, context, display, tooltip, type, ULTIMATE);
	}
}
