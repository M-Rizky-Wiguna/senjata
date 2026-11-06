package com.rizky.legendweapons.item;

import net.minecraft.item.ToolMaterial;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.ItemTags;

public class ModToolMaterials {
	/**
	 * Material senjata legend.
	 * Urutan: blok yang tidak bisa dipanen, durability, kecepatan menambang,
	 * bonus damage, enchantability, bahan perbaikan.
	 * Netherite vanilla: 2031 durability, bonus damage 4.0.
	 */
	public static final ToolMaterial LEGEND = new ToolMaterial(
		BlockTags.INCORRECT_FOR_NETHERITE_TOOL,
		4062,
		10.0F,
		6.0F,
		25,
		ItemTags.NETHERITE_TOOL_MATERIALS
	);
}
