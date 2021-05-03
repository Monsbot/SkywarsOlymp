package com.monsbot.skywarsolymp.utilitys;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Scanner;

public class FileReader {
    public static boolean gameStarted; //has nothing to do with this stuff, I just don't know where to put it

    String line;
    File file = new File(Bukkit.getServer().getWorldContainer(), "mapConfig.yml");

    public int getMaxPlayers() {
        String[] tempArray;
        int max = 0;

        try {
            Scanner scanner = new Scanner(file);

            while (scanner.hasNextLine()) { //loop for the scanner
                line = scanner.nextLine();
                if(line.startsWith("max")) {
                    tempArray = line.split(": ");
                    max = Integer.parseInt(tempArray[1]);
                }
            }
            scanner.close();

            if(max < 1) {
                Bukkit.getConsoleSender().sendMessage("§cDid not find info for max players, returning 1");
                max = 1;
            }

        } catch (FileNotFoundException e) {
            Bukkit.getConsoleSender().sendMessage("§cDid not find file '" + getLevelName() + ".yml'");
        }

        return max;
    }

    public Location getSpawnLocation(int id) {
        Location loc = null;

        try {
            String[] tempArray;
            Scanner scanner = new Scanner(file);

            while (scanner.hasNextLine()) { //loop for scanner
                line = scanner.nextLine();
                if(line.startsWith("loc" + id)) {
                    tempArray = line.split(" ");
                    String tempStr = tempArray[1];
                    int x = Integer.parseInt(tempStr);
                    tempStr = tempArray[2];
                    int y = Integer.parseInt(tempStr);
                    int z = Integer.parseInt(tempArray[3]);

                    loc = new Location(Bukkit.getWorld(getLevelName()), x + 0.5, y, z + 0.5);

                }
            }

            scanner.close(); //close the scanner
            if(loc == null) {
                Bukkit.getConsoleSender().sendMessage("did not find location for 'loc " + id + "', returning null");
            }
            return loc; //return the location asked for

        } catch (FileNotFoundException e) {
            Bukkit.getConsoleSender().sendMessage("§cDid not find file '" + World.class.getSimpleName() + ".yml'");
            return null;
        }
    }

    public Location getCenter() {
        int cX =0, cZ=0;
        for (int i = 1; i <= getMaxPlayers(); i++) {
            cX = cX + getSpawnLocation(i).getBlockX();
            cZ = cZ + getSpawnLocation(i).getBlockZ();
        }

        if(getMaxPlayers() <= 1) {
            Bukkit.getConsoleSender().sendMessage("Something went wrong with getCenter() (check if the file is there)");
            return new Location(Bukkit.getWorld(getLevelName()),  0.5 , getSpawnLocation(1).getY(),0.5);
        }
        cX = cX / getMaxPlayers();
        cZ = cZ / getMaxPlayers();

        return  new Location(Bukkit.getWorld(getLevelName()),  cX + 0.5 , getSpawnLocation(1).getY(), cZ  + 0.5);
    }

    public int getDistanceToCenter(Location location) {
        int x = getCenter().getBlockX();
        int z = getCenter().getBlockZ();
        int x_ = location.getBlockX();
        int z_ = location.getBlockZ();
        x = makePositive(makePositive(x) - makePositive(x_));
        z = makePositive(makePositive(z) - makePositive(z_));
        return x + z;
    }

    private int makePositive(int i) {
        if(i < 0) return -i;
        return i;
    }

    public String getLevelName() {

        String[] tempArray;
        String mapName = null;
        try {
            Scanner scanner = new Scanner(file);

            while (scanner.hasNextLine()) { //loop for the scanner
                line = scanner.nextLine();
                if(line.startsWith("mapName")) {
                    tempArray = line.split(": ");
                    String[] tempArray2 = Arrays.copyOfRange(tempArray, 1, tempArray.length);
                    StringBuilder stringBuilder = new StringBuilder();
                    for(String tempString : tempArray2) {
                        stringBuilder.append(tempString).append(" ");
                    } stringBuilder.setLength(stringBuilder.length() - 1);
                    mapName = stringBuilder.toString();
                }
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            return "404";
        }
        if(mapName == null) {
            return "404";
        }
        return mapName;
    }

    public String getHubName() {

        String[] tempArray;
        String hubName = null;
        try {
            Scanner scanner = new Scanner(file);

            while (scanner.hasNextLine()) { //loop for the scanner
                line = scanner.nextLine();
                if(line.startsWith("hub")) {
                    tempArray = line.split(": ");
                    String[] tempArray2 = Arrays.copyOfRange(tempArray, 1, tempArray.length);
                    StringBuilder stringBuilder = new StringBuilder();
                    for(String tempString : tempArray2) {
                        stringBuilder.append(tempString).append(" ");
                    } stringBuilder.setLength(stringBuilder.length() - 1);
                    hubName = stringBuilder.toString();
                }
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            return "Lobby-1";
        }
        if(hubName == null) {
            return "Lobby-1";
        }
        return hubName;
    }
}