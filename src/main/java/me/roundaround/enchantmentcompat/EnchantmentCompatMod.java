package me.roundaround.enchantmentcompat;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;

public final class EnchantmentCompatMod implements ModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("enchantmentcompat");
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

	/** Simple toggle for the mod. */
	public static boolean modEnabled = true;
	/** Allow bow enchantments (infinity, mending) to combine. */
	public static boolean bows = true;
	/** Allow crossbow enchantments (multishot, piercing) to combine. */
	public static boolean crossbows = true;
	/** Allow armor enchantments (the protections) to combine. */
	public static boolean armor = true;
	/** Allow damage enchantments (sharpness, smite, bane of arthropods) to combine. */
	public static boolean damage = true;

	/** The on-disk shape of config/enchantmentcompat.json. */
	private static final class Data {
		boolean modEnabled = true;
		boolean bows = true;
		boolean crossbows = true;
		boolean armor = true;
		boolean damage = true;
	}

	@Override
	public void onInitialize() {
		Path file = FabricLoader.getInstance().getConfigDir().resolve("enchantmentcompat.json");
		Data data = new Data();
		try {
			if (Files.exists(file)) {
				try (Reader reader = Files.newBufferedReader(file)) {
					Data read = GSON.fromJson(reader, Data.class);
					if (read != null) {
						data = read;
					}
				}
			} else {
				Files.writeString(file, GSON.toJson(data));
			}
		} catch (Exception e) {
			LOGGER.warn("Could not read {}, using defaults", file, e);
		}
		modEnabled = data.modEnabled;
		bows = data.bows;
		crossbows = data.crossbows;
		armor = data.armor;
		damage = data.damage;
	}
}
