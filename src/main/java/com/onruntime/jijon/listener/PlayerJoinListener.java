package com.onruntime.jijon.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import net.md_5.bungee.api.ChatColor;

public class PlayerJoinListener implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.setJoinMessage("§8[§b+§8] §b" + event.getPlayer().getDisplayName() + "§7 a rejoint le serveur !");

        event.getPlayer().setDisplayName(ChatColor.GRAY + event.getPlayer().getName());
        event.getPlayer().setPlayerListName("§7" + event.getPlayer().getName());
    }
}
