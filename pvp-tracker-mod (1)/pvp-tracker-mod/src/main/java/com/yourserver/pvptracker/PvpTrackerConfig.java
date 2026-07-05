package com.yourserver.pvptracker;

import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public class PvpTrackerConfig {

    public double range = 30.0;
    public int updateIntervalTicks = 10;

    private static final Path CONFIG_PATH =
            FabricLoader.getInstance().getConfigDir().resolve("pvptracker.properties");

    public static PvpTrackerConfig load() {
        PvpTrackerConfig config = new PvpTrackerConfig();
        Properties props = new Properties();

        if (Files.exists(CONFIG_PATH)) {
            try (InputStream in = Files.newInputStream(CONFIG_PATH)) {
                props.load(in);
                config.range = Double.parseDouble(props.getProperty("range", "30.0"));
                config.updateIntervalTicks = Integer.parseInt(props.getProperty("update-interval-ticks", "10"));
            } catch (IOException | NumberFormatException e) {
                System.err.println("[PvPTracker] Failed to read config, using defaults: " + e.getMessage());
            }
        } else {
            config.save();
        }
        return config;
    }

    public void save() {
        Properties props = new Properties();
        props.setProperty("range", String.valueOf(range));
        props.setProperty("update-interval-ticks", String.valueOf(updateIntervalTicks));

        try {
            Files.createDirectories(CONFIG_PATH.getParent());
            try (OutputStream out = Files.newOutputStream(CONFIG_PATH)) {
                props.store(out, "PvPTracker config - range in blocks, update-interval-ticks (20 ticks = 1 second)");
            }
        } catch (IOException e) {
            System.err.println("[PvPTracker] Failed to write config: " + e.getMessage());
        }
    }
}
