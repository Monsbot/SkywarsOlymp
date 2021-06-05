package com.monsbot.skywarsolymp.utilitys;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class Playerdata {
    static HashMap<String, Integer> killcount = new HashMap<>();
    static HashMap<String, String> tempSave = new HashMap<>();

    public void setKillcount(Player key, int value) {
        if(killcount.containsKey(key.getUniqueId().toString())) {
            killcount.replace(key.getUniqueId().toString(), value);
        } else {
            killcount.put(key.getUniqueId().toString(), value);
        }
    }

    public int getKillcount(Player key) {
        if(!killcount.containsKey(key.getUniqueId().toString())) {
            setKillcount(key, 0);
        }
        return killcount.get(key.getUniqueId().toString());
    }

    public void removeKillcount(Player key) {
        if(!killcount.containsKey(key.getUniqueId().toString())) {
            killcount.remove(key.getUniqueId().toString());
        }
    }

    public void setTempSave(Player player, Player killer) {
        if(!tempSave.containsKey(player.getUniqueId().toString())) {
            tempSave.put(player.getUniqueId().toString(), killer.getUniqueId().toString());
        } else {
            tempSave.replace(player.getUniqueId().toString(), killer.getUniqueId().toString());
        }

        new Thread() {
            public void run() {
                try {
                    sleep(15000);
                    tempSave.remove(player.getUniqueId().toString());
                } catch (InterruptedException ignored) {}
            }
        }.start();

    }

    public boolean tempSaveExists(Player player) {
        return tempSave.containsKey(player.getUniqueId().toString());
    }

    public Player getTempSave(Player player) {
        try {
            return Bukkit.getPlayer(UUID.fromString(tempSave.get(player.getUniqueId().toString())));
            //return a player from the uuid you get when entering the uuid of the other player
        } catch (NullPointerException e) {
            return player;
        }
    }

}
