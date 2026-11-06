package com.rizky.legendweapons.item.custom;

import com.rizky.legendweapons.skill.Skill;
import com.rizky.legendweapons.skill.SkillHandler;
import com.rizky.legendweapons.skill.SkillHolder;
import com.rizky.legendweapons.skill.SkillTooltip;
import com.rizky.legendweapons.skill.impl.InfernoSlashSkill;
import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

import java.util.function.Consumer;

/**
 * Pedang Api Abadi: membakar musuh yang terkena serangan.
 * Skill aktif: Inferno Slash.
 */
public class FlameSwordItem extends Item implements SkillHolder {
	private static final Skill SKILL = new InfernoSlashSkill();

	public FlameSwordItem(Settings settings) {
		super(settings);
	}

	@Override
	public ActionResult use(World world, PlayerEntity user, Hand hand) {
		return SkillHandler.tryCast(world, user, hand);
	}

	@Override
	public Skill getSkill() {
		return SKILL;
	}

	@Override
	public void postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
		target.setOnFireForTicks(80);

		if (target.getEntityWorld() instanceof ServerWorld world) {
			world.spawnParticles(ParticleTypes.FLAME,
				target.getX(), target.getBodyY(0.5), target.getZ(),
				20, 0.3, 0.5, 0.3, 0.02);
		}
		super.postHit(stack, target, attacker);
	}

	@Override
	public void appendTooltip(ItemStack stack, TooltipContext context, TooltipDisplayComponent display,
	                          Consumer<Text> tooltip, TooltipType type) {
		SkillTooltip.append(stack, context, display, tooltip, type, SKILL);
	}
}
