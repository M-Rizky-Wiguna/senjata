package com.rizky.legendweapons.skill;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;

/**
 * Kemampuan aktif yang bisa dipakai dengan klik kanan.
 */
public interface Skill {
	/**
	 * Menjalankan efek skill. Hanya dipanggil di sisi server.
	 *
	 * @return true jika skill berhasil dipakai, sehingga cooldown dan durability diterapkan.
	 */
	boolean cast(ServerWorld world, PlayerEntity player, ItemStack stack);

	int cooldownTicks();

	int durabilityCost();

	String nameKey();

	String descriptionKey();
}
