package com.rizky.legendweapons.item;

import com.rizky.legendweapons.LegendWeapons;
import com.rizky.legendweapons.item.custom.FlameSwordItem;
import com.rizky.legendweapons.item.custom.LegendAxeItem;
import com.rizky.legendweapons.item.custom.ThunderSwordItem;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

import java.util.function.Function;

public class ModItems {
	public static final Item FLAME_SWORD = register(
		"flame_sword",
		FlameSwordItem::new,
		new Item.Settings().sword(ModToolMaterials.LEGEND, 3.0F, -2.4F).rarity(Rarity.EPIC)
	);

	public static final Item THUNDER_SWORD = register(
		"thunder_sword",
		ThunderSwordItem::new,
		new Item.Settings().sword(ModToolMaterials.LEGEND, 3.0F, -2.4F).rarity(Rarity.EPIC)
	);

	public static final Item LEGEND_AXE = register(
		"legend_axe",
		settings -> new LegendAxeItem(ModToolMaterials.LEGEND, 6.0F, -3.1F, settings),
		new Item.Settings().rarity(Rarity.EPIC)
	);

	private static Item register(String name, Function<Item.Settings, Item> factory, Item.Settings settings) {
		RegistryKey<Item> key = RegistryKey.of(RegistryKeys.ITEM, Identifier.of(LegendWeapons.MOD_ID, name));
		return Registry.register(Registries.ITEM, key, factory.apply(settings.registryKey(key)));
	}

	public static void initialize() {
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register(entries -> {
			entries.add(FLAME_SWORD);
			entries.add(THUNDER_SWORD);
			entries.add(LEGEND_AXE);
		});
	}
}
