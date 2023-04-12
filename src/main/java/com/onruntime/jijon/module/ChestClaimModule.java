package com.onruntime.jijon.module;

import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.Plugin;

import com.onruntime.jijon.Jijon;
import com.onruntime.jijon.data.Clan;
import com.onruntime.jijon.manager.ClanManager;

public class ChestClaimModule implements IModule, Listener {
	private final ClanManager clanManager;

	public ChestClaimModule(Jijon jijon) {
		this.clanManager = jijon.getClanManager();
	}

	@Override
	public void init(Plugin plugin) {
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onChestOpen(PlayerInteractEvent event) {
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK
				&& (event.getClickedBlock().getType() == Material.CHEST
						|| event.getClickedBlock().getType() == Material.TRAPPED_CHEST)) {
			if (event.getClickedBlock().getRelative(0, 1, 0).getType() == Material.OAK_SIGN
					|| event.getClickedBlock().getRelative(0, 1, 0).getType() == Material.OAK_WALL_SIGN) {
				Sign sign = (Sign) event.getClickedBlock().getRelative(0, 1, 0);

				if ((sign.getLine(1) != null && sign.getLine(1).equalsIgnoreCase("ratio by")) && sign.getLine(2) == null) {
					boolean canOpen = false;

					if(this.clanManager.doesClanExists(sign.getLine(2))) {
						Clan clan = this.clanManager.getClan(sign.getLine(2));

						if (clan.isMember(event.getPlayer())) {
							canOpen = true;
						}
					} else if (sign.getLine(2).equalsIgnoreCase(event.getPlayer().getName())) {
						canOpen = true;
					}

					if (!canOpen) {
						event.setCancelled(true);
						event.getPlayer().sendMessage("§cAllez mange moi ce ratio tu peux pas ouvrir ce coffre.");
						return;
					}
				}
			}
		}
	}
}
