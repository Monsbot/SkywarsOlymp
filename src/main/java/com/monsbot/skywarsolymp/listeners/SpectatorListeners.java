package com.monsbot.skywarsolymp.listeners;

import com.monsbot.skywarsolymp.gameplay.Gui;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class SpectatorListeners implements Listener {

    Gui gui = new Gui();

    @EventHandler
    public void playerInteractEvent(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        try {
            gui.itemSwitch(player, event.getItem());
        } catch (NullPointerException ignore) {

        }

        if(player.getGameMode() == GameMode.ADVENTURE) {
            event.setCancelled(true);
        }
    }


    @EventHandler
    public void pickUpItemEvent(PlayerPickupItemEvent event) {
        if(event.getPlayer().getGameMode() == GameMode.ADVENTURE) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void changeXpEvent(PlayerExpChangeEvent event) {
        if(event.getPlayer().getGameMode() == GameMode.ADVENTURE) {
            event.setAmount(0);
        }
    }

    @EventHandler
    public void dropItemEvent(PlayerDropItemEvent event) {
        if(event.getPlayer().getGameMode() == GameMode.ADVENTURE) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void foodLevelChangeEvent(FoodLevelChangeEvent event) {
        if(event.getEntity().getGameMode() == GameMode.ADVENTURE) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void inventoryClickedEvent(InventoryClickEvent event) {

        ItemStack clicked = event.getCurrentItem(); // The item that was clicked
        Inventory inventory = event.getInventory();
        Player player = (Player) event.getWhoClicked();

        if(player.getGameMode() == GameMode.ADVENTURE) {
            event.setCancelled(true);
            gui.guiSwitch(player, clicked, inventory);
            return;
        }
        event.setCancelled(gui.guiSwitch(player, clicked, inventory));
    }

    //I have no Idea how to do this, I started searching at 10:35 am and now at 22:00 I gave up. What is this crap I'm done
    /*
    @EventHandler//(priority = EventPriority.HIGH)
    public void entityDamageByEntityEvent(EntityDamageByEntityEvent event) {
        if(!(event.getDamager() instanceof Projectile) || !(event.getEntity() instanceof Player)) {
            return;
        }

        Projectile projectile = (Projectile) event.getDamager();
        Vector velocity = projectile.getVelocity();

        Player shooter = (Player) projectile.getShooter();
        Player player = (Player) event.getEntity();
        Boolean wasFlying = player.isFlying();


        if(player.getGameMode() == GameMode.ADVENTURE) {
            Location tempLoc = player.getLocation();

            player.teleport(player.getLocation().add(0, 5, 0));
            player.setFlying(true);


            Projectile newProjectile;
            String type;
            if(projectile instanceof Arrow) {
                newProjectile = shooter.launchProjectile(Arrow.class);
                type = "arrow";
            } else {
                newProjectile = shooter.launchProjectile(Snowball.class);
                type = "snowball";
            }
            newProjectile.setShooter(shooter);
            newProjectile.teleport(tempLoc);
            newProjectile.setVelocity(velocity);
            newProjectile.setBounce(false);

            player.setFlying(wasFlying);
            player.sendMessage("§cYou have been teleported because you blocked an " + type + "!");

            event.setCancelled(true);
            projectile.remove();
        }

    }*/
}
