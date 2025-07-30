package top.offsetmonkey538.villagerdrops;

import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VillagerDrops implements ModInitializer {
	public static final String MOD_ID = "villager-drops";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
	}

	public static Identifier id(String path) {
		return Identifier.of(MOD_ID, path);
	}
}
