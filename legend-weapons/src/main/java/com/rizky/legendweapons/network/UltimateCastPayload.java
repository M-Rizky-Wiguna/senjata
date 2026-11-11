package com.rizky.legendweapons.network;

import com.rizky.legendweapons.LegendWeapons;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

/**
 * Paket S2C: memberi tahu klien bahwa seorang pemain memakai skill ultimate,
 * sehingga animasi cinematic ala RPG bisa diputar di layar.
 */
public record UltimateCastPayload(String skillNameKey, int color, int castTimeTicks)
	implements CustomPayload {

	public static final CustomPayload.Id<UltimateCastPayload> ID =
		new CustomPayload.Id<>(Identifier.of(LegendWeapons.MOD_ID, "ultimate_cast"));

	public static final PacketCodec<RegistryByteBuf, UltimateCastPayload> CODEC = PacketCodec.tuple(
		PacketCodecs.STRING, UltimateCastPayload::skillNameKey,
		PacketCodecs.VAR_INT, UltimateCastPayload::color,
		PacketCodecs.VAR_INT, UltimateCastPayload::castTimeTicks,
		UltimateCastPayload::new
	);

	@Override
	public CustomPayload.Id<? extends CustomPayload> getId() {
		return ID;
	}
}
