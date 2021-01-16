package com.onruntime.jijon.data;

import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public interface Warp {
    String getName();
    OfflinePlayer getAuthor();

    Location getLocation();
    void setLocation(Location location);

    void teleport(Player player);
}
