package com.onruntime.jijon.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * This file is a part of Valon, located on fr.the3dx900.valon.listener
 * <p>
 * Copyright (c) BerryGames https://berrygames.net/ - All rights reserved
 * <p>
 *
 * @author Jérèm {@literal <hey@3dx900.fr>}
 * Created the 22/09/2019 at 14:30.
 */
public class PlayerJoinListener implements Listener {

    @EventHandler()
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.setJoinMessage("§8[§b+§8] §b" + event.getPlayer().getDisplayName() + "§7 a rejoint le serveur !");
    }
}
