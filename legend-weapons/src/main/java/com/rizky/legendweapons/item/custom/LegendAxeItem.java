package com.rizky.legendweapons.item.custom;

import com.rizky.legendweapons.skill.Skill;
import com.rizky.legendweapons.skill.SkillHandler;
import com.rizky.legendweapons.skill.SkillHolder;
import com.rizky.legendweapons.skill.SkillTooltip;
import com.rizky.legendweapons.skill.UltimateHolder;
import com.rizky.legendweapons.skill.impl.EarthshatterSkill;
import com.rizky.legendweapons.skill.impl.TitanQuakeSkill;
import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolMaterial;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

import java.util.function.Consumer;

/**
 * Kapak Penghancur: knockback kuat, memperlambat dan melemahkan musuh.
 * Skill aktif: Earthshatter. Ultimate: Titan Quake.
 */
public class LegendAxeItem extends AxeItem implements SkillHolder, UltimateHolder {
	private static final Skill SKILL = new EarthshatterSkill();
	private static final TitanQuakeSkill ULTIMATE = new TitanQuakeSkill();

	public LegendAxeItem(ToolMaterial material, float attackDamage, float attackSpeed, Settings settings) {
		super(material, attackDamage, attackSpeed, settings);
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
	public TitanQuakeSkill getUltimate() {
		return ULTIMATE;
	}

	@Override
	public void postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
		target.takeKnockback(1.5,
			attacker.getX() - target.getX(),
			attacker.getZ() - target.getZ());
		target.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 100, 1));
		target.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 100, 0));
		super.postHit(stack, target, attacker);
	}

	@Override
	public void appendTooltip(ItemStack stack, TooltipContext context, TooltipDisplayComponent display,
	                          Consumer<Text> tooltip, TooltipType type) {
		SkillTooltip.append(stack, context, display, tooltip, type, SKILL);
		SkillTooltip.appendUltimate(stack, context, display, tooltip, type, ULTIMATE);
	}
}
