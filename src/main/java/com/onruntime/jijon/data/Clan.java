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

	void invite(Player player, Player inviter);
	ArrayList<OfflinePlayer> getInvites();
	void acceptInvite(OfflinePlayer player);

	void broadcast(String message);

	void disband();
}