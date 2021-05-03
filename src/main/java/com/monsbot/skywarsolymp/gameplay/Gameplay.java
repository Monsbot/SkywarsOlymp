package com.monsbot.skywarsolymp.gameplay;

import com.monsbot.skywarsolymp.SkywarsOlymp;
import com.monsbot.skywarsolymp.utilitys.FileReader;
import com.monsbot.skywarsolymp.utilitys.Messages;
import com.monsbot.skywarsolymp.utilitys.Playerdata;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

//the gameplay stuff
public class Gameplay {

    FileReader fr = new FileReader();
    Playerdata pd = new Playerdata();
    Messages msg = new Messages();
    Chests ch = new Chests();
    Gui gui = new Gui();

    /*public void sjtartCountdown(int countDown) {

        int i = 1; //no idea what I did here
        int n = countDown;
        while(n >= 0) {

            int j = n;
            Bukkit.getScheduler().runTaskLater(SkywarsOlymp.getPlugin(SkywarsOlymp.class), () -> {

                int shownCountDown = 10;

                int playercount = 0;
                for(Player ignored: Bukkit.getOnlinePlayers()) {
                    playercount++;
                }
                if(fr.getMaxPlayers() == playercount && !(j <= 10)) {
                    startTimer(10);
                } else {
                    if(j <= 10) {
                        if(j > shownCountDown - shownCountDown / 3 ) {
                            Bukkit.broadcastMessage("§c" + j);
                        } else if (j < shownCountDown - shownCountDown / 3 * 2 && j > 0) {
                            Bukkit.broadcastMessage("§a" + j);
                        } else if (j == 0) {
                            onBegin();
                        } else {
                            Bukkit.broadcastMessage("§6" + j);
                        }
                    }
                    gui.scBeforeStart(j + "s");
                }
            }, 20 * i);
            i++;
            n--;
        }
    }*/

    private int time;
    private int taskID;

    public void startCountdown(int amount) {
        time = amount;
        startTimer();
    }

    private void startTimer() {

        BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
        taskID = scheduler.scheduleSyncRepeatingTask(SkywarsOlymp.plugin, () -> {

            for(Player pl: Bukkit.getOnlinePlayers()) {
                givePlayerItems(pl, time);
                gui.scBeforeStart(time + "s", pl);
            }
            int shownCountdown = 10;

            if(time <= shownCountdown) {
                if(time > shownCountdown - shownCountdown / 3 ) {
                    Bukkit.broadcastMessage("§c" + time);
                } else if (time < shownCountdown - shownCountdown / 3 * 2 && time > 0) {
                    Bukkit.broadcastMessage("§a" + time);
                } else if (time == 0) {
                    gui.scBeforeStart("...");
                    onBegin();
                    stopTimer();
                } else {
                    Bukkit.broadcastMessage("§6" + time);
                }
            } else if(Bukkit.getOnlinePlayers().size() == fr.getMaxPlayers()) {
                time = 11;
            }
            time--;

        }, 0L, 20L);
    }

    public void stopTimer() {
        Bukkit.getScheduler().cancelTask(taskID);
    }

    public void onBegin() {
        Bukkit.broadcastMessage("§a§lStart");
        FileReader.gameStarted = true;
        gui.scAfterStart();
        ch.fillChests(30, 3, true);

        for (Player pl : Bukkit.getServer().getOnlinePlayers()) {
            PlayerInventory inventory = pl.getInventory();
            String selectedKit = "null";
            try {
                selectedKit = inventory.getItem(4).getItemMeta().getLore().get(0).substring(22);
            } catch (NullPointerException | StringIndexOutOfBoundsException ignored) {
                inventory.setItem(4, new ItemStack(Material.BEDROCK));
            }
            inventory.clear();
            pl.closeInventory();

            Bukkit.getConsoleSender().sendMessage("§a" + pl.getDisplayName() + " §rkit: §6" + selectedKit);

            switch (selectedKit) {
                case "Slime":
                    ItemStack potion = new ItemStack(Material.POTION, 1, (short) PotionType.JUMP.ordinal());
                    PotionMeta potionMeta = (PotionMeta) potion.getItemMeta();
                    potionMeta.addCustomEffect(PotionEffectType.JUMP.createEffect(30 * 20, 2), false);
                    potionMeta.addCustomEffect(PotionEffectType.SLOW.createEffect(60 * 20, 1), false);
                    potionMeta.setDisplayName("§2Slime potion");
                    potion.setItemMeta(potionMeta);

                    inventory.setItem(0, potion);
                    inventory.setItem(1, potion);
                    break;

                case "Tank":
                    ItemStack armor = new ItemStack(Material.IRON_CHESTPLATE);
                    ItemMeta itemMeta = armor.getItemMeta();
                    itemMeta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 5, true);
                    itemMeta.setDisplayName("§rTank armor");

                    armor.setItemMeta(itemMeta);
                    inventory.setChestplate(armor);
                    armor = new ItemStack(Material.IRON_BOOTS);
                    armor.setItemMeta(itemMeta);
                    inventory.setBoots(armor);
                    break;

                case "Snow ball":
                    ItemStack snowBalls = new ItemStack(Material.SNOW_BALL, 16);
                    inventory.setItem(0, snowBalls);
                    break;

                case "Flint and Steel":
                    ItemStack flintAndSteel = new ItemStack(Material.FLINT_AND_STEEL);
                    flintAndSteel.setDurability((short) 16);
                    inventory.setItem(0, flintAndSteel);
                    break;

                default:
                    ItemStack wool = new ItemStack(Material.WOOL, 64);
                    inventory.setItem(0, wool);
                    break;
            }

            pl.setGameMode(GameMode.SURVIVAL);
        }

        for (int i = 1; i <= fr.getMaxPlayers(); i++) {
            makeBox(Material.AIR, fr.getSpawnLocation(i));
        }
    }

    public void gameEnd(Player player) {
        gui.sendTitle(player, "§a§lVictory!");
        int drc = pd.getKillcount(player) + 1;
        drc = drc * 10;
        drc = drc + 20;
        gui.sendSubTitle(player, "§6+ " + drc + " Drc"); //do the drc stuff
        fakeSpectator(player);

        Bukkit.getScheduler().runTaskLater(SkywarsOlymp.getPlugin(SkywarsOlymp.class), new Runnable() {

            @Override
            public void run() {
                Bukkit.broadcastMessage("§l" + player.getName() + " §awon" + "\n§7§oServer reloading in ca 30 seconds");
            }

        }, 1);
        Bukkit.getScheduler().runTaskLater(SkywarsOlymp.getPlugin(SkywarsOlymp.class), new Runnable() {

            @Override
            public void run() {
                Bukkit.broadcastMessage("§7§oServer reloading now..."); //reloading the server
                for(Player pl: Bukkit.getServer().getOnlinePlayers()) {
                    msg.connect(pl, fr.getHubName()); //teleport player to hub
                }
                Bukkit.getServer().reload();

            }

        }, 420); //nice
    }

    public void makeBox(Material material, Location location) {
        location.subtract(0, 1, 0);
       for(int i = 1; i <= 5; i++) {
           location.getBlock().setType(material);
           location.subtract(1, 0,0).getBlock().setType(material);
           location.add(1, 0, 0);
           location.subtract(0, 0,1).getBlock().setType(material);
           location.add(0, 0, 1);
           location.add(1, 0,0).getBlock().setType(material);
           location.subtract(1, 0, 0);
           location.add(0, 0,1).getBlock().setType(material); //ye I dunno, it works
           location.subtract(0, 0, 1);
           location.add(0, 1, 0);
       }

       location.subtract(0, 2, 0).getBlock().setType(Material.AIR);
       location.subtract(0, 1, 0).getBlock().setType(Material.AIR); //fill in the air
       location.subtract(0, 1, 0).getBlock().setType(Material.AIR);
    }

    public void fakeSpectator(Player player) {
        Inventory inventory = player.getInventory();

        inventory.clear();
        player.setGameMode(GameMode.ADVENTURE);
        player.setAllowFlight(true);
        player.setFlying(true);

        for(Player pl: Bukkit.getOnlinePlayers()) {
            if(pl != player) {
                pl.hidePlayer(player);
            }
        }

        inventory.setItem(0, gui.getItem("tp"));
        inventory.setItem(8, gui.getItem("hub"));
    }

    public void givePlayerItems(Player player, int countdown) {

        Inventory inventory = player.getInventory();

        ItemStack itemStack = new ItemStack(Material.WATCH);
        ItemMeta itemMeta = itemStack.getItemMeta();
        List<String> lore;

        if(countdown > -1) {
            itemMeta.setDisplayName("§5§l" + countdown);
            lore = Collections.singletonList("§r§8Game starting in: §6" + countdown);
            itemMeta.setLore(lore);
            itemStack.setItemMeta(itemMeta);
            inventory.setItem(0, itemStack);

        } else {
            itemMeta.setDisplayName("§r§5§lWaiting for more players");
            lore = Collections.singletonList("§r§8Waiting for more players...");
            itemMeta.setLore(lore);
            itemStack.setItemMeta(itemMeta);
            inventory.setItem(0, itemStack);

            inventory.setItem(4, gui.getItem("kit"));
            inventory.setItem(8, gui.getItem("hub"));
        }
    }

}
