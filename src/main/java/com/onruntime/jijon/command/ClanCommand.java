package com.onruntime.jijon.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.onruntime.jijon.Jijon;
import com.onruntime.jijon.manager.ClanManager;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class ClanCommand implements CommandExecutor {

	private final ClanManager clanManager;

	public ClanCommand(ClanManager clanManager) {
		this.clanManager = clanManager;
	}

	@Override
	public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
		var sender = (Player) commandSender;

		if (args.length >= 1) {
			if (args[0].equalsIgnoreCase("create")) {
				if (args.length >= 2) {
					var name = args[1];
					var tag = args.length >= 3 ? args[2] : null;

					clanManager.addClan(name, tag, sender);
					sender.sendMessage(String.format("§7Tu est maintenant le chef du clan §d§l%s§7.", name));

					return true;
				}

				return false;
			} else if (args[0].equalsIgnoreCase("invite")) {
				if (args.length >= 2) {
					var clan = this.clanManager.getPlayerClan(sender);
					var target = Jijon.INSTANCE.getServer().getPlayer(args[1]);

					if (target != null) {
						sender.sendMessage(String.format("§7Tu as invité §e§l%s§7 dans le clan §d§l%s§7.",
								target.getName(), clan.getName()));

						clan.invite(target, sender);

						var component = new TextComponent("Clique ici");
						component.setClickEvent(new ClickEvent(
								ClickEvent.Action.RUN_COMMAND, "/clan accept " + clan.getUniqueId()));

						target.getPlayer().sendMessage(
								String.format("§e§l%s§7T'as invité dans le clan §d§l%s. %s§7pour le rejoindre.",
										sender.getName(), clan.getName(), component.toLegacyText()));

						return true;
					}

					sender.sendMessage("§cCe joueur n'est pas connecté.");
					return false;
				}

				return false;
			} else if (args[0].equalsIgnoreCase("join")) {
				if (args.length >= 2) {
					var clan = this.clanManager.getClan(args[1]);

					if (clan != null) {
						if (clan.isMember(sender)) {
							sender.sendMessage("§cTu es déjà membre de ce clan.");
							return false;
						}

						if (clan.getInvites().contains(sender)) {
							clan.acceptInvite(sender);

							return true;
						}

						sender.sendMessage("§cTu n'as pas été invité dans ce clan.");
						return false;
					}

					sender.sendMessage("§cCe clan n'existe pas.");
					return false;
				}

				return false;
			} else if (args[0].equalsIgnoreCase("kick")) {
				if (args.length >= 2) {
					var clan = this.clanManager.getPlayerClan(sender);
					var target = Jijon.INSTANCE.getServer().getPlayer(args[1]);

					if (target != null) {
						if (clan.isMember(target)) {
							if (clan.getLeader().equals(sender)) {
								clan.removeMember(target);

								sender.sendMessage(String.format("§7Tu as expulsé §e§l%s§7 du clan §d§l%s§7.",
										target.getName(), clan.getName()));

								return true;
							}

							sender.sendMessage("§cTu n'es pas le chef de ce clan.");

							return false;
						}

						sender.sendMessage("§cCe joueur n'est pas membre de ton clan.");
						return false;
					}

					sender.sendMessage("§cCe joueur n'est pas connecté.");
					return false;
				}

				return false;
			} else if (args[0].equalsIgnoreCase("leave")) {
				var clan = this.clanManager.getPlayerClan(sender);

				if (clan != null) {
					if (clan.getLeader().equals(sender)) {
						sender.sendMessage("§cTu ne peux pas quitter ton clan car tu es le chef.");
						return false;
					}

					clan.removeMember(sender);

					sender.sendMessage(String.format("§7Tu as quitté le clan §d§l%s§7.", clan.getName()));
					return true;
				}

				sender.sendMessage("§cTu n'es pas membre d'un clan.");
				return false;
			} else if (args[0].equalsIgnoreCase("disband")) {
				var clan = this.clanManager.getPlayerClan(sender);

				if (clan != null) {
					if (clan.getLeader().equals(sender)) {
						clan.disband();

						sender.sendMessage(String.format("§7Tu as dissout le clan §d§l%s§7.", clan.getName()));

						return true;
					}

					sender.sendMessage("§cTu n'es pas le chef de ce clan.");

					return false;
				}

				sender.sendMessage("§cTu n'es pas membre d'un clan.");
				return false;
			} else if (args[0].equalsIgnoreCase("rename")) {
				if (args.length >= 2) {
					var clan = this.clanManager.getPlayerClan(sender);
					var name = args[1];

					if (clan != null) {
						if (clan.getLeader().equals(sender)) {
							clan.setName(name);

							sender.sendMessage(String.format("§7Tu as renommé le clan §d§l%s§7 en §d§l%s§7.",
									clan.getName(), name));

							return true;
						}

						sender.sendMessage("§cTu n'es pas le chef de ce clan.");

						return false;
					}

					sender.sendMessage("§cTu n'es pas membre d'un clan.");
					return false;
				}

				return false;
			} else if (args[0].equalsIgnoreCase("tag")) {
				if (args.length >= 2) {
					var clan = this.clanManager.getPlayerClan(sender);
					var tag = args[1];

					if (clan != null) {
						if (clan.getLeader().equals(sender)) {
							clan.setTag(tag);

							sender.sendMessage(String.format("§7Tu as changé le tag du clan §d§l%s§7 en §d§l%s§7.",
									clan.getName(), tag));

							return true;
						}

						sender.sendMessage("§cTu n'es pas le chef de ce clan.");

						return false;
					}

					sender.sendMessage("§cTu n'es pas membre d'un clan.");
					return false;
				}

				return false;
			} else if (args[0].equalsIgnoreCase("help")) {
				sender.sendMessage("§7§m----------------------------------------");
				sender.sendMessage("§7§lClan §7- §d§lCommandes");
				sender.sendMessage("§7§m----------------------------------------");
				sender.sendMessage("§7/clan create <name> [tag] §7- §d§lCréer un clan");
				sender.sendMessage("§7/clan invite <player> §7- §d§lInviter un joueur");
				sender.sendMessage("§7/clan accept <player> §7- §d§lAccepter une invitation");
				sender.sendMessage("§7/clan kick <player> §7- §d§lExpulser un joueur");
				sender.sendMessage("§7/clan leave §7- §d§lQuitter le clan");
				sender.sendMessage("§7/clan disband §7- §d§lDissoudre le clan");
				sender.sendMessage("§7/clan rename <name> §7- §d§lRenommer le clan");
				sender.sendMessage("§7/clan tag <tag> §7- §d§lChanger le tag du clan");
				sender.sendMessage("§7/clan info §7- §d§lAfficher les informations du clan");
				sender.sendMessage("§7/clan list §7- §d§lAfficher la liste des clans");
				sender.sendMessage("§7§m----------------------------------------");

				return true;
			}
		}

		return false;
	}
}
