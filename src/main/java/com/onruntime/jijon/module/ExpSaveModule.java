package com.onruntime.jijon.module;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.entity.ThrownExpBottle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.ExpBottleEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public class ExpSaveModule implements IModule, Listener {

	public ExpSaveModule() {
	}

	@Override
	public void init(Plugin plugin) {
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler
	public void onExpBottle(ExpBottleEvent event) {
		ThrownExpBottle bottle = event.getEntity();

		if (bottle.getShooter() instanceof Player) {
			Player player = (Player) bottle.getShooter();

			event.setExperience(0);
			player.setLevel(player.getLevel() + 1);

			ItemStack newBottle = new ItemStack(Material.GLASS_BOTTLE);
			player.getInventory().addItem(newBottle);
		}
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getClickedBlock().getType() != Material.BEEHIVE)
				return;
			if (event.getItem() != null && event.getItem().getType() == Material.GLASS_BOTTLE) {
				if (event.getPlayer().getLevel() > 0) {
					int amount = event.getPlayer().getInventory().getItemInMainHand().getAmount();
					if (amount > 1)
						event.getPlayer().getInventory().getItemInMainHand().setAmount(amount - 1);
					else
						event.getPlayer().getInventory().setItemInMainHand(new ItemStack(Material.AIR));

					event.getPlayer().setLevel(event.getPlayer().getLevel() - 1);

					ItemStack itemStack = new ItemStack(Material.EXPERIENCE_BOTTLE);
					event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.ITEM_BOTTLE_FILL, 1f, 1f);
					event.getPlayer().getInventory().addItem(itemStack);
				} else {
					event.getPlayer().sendMessage("§cT'as pas assez de levels pour mettre en bouteille.");
				}
			}
		}
	}
}
