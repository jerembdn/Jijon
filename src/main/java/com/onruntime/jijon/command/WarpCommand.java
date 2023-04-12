package com.onruntime.jijon.command;

import com.onruntime.jijon.manager.WarpManager;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.*;

public class WarpCommand implements TabExecutor {

    private final WarpManager manager;

    public WarpCommand(WarpManager manager) {
        this.manager = manager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player = (Player) sender;

        if (label.equalsIgnoreCase("setwarp") && args.length >= 1) {
            if (manager.exists(args[0])) {
                player.sendMessage("§7Un warp avec ce nom existe déjà. (§c" + args[0] + "§7)\n");
                return false;
            }
            manager.addWarp(args[0], player);
            player.sendMessage("§7T'as ajouté le warp " + args[0]);
            return true;
        } else if (label.equalsIgnoreCase("delwarp") && args.length >= 1) {
            if (!manager.exists(args[0])) {
                player.sendMessage("§cJ'arrive pas à trouver un warp avec ce nom gros.\n");
                return false;
            }
            manager.removeWarp(args[0]);
            player.sendMessage("§7T'as supprimé le warp " + args[0]);
            return true;
        } else if (label.equalsIgnoreCase("warps")) {
            player.performCommand("warp list");
            return true;
        } else if (label.equalsIgnoreCase("warp")) {
            if (args.length == 0 || args[0].equalsIgnoreCase("list")) {
                player.sendMessage(
                        String.format("§7Les warps du serv (§b%d§7) : %s",
                                manager.getWarps().size(),
                                String.join(", ", manager.getWarps().keySet())
                        )
                );
                return true;
            }

            if(manager.exists(args[0])) {
                var warp = manager.getWarp(args[0]);

                if(player.isSleeping()) {
                    player.sendMessage("§cTu peux pas être tp comme tu dors mdr.");
                    return true;
                }

                if(warp.getLocation().getWorld() != player.getWorld() && (args[1] == null || !args[1].equalsIgnoreCase("§force"))) {
                    var component = new TextComponent("Clique ici");
                    component.setClickEvent(new ClickEvent(
                            ClickEvent.Action.RUN_COMMAND, "/warp " + warp + " §force"
                    ));

                    player.sendMessage(String.format("§7Le monde de ce warp est §c§l%s§7. %s pour accepter le tp.",
                            warp.getLocation().getWorld().getName(),
                            component)
                    );
                    return true;
                }

                warp.teleport(player);
                return true;
            } else {
                player.sendMessage("§cJ'arrive pas à trouver un warp avec ce nom gros.\n");
                return false;
            }
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String label, String[] args) {
        List<String> result = new ArrayList<>();

        if (label.equalsIgnoreCase("warp") || label.equalsIgnoreCase("delwarp"))
            StringUtil.copyPartialMatches(args[0], manager.getWarps().keySet(), result);

        Collections.sort(result);
        return result;
    }
}
