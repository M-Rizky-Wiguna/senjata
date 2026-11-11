package com.rizky.legendweapons;

import com.rizky.legendweapons.item.ModItems;
import com.rizky.legendweapons.network.ModNetworking;
import com.rizky.legendweapons.skill.SkillScheduler;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LegendWeapons implements ModInitializer {
	public static final String MOD_ID = "legendweapons";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ModItems.initialize();
		ModNetworking.initialize();

		// Penjadwal efek ultimate (dijalankan saat animasi cinematic mencapai klimaks).
		ServerTickEvents.END_SERVER_TICK.register(SkillScheduler::tick);

		LOGGER.info("Legend Weapons dimuat!");
	}
}
