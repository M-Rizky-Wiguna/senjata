package com.rizky.legendweapons;

import com.rizky.legendweapons.item.ModItems;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LegendWeapons implements ModInitializer {
	public static final String MOD_ID = "legendweapons";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ModItems.initialize();
		LOGGER.info("Legend Weapons dimuat!");
	}
}
