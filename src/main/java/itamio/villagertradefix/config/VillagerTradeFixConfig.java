package itamio.villagertradefix.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple JSON config replacing the original Cloth Config/AutoConfig setup.
 * File: config/villagertradefix263.json
 */
public class VillagerTradeFixConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger("VillagerTradeFix");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static VillagerTradeFixConfig instance;

    public double baseVillagerOfTheHeroDiscount = 0.2D;
    public double extraVillagerOfTheHeroDiscount = 0.05D;
    public double maxReputationDiscount = 0.2D;
    public int maxReputation = 25;

    public static VillagerTradeFixConfig get() {
        if (instance == null) {
            load();
        }
        return instance;
    }

    public static void load() {
        Path configPath = FabricLoader.getInstance().getConfigDir();
        File file = new File(configPath.toFile(), "villagertradefix263.json");
        VillagerTradeFixConfig config = new VillagerTradeFixConfig();
        if (file.exists()) {
            try (FileReader reader = new FileReader(file)) {
                VillagerTradeFixConfig loaded = GSON.fromJson(reader, VillagerTradeFixConfig.class);
                if (loaded != null) {
                    config = loaded;
                }
            } catch (IOException | RuntimeException e) {
                LOGGER.warn("Failed to load config, using defaults", e);
            }
        }
        instance = config;
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(GSON.toJson(config));
        } catch (IOException e) {
            LOGGER.warn("Failed to write config", e);
        }
    }
}
