package com.onruntime.jijon.listener;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

/**
 * This file is a part of Valon, located on fr.the3dx900.valon.listener
 * <p>
 * Copyright (c) BerryGames https://berrygames.net/ - All rights reserved
 * <p>
 *
 * @author Jérèm {@literal <hey@3dx900.fr>}
 * Created the 22/09/2019 at 14:36.
 */
public class AsyncPlayerChatListener implements Listener {

    @EventHandler()
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        Bukkit.getOnlinePlayers()
                .stream()
                .filter(player -> event.getMessage().contains(player.getDisplayName()) && event.getPlayer() != player)
                .forEach(player -> {
                    event.setMessage(event.getMessage().replace(player.getDisplayName(), "§5§l" + player.getDisplayName() + "§7"));
                    player.playSound(player.getLocation(), Sound.ENTITY_CHICKEN_EGG, 1f, 1f);
                });

        event.setFormat("§7" + event.getPlayer().getDisplayName() + " §8»§7 " + event.getMessage().replaceAll("%", "%%"));
    }
}
