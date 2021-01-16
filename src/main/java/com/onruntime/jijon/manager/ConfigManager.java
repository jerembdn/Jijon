package com.onruntime.jijon.manager;

import com.onruntime.jijon.util.Files;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ConfigManager implements Manager {

    private final JavaPlugin plugin;

    private final Map<String, FileConfiguration> configs;

    public ConfigManager(JavaPlugin plugin) {
        this.plugin = plugin;

        configs = new HashMap<>();
    }

    @Override
    public void init() {
        plugin.saveConfig();

        configs.put("default", plugin.getConfig());
        getConfigFiles(plugin.getDataFolder()).forEach(file -> {
            configs.put(file.getName(), file);
        });
    }

    @Override
    public void stop() {
        plugin.saveConfig();
    }

    private List<FileConfiguration> getConfigFiles(File folder) {
        var filter = Files.getConfigFilter();
        var files = folder.listFiles(filter);

        assert files != null;
        return Arrays.stream(files).map(file -> {
            var config = new YamlConfiguration();
            try {
                config.load(file);
            } catch (IOException | InvalidConfigurationException e) {
                e.printStackTrace();
            }

            return config;
        }).collect(Collectors.toList());
    }

    public FileConfiguration getConfig(String name) {
        return configs.getOrDefault(name, null);
    }

    public FileConfiguration createConfig(String name) throws Exception {
        if(configs.containsKey(name)) {
            return configs.get(name);
        }

        var fileName = String.format("%s.%s", name, "yml");
        var configFile = new File(plugin.getDataFolder(), fileName);
        if (!configFile.exists()) {
            if(configFile.getParentFile().mkdirs()) {
                throw new Exception("Failed to create config file.");
            }
            plugin.saveResource(fileName, false);
        }

        var config = new YamlConfiguration();
        config.load(configFile);
        configs.put(name, config);

        return config;
    }
}
