package com.rizky.legendweapons.network;

import com.rizky.legendweapons.skill.UltimateSkill;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

import java.util.List;

/**
 * Registrasi payload jaringan mod dan helper pengiriman.
 */
public final class ModNetworking {
	/**
	 * Radius (blok) pemain yang ikut menonton animasi ultimate.
	 */
	private static final double VIEW_RADIUS = 64.0;

	private ModNetworking() {
	}

	public static void initialize() {
		PayloadTypeRegistry.playS2C().register(UltimateCastPayload.ID, UltimateCastPayload.CODEC);
	}

	/**
	 * Mengirim paket animasi ultimate ke semua pemain di sekitar pemain yang memakai skill.
	 */
	public static void sendUltimateCast(ServerWorld world, PlayerEntity caster, UltimateSkill ultimate) {
		Vec3d center = caster.getEntityPos();
		List<ServerPlayerEntity> viewers = world.getPlayers(
			player -> player.getEntityPos().squaredDistanceTo(center) <= VIEW_RADIUS * VIEW_RADIUS);

		UltimateCastPayload payload = new UltimateCastPayload(
			ultimate.nameKey(), ultimate.overlayColor(), ultimate.castTimeTicks());

		for (ServerPlayerEntity viewer : viewers) {
			ServerPlayNetworking.send(viewer, payload);
		}
	}
}
