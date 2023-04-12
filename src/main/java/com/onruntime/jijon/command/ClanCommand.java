package com.onruntime.jijon.command;

import java.util.stream.Collectors;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.onruntime.jijon.Jijon;
import com.onruntime.jijon.manager.ClanManager;
import com.onruntime.jijon.util.FormatMessage;

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
					if (this.clanManager.isPlayerInClan(sender)) {
						sender.sendMessage(FormatMessage.error("§cTu es déjà dans un clan."));
						return true;
					}

					var name = args[1];
					var tag = args.length >= 3 ? args[2].toUpperCase() : name.substring(0, 4).toUpperCase();

					var clan = clanManager.addClan(name, tag, sender);
					sender.sendMessage(
							FormatMessage.format(clan.getTag(), "§7Tu est maintenant le chef du clan §a§l%s§7.", name));

					return true;
				}

				return false;
			} else if (args[0].equalsIgnoreCase("invite")) {
				if (args.length >= 2) {
					var clan = this.clanManager.getPlayerClan(sender);
					var target = Jijon.INSTANCE.getServer().getPlayer(args[1]);

					if (target != null) {
						if (target == sender) {
							sender.sendMessage(FormatMessage.error("§cTu ne peux pas t'inviter toi même."));
							return true;
						}

						sender.sendMessage(
								FormatMessage.format(clan.getTag(), "§7Tu as invité §e§l%s§7 dans le clan §a§l%s§7.",
										target.getName(), clan.getName()));

						clan.invite(target, sender);

						var component = new TextComponent("Clique ici");
						component.setClickEvent(new ClickEvent(
								ClickEvent.Action.RUN_COMMAND, "/clan join " + clan.getUniqueId()));

						target.getPlayer().sendMessage(
								String.format("§e§l%s§7 t'as invité dans le clan §a§l%s. %s§7 pour le rejoindre.",
										sender.getName(), clan.getName(), component.toLegacyText()));

						return true;
					}

					sender.sendMessage(FormatMessage.error("§cCe joueur n'est pas connecté."));
					return true;
				}

				return false;
			} else if (args[0].equalsIgnoreCase("join")) {
				if (args.length >= 2) {
					var clan = this.clanManager.getClan(args[1]);

					if (clan != null) {
						if (clan.isMember(sender)) {
							sender.sendMessage(FormatMessage.error("§cTu es déjà membre de ce clan."));
							return true;
						}

						if (clan.getInvites().contains(sender)) {
							clan.acceptInvite(sender);

							sender.sendMessage(FormatMessage.format(clan.getTag(),
									"§7Tu fais officiellement parti du clan §a§l%s§7.", clan.getName()));
							clan.broadcast(String.format("§e§l%s§7 a rejoint le clan.", sender.getName()), sender);

							return true;
						}

						sender.sendMessage(FormatMessage.error("§cTu n'as pas été invité dans ce clan."));
						return true;
					}

					sender.sendMessage(FormatMessage.error("§cCe clan n'existe pas."));
					return true;
				}

				return false;
			} else if (args[0].equalsIgnoreCase("kick")) {
				if (args.length >= 2) {
					var clan = this.clanManager.getPlayerClan(sender);
					var target = Jijon.INSTANCE.getServer().getPlayer(args[1]);

					if (clan == null) {
						sender.sendMessage(FormatMessage.error("§cTu n'as pas de clan."));
						return true;
					}

					if (target == clan.getLeader()) {
						sender.sendMessage(FormatMessage.error("§cTu ne peux pas expulser le chef du clan."));
						return true;
					}

					if (target != null) {
						if (clan.isMember(target)) {
							if (clan.getLeader().equals(sender)) {
								clan.removeMember(target);

								sender.sendMessage(FormatMessage.format(clan.getTag(),
										"§7Tu as expulsé §e§l%s§7 du clan §a§l%s§7.",
										target.getName(), clan.getName()));

								target.sendMessage(FormatMessage.format(clan.getTag(),
										"§7Tu as été expulsé du clan §a§l%s§7.", clan.getName()));

								clan.broadcast(String.format("§e§l%s§7 a été expulsé du clan.", target.getName()),
										sender);

								return true;
							}

							sender.sendMessage(FormatMessage.error("§cTu n'es pas le chef de ce clan."));
							return true;
						}

						sender.sendMessage(FormatMessage.error("§cCe joueur n'est pas membre de ton clan."));
						return true;
					}

					sender.sendMessage(FormatMessage.error("§cCe joueur n'est pas connecté."));
					return true;
				}

				return false;
			} else if (args[0].equalsIgnoreCase("leave")) {
				var clan = this.clanManager.getPlayerClan(sender);

				if (clan != null) {
					if (clan.getLeader().equals(sender)) {
						sender.sendMessage(FormatMessage.error("§cTu ne peux pas quitter ton clan car tu es le chef."));
						return true;
					}

					clan.removeMember(sender);

					sender.sendMessage(
							FormatMessage.format(clan.getTag(), "§7Tu as quitté le clan §a§l%s§7.", clan.getName()));

					clan.broadcast(String.format("§e§l%s§7 a quitté le clan.", sender.getName()));
					return true;
				}

				sender.sendMessage(FormatMessage.error("§cTu n'es pas membre d'un clan."));
				return true;
			} else if (args[0].equalsIgnoreCase("disband")) {
				var clan = this.clanManager.getPlayerClan(sender);

				if (clan != null) {
					if (clan.getLeader().equals(sender)) {
						clan.disband();

						sender.sendMessage(FormatMessage.format(clan.getTag(), "§7Tu as dissout le clan §a§l%s§7.",
								clan.getName()));

						return true;
					}

					sender.sendMessage(FormatMessage.error("§cTu n'es pas le chef de ce clan."));
					return true;
				}

				sender.sendMessage(FormatMessage.error("§cTu n'es pas membre d'un clan."));
				return true;
			} else if (args[0].equalsIgnoreCase("rename")) {
				if (args.length >= 2) {
					var clan = this.clanManager.getPlayerClan(sender);
					var name = args[1];

					if (clan != null) {
						if (clan.getLeader().equals(sender)) {
							var oldName = clan.getName();
							clan.setName(name);

							sender.sendMessage(
									FormatMessage.format(clan.getTag(), "§7Tu as renommé le clan §a%s§7 en §a§l%s§7.",
											oldName, name));

							return true;
						}

						sender.sendMessage(FormatMessage.error("§cTu n'es pas le chef de ce clan."));

						return true;
					}

					sender.sendMessage(FormatMessage.error("§cTu n'es pas membre d'un clan."));
					return true;
				}

				return false;
			} else if (args[0].equalsIgnoreCase("tag")) {
				if (args.length >= 2) {
					var clan = this.clanManager.getPlayerClan(sender);
					var tag = args[1];

					if (clan != null) {
						if (clan.getLeader().equals(sender)) {
							var oldTag = clan.getTag();
							clan.setTag(tag.toUpperCase());

							sender.sendMessage(FormatMessage.format(clan.getTag(),
									"§7Tu as changé le tag du clan §a%s§7 en §a§l%s§7.",
									oldTag, tag));

							return true;
						}

						sender.sendMessage(FormatMessage.error("§cTu n'es pas le chef de ce clan."));

						return true;
					}

					sender.sendMessage(FormatMessage.error("§cTu n'es pas membre d'un clan."));
					return true;
				}

				return false;
			} else if (args[0].equalsIgnoreCase("info")) {
				var clan = this.clanManager.getPlayerClan(sender);

				if (clan == null) {
					sender.sendMessage(FormatMessage.error("§cTu n'es pas membre d'un clan."));
					return true;
				}

				final var leader = clan.getLeader();

				if (args.length >= 2) {
					clan = this.clanManager.getClan(args[1]);

					if (clan == null) {
						sender.sendMessage(FormatMessage.error("§cCe clan n'existe pas."));
						return true;
					}
				}

				sender.sendMessage("§r");
				sender.sendMessage("§a§lClan§7 - §f§l" + clan.getName());
				sender.sendMessage("§r");
				sender.sendMessage("§a●§7 Tag: §a§l" + clan.getTag());
				sender.sendMessage("§a●§7 Chef: §a§l" + clan.getLeader().getName());
				sender.sendMessage("§a●§7 Membres (" + clan.getMembers().size() + "): §f" + clan.getMembers().stream()
						.map(player -> leader.getUniqueId() == player.getUniqueId() ? "§a§l" + player.getName() + "§f"
								: player.getName())
						.collect(Collectors.joining()));
				sender.sendMessage("§r");

				return true;
			} else if (args[0].equalsIgnoreCase("help")) {
				sender.sendMessage("§r");
				sender.sendMessage("§a§lClan§f - Aide");
				sender.sendMessage("§r");
				sender.sendMessage("§a● §7/clan create <name> §b[tag]§7 - §fCréer un clan");
				sender.sendMessage("§a● §7/clan invite <player> - §fInviter un joueur");
				sender.sendMessage("§a● §7/clan accept <player> - §fAccepter une invitation");
				sender.sendMessage("§a● §7/clan kick <player> - §fExpulser un joueur");
				sender.sendMessage("§a● §7/clan leave - §fQuitter son clan");
				sender.sendMessage("§a● §7/clan disband - §fDissoudre son clan");
				sender.sendMessage("§a● §7/clan rename <name> - §fRenommer son clan");
				sender.sendMessage("§a● §7/clan tag <tag> - §fChanger le tag de son clan");
				sender.sendMessage("§a● §7/clan info §b[clan]§7 - §fAfficher les informations d'un clan");
				sender.sendMessage("§r");

				return true;
			}
		}

		return false;
	}
}
