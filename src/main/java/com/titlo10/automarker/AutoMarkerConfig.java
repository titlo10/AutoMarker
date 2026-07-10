package com.titlo10.automarker;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class AutoMarkerConfig {
    public static final int CURRENT_VERSION = 1;
    private static final File CONFIG_FILE = new File(
        FabricLoader.getInstance().getConfigDir().toFile(),
        "replaymod_auto_markers.json"
    );
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public int version = CURRENT_VERSION;
    public boolean enableDeaths = true;
    public boolean enablePvpKills = true;
    public boolean enableTotemPops = true;
    public boolean enableAchievements = true;
    public boolean enableDimensionChanges = true;
    public String chatKeywords = "";

    public static AutoMarkerConfig load() {
        if (CONFIG_FILE.exists()) {
            try (Reader reader = Files.newBufferedReader(CONFIG_FILE.toPath(), StandardCharsets.UTF_8)) {
                AutoMarkerConfig config = GSON.fromJson(reader, AutoMarkerConfig.class);
                if (config != null) {
                    config.normalize();
                    return config;
                }
            } catch (IOException | JsonParseException | IllegalStateException e) {
                AutoMarker.LOGGER.error("Failed to load AutoMarker config; restoring defaults", e);
                backupBrokenConfig();
            }
        }
        AutoMarkerConfig defaultConfig = new AutoMarkerConfig();
        defaultConfig.save();
        return defaultConfig;
    }

    public void save() {
        normalize();
        Path target = CONFIG_FILE.toPath();
        Path temporary = target.resolveSibling(CONFIG_FILE.getName() + ".tmp");
        try {
            Files.createDirectories(target.getParent());
            try (Writer writer = Files.newBufferedWriter(temporary, StandardCharsets.UTF_8)) {
                GSON.toJson(this, writer);
            }
            try {
                Files.move(temporary, target, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            } catch (java.nio.file.AtomicMoveNotSupportedException ignored) {
                Files.move(temporary, target, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            AutoMarker.LOGGER.error("Failed to save auto-marker config", e);
            try {
                Files.deleteIfExists(temporary);
            } catch (IOException ignored) {
            }
        }
    }

    public AutoMarkerConfig copy() {
        AutoMarkerConfig copy = new AutoMarkerConfig();
        copy.version = version;
        copy.enableDeaths = enableDeaths;
        copy.enablePvpKills = enablePvpKills;
        copy.enableTotemPops = enableTotemPops;
        copy.enableAchievements = enableAchievements;
        copy.enableDimensionChanges = enableDimensionChanges;
        copy.chatKeywords = chatKeywords;
        return copy;
    }

    public void copyFrom(AutoMarkerConfig other) {
        version = other.version;
        enableDeaths = other.enableDeaths;
        enablePvpKills = other.enablePvpKills;
        enableTotemPops = other.enableTotemPops;
        enableAchievements = other.enableAchievements;
        enableDimensionChanges = other.enableDimensionChanges;
        chatKeywords = other.chatKeywords;
        normalize();
    }

    private void normalize() {
        version = CURRENT_VERSION;
        if (chatKeywords == null) {
            chatKeywords = "";
        }
    }

    private static void backupBrokenConfig() {
        Path source = CONFIG_FILE.toPath();
        Path backup = source.resolveSibling(CONFIG_FILE.getName() + ".broken");
        try {
            Files.move(source, backup, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            AutoMarker.LOGGER.error("Failed to back up broken AutoMarker config", e);
        }
    }
}
