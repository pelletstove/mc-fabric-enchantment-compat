package me.roundaround.enchantmentcompat.test;

import me.roundaround.enchantmentcompat.EnchantmentCompatMod;
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;

public class EnchantmentCompatTests {
	private static Holder<Enchantment> get(GameTestHelper helper, ResourceKey<Enchantment> key) {
		return helper.getLevel().registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(key);
	}

	private static void expect(GameTestHelper helper, boolean compatible, ResourceKey<Enchantment> a, ResourceKey<Enchantment> b) {
		boolean forward = Enchantment.areCompatible(get(helper, a), get(helper, b));
		boolean backward = Enchantment.areCompatible(get(helper, b), get(helper, a));
		if (forward != compatible || backward != compatible) {
			helper.fail(a.identifier() + " + " + b.identifier() + ": expected " + compatible + " but got " + forward + "/" + backward);
		}
	}

	/** Runs the body with the config at its defaults, restoring whatever it found. */
	private static void withDefaults(Runnable body) {
		boolean enabled = EnchantmentCompatMod.modEnabled;
		boolean bows = EnchantmentCompatMod.bows;
		boolean crossbows = EnchantmentCompatMod.crossbows;
		boolean armor = EnchantmentCompatMod.armor;
		boolean damage = EnchantmentCompatMod.damage;
		EnchantmentCompatMod.modEnabled = true;
		EnchantmentCompatMod.bows = true;
		EnchantmentCompatMod.crossbows = true;
		EnchantmentCompatMod.armor = true;
		EnchantmentCompatMod.damage = true;
		try {
			body.run();
		} finally {
			EnchantmentCompatMod.modEnabled = enabled;
			EnchantmentCompatMod.bows = bows;
			EnchantmentCompatMod.crossbows = crossbows;
			EnchantmentCompatMod.armor = armor;
			EnchantmentCompatMod.damage = damage;
		}
	}

	@GameTest
	public void damageEnchantmentsCombine(GameTestHelper helper) {
		withDefaults(() -> {
			expect(helper, true, Enchantments.SHARPNESS, Enchantments.SMITE);
			expect(helper, true, Enchantments.SHARPNESS, Enchantments.BANE_OF_ARTHROPODS);
		});
		helper.succeed();
	}

	@GameTest
	public void protectionsCombine(GameTestHelper helper) {
		withDefaults(() -> {
			expect(helper, true, Enchantments.PROTECTION, Enchantments.FIRE_PROTECTION);
			expect(helper, true, Enchantments.BLAST_PROTECTION, Enchantments.PROJECTILE_PROTECTION);
		});
		helper.succeed();
	}

	@GameTest
	public void bowAndCrossbowEnchantmentsCombine(GameTestHelper helper) {
		withDefaults(() -> {
			expect(helper, true, Enchantments.INFINITY, Enchantments.MENDING);
			expect(helper, true, Enchantments.MULTISHOT, Enchantments.PIERCING);
		});
		helper.succeed();
	}

	@GameTest
	public void unrelatedExclusionsStayVanilla(GameTestHelper helper) {
		withDefaults(() -> {
			// mining enchantments are not part of this mod
			expect(helper, false, Enchantments.SILK_TOUCH, Enchantments.FORTUNE);
			// an enchantment never combines with itself
			expect(helper, false, Enchantments.SHARPNESS, Enchantments.SHARPNESS);
			// non-exclusive pairs stay compatible
			expect(helper, true, Enchantments.SHARPNESS, Enchantments.MENDING);
		});
		helper.succeed();
	}

	@GameTest
	public void singleCategoryCanBeSwitchedOff(GameTestHelper helper) {
		withDefaults(() -> {
			EnchantmentCompatMod.damage = false;
			expect(helper, false, Enchantments.SHARPNESS, Enchantments.SMITE);
			expect(helper, true, Enchantments.PROTECTION, Enchantments.FIRE_PROTECTION);
		});
		helper.succeed();
	}

	@GameTest
	public void modCanBeSwitchedOff(GameTestHelper helper) {
		withDefaults(() -> {
			EnchantmentCompatMod.modEnabled = false;
			expect(helper, false, Enchantments.SHARPNESS, Enchantments.SMITE);
			expect(helper, false, Enchantments.PROTECTION, Enchantments.FIRE_PROTECTION);
			expect(helper, false, Enchantments.INFINITY, Enchantments.MENDING);
			expect(helper, false, Enchantments.MULTISHOT, Enchantments.PIERCING);
		});
		helper.succeed();
	}
}
