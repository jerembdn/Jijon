package com.onruntime.jijon.manager;

import com.onruntime.jijon.Jijon;
import com.onruntime.jijon.data.Warp;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class WarpManager implements Manager {

    private FileConfiguration config;
    private ConfigurationSection section;

    @Getter
    private final Map<String, Warp> warps;

    public WarpManager() {
        warps = new HashMap<>();
    }

    @Override
    public void init() {
        try {
            config = Jijon.INSTANCE.getConfigManager().createConfig("warps");
            config.createSection("warps");

            section = config.getConfigurationSection("warps");

            if (section != null) {
                section.getKeys(true).forEach(warp -> {
                    var author = section.getOfflinePlayer(String.format("%s.author", warp));
                    var location = section.getLocation(String.format("%s.location", warp));

                    addWarp(warp, author, location);
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stop() {
        config.set("warps", null);

        warps.forEach((s, warp) -> section.set(s, warp));
    }

    public Warp getWarp(String name) {
        return warps.getOrDefault(name, null);
    }

    public void addWarp(String name, Location location) {
        addWarp(name, null, location);
    }

    public void addWarp(String name, Player player) {
        addWarp(name, player, player.getLocation());
    }

    public void addWarp(String name, OfflinePlayer player, Location location) {
        Warp warp = new WarpImpl(name, player, location);
        warps.put(name, warp);
    }

    public void removeWarp(String name) {
        warps.remove(name);
    }

    public boolean exists(String name) {
        return warps.containsKey(name);
    }

    static class WarpImpl implements Warp {
        @Getter
        private final String name;
        @Getter
        private final OfflinePlayer author;
        @Getter
        private Location location;

        public WarpImpl(String name, OfflinePlayer author, Location location) {
            this.name = name;
            this.author = author;
            this.location = location;
        }

        public void setLocation(Location location) {
            this.location = location;
        }

        @Override
        public void teleport(Player player) {
            player.sendMessage("§7Tu vas être tp dans 2 secondes...");
            new BukkitRunnable() {
                @Override
                public void run() {
                    if(!player.isOnline()) {
                        cancel();
                        return;
                    }

                    player.teleport(getLocation());
                    player.setFallDistance(0);
                    if(player.isInsideVehicle() && player.getVehicle() != null)  {
                        var vehicle = player.getVehicle();
                        vehicle.teleport(getLocation());
                        vehicle.setFallDistance(0);
                        vehicle.addPassenger(player);
                    }

                    player.sendMessage(String.format("§7T'as été tp au warp %s", getName()));
                }
            }.runTaskLaterAsynchronously(Jijon.INSTANCE, 2000);
        }
    }
}
