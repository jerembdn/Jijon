package com.onruntime.jijon;

import com.onruntime.jijon.command.*;
import com.onruntime.jijon.listener.*;
import com.onruntime.jijon.manager.*;
import com.onruntime.jijon.module.*;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class Jijon extends JavaPlugin {

    public static Jijon INSTANCE;

    private Map<String, Manager> managers;

    private List<IModule> modules;

    @Override
    public void onLoad() {
        super.onLoad();

        INSTANCE = this;
        this.managers = new HashMap<>();
        this.modules = new ArrayList<>();

        // - Register managers
        this.managers.put("config", new ConfigManager(this));
        this.managers.put("warp", new WarpManager());
        this.managers.put("clan", new ClanManager());

        // - Register modules
        this.modules.add(new ChestClaimModule(this));
        this.modules.add(new ExpSaveModule());
    }

    @Override
    public void onEnable() {
        super.onEnable();

        // - Register events listeners
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerQuitListener(), this);
        getServer().getPluginManager().registerEvents(new AsyncPlayerChatListener(), this);

        // - Initialize managers
        this.managers.forEach((s, manager) -> manager.init());

        // - Initialize modules
        this.modules.forEach(module -> module.init(this));

        // - Register commands
        Objects.requireNonNull(getCommand("warp")).setExecutor(new WarpCommand(this.getWarpManager()));
        Objects.requireNonNull(getCommand("clan")).setExecutor(new ClanCommand(this.getClanManager()));
    }

    @Override
    public void onDisable() {
        super.onDisable();

        this.managers.forEach((s, manager) -> manager.stop());
    }

    public ConfigManager getConfigManager() {
        return (ConfigManager) managers.get("config");
    }

    public WarpManager getWarpManager() {
        return (WarpManager) managers.get("warp");
    }

    public ClanManager getClanManager() {
        return (ClanManager) managers.get("clan");
    }
}
