package com.onruntime.jijon.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * This file is a part of Valon, located on fr.the3dx900.valon.command
 * <p>
 * Copyright (c) BerryGames https://berrygames.net/ - All rights reserved
 * <p>
 *
 * @author Jérèm {@literal <hey@3dx900.fr>}
 * Created the 22/02/2020 at 20:28.
 */
public class SearchCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if(args[0].equalsIgnoreCase("on")) {
            return true;
        }
        if(args[0].equalsIgnoreCase("off")) {
            return true;
        }
        return false;
    }
}
