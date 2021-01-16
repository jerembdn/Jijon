package com.onruntime.jijon;

import com.onruntime.jijon.command.WarpCommand;
import com.onruntime.jijon.listener.*;
import com.onruntime.jijon.manager.ConfigManager;
import com.onruntime.jijon.manager.Manager;
import com.onruntime.jijon.manager.WarpManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class Jijon extends JavaPlugin {

    public static Jijon INSTANCE;

    private Map<String, Manager> managers;

    @Override
    public void onLoad() {
        super.onLoad();

        INSTANCE = this;

        managers = new HashMap<>();
        managers.put("config", new ConfigManager(this));
        managers.put("warp", new WarpManager());
    }

    @Override
    public void onEnable() {
        super.onEnable();

        getServer().getPluginManager().registerEvents(new PlayerInteractListener(), this);
        getServer().getPluginManager().registerEvents(new ExpBottleListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerQuitListener(), this);
        getServer().getPluginManager().registerEvents(new AsyncPlayerChatListener(), this);

        managers.forEach((s, manager) -> manager.init());

        Objects.requireNonNull(getCommand("warp")).setExecutor(new WarpCommand());
    }

    @Override
    public void onDisable() {
        super.onDisable();

        managers.forEach((s, manager) -> manager.stop());
    }

    public ConfigManager getConfigManager() {
        return (ConfigManager) managers.get("config");
    }

    public WarpManager getWarpManager() {
        return (WarpManager) managers.get("warp");
    }
}
