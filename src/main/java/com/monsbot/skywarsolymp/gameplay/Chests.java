package com.monsbot.skywarsolymp.gameplay;

import com.monsbot.skywarsolymp.SkywarsOlymp;
import com.monsbot.skywarsolymp.utilitys.FileReader;
import org.bukkit.*;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Chests {

    FileReader fr = new FileReader();
    Gui gui = new Gui();

    public void fillChests(boolean sendMessage) {

        try {
            World world = Bukkit.getServer().getWorld(fr.getLevelName());
            for (Chunk c : world.getLoadedChunks()) {
                for (BlockState blockState : c.getTileEntities()) {

                    if (blockState instanceof Chest) {
                        Chest chest = (Chest) blockState;
                        fillChest(chest);
                    }
                }
            }
            if(sendMessage) {
                Bukkit.broadcastMessage("§7§oChests have been refilled");
            }
        } catch (NullPointerException ignore) {}
    }
    private void fillChest(Chest chest) {

        Inventory inventory = chest.getBlockInventory();

        //if(!isEmpty(inventory)) return;

        Random random = new Random();
        int n = random.nextInt(14);
        List<ItemStack> itemList = new ArrayList<>();

        if(isInCenter(chest.getBlock().getLocation())) {
            itemList.add(new ItemStack(Material.GOLDEN_APPLE));
            itemList.add(new ItemStack(Material.ARROW, 16));

            itemList.add(new ItemStack(Material.DIAMOND_HELMET));
            itemList.add(new ItemStack(Material.DIAMOND_CHESTPLATE));
            itemList.add(new ItemStack(Material.DIAMOND_LEGGINGS));
            itemList.add(new ItemStack(Material.DIAMOND_BOOTS));

            itemList.add(new ItemStack(Material.BOW));
            itemList.add(new ItemStack(Material.SNOW_BALL, 16));
            itemList.add(new ItemStack(Material.COOKED_BEEF, 32));
            itemList.add(new ItemStack(Material.ENDER_PEARL));
            itemList.add(new ItemStack(Material.WOOD, 16));
            itemList.add(new ItemStack(Material.WEB, 3));
        } else {
            itemList.add(new ItemStack(Material.APPLE));
            itemList.add(new ItemStack(Material.ARROW, 8));
            itemList.add(new ItemStack(Material.COOKED_BEEF, 16));
            itemList.add(new ItemStack(Material.SNOW_BALL, 16));
            itemList.add(new ItemStack(Material.STICK));
            itemList.add(new ItemStack(Material.STICK, 2));
            itemList.add(new ItemStack(Material.WATER_BUCKET));
            itemList.add(new ItemStack(Material.LAVA_BUCKET));
            itemList.add(new ItemStack(Material.WOOD, 8));
            itemList.add(new ItemStack(Material.WOOD, 16));
            itemList.add(new ItemStack(Material.WOOD, 32));
            itemList.add(new ItemStack(Material.SPONGE));
            itemList.add(new ItemStack(Material.STRING));
            itemList.add(new ItemStack(Material.IRON_INGOT));

            itemList.add(new ItemStack(Material.IRON_HELMET));
            itemList.add(new ItemStack(Material.IRON_CHESTPLATE));
            itemList.add(new ItemStack(Material.IRON_LEGGINGS));
            itemList.add(new ItemStack(Material.IRON_BOOTS));

            itemList.add(new ItemStack(Material.LEATHER_HELMET));
            itemList.add(new ItemStack(Material.LEATHER_CHESTPLATE));
            itemList.add(new ItemStack(Material.LEATHER_LEGGINGS));
            itemList.add(new ItemStack(Material.LEATHER_BOOTS));
        }

        while (n != 0) {
            int n2 = new Random().nextInt(26);
            int n3 = new Random().nextInt(itemList.size());

            inventory.setItem(n2, itemList.get(n3));
            n--;
        }
        inventory.setItem(0, new ItemStack(Material.WOOD, fr.getDistanceToCenter(chest.getLocation())));

        if(isEmpty(inventory)) {
            fillChest(chest);
        }
    }

    private int taskIDChests;

    private void stopChestTimer() {
        Bukkit.getScheduler().cancelTask(taskIDChests);
    }

    public void fillChests(int time, int count, boolean sendMessage) {
        if(count < 1) {
            return;
        } else if(count <= 2) {
            fillChests(sendMessage);
            return;
        }
        count--;

        int totalTime = time * count;

        BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
        taskIDChests = scheduler.scheduleSyncRepeatingTask(SkywarsOlymp.plugin, new Runnable() {

            int timeLeft = totalTime;
            int individualTimeLeft;

            @Override
            public void run() {

                individualTimeLeft = timeLeft / time;
                individualTimeLeft = individualTimeLeft * time;

                if(timeLeft == 0) {
                    gui.scAfterStart("...");
                    stopChestTimer();
                } else {
                    gui.scAfterStart("" + (timeLeft - individualTimeLeft));
                }

                //Bukkit.broadcastMessage("Is this right?: " + "§6§l" + individualTimeLeft);

                if(timeLeft % time == 0) {
                    fillChests(sendMessage);
                }

                timeLeft--;
            }
        }, 0L, 20);
    }

    public boolean isEmpty(Inventory inv) {
        for(ItemStack item : inv.getContents()) {
            if(item != null) {
                return false;
            }
        }
        return true;
    }

    public void clearChests() { //clear the chests
        World world = Bukkit.getServer().getWorld("lol"); //cool lol
        for (Player pl: Bukkit.getOnlinePlayers()) {
            world = pl.getWorld();
        }
        for (Chunk c : world.getLoadedChunks()) {
            for (BlockState blockState : c.getTileEntities()) {

                if (blockState instanceof Chest) {
                    Chest chest = (Chest) blockState;
                    Inventory inventory = chest.getBlockInventory();
                    inventory.clear();
                }
            }
        }
    }

    public boolean isInCenter(Location location) {
        Integer[] allX = {};
        Integer[] allZ = {};

        for( int i = 0; i < fr.getMaxPlayers(); i++) {
            ArrayList<Integer> arrayListX = new ArrayList<>(Arrays.asList(allX));
            arrayListX.add( fr.getSpawnLocation(i + 1).getBlockX() );
            allX = arrayListX.toArray(allX);

            ArrayList<Integer> arrayListZ = new ArrayList<>(Arrays.asList(allZ));
            arrayListZ.add( fr.getSpawnLocation(i + 1).getBlockZ() );
            allZ = arrayListZ.toArray(allZ);
        }



        boolean isInPx = location.getBlockX() <= getLargest(allX)/3;
        boolean isInNx = location.getBlockX() >= getSmallest(allX)/3;
        boolean isInPz = location.getBlockZ() <= getLargest(allZ)/3;
        boolean isInNz = location.getBlockZ() >= getSmallest(allZ)/3;

        boolean isInX = isInNx && isInPx;
        boolean isInZ = isInNz && isInPz;

        return isInX && isInZ;
    }
    private int getLargest(Integer[] a){
        Arrays.sort(a);
        return a[a.length-1];
    } //array stuff
    public int getSmallest(Integer[] a){
        Arrays.sort(a);
        return a[0];
    }

}
