package com.rizky.legendweapons.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Util;

/**
 * State animasi cinematic ultimate ala RPG: letterbox, judul skill, flash layar,
 * vignette, dan camera shake. Semua berjalan di sisi klien.
 */
public final class UltimateAnimation {
	/** Durasi slide-in judul dan letterbox (ms). */
	private static final int INTRO_MS = 350;
	/** Durasi judul tetap tampil setelah klimaks sebelum fade out (ms). */
	private static final int HOLD_MS = 450;
	/** Durasi fade out (ms). */
	private static final int OUTRO_MS = 350;
	/** Durasi flash putih saat klimaks (ms). */
	private static final int FLASH_MS = 350;
	/** Durasi camera shake (ms). */
	private static final long SHAKE_MS = 600;
	/** Amplitudo awal camera shake (derajat). */
	private static final float SHAKE_AMPLITUDE = 4.0F;

	private static ActiveAnimation current;

	private static boolean shaking;
	private static long shakeStart;
	private static float prevYawOffset;
	private static float prevPitchOffset;

	private UltimateAnimation() {
	}

	private record ActiveAnimation(long startTimeMs, String skillNameKey, int color, int castTimeMs) {
	}

	/**
	 * Memulai animasi (dipanggil ketika paket ultimate diterima dari server).
	 */
	public static void begin(MinecraftClient client, String skillNameKey, int color, int castTimeTicks) {
		current = new ActiveAnimation(Util.getMeasuringTimeMs(), skillNameKey, color, castTimeTicks * 50);

		ClientPlayerEntity player = client.player;
		if (player != null) {
			player.playSound(SoundEvents.BLOCK_BEACON_ACTIVATE, 1.0F, 0.7F);
		}
	}

	/**
	 * Dipanggil tiap tick klien: memicu klimaks (shake + suara) dan membersihkan state.
	 */
	public static void tick(MinecraftClient client) {
		ActiveAnimation animation = current;
		if (animation == null) {
			return;
		}

		long elapsed = Util.getMeasuringTimeMs() - animation.startTimeMs();

		if (!shaking && elapsed >= animation.castTimeMs()) {
			startShake();
			ClientPlayerEntity player = client.player;
			if (player != null) {
				player.playSound(SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, 0.8F, 1.0F);
			}
		}

		if (elapsed >= animation.castTimeMs() + HOLD_MS + OUTRO_MS) {
			finish(client);
		}
	}

	/**
	 * Menggambar overlay cinematic di layar (dipanggil tiap frame dari HUD layer).
	 */
	public static void render(DrawContext context) {
		ActiveAnimation animation = current;
		if (animation == null) {
			return;
		}

		long elapsed = Util.getMeasuringTimeMs() - animation.startTimeMs();
		int totalMs = animation.castTimeMs() + HOLD_MS + OUTRO_MS;
		if (elapsed >= totalMs) {
			return;
		}

		int width = context.getScaledWindowWidth();
		int height = context.getScaledWindowHeight();
		int centerX = width / 2;
		int centerY = height / 2;
		int color = animation.color() & 0xFFFFFF;

		float intro = clamp01((float) elapsed / INTRO_MS);
		float outro = 1.0F - clamp01((float) (elapsed - (totalMs - OUTRO_MS)) / OUTRO_MS);
		float visibility = Math.min(easeOut(intro), outro);

		// --- Letterbox: bar hitam atas dan bawah ala cutscene RPG ---
		int barHeight = Math.round(height * 0.15F * visibility);
		if (barHeight > 0) {
			context.fill(0, 0, width, barHeight, 0xE6000000);
			context.fill(0, height - barHeight, width, height, 0xE6000000);
			// Gradasi lembut dari bar ke tengah layar.
			context.fillGradient(0, barHeight, width, barHeight + height / 6, 0x66000000, 0x00000000);
			context.fillGradient(0, height - barHeight - height / 6, width, height - barHeight,
				0x00000000, 0x66000000);
		}

		// --- Vignette gelap tipis di seluruh layar ---
		int vignette = Math.round(50.0F * visibility);
		if (vignette > 0) {
			context.fill(0, 0, width, height, (vignette << 24));
		}

		// --- Judul skill ---
		TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
		Text name = Text.translatable(animation.skillNameKey());
		String label = "-  U L T I M A T E  -";

		float slide = 24.0F * (1.0F - easeOut(intro)) * outro;
		int nameY = centerY - 22 + Math.round(slide);

		// Denyut skala saat klimaks: nama sedikit "meledak" lalu mengecil kembali.
		float pop = 0.0F;
		if (elapsed >= animation.castTimeMs()) {
			float popProgress = clamp01((float) (elapsed - animation.castTimeMs()) / 150.0F);
			pop = (float) Math.sin(Math.PI * popProgress) * 0.35F;
		}

		float glow = 0.35F + 0.65F * clamp01((float) elapsed / animation.castTimeMs());

		// Label kecil di atas nama.
		context.drawCenteredTextWithShadow(textRenderer, label, centerX, nameY - 18,
			withAlpha(0xFFD37F, Math.round(255.0F * visibility)));

		// Nama besar dengan efek glow warna skill.
		drawGlowingTitle(context, textRenderer, name, centerX, nameY, color, glow,
			3.0F + pop, Math.round(255.0F * visibility));

		// Garis aksen di kiri-kanan nama.
		int accentAlpha = Math.round(200.0F * visibility);
		int accentY = nameY + 10;
		context.fillGradient(centerX - 120, accentY, centerX - 34, accentY + 2,
			0x00000000, withAlpha(color, accentAlpha));
		context.fillGradient(centerX + 34, accentY, centerX + 120, accentY + 2,
			withAlpha(color, accentAlpha), 0x00000000);

		// --- Flash putih saat klimaks ---
		if (elapsed >= animation.castTimeMs() && elapsed < animation.castTimeMs() + FLASH_MS) {
			float flash = clamp01((float) (elapsed - animation.castTimeMs()) / FLASH_MS);
			int flashAlpha = Math.round(180.0F * (1.0F - flash) * (1.0F - flash));
			if (flashAlpha > 0) {
				context.fill(0, 0, width, height, (flashAlpha << 24) | 0xFFFFFF);
			}
		}
	}

	// ------------------------------------------------------------------
	// Camera shake
	// ------------------------------------------------------------------

	/**
	 * Dipanggil tiap frame render dunia: menggeser yaw/pitch kamera sedikit dengan
	 * offset yang selalu diperbaiki frame berikutnya, sehingga rotasi asli pemain
	 * tetap utuh dan input mouse tetap berfungsa selama guncangan.
	 */
	public static void applyShakeFrame(MinecraftClient client) {
		if (!shaking) {
			return;
		}

		PlayerEntity player = client.player;
		if (player == null) {
			return;
		}

		long now = Util.getMeasuringTimeMs();
		float progress = (now - shakeStart) / (float) SHAKE_MS;
		if (progress >= 1.0F) {
			stopShake(player);
			return;
		}

		// Amplitudo meluruh cepat, gerakan gabungan dua gelombang sinus biar halus.
		float amplitude = SHAKE_AMPLITUDE * (1.0F - progress) * (1.0F - progress);
		double t = now / 1000.0;
		float yawOffset = (float) (amplitude * (Math.sin(t * 37.0) * 0.7 + Math.sin(t * 61.0) * 0.3));
		float pitchOffset = (float) (amplitude * (Math.cos(t * 43.0) * 0.7 + Math.sin(t * 29.0) * 0.3));

		player.setYaw(player.getYaw() - prevYawOffset + yawOffset);
		player.setPitch(clampPitch(player.getPitch() - prevPitchOffset + pitchOffset));

		prevYawOffset = yawOffset;
		prevPitchOffset = pitchOffset;
	}

	private static void startShake() {
		shaking = true;
		shakeStart = Util.getMeasuringTimeMs();
		prevYawOffset = 0.0F;
		prevPitchOffset = 0.0F;
	}

	private static void stopShake(PlayerEntity player) {
		player.setYaw(player.getYaw() - prevYawOffset);
		player.setPitch(clampPitch(player.getPitch() - prevPitchOffset));
		prevYawOffset = 0.0F;
		prevPitchOffset = 0.0F;
		shaking = false;
	}

	private static void finish(MinecraftClient client) {
		current = null;
		if (shaking && client.player != null) {
			stopShake(client.player);
		}
	}

	// ------------------------------------------------------------------
	// Helper gambar
	// ------------------------------------------------------------------

	private static void drawGlowingTitle(DrawContext context, TextRenderer textRenderer, Text text,
	                                     int x, int y, int color, float glow, float scale, int alpha) {
		var matrices = context.getMatrices();

		matrices.pushMatrix();
		matrices.translate(x, y);
		matrices.scale(scale, scale);

		// Glow: salinan teks di 8 arah sekeliling dengan alpha rendah.
		int glowAlpha = Math.round(alpha * 0.45F * glow);
		int glowColor = withAlpha(color, glowAlpha);
		for (int dx = -1; dx <= 1; dx++) {
			for (int dy = -1; dy <= 1; dy++) {
				if (dx != 0 || dy != 0) {
					context.drawCenteredTextWithShadow(textRenderer, text, dx * 2, dy * 2, glowColor);
				}
			}
		}

		// Teks inti: warna skill dicerahkan ke putih.
		int core = withAlpha(lighten(color, 0.35F), alpha);
		context.drawCenteredTextWithShadow(textRenderer, text, 0, 0, core);

		matrices.popMatrix();
	}

	private static int withAlpha(int rgb, int alpha) {
		return ((alpha & 0xFF) << 24) | (rgb & 0xFFFFFF);
	}

	private static int lighten(int rgb, float amount) {
		int r = (rgb >> 16) & 0xFF;
		int g = (rgb >> 8) & 0xFF;
		int b = rgb & 0xFF;
		r += Math.round((255 - r) * amount);
		g += Math.round((255 - g) * amount);
		b += Math.round((255 - b) * amount);
		return (r << 16) | (g << 8) | b;
	}

	private static float easeOut(float t) {
		float inv = 1.0F - t;
		return 1.0F - inv * inv * inv;
	}

	private static float clamp01(float value) {
		return Math.max(0.0F, Math.min(1.0F, value));
	}

	private static float clampPitch(float pitch) {
		return Math.max(-90.0F, Math.min(90.0F, pitch));
	}
}
