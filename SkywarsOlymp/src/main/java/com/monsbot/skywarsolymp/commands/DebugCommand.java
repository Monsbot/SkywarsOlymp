package com.monsbot.skywarsolymp.commands;

import com.monsbot.skywarsolymp.gameplay.Gameplay;
import com.monsbot.skywarsolymp.gameplay.Gui;
import com.monsbot.skywarsolymp.utilitys.FileWriter;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

public class DebugCommand implements CommandExecutor {

    Gameplay gp = new Gameplay();
    Gui gui = new Gui();

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {

        if(!commandSender.isOp()) return false;

        if(args.length == 0) {
            commandSender.sendMessage("§c/" + label + " <world/file/start/stoptasks/gui>");
            return false;
        }
        String[] args2 = Arrays.copyOfRange(args, 1, args.length);

        switch (args[0].toLowerCase()) {
            case "world":
                world(args2, commandSender);
                break;

            case "file":
                file(args2, commandSender);
                break;

            case "start":
                start(args2, commandSender);
                break;

            case "stoptasks":
                Bukkit.getScheduler().cancelAllTasks();
                commandSender.sendMessage("§acanceled all tasks");
                break;

            case "gui":
                openGui(args2, commandSender);
                break;
            default:
                commandSender.sendMessage("§c/" + label + " <world/file/start/stoptasks/gui>");
                break;
        }
        return true;
    }

    private void world(String[] args, CommandSender commandSender) {

        if(args.length == 0) {
            commandSender.sendMessage("§c/debugSw world <delete/create/teleport/unload/load>");
            return;
        }
        WorldCreator worldCreator = new WorldCreator(args[1]);

        switch (args[0]) {
            case "create":
                worldCreator.generator(new ChunkGenerator() {
                    @Override
                    public byte[] generate(World world, Random random, int x, int z) {
                        return new byte[32768]; //Empty byte array
                    }
                });
                Bukkit.getServer().createWorld(worldCreator);

                commandSender.sendMessage("§aWorld: " + args[1] +  " created");
                break;

            case "delete":
                try {
                    if(FileWriter.deleteWorld(args[1])) {
                        commandSender.sendMessage(  "§aWorld: " + args[1] + " deleted");
                        return;
                    }
                    commandSender.sendMessage("§cWorld: " + args[1] + " couldn't get deleted");
                } catch (NullPointerException e) {
                    commandSender.sendMessage("§cWorld: " + args[1] + " not found");
                }
                break;

            case "teleport":

                if(!(commandSender instanceof Player)) {
                    commandSender.sendMessage("§conly Players can do that");
                    return;
                }

                Player player = (Player) commandSender;
                try {
                    player.teleport(new Location(Bukkit.getWorld(args[1]), player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ()));
                } catch (NullPointerException e) {
                    player.sendMessage("§cWorld: " + args[1] +  " not found");
                }
                break;
            case "unload":
                Bukkit.unloadWorld(args[1], false);
                commandSender.sendMessage("§aUnloaded world: " + Bukkit.getWorld(args[1]).toString());
                break;

            case "load":

                worldCreator.environment(World.Environment.THE_END);
                Bukkit.getServer().createWorld(worldCreator);
                commandSender.sendMessage("§aLoaded world: " +args[1]);
                break;
            default:
                commandSender.sendMessage("§cunknown action '" + args[0] +  "'\n<delete/create/teleport/unload/load>");
                break;
        }
    }

    File file = new File(Bukkit.getServer().getWorldContainer(), "mapConfig.yml");
    private void file(String[] args, CommandSender commandSender) {
        if(args.length == 0) {
            commandSender.sendMessage("§c/debugSw file <example/add/clear/content/help/create>");
            return;
        }

        switch (args[0]) {
            case "help":
                commandSender.sendMessage(
                        "file help" +
                                "§r\n  §lexample §8§oshows example of mapConfig.yml" +
                                "§r\n  §ladd §r<variable> <value> §8§oadds the variable with value to the mapConfig file" +
                                "§r\n  §lclear §8§oclears the mapConfig file" +
                                "§r\n  §lcontent §8§oshows the content of the mapConfig file" +
                                "§r\n  §lhelp §8§o shows this list");
                break;

            case "example":
                commandSender.sendMessage("mapConfig §a§oexample" +
                        "§r\n-------------------------" +
                        "§r\n§4§lmax: 4§r    §8§oPlayers needed to start" +
                        "§r\n§e§lloc1:§r 0 20 30    §8§oloc<p>: x y z" +
                        "§r\n§e§lloc2:§r 0 20 -30   §8§oone loc for each player" +
                        "§r\n§e§lloc3:§r 30 20 0" +
                        "§r\n§e§lloc4:§r -30 20 0" +
                        "§r\n§4§lmapName:§r Trees    §8§omap name" +
                        "§r\n§a§lhub:§r Lobby-1    §8§oname of hub" +
                        "§r\n-------------------------" +
                        "§r\n§4§oessential" +
                        "\n§e§odepends on other variable" +
                        "\n§a§ousually not needed");
                break;

            case "clear":
                commandSender.sendMessage(FileWriter.clearConfig());
                break;

            case "add":
                if(args.length <= 2) {
                    commandSender.sendMessage("§c/debugSw file add <parameter> <value>");
                    return;
                }
                StringBuilder value = new StringBuilder();

                for(String i : Arrays.copyOfRange(args, 1, args.length)) {
                    value.append(i).append(" ");
                } value.setLength(value.length() - 2);

                commandSender.sendMessage(FileWriter.addToConfig(args[1], value.toString()));
                break;

            case "content":
                StringBuilder output = new StringBuilder();
                try {
                    Scanner scanner = new Scanner(file);

                    while (scanner.hasNextLine()) { //loop for the scanner
                        output.append(scanner.nextLine()).append("\n");
                    }
                    scanner.close();
                    output.setLength(output.length() - 1);
                    commandSender.sendMessage("mapConfig.yml ---\n" + output.toString());
                } catch (FileNotFoundException e) {
                    commandSender.sendMessage("§cThe File doesn't exist");
                }
                break;

            case "create":
                try {
                    commandSender.sendMessage("File created: " + file.createNewFile());
                } catch (IOException e) {
                    commandSender.sendMessage("§cIOException!");
                } catch (SecurityException e) {
                    commandSender.sendMessage("§cSecurity Exception!");
                }
                break;
        }
    }

    private void start(String[] args, CommandSender commandSender) {

        if (args.length >= 1) {
            try {
                gp.startCountdown(Integer.parseInt(args[0]));
            } catch (Exception e) {
                gp.onBegin();
                commandSender.sendMessage("§aStarted game");
            }
            return;
        }
        gp.onBegin();
        commandSender.sendMessage("§aStarted game");

    }

    private void openGui(String[] args, CommandSender commandSender) {

        if(args.length < 1) {
            commandSender.sendMessage("§cAt least one argument required!\n§cgui <slots>");
            return;
        } else if(!(commandSender instanceof Player)) {
            commandSender.sendMessage("§cYou are not a Player!");
            return;
        }
        Player player = (Player) commandSender;

        int slots = Integer.parseInt(args[0]);
        if(slots == 0) {
            commandSender.sendMessage("§cYou can't use 0!");
            return;
        }
        while (slots % 9 != 0) {
            slots++;
        }
        try {
            String title = "§6§lDebug Gui! §r§8(" + slots + " Slots)";
            if(title.length() > 32) {
                title = title.substring(0, 29);
                title = title + "...";
            }
            Inventory debugInventory = Bukkit.createInventory(null, slots, title);

            debugInventory.setItem(0, gui.getItem("tp"));
            debugInventory.setItem(1, gui.getItem("kit"));
            debugInventory.setItem(2, gui.getItem("hub"));
            ItemStack itemStack = new ItemStack(Material.IRON_CHESTPLATE);
            ItemMeta itemMeta = itemStack.getItemMeta();

            player.openInventory(debugInventory);
        } catch (NumberFormatException e) {
            commandSender.sendMessage("§c'" + args[0] + "' is not a number!");
        } catch (OutOfMemoryError e) {
            commandSender.sendMessage("§cThat was too high...");
        }

    }
}