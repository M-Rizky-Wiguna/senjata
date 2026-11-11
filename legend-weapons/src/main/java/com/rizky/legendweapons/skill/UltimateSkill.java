package com.rizky.legendweapons.skill;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;

/**
 * Skill ultimate: versi jauh lebih kuat dari skill biasa dengan animasi cinematic.
 * Dipakai dengan sneak + klik kanan.
 */
public interface UltimateSkill extends Skill {
	/**
	 * Validasi saat skill mulai dipakai (sebelum wind-up). Jika false, ultimate batal
	 * dan animasi tidak dimulai. Umpan balik ke pemain sebaiknya diberikan di sini.
	 */
	boolean canCast(ServerWorld world, PlayerEntity player, ItemStack stack);

	/**
	 * Efek sesungguhnya, dieksekusi di sisi server saat animasi mencapai klimaks
	 * ({@link #castTimeTicks()} tick setelah tombol ditekan).
	 */
	void impact(ServerWorld world, PlayerEntity player, ItemStack stack);

	/**
	 * Lama wind-up dalam tick sebelum efek mendarat. Selama waktu ini animasi
	 * cinematic diputar di layar semua pemain terdekat.
	 */
	int castTimeTicks();

	/**
	 * Warna RGB animasi cinematic (misalnya 0xFF6A00 untuk api).
	 */
	int overlayColor();
}
