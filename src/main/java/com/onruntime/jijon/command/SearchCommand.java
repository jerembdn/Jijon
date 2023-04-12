package com.onruntime.jijon.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

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
