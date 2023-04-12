package com.onruntime.jijon.data;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public interface Clan {
	UUID getUniqueId();
	String getName();
	void setName(String name);

	String getTag();
	void setTag(String tag);

	OfflinePlayer getLeader();
	void setLeader(OfflinePlayer player);

	ArrayList<OfflinePlayer> getMembers();
	boolean isMember(OfflinePlayer player);
	void addMember(OfflinePlayer player);
	void removeMember(OfflinePlayer player);

	default void setPlayerTags(Player player) {
		player.setDisplayName(String.format("§a§l%s§r %s", this.getTag(), player.getName()));
		player.setPlayerListName(String.format("§a§l%s§r %s", this.getTag(), player.getName()));
	}
	default void resetPlayerTags(Player player) {
		player.setDisplayName(player.getName());
		player.setPlayerListName(player.getName());
	}

	void invite(Player player, Player inviter);
	ArrayList<OfflinePlayer> getInvites();
	boolean isInvited(OfflinePlayer player);
	void acceptInvite(OfflinePlayer player);

	void broadcast(String message, Player ...without);

	void disband();
}