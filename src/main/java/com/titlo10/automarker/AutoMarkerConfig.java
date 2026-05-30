package com.titlo10.automarker;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class AutoMarkerConfig {
    private static final File CONFIG_FILE = new File(
        FabricLoader.getInstance().getConfigDir().toFile(),
        "replaymod_auto_markers.json"
    );
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public boolean enableDeaths = true;
    public boolean enableAchievements = true;
    public boolean enableDimensionChanges = true;
    public String chatKeywords = "";

    public static AutoMarkerConfig load() {
        if (CONFIG_FILE.exists()) {
            try (FileReader reader = new FileReader(CONFIG_FILE)) {
                AutoMarkerConfig config = GSON.fromJson(reader, AutoMarkerConfig.class);
                if (config != null) {
                    return config;
                }
            } catch (IOException e) {
                AutoMarker.LOGGER.error("Failed to load auto-marker config", e);
            }
        }
        AutoMarkerConfig defaultConfig = new AutoMarkerConfig();
        defaultConfig.save();
        return defaultConfig;
    }

    public void save() {
        try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
            GSON.toJson(this, writer);
        } catch (IOException e) {
            AutoMarker.LOGGER.error("Failed to save auto-marker config", e);
        }
    }
}
