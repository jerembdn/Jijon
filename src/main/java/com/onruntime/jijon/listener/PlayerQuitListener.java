package com.onruntime.jijon.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener implements Listener {
    @EventHandler()
    public void onPlayerQuit(PlayerQuitEvent event) {
        event.setQuitMessage("§8[§d-§8] §d" + event.getPlayer().getDisplayName() + "§7 a quitté le serveur !");
    }
}
