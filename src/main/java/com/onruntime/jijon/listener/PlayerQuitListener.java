package com.onruntime.jijon.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * This file is a part of Valon, located on fr.the3dx900.valon.listener
 * <p>
 * Copyright (c) BerryGames https://berrygames.net/ - All rights reserved
 * <p>
 *
 * @author Jérèm {@literal <hey@3dx900.fr>}
 * Created the 22/09/2019 at 14:34.
 */
public class PlayerQuitListener implements Listener {

    @EventHandler()
    public void onPlayerQuit(PlayerQuitEvent event) {
        event.setQuitMessage("§8[§d-§8] §d" + event.getPlayer().getDisplayName() + "§7 a quitté le serveur !");
    }
}
