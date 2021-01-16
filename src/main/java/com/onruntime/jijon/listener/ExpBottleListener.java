package com.onruntime.jijon.listener;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.ThrownExpBottle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ExpBottleEvent;
import org.bukkit.inventory.ItemStack;

public class ExpBottleListener implements Listener {

    @EventHandler
    public void onExpBottle(ExpBottleEvent event) {
        ThrownExpBottle bottle = event.getEntity();
        if(bottle.getShooter() instanceof Player) {
            event.setExperience(0);
            Player player = (Player) bottle.getShooter();
            player.setLevel(player.getLevel() + 1);
            ItemStack newBottle = new ItemStack(Material.GLASS_BOTTLE);
            player.getInventory().addItem(newBottle);
        }
    }
}
