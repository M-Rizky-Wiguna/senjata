package com.rizky.legendweapons.client;

import com.rizky.legendweapons.LegendWeapons;
import com.rizky.legendweapons.network.UltimateCastPayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;

/**
 * Entrypoint sisi klien: mendaftarkan HUD animasi ultimate, penerima paket jaringan,
 * tick loop animasi, dan camera shake.
 */
public class LegendWeaponsClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		HudElementRegistry.addLast(Identifier.of(LegendWeapons.MOD_ID, "ultimate_hud"),
			(context, tickCounter) -> UltimateAnimation.render(context));

		ClientPlayNetworking.registerGlobalReceiver(UltimateCastPayload.ID, (payload, context) ->
			context.client().execute(() -> UltimateAnimation.begin(
				context.client(), payload.skillNameKey(), payload.color(), payload.castTimeTicks())));

		ClientTickEvents.END_CLIENT_TICK.register(UltimateAnimation::tick);

		MinecraftClient client = MinecraftClient.getInstance();
		WorldRenderEvents.START_MAIN.register(event -> UltimateAnimation.applyShakeFrame(client));
	}
}
