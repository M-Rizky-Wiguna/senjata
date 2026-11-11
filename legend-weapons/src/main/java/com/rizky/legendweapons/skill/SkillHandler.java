package com.rizky.legendweapons.skill;

import com.rizky.legendweapons.network.ModNetworking;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

/**
 * Menangani pemakaian skill: cek cooldown, eksekusi, biaya durability, dan feedback.
 * Skill biasa dipakai dengan klik kanan; skill ultimate dengan sneak + klik kanan.
 */
public final class SkillHandler {
	private SkillHandler() {
	}

	public static ActionResult tryCast(World world, PlayerEntity player, Hand hand) {
		ItemStack stack = player.getStackInHand(hand);

		if (!(stack.getItem() instanceof SkillHolder holder)) {
			return ActionResult.PASS;
		}

		Skill skill = holder.getSkill();

		// Di sisi klien cukup laporkan sukses agar paket interaksi tetap dikirim ke server.
		if (!(world instanceof ServerWorld serverWorld)) {
			return ActionResult.SUCCESS;
		}

		if (player.isSpectator()) {
			return ActionResult.FAIL;
		}

		if (player.getItemCooldownManager().isCoolingDown(stack)) {
			float progress = player.getItemCooldownManager().getCooldownProgress(stack, 0.0F);
			int seconds = Math.max(1, Math.round(progress * skill.cooldownTicks() / 20.0F));
			player.sendMessage(Text.translatable("message.legendweapons.cooldown", seconds)
				.formatted(Formatting.RED), true);
			return ActionResult.FAIL;
		}

		if (!skill.cast(serverWorld, player, stack)) {
			return ActionResult.FAIL;
		}

		player.getItemCooldownManager().set(stack, skill.cooldownTicks());

		if (!player.isCreative()) {
			EquipmentSlot slot = hand == Hand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND;
			stack.damage(skill.durabilityCost(), player, slot);
		}

		player.swingHand(hand, true);
		return ActionResult.SUCCESS;
	}

	/**
	 * Menangani pemakaian skill ultimate: validasi, animasi cinematic, efek tertunda,
	 * cooldown panjang, dan biaya durability besar.
	 */
	public static ActionResult tryCastUltimate(World world, PlayerEntity player, Hand hand) {
		ItemStack stack = player.getStackInHand(hand);

		if (!(stack.getItem() instanceof UltimateHolder holder)) {
			return ActionResult.PASS;
		}

		UltimateSkill ultimate = holder.getUltimate();

		// Di sisi klien cukup laporkan sukses agar paket interaksi tetap dikirim ke server.
		if (!(world instanceof ServerWorld serverWorld)) {
			return ActionResult.SUCCESS;
		}

		if (player.isSpectator()) {
			return ActionResult.FAIL;
		}

		if (player.getItemCooldownManager().isCoolingDown(stack)) {
			float progress = player.getItemCooldownManager().getCooldownProgress(stack, 0.0F);
			int seconds = Math.max(1, Math.round(progress * ultimate.cooldownTicks() / 20.0F));
			player.sendMessage(Text.translatable("message.legendweapons.cooldown", seconds)
				.formatted(Formatting.RED), true);
			return ActionResult.FAIL;
		}

		if (!ultimate.canCast(serverWorld, player, stack)) {
			return ActionResult.FAIL;
		}

		// Putar animasi cinematic di layar semua pemain terdekat.
		ModNetworking.sendUltimateCast(serverWorld, player, ultimate);

		// Jadwalkan efek sesungguhnya saat animasi mencapai klimaks.
		SkillScheduler.schedule(ultimate.castTimeTicks(), () -> {
			if (player.isRemoved() || player.getEntityWorld() != serverWorld) {
				return;
			}
			ultimate.impact(serverWorld, player, stack);
		});

		player.getItemCooldownManager().set(stack, ultimate.cooldownTicks());

		if (!player.isCreative()) {
			EquipmentSlot slot = hand == Hand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND;
			stack.damage(ultimate.durabilityCost(), player, slot);
		}

		player.swingHand(hand, true);
		return ActionResult.SUCCESS;
	}
}
