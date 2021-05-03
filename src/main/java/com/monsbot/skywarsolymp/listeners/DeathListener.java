package com.monsbot.skywarsolymp.listeners;

import com.monsbot.skywarsolymp.utilitys.FileReader;
import com.monsbot.skywarsolymp.utilitys.Playerdata;
import com.monsbot.skywarsolymp.gameplay.Gameplay;
import com.monsbot.skywarsolymp.gameplay.Gui;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player; //this is a lot of stuff to import lol
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;

//the death event
public class DeathListener implements Listener {

    FileReader fr = new FileReader();
    Playerdata pd = new Playerdata();
    Gameplay gp = new Gameplay(); //get Instances and ints for the random thing later
    Gui gui = new Gui();
    int min = 0;
    int max = 5;

    @EventHandler
    public void entityDamageEvent(EntityDamageEvent event) {

        if(event.getEntity() instanceof Player) { //check if the entity is a player

            Player player = (Player) event.getEntity(); //use it as a player

            if(player.getGameMode() != GameMode.SURVIVAL) {
                if(player.getLocation().getBlockY() < -50) {
                    player.teleport(new Location(player.getWorld(), player.getLocation().getX(), fr.getCenter().getBlockY() + 30, player.getLocation().getBlockZ(), player.getLocation().getYaw(), player.getLocation().getPitch()));
                }
                event.setCancelled(true);
                return;
            }

            //if the player is attacked by an entity the event is an instance of the other event

            if(event instanceof EntityDamageByEntityEvent) { //this took me way to long to find the solution for

                EntityDamageByEntityEvent event2 = (EntityDamageByEntityEvent) event;

                Player killer = null; //make the killer null

                if(event2.getDamager() instanceof Player) { //check if the damager is a player
                    killer = (Player) event2.getDamager();
                } else if(event2.getDamager() instanceof Projectile) { //or check if it is a projectile
                    Projectile projectile = (Projectile) event2.getDamager();
                    if(projectile.getShooter() instanceof Player) { //if it is a projectile, then get the entity who shot the projectile
                        killer = (Player) projectile.getShooter();
                    }
                }
                //if none of the stuff above is true, the killer is still null

                try {
                    if(killer.getGameMode() != GameMode.SURVIVAL) {
                        event.setCancelled(true);
                        return;
                    }
                } catch (NullPointerException ignore) {

                }

                if(player.getHealth() <= event.getDamage()) {
                    //check if the damage is bigger than the health. If that is the case the player would die, but we want to do our own stuff

                    fakePlayerDeath(player); //"fake" the player death
                    event.setCancelled(true); //cancel the event, so the player doesn't really die
                    if(killer == null) {
                        //if the killer is still null do this:
                        Bukkit.getServer().broadcastMessage(generateDeathMessage(event.getCause(), player)); //make a death message
                    } else {
                        //but if the killer does exist do this:

                        if(player.getGameMode() != GameMode.SURVIVAL) {
                            event.setCancelled(true);
                            return;
                        }

                        Bukkit.getServer().broadcastMessage(generateDeathMessage(killer, player)); //make a death message with the killer
                        pd.setKillcount(killer, pd.getKillcount(killer) + 1); //update the killcount of the killer
                    }
                    gui.scAfterStart(); //update the gui again

                } else {

                    if(killer != null) {

                        if(player.getGameMode() != GameMode.SURVIVAL) {
                            event.setCancelled(true);
                            return;
                        }

                        pd.setTempSave(player, killer);  //if the player doesn't die and there is a "killer" (who didn't kill anyone), then save that information
                    }

                }

            } else { //here the stuff if the player got for Example fall damage

                if(((Player) event.getEntity()).getHealth() <= event.getDamage() || event.getCause() == EntityDamageEvent.DamageCause.VOID) {
                    //this is the stuff wich gets executed when the player dies (same stuff as above, but also void)

                    if(pd.tempSaveExists(player)) {
                        //check if anyone attacked the player before. If the player got kicked of a cliff by someone, we want to know that someone
                        pd.setKillcount(pd.getTempSave(player), pd.getKillcount(pd.getTempSave(player)) + 1); //update the killcount for the player
                        Bukkit.getServer().broadcastMessage(generateDeathMessage(pd.getTempSave(player), player)); //generate the deathmessage
                    } else {
                        Bukkit.getServer().broadcastMessage(generateDeathMessage(event.getCause(), player)); //if no one attacked the player before we just let the player die
                    }

                    //fake the player death again
                    if(event.getCause() == EntityDamageEvent.DamageCause.VOID) {
                        player.teleport(fr.getCenter()); //else the player would just die again
                    }
                    fakePlayerDeath(player); //fake the player death again, but only if the player is still alive
                    gui.scAfterStart(); //update the gui and prevent the player from dying fr
                    event.setCancelled(true);
                }
            }
        }
    }

    private String generateDeathMessage(EntityDamageEvent.DamageCause d, Player deadGuy) {

        int b = (int)(Math.random()*(max-min+1)+min); //generating a random death message

        switch (b){ //hehe switch cool
            case 0:

                switch (d) {
                    case FALL:
                        return "§l" + deadGuy.getName() + " §6fell too fast";
                    case FIRE:
                        return "§l" + deadGuy.getName() + " §6was fried";
                    case LAVA:
                        return "§l" + deadGuy.getName() + " §6was deep-fried";
                    case DROWNING:
                        return "§l" + deadGuy.getName() + " §6tried to be Jesus";
                    case STARVATION:
                        return "§l" + deadGuy.getName() + " §6sprinted too much";
                    case BLOCK_EXPLOSION:
                        return "§l" + deadGuy.getName() + " §6tried to be a creeper";
                    case ENTITY_EXPLOSION:
                        return "§l" + deadGuy.getName() + " §6mistook a creeper for a plant";
                    default:
                        return "§l" + deadGuy.getName() + " §6took a break from living";
                }
            case 1:

                switch (d) {
                    case FALL:
                        return "§l" + deadGuy.getName() + " §6didn't expect the ground";
                    case FIRE:
                        return "§l" + deadGuy.getName() + " §6got baked";
                    case LAVA:
                        return "§l" + deadGuy.getName() + " §6got cooked";
                    case DROWNING:
                        return "§l" + deadGuy.getName() + " §6searched for fish";
                    case STARVATION:
                        return "§l" + deadGuy.getName() + " §6starved";
                    case BLOCK_EXPLOSION:
                        return "§l" + deadGuy.getName() + " §6went of with a bang";
                    case ENTITY_EXPLOSION:
                        return "§l" + deadGuy.getName() + " §6got blown away";
                    default:
                        return "§l" + deadGuy.getName() + " §6disconnected from life";
                }
            case 2:

                switch (d) {
                    case FALL:
                        return "§l" + deadGuy.getName() + " §6fell to fast";
                    case FIRE:
                        return "§l" + deadGuy.getName() + " §6was flamed";
                    case LAVA:
                        return "§l" + deadGuy.getName() + " §6found out, that lava is hot";
                    case DROWNING:
                        return "§l" + deadGuy.getName() + " §6drank too much water";
                    case STARVATION:
                        return "§l" + deadGuy.getName() + " §6didn't drink enough water";
                    case BLOCK_EXPLOSION:
                        return "§l" + deadGuy.getName() + " §6got bombed";
                    case ENTITY_EXPLOSION:
                        return "§l" + deadGuy.getName() + " §6exploded";
                    default:
                        return "§l" + deadGuy.getName() + " §6died";
                }
            case 3:

                switch (d) {
                    case FALL:
                        return "§l" + deadGuy.getName() + " §6fell of a cliff";
                    case FIRE:
                        return "§l" + deadGuy.getName() + " §6burned to a crisp";
                    case LAVA:
                        return "§l" + deadGuy.getName() + " §6was boiled by lava";
                    case DROWNING:
                        return "§l" + deadGuy.getName() + " §6dissolved in water";
                    case STARVATION:
                        return "§l" + deadGuy.getName() + " §6didn't eat enough cookies";
                    case BLOCK_EXPLOSION:
                        return "§l" + deadGuy.getName() + " §6was launched into space";
                    case ENTITY_EXPLOSION:
                        return "§l" + deadGuy.getName() + " §6didn't see the dynamite";
                    default:
                        return "§l" + deadGuy.getName() + " §6thought they would respawn";
                }
            case 4:
                switch (d) {
                    case FALL:
                        return "§l" + deadGuy.getName() + " §6didn't fall in water";
                    case FIRE:
                        return "§l" + deadGuy.getName() + " §6didn't extinguish the fire";
                    case LAVA:
                        return "§l" + deadGuy.getName() + " §6was glazed with lava";
                    case DROWNING:
                        return "§l" + deadGuy.getName() + " §6did the reverse squid";
                    case STARVATION:
                        return "§l" + deadGuy.getName() + " §6experienced starvation";
                    case BLOCK_EXPLOSION:
                        return "§l" + deadGuy.getName() + " §6went §iboom";
                    case ENTITY_EXPLOSION:
                        return "§l" + deadGuy.getName() + " §6was exploded";
                    default:
                        return "§l" + deadGuy.getName() + " §6thought they would respawn";
                }
            case 5:
                switch (d) {
                    case FALL:
                        return "§l" + deadGuy.getName() + "'s §6parachute didn't open";
                    case FIRE:
                        return "§l" + deadGuy.getName() + " §6was fired";
                    case LAVA:
                        return "§l" + deadGuy.getName() + " §6was boiled lava";
                    case DROWNING:
                        return "§l" + deadGuy.getName() + " §6tried to breathe water";
                    case STARVATION:
                        return "§l" + deadGuy.getName() + " §6was malnourished";
                    case BLOCK_EXPLOSION:
                        return "§l" + deadGuy.getName() + " §6got TNTed";
                    case ENTITY_EXPLOSION:
                        return "§l" + deadGuy.getName() + " §6fell victim to a creeper";
                    default:
                        return "§l" + deadGuy.getName() + " §6closed life.exe";
                }
        }

        return ""; //this will only happen if the random number is outside the boundries, but that won't happen anyways
    }

    private String generateDeathMessage(Player killer, Player deadGuy) {
        //basically the same stuff as above but without the causes

        int b = (int)(Math.random()*(max-min+1)+min);
        switch (b) {
            case 0:
                return "§l" + deadGuy.getName() + " §6got rekt by §r§l" + killer.getName();
            case 1:
                return "§l" + deadGuy.getName() + " §6got killed by §r§l" + killer.getName();
            case 2:
                return "§l" + deadGuy.getName() + " §6was shredded to pieces by §r§l" + killer.getName();
            case 3:
                return "§l" + deadGuy.getName() + " §6couldn't handle §r§l" + killer.getName();
            case 4:
                return "§l" + deadGuy.getName() + " §6was made disappear by §r§l" + killer.getName();
            case 5:
                return "§l" + deadGuy.getName() + " §6wasn't expecting §r§l" + killer.getName();
            default:
                return "this message should not show up §lever§r lol";
        }

    }


    //when the player would theoretically die we just do our own stuff because we are very cool
    private void fakePlayerDeath(Player player) {

        try  {
            try { //a lot of tries here huh
                for (ItemStack itemStack : player.getInventory().getContents()) {
                    if(itemStack != null) {
                        player.getWorld().dropItemNaturally(player.getLocation(), itemStack); //drop the item if the item exist
                        player.getInventory().removeItem(itemStack); //also remove the item
                    }
                }
                //also drop the armor
                if(player.getInventory().getHelmet() != null) {
                    //helmet...
                    player.getWorld().dropItemNaturally(player.getLocation(), player.getInventory().getHelmet());
                    player.getInventory().setHelmet(null);
                }

                if(player.getInventory().getChestplate() != null) {
                    //chestplate...
                    player.getWorld().dropItemNaturally(player.getLocation(), player.getInventory().getChestplate());
                    player.getInventory().setChestplate(null);
                }

                if(player.getInventory().getLeggings() != null) {
                    //leggings...
                    player.getWorld().dropItemNaturally(player.getLocation(), player.getInventory().getLeggings());
                    player.getInventory().setLeggings(null);
                }

                if(player.getInventory().getBoots() != null) {
                    //boots...
                    player.getWorld().dropItemNaturally(player.getLocation(), player.getInventory().getBoots());
                    player.getInventory().setBoots(null);
                }
            } catch (NullPointerException ignore) {
                //dunno how that would happen
            }

        }
        catch(IllegalArgumentException ignore) {

            //I don't even know what kind of exception this is
            //Bukkit.getConsoleSender().sendMessage("lol exception");
            //e.printStackTrace();
            //so I will ignore the execption :)
        }

        //reset to default values

        player.getWorld().strikeLightningEffect(player.getLocation().subtract(0, 1, 0));
        player.setHealth(20);
        player.setFoodLevel(20); //reset all the stuff
        player.setSaturation(5);
        player.getActivePotionEffects().clear();

        gp.fakeSpectator(player);

        gui.sendTitle(player, "§c§lYou died!");
        int drc = pd.getKillcount(player) + 1; //drc is the currency and not the "Democratic Republic of Congo"
        drc = drc * 10;
        gui.sendSubTitle(player, "§6+ " + drc + " Drc");
        //where coin api
        if(FileReader.gameStarted) {
            gui.scAfterStart(); //update gui
        }

        int players = 0;
        for(Player pl : Bukkit.getServer().getOnlinePlayers()) {
            if(pl.getGameMode() != GameMode.ADVENTURE) {
                players++; //count the alive players
            }
        }
        if(players == 1) {
            for(Player pl : Bukkit.getServer().getOnlinePlayers()) {
                if(pl.getGameMode() != GameMode.ADVENTURE) { //end the game if there is only one alive person left
                    gp.gameEnd(pl);
                }
            }
        }
    }
}
