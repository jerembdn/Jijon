package com.onruntime.jijon.manager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import com.onruntime.jijon.Jijon;
import com.onruntime.jijon.data.Clan;
import com.onruntime.jijon.util.FormatMessage;

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
		Jijon.INSTANCE.getServer().getPluginManager().registerEvents(this, Jijon.INSTANCE);

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

	@Override
	public void stop() {
		config.set("clans", null);

		clans.forEach((s, clan) -> section.set(s.toString(), clan));
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

	public boolean isPlayerInClan(Player player) {
		return clans.values().stream().anyMatch(clan -> clan.isMember(player));
	}

	public boolean doesClanExists(String name) {
		return clans.values().stream().anyMatch(clan -> clan.getName().equalsIgnoreCase(name));
	}

	public Clan addClan(String name, String tag, OfflinePlayer leader) {
		return addClan(name, tag, leader, new HashMap<UUID, OfflinePlayer>(), new HashMap<UUID, OfflinePlayer>());
	}

	public Clan addClan(String name, String tag, OfflinePlayer leader, HashMap<UUID, OfflinePlayer> members) {
		return addClan(name, tag, leader, members, new HashMap<UUID, OfflinePlayer>());
	}

	public Clan addClan(String name, String tag, OfflinePlayer leader, HashMap<UUID, OfflinePlayer> members,
			HashMap<UUID, OfflinePlayer> invites) {
		var uniqueId = UUID.randomUUID();
		var clan = new ClanImpl(uniqueId, name, tag, leader, members, invites);
		clans.put(uniqueId, clan);

		return clan;
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerJoin(PlayerJoinEvent event) {
		var player = event.getPlayer();

		this.clans.values().forEach(clan -> {
			if (clan.isInvited(player)) {
				var component = new TextComponent("Clique ici");
				component.setClickEvent(new ClickEvent(
						ClickEvent.Action.RUN_COMMAND, "/clan join " + clan.getUniqueId()));

				player.sendMessage(
						FormatMessage.format(clan.getTag(),
								"§7Tu as été invité dans le clan §a§l%s§7. %s§7 pour le rejoindre.", clan.getName(),
								component.toLegacyText()));
			}
		});

		if (this.isPlayerInClan(player)) {
			var clan = this.getPlayerClan(player);

			clan.setPlayerTags(player);
		}
	}

	static class ClanImpl implements Clan {
		@Getter
		private final UUID uniqueId;

		@Getter
		@Setter
		private String name;

		@Getter
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

			if (tag != null && tag.length() > 4) {
				this.setTag(tag.substring(0, 4));
			}

			var tempMembers = new ArrayList<OfflinePlayer>();

			tempMembers.add(leader);
			tempMembers.addAll(members.values());

			tempMembers.forEach(player -> {
				if (player.isOnline()) {
					this.setPlayerTags(player.getPlayer());
				}
			});
		}

		@Override
		public void setTag(String tag) {
			if (tag != null && tag.length() > 4) {
				this.tag = tag.substring(0, 4);
			} else {
				this.tag = tag;
			}

			getMembers().forEach(player -> {
				if (player.isOnline()) {
					this.setPlayerTags(player.getPlayer());
				}
			});
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

			if (player.isOnline()) {
				this.setPlayerTags(player.getPlayer());
			}
		}

		@Override
		public void removeMember(OfflinePlayer player) {
			this.members.remove(player.getUniqueId());

			if (player.isOnline()) {
				this.resetPlayerTags(player.getPlayer());
			}
		}

		@Override
		public void invite(Player who, Player inviter) {
			if (!invites.containsKey(who.getUniqueId())) {
				this.invites.put(who.getUniqueId(), who);
			}
		}

		@Override
		public ArrayList<OfflinePlayer> getInvites() {
			return new ArrayList<OfflinePlayer>(invites.values());
		}

		@Override
		public boolean isInvited(OfflinePlayer player) {
			return invites.containsKey(player.getUniqueId());
		}

		@Override
		public void acceptInvite(OfflinePlayer player) {
			if (invites.containsKey(player.getUniqueId())) {
				this.addMember(player);
				this.invites.remove(player.getUniqueId());
			}
		}

		@Override
		public void broadcast(String message, Player... without) {
			var players = new ArrayList<Player>();

			getMembers().forEach(player -> {
				if (player.isOnline()) {
					var onlinePlayer = (Player) player;
					players.add(onlinePlayer);
				}
			});

			players.removeAll(Arrays.asList(without));

			players.forEach(player -> player.sendMessage(FormatMessage.format(this.getTag(), message)));
		}

		@Override
		public void disband() {
			broadcast("§7Le clan §a§l%s§7 a été dissout." + this.getName());
		}
	}
}
