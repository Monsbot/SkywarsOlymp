package com.monsbot.skywarsolymp.listeners;

import com.monsbot.skywarsolymp.SkywarsOlymp;
import com.monsbot.skywarsolymp.utilitys.FileReader;
import com.monsbot.skywarsolymp.utilitys.Playerdata;
import com.monsbot.skywarsolymp.gameplay.Gameplay;
import com.monsbot.skywarsolymp.gameplay.Gui;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;

//here is the player join/leave stuff
public class JoinListener implements Listener {

    FileReader fr = new FileReader();
    Gameplay gp = new Gameplay();
    Playerdata pd = new Playerdata(); //creating all the instances I need
    Gui gui = new Gui();
    int PlayersOnline; //creating an integer for the Players (pLaYStAtiON - fOr ThE PlAyErS)

    @EventHandler
    public void playerJoined(PlayerJoinEvent event) {
        Player player = event.getPlayer(); //making the Player who joins a variable
        Location location;

        if(!SkywarsOlymp.worldReady) {
            player.kickPlayer("Couldn't join world, please try again");
            return;
        }

        player.getInventory().clear();
        player.getInventory().setHelmet(null);
        player.getInventory().setChestplate(null); //if the player had stuff from the previous game it will be cleared
        player.getInventory().setLeggings(null);
        player.getInventory().setBoots(null);
        pd.setKillcount(player, 0); //same with the killcount

        player.setHealth(20);
        player.setFoodLevel(20);
        player.setSaturation(5);
        player.setExp(0);
        player.setLevel(0);
        //player.getActivePotionEffects().clear(); //those values are also being reset
        for(PotionEffect pe: player.getActivePotionEffects()) {
            player.removePotionEffect(pe.getType());
        }

        PlayersOnline = 0; //the players are 0 so we can add 1 for every online Player

        for (Player ignored : Bukkit.getServer().getOnlinePlayers()) {
            PlayersOnline++; //this loops through all the players and will add one per player to the Playercount
        }

        if(PlayersOnline <= fr.getMaxPlayers() && !FileReader.gameStarted) { //check if people can still join

            gui.scBeforeStart(); //show/update the "before start" scoreboard

            player.setGameMode(GameMode.ADVENTURE); //set the gamemode to adventure so the player can't break blocks
            try {
                location = fr.getSpawnLocation(PlayersOnline);
                // If the Playercount is for example 5, that means the player is the 5th player to join and should get the 5th spawn location
                gp.makeBox(Material.GLASS, fr.getSpawnLocation(PlayersOnline)); //make the box around the player
                gp.givePlayerItems(player, -1);

            } catch (NullPointerException e) {
                //if the spawn location can't be found something went wrong with the yml file
                gp.fakeSpectator(player); //set the gamemode to spectator, if the player get's set to adventure they don't die or stuff
                player.sendMessage("§l§4Something went wrong! Please contact staff about this."); //telling the player kinda what's wrong
                Bukkit.getServer().broadcastMessage("§c§l" + player.getName() + "§c could not be spawned, continuing without them"); //telling the other players
                Bukkit.getConsoleSender().sendMessage("§cCould not teleport " + player.getName() + " to location! Please check if the yml file works"); //telling the console something went wrong

                /* how the file in "<ServerFolder>\mapConfig.yml" could look like
                * - mapConfig.yml ---------
                *   max: 4
                *   loc1: 0 20 30
                *   loc2: 0 20 -30
                *   loc3: 30 20 0
                *   loc4: -30 20 0
                *   mapName: Trees
                *   hub: Lobby-1
                * -----------------------
                * "max" is how many players need to join for the game. For each player one spawn location. If this value isn't there the whole Plugin will probably collapse
                * "loc<Player>" needs to be added. If a player doesn't have a spawn location, the plugin will inform them to contact staff and the game continues without them
                * "mapName" is the name of the map. If it isn't there '404' will be used as the map name
                * "hub" is the name of the hub. If this isn't there 'Lobby-1' will just be used (last time I checked that was the hub Name lol)
                * the order of those parameters doesn't matter */

                location = player.getLocation();

            }
            if(PlayersOnline == fr.getMaxPlayers() / 4 * 3 && !FileReader.gameStarted) { //if the game has not started and the playercount is right
                event.setJoinMessage("§e" + player.getName() + " joined (" + PlayersOnline + "/" + fr.getMaxPlayers() + ")");
                gp.startCountdown(60); //do the begin stuff
            } else if (PlayersOnline == fr.getMaxPlayers() && !FileReader.gameStarted) {
                event.setJoinMessage("§e" + player.getName() + " joined (" + PlayersOnline + "/" + fr.getMaxPlayers() + ")\n§lGame Starting"); //if I make Bukkit.broadcastMessage(); it will show the message BEFORE the joinmessage, and that kinda looks bad
            } else {
                event.setJoinMessage("§e" + player.getName() + " joined (" + PlayersOnline + "/" + fr.getMaxPlayers() + ")"); //if not just do the joinmessage
            }

        } else {
            gui.scAfterStart(); //do the stuff if the game has already started
            gp.fakeSpectator(player); //set the gamemode to spectator
            location = fr.getCenter(); //set spawn location to center
            event.setJoinMessage(""); //idk why, but it works
        }
        player.teleport(new Location(Bukkit.getWorld(fr.getLevelName()), location.getX(), location.getY(), location.getZ())); //teleport the player to the spawn location.

    }

    @EventHandler
    public void playerLeaveEvent(PlayerQuitEvent event) {
        pd.removeKillcount(event.getPlayer()); //when they leave the Killcount resets

        PlayersOnline = 0;
        for (Player ignored : Bukkit.getServer().getOnlinePlayers()) {
            PlayersOnline++; //the same stuff as above, counting the players
        }
        //do the gui stuff
        if(PlayersOnline <= 1 && !fr.gameStarted) {
            gp.stopTimer();
        }

        if(fr.gameStarted) {
            gui.scAfterStart();
        } else {
            gui.scBeforeStart();
        }
    }

}
