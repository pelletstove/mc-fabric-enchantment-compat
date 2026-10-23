package me.roundaround.enchantmentcompat.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import me.roundaround.enchantmentcompat.EnchantmentCompatMod;
import net.minecraft.core.Holder;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.enchantment.Enchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Objects;
import java.util.Optional;

@Mixin(Enchantment.class)
public abstract class EnchantmentMixin {
	@ModifyExpressionValue(method = "areCompatible", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/HolderSet;contains(Lnet/minecraft/core/Holder;)Z", ordinal = 0))
	private static boolean enchantmentcompat$firstExclusiveSetContainsSecond(boolean original, @Local(argsOnly = true, ordinal = 0) Holder<Enchantment> enchantment) {
		return enchantmentcompat$adjust(enchantment).orElse(original);
	}

	@ModifyExpressionValue(method = "areCompatible", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/HolderSet;contains(Lnet/minecraft/core/Holder;)Z", ordinal = 1))
	private static boolean enchantmentcompat$secondExclusiveSetContainsFirst(boolean original, @Local(argsOnly = true, ordinal = 1) Holder<Enchantment> enchantment) {
		return enchantmentcompat$adjust(enchantment).orElse(original);
	}

	/** An empty result leaves vanilla's answer alone, false means "this exclusive set does not apply". */
	@Unique
	private static Optional<Boolean> enchantmentcompat$adjust(Holder<Enchantment> enchantment) {
		if (!EnchantmentCompatMod.modEnabled) {
			return Optional.empty();
		}
		if (EnchantmentCompatMod.bows && enchantmentcompat$isIn(enchantment, EnchantmentTags.BOW_EXCLUSIVE)) {
			return Optional.of(false);
		}
		if (EnchantmentCompatMod.crossbows && enchantmentcompat$isIn(enchantment, EnchantmentTags.CROSSBOW_EXCLUSIVE)) {
			return Optional.of(false);
		}
		if (EnchantmentCompatMod.armor && enchantmentcompat$isIn(enchantment, EnchantmentTags.ARMOR_EXCLUSIVE)) {
			return Optional.of(false);
		}
		if (EnchantmentCompatMod.damage && enchantmentcompat$isIn(enchantment, EnchantmentTags.DAMAGE_EXCLUSIVE)) {
			return Optional.of(false);
		}
		return Optional.empty();
	}

	@Unique
	private static boolean enchantmentcompat$isIn(Holder<Enchantment> enchantment, TagKey<Enchantment> tagKey) {
		return Objects.equals(enchantment.value().exclusiveSet().unwrapKey().orElse(null), tagKey);
	}
}
