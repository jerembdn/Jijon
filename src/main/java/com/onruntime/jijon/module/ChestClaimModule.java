package com.onruntime.jijon.module;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.Plugin;

import com.onruntime.jijon.Jijon;
import com.onruntime.jijon.manager.ClanManager;
import com.onruntime.jijon.util.FormatMessage;

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
			if (this.isProtected(event.getClickedBlock())) {
				Directional blockData = (Directional) event.getClickedBlock().getBlockData();
				Sign sign = (Sign) event.getClickedBlock().getRelative(blockData.getFacing(), 1).getState();

				if ((sign.getLine(0) != null && sign.getLine(0).equalsIgnoreCase("ratio by"))) {
					var authors = this.createAuthors(sign.getLine(1), sign.getLine(2), sign.getLine(3));

					boolean canOpen = this.canInteract(authors, event.getPlayer());

					if (!canOpen) {
						event.setCancelled(true);
						event.getPlayer().sendMessage(FormatMessage.error("§cAllez mange moi ce ratio tu peux pas ouvrir ce coffre."));
						return;
					}
				}
			}
		}
	}

	@EventHandler()
	public void onSignChange(SignChangeEvent event) {
		if (event.getLine(0).equalsIgnoreCase("ratio by")) {
			var authors = this.createAuthors(event.getLine(1), event.getLine(2), event.getLine(3));

			if (!this.canInteract(authors, event.getPlayer())) {
				event.setCancelled(true);
				event.getPlayer().sendMessage(FormatMessage.error("§cTu dois être dans le clan ou être le propriétaire du coffre pour pouvoir le protéger."));
				return;
			} else {
				event.getPlayer().sendMessage(FormatMessage.format("Claim", "§aCe coffre est désormais protégé et ne peut être ouvert ou détruit."));
				return;
			}
		}
	}

	@EventHandler()
	public void onBlockBreak(BlockBreakEvent event) {
		if (event.getPlayer().isOp()) {
			return;
		}

		if (event.getBlock().getType() == Material.CHEST || event.getBlock().getType() == Material.TRAPPED_CHEST) {
			if (this.isProtected(event.getBlock())) {
				Directional blockData = (Directional) event.getBlock().getBlockData();
				Sign sign = (Sign) event.getBlock().getRelative(blockData.getFacing(), 1).getState();

				if ((sign.getLine(0) != null && sign.getLine(0).equalsIgnoreCase("ratio by"))) {
					var authors = this.createAuthors(sign.getLine(1), sign.getLine(2), sign.getLine(3));

					boolean canBreak = this.canInteract(authors, event.getPlayer());

					if (!canBreak) {
						event.setCancelled(true);
						event.getPlayer().sendMessage(FormatMessage.error("§cCe coffre est protégé, vous n'êtes pas autorisé à le casser."));
						return;
					}
				}
			}
		}

		if (event.getBlock().getType() == Material.OAK_WALL_SIGN || event.getBlock().getType() == Material.BIRCH_WALL_SIGN
				|| event.getBlock().getType() == Material.ACACIA_WALL_SIGN || event.getBlock().getType() == Material.BAMBOO_WALL_SIGN
				|| event.getBlock().getType() == Material.BAMBOO_WALL_SIGN || event.getBlock().getType() == Material.CHERRY_WALL_SIGN
				|| event.getBlock().getType() == Material.JUNGLE_WALL_SIGN || event.getBlock().getType() == Material.SPRUCE_WALL_SIGN
				|| event.getBlock().getType() == Material.CRIMSON_WALL_SIGN || event.getBlock().getType() == Material.DARK_OAK_WALL_SIGN
				|| event.getBlock().getType() == Material.MANGROVE_WALL_SIGN) {
			Sign sign = (Sign) event.getBlock().getState();

			if ((sign.getLine(0) != null && sign.getLine(0).equalsIgnoreCase("ratio by"))) {
				var authors = this.createAuthors(sign.getLine(1), sign.getLine(2), sign.getLine(3));

				boolean canBreak = this.canInteract(authors, event.getPlayer());

				if (!canBreak) {
					event.setCancelled(true);
					event.getPlayer().sendMessage(FormatMessage.error("§cCe coffre est protégé, vous n'êtes pas autorisé à le casser."));
					return;
				}
			}
		}
	}

	public boolean canInteract(ArrayList<String> authors, Player player) {
		return authors.contains(player.getName()) || authors.contains(this.clanManager.getPlayerClan(player).getName());
	}

	public boolean isProtected(Block block) {
		Directional blockData = (Directional) block.getBlockData();
		Material blockType = block.getRelative(blockData.getFacing(), 1).getType();

		return blockType == Material.OAK_WALL_SIGN || blockType == Material.BIRCH_WALL_SIGN
				|| blockType == Material.ACACIA_WALL_SIGN || blockType == Material.BAMBOO_WALL_SIGN
				|| blockType == Material.BAMBOO_WALL_SIGN || blockType == Material.CHERRY_WALL_SIGN
				|| blockType == Material.JUNGLE_WALL_SIGN || blockType == Material.SPRUCE_WALL_SIGN
				|| blockType == Material.CRIMSON_WALL_SIGN || blockType == Material.DARK_OAK_WALL_SIGN
				|| blockType == Material.MANGROVE_WALL_SIGN;
	}

	private ArrayList<String> createAuthors(String ...authors) {
		return new ArrayList<String>(Arrays.asList(authors));
	}
}
