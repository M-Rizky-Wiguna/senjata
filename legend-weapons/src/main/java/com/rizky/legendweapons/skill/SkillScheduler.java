package com.rizky.legendweapons.skill;

import com.rizky.legendweapons.LegendWeapons;
import net.minecraft.server.MinecraftServer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Penjadwal tugas sederhana di sisi server, dipakai untuk menunda efek ultimate
 * sampai animasi cinematic mencapai klimaks.
 */
public final class SkillScheduler {
	private static final class Task {
		private int ticksLeft;
		private final Runnable action;

		private Task(int ticksLeft, Runnable action) {
			this.ticksLeft = ticksLeft;
			this.action = action;
		}
	}

	private static final List<Task> TASKS = new ArrayList<>();

	private SkillScheduler() {
	}

	public static void schedule(int delayTicks, Runnable action) {
		TASKS.add(new Task(Math.max(1, delayTicks), action));
	}

	/**
	 * Dipanggil setiap tick server dari {@code LegendWeapons.onInitialize()}.
	 */
	public static void tick(MinecraftServer server) {
		if (TASKS.isEmpty()) {
			return;
		}

		Iterator<Task> iterator = TASKS.iterator();
		while (iterator.hasNext()) {
			Task task = iterator.next();
			task.ticksLeft--;

			if (task.ticksLeft <= 0) {
				iterator.remove();
				try {
					task.action.run();
				} catch (Exception e) {
					LegendWeapons.LOGGER.error("Tugas skill terjadwal gagal dijalankan", e);
				}
			}
		}
	}
}
