package com.monsbot.skywarsolymp;

import com.monsbot.skywarsolymp.commands.DebugCommand;
import com.monsbot.skywarsolymp.listeners.DeathListener;
import com.monsbot.skywarsolymp.listeners.SpectatorListeners;
import com.monsbot.skywarsolymp.listeners.JoinListener;
import com.monsbot.skywarsolymp.utilitys.FileReader;
import com.monsbot.skywarsolymp.utilitys.Messages;
import com.monsbot.skywarsolymp.utilitys.FileWriter;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

//the main class (for Spigot)
public final class SkywarsOlymp extends JavaPlugin {

    //public boolean gameStarted = false;  //moved to FileReader
    public static SkywarsOlymp plugin;
    private FileReader fr = new FileReader();
    public static boolean worldReady = false;

    @Override
    public void onEnable() {
        // Plugin startup logic
        setInstance(this);
        this.register();
        this.prepareWorld();
        Bukkit.getConsoleSender().sendMessage("§aplugin loaded"); //green bc green is cool
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        for(Player pl: Bukkit.getServer().getOnlinePlayers()) {
            pl.kickPlayer("§cServer restarting\n§r§9§n§ohttps://tinyurl.com/SkywarsKicked"); //kick other players (plugin messages don't work if you connect directly to the server)
        }
        Bukkit.unloadWorld(Bukkit.getWorld(fr.getLevelName()), false);
        try { FileWriter.deleteWorld(fr.getLevelName()); } catch (Exception e) {
            e.printStackTrace();
        }
        Bukkit.getConsoleSender().sendMessage("§1plugin unloaded"); //not red, because "red is the universal sign for 'You fucked something up'" - someone
    }




    private static void setInstance(SkywarsOlymp instance) { //here the instance is being set
        SkywarsOlymp.plugin = instance;
    }

    public static SkywarsOlymp getInstance() { //and this is where you get the instance
        return plugin;
    }

    public void register() { //registering the commands, listeners, etc.
        //register the events
        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new JoinListener(), this);
        pluginManager.registerEvents(new DeathListener(), this);
        pluginManager.registerEvents(new SpectatorListeners(), this);


        //register the commands
        Bukkit.getPluginCommand("debugSw").setExecutor(new DebugCommand());

        //register Plugin Messanger Channel
        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        this.getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", new Messages());

        FileWriter.setServerProperty(FileWriter.ServerProperty.SPAWN_PROTECTION, 0);
        FileWriter.setServerProperty(FileWriter.ServerProperty.PVP, true);
        FileWriter.setServerProperty(FileWriter.ServerProperty.ANNOUNCE_PLAYER_ACHIEVEMENTS, true);
        FileWriter.setServerProperty(FileWriter.ServerProperty.NETHER, true);
        FileWriter.savePropertiesFile(); //save file
    }

    private void prepareWorld() {
        worldReady = false;
        try { FileWriter.deleteWorld(fr.getLevelName()); }catch(Exception ignore){}
        //maybe the server didn't close properly so the plugin couldn't delete the old world idk just in case
        FileWriter.copyWorld(Bukkit.getWorlds().get(0), fr.getLevelName());

        World world = Bukkit.getWorld(fr.getLevelName());
        world.setGameRuleValue("doDayLightCycle", "false"); //why don't use something like Gamerule.DODAYLIGHTCYCLE here
        world.setGameRuleValue("doMobSpawning", "false");
        world.setGameRuleValue("doWeatherCycle", "false");


        worldReady = true;
    }
}
