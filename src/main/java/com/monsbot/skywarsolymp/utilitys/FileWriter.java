package com.monsbot.skywarsolymp.utilitys;

import net.minecraft.server.v1_8_R3.DedicatedServer;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

public class FileWriter {

    static File file = new File(Bukkit.getServer().getWorldContainer(), "mapConfig.yml");

    public static void savePropertiesFile() {
        ((DedicatedServer) MinecraftServer.getServer()).propertyManager.savePropertiesFile();
    }

    public static void setServerProperty(ServerProperty property, Object value) {
        ((DedicatedServer) MinecraftServer.getServer()).propertyManager.setProperty(property.getPropertyName(), value);
    }
    public enum ServerProperty {

        SPAWN_PROTECTION("spawn-protection"),
        PVP("pvp"),
        NETHER("allow-nether"),
        ANNOUNCE_PLAYER_ACHIEVEMENTS("announce-player-achievements")/*,
        SERVER_NAME("server-name"),
        FORCE_GAMEMODE("force-gamemode"),
        DEFAULT_GAMEMODE("gamemode"),
        QUERY("enable-query"),
        PLAYER_IDLE_TIMEOUT("player-idle-timeout"),
        DIFFICULTY("difficulty"),
        SPAWN_MONSTERS("spawn-monsters"),
        OP_PERMISSION_LEVEL("op-permission-level"),
        RESOURCE_PACK_HASH("resource-pack-hash"),
        RESOURCE_PACK("resource-pack"),
        SNOOPER("snooper-enabled"),
        LEVEL_NAME("level-name"),
        LEVEL_TYPE("level-type"),
        LEVEL_SEED("level-seed"),
        HARDCORE("hardcore"),
        COMMAND_BLOCKS("enable-command-blocks"),
        MAX_PLAYERS("max-players"),
        PACKET_COMPRESSION_LIMIT("network-compression-threshold"),
        MAX_WORLD_SIZE("max-world-size"),
        IP("server-ip"),
        PORT("server-port"),
        DEBUG_MODE("debug"),
        SPAWN_NPCS("spawn-npcs"),
        SPAWN_ANIMALS("spawn-animals"),
        FLIGHT("allow-flight"),
        VIEW_DISTANCE("view-distance"),
        WHITE_LIST("white-list"),
        GENERATE_STRUCTURES("generate-structures"),
        MAX_BUILD_HEIGHT("max-build-height"),
        MOTD("motd"),
        REMOTE_CONTROL("enable-rcon")*/;


        private String propertyName;

        ServerProperty(String propertyName) {
            this.propertyName = propertyName;
        }

        public String getPropertyName() {
            return propertyName;
        }

    }

    public static boolean deleteWorld(String worldName) {
        try {
            File destination = Bukkit.getWorld(worldName).getWorldFolder();
            Bukkit.unloadWorld(worldName, false);
            FileUtils.cleanDirectory(destination);
            return destination.delete();
        } catch (NullPointerException | IOException e) {
            return false;
        }
    }

    private static void copyFileStructure(File source, File target) {
        try {
            ArrayList<String> ignore = new ArrayList<>(Arrays.asList("uid.dat", "session.lock"));

            if(!ignore.contains(source.getName())) {
                if(source.isDirectory()) {
                    if(!target.exists())
                        if (!target.mkdirs())
                            throw new IOException("Couldn't create world directory!");

                    String[] files = source.list();
                    for (String file : files) {
                        File srcFile = new File(source, file);
                        File destFile = new File(target, file);
                        copyFileStructure(srcFile, destFile);
                    }
                } else {
                    InputStream in = new FileInputStream(source);
                    OutputStream out = new FileOutputStream(target);
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = in.read(buffer)) > 0)
                        out.write(buffer, 0, length);
                    in.close();
                    out.close();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void copyWorld(World originalWorld, String newWorldName) {
        copyFileStructure(originalWorld.getWorldFolder(), new File(Bukkit.getWorldContainer(), newWorldName));
        new WorldCreator(newWorldName).createWorld();
    }



    public static String clearConfig() {
        return writeConfigFile("", false, "cleared");
    }

    public static String addToConfig(String variable, String value) {
        String text = variable + ": " + value;
        return writeConfigFile(text, true, "added '" + text + "' to ");
    }

    private static String writeConfigFile(String text, boolean append, String action) {
        String result = "§cRandom Error";
        text = "\n" + text;
        try {
            if (file.exists()) {
                FileOutputStream fos = new FileOutputStream(file, append);
                fos.write(text.getBytes());
                fos.close();
                result = action + " mapConfig.yml";
            }
        } catch (FileNotFoundException e) {
            result = "§cfile doesn't exist yet";

        } catch (IOException e) {
            result = "§cIOException";
        }
        return result;
    }
}
