package com.onruntime.jijon.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import com.onruntime.jijon.Jijon;
import com.onruntime.jijon.data.Clan;

import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class ClanManager implements Manager, Listener {

	private FileConfiguration config;
	private ConfigurationSection section;

	@Getter
	private final Map<UUID, Clan> clans;

	public ClanManager() {
		this.clans = new HashMap<>();
	}

	@Override
	public void init() {
		try {
			this.config = Jijon.INSTANCE.getConfigManager().createConfig("clans");
			config.createSection("clans");

			this.section = config.getConfigurationSection("clans");

			if (section != null) {
				section.getKeys(true).forEach(clan -> {
					var name = section.getString(String.format("%s.name", clan));
					var tag = section.getString(String.format("%s.tag", clan));
					var leader = section.getOfflinePlayer(String.format("%s.leader", clan));
					var membersUniqueIds = section.getStringList(String.format("%s.members", clan));
					var invitesUniqueIds = section.getStringList(String.format("%s.invites", clan));

					var members = new HashMap<UUID, OfflinePlayer>();
					var invites = new HashMap<UUID, OfflinePlayer>();

					membersUniqueIds.forEach(uniqueId -> {
						var player = Jijon.INSTANCE.getServer().getOfflinePlayer(UUID.fromString(uniqueId));
						members.put(player.getUniqueId(), player);
					});

					invitesUniqueIds.forEach(uniqueId -> {
						var player = Jijon.INSTANCE.getServer().getOfflinePlayer(UUID.fromString(uniqueId));
						invites.put(player.getUniqueId(), player);
					});

					addClan(name, tag, leader, members, invites);
				});
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Clan getClan(UUID uniqueId) {
		return clans.getOrDefault(uniqueId, null);
	}

	public Clan getClan(String name) {
		return clans.values().stream().filter(clan -> clan.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
	}

	public Clan getPlayerClan(Player player) {
		return clans.values().stream().filter(clan -> clan.isMember(player))
				.findFirst().orElse(null);
	}

	public boolean doesClanExists(String name) {
		return clans.values().stream().anyMatch(clan -> clan.getName().equalsIgnoreCase(name));
	}

	public void addClan(String name, String tag, OfflinePlayer leader) {
		addClan(name, tag, leader, new HashMap<UUID, OfflinePlayer>(), new HashMap<UUID, OfflinePlayer>());
	}

	public void addClan(String name, String tag, OfflinePlayer leader, HashMap<UUID, OfflinePlayer> members) {
		addClan(name, tag, leader, members, new HashMap<UUID, OfflinePlayer>());
	}

	public void addClan(String name, String tag, OfflinePlayer leader, HashMap<UUID, OfflinePlayer> members,
			HashMap<UUID, OfflinePlayer> invites) {
		var uniqueId = UUID.randomUUID();
		var clan = new ClanImpl(uniqueId, name, tag, leader, members, invites);
		clans.put(uniqueId, clan);
	}

	static class ClanImpl implements Clan {
		@Getter
		private final UUID uniqueId;

		@Getter
		@Setter
		private String name;

		@Getter
		@Setter
		private String tag;

		@Getter
		@Setter
		private OfflinePlayer leader;

		private final Map<UUID, OfflinePlayer> members;

		private final Map<UUID, OfflinePlayer> invites;

		public ClanImpl(UUID uniqueId, String name, String tag, OfflinePlayer leader) {
			this(uniqueId, name, tag, leader, new HashMap<UUID, OfflinePlayer>(), new HashMap<UUID, OfflinePlayer>());
		}

		public ClanImpl(
				UUID uniqueId,
				String name,
				String tag,
				OfflinePlayer leader,
				HashMap<UUID, OfflinePlayer> members,
				HashMap<UUID, OfflinePlayer> invites) {
			this.uniqueId = uniqueId;
			this.name = name;
			this.tag = tag;
			this.leader = leader;
			this.members = members;
			this.invites = invites;
		}

		@Override
		public ArrayList<OfflinePlayer> getMembers() {
			var members = new ArrayList<OfflinePlayer>();

			members.add(leader);
			members.addAll(this.members.values());

			return members;
		}

		@Override
		public boolean isMember(OfflinePlayer player) {
			return getMembers().contains(player);
		}

		@Override
		public void addMember(OfflinePlayer player) {
			this.members.put(player.getUniqueId(), player);
		}

		@Override
		public void removeMember(OfflinePlayer player) {
			this.members.remove(player.getUniqueId());
		}

		@Override
		public void invite(Player who, Player inviter) {
			if (!invites.containsKey(who.getUniqueId())) {
				this.invites.put(who.getUniqueId(), who);

				var component = new TextComponent("Clique ici");
				component.setClickEvent(new ClickEvent(
						ClickEvent.Action.RUN_COMMAND, "/clan accept " + this.getUniqueId()));

				who.getPlayer().sendMessage(
						String.format("§e§l%s§7T'as invité dans le clan §d§l%s. %s§7pour le rejoindre.",
								inviter.getName(), this.getName(), component.toLegacyText()));
			}
		}

		@Override
		public ArrayList<OfflinePlayer> getInvites() {
			return new ArrayList<OfflinePlayer>(invites.values());
		}

		@Override
		public void acceptInvite(OfflinePlayer player) {
			if (invites.containsKey(player.getUniqueId())) {
				this.broadcast("§d§l%s§7 a rejoint le clan." + player.getName());

				this.addMember(player);
				this.invites.remove(player.getUniqueId());

				if (player.isOnline()) {
					player.getPlayer().sendMessage("§7Tu fais officiellement parti du clan §d§l%s§7." + this.getName());
				}
			}
		}

		@Override
		public void broadcast(String message) {
			getMembers().stream().filter(member -> member.isOnline()).forEach(member -> {
				member.getPlayer().sendMessage(message);
			});
		}

		@Override
		public void disband() {
			broadcast("§7Le clan §d§l%s§7 a été dissout." + this.getName());
		}
	}
}
