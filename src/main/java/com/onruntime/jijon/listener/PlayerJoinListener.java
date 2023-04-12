package com.onruntime.jijon.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {
    @EventHandler()
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.setJoinMessage("§8[§b+§8] §b" + event.getPlayer().getDisplayName() + "§7 a rejoint le serveur !");
    }
}
