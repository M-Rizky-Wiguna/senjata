package com.rizky.legendweapons.skill;

import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.function.Consumer;

/**
 * Menampilkan informasi skill pada tooltip item.
 */
public final class SkillTooltip {
	private SkillTooltip() {
	}

	public static void append(ItemStack stack, Item.TooltipContext context, TooltipDisplayComponent display,
	                          Consumer<Text> tooltip, TooltipType type, Skill skill) {
		tooltip.accept(Text.empty());
		tooltip.accept(Text.translatable("tooltip.legendweapons.activate",
				Text.translatable(skill.nameKey()).formatted(Formatting.GOLD))
			.formatted(Formatting.GRAY));
		tooltip.accept(Text.translatable(skill.descriptionKey()).formatted(Formatting.DARK_GRAY));
		tooltip.accept(Text.translatable("tooltip.legendweapons.cooldown", skill.cooldownTicks() / 20)
			.formatted(Formatting.DARK_GRAY));
		tooltip.accept(Text.translatable("tooltip.legendweapons.durability", skill.durabilityCost())
			.formatted(Formatting.DARK_GRAY));
	}

	public static void appendUltimate(ItemStack stack, Item.TooltipContext context, TooltipDisplayComponent display,
	                                  Consumer<Text> tooltip, TooltipType type, UltimateSkill ultimate) {
		tooltip.accept(Text.empty());
		tooltip.accept(Text.translatable("tooltip.legendweapons.ultimate_activate",
				Text.translatable(ultimate.nameKey()).formatted(Formatting.GOLD))
			.formatted(Formatting.LIGHT_PURPLE));
		tooltip.accept(Text.translatable(ultimate.descriptionKey()).formatted(Formatting.DARK_GRAY));
		tooltip.accept(Text.translatable("tooltip.legendweapons.cooldown", ultimate.cooldownTicks() / 20)
			.formatted(Formatting.DARK_GRAY));
		tooltip.accept(Text.translatable("tooltip.legendweapons.durability", ultimate.durabilityCost())
			.formatted(Formatting.DARK_GRAY));
	}
}
