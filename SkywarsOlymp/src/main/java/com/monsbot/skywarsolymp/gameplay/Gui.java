package com.monsbot.skywarsolymp.gameplay;

import com.monsbot.skywarsolymp.utilitys.FileReader;
import com.monsbot.skywarsolymp.utilitys.Messages;
import com.monsbot.skywarsolymp.utilitys.Playerdata;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle;
import net.minecraft.server.v1_8_R3.PlayerConnection;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scoreboard.*;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class Gui {
FileReader fr = new FileReader();
Playerdata pd = new Playerdata();
Messages msg = new Messages();

ScoreboardManager manager = Bukkit.getScoreboardManager();
int Players;

//Scores, not very clean
Score s0;
Score s1;
Score s2;
Score s3;
Score s4;
Score s5;
Score s6;
Score s7;
Score s8;
Score s9;

Score s10;
Score s11;

    public void scBeforeStart(Player player) {
        scBeforeStart("...", player);
    }

    public void scBeforeStart() {
        for (Player pl : Bukkit.getServer().getOnlinePlayers()) {
            scBeforeStart(pl);
        }
    }

    public void scBeforeStart(String count) {
        for (Player pl : Bukkit.getServer().getOnlinePlayers()) {
            scBeforeStart(count, pl);
        }
    }

    public void scBeforeStart(String count, Player player) {

    Scoreboard board = manager.getNewScoreboard();
    Objective obj = board.registerNewObjective("EpicBruhMoment", "dummy");
    Players = 0;

    obj.setDisplaySlot(DisplaySlot.SIDEBAR);
    obj.setDisplayName("§8» §b§lOlympMC §8x §dSkywars "); //title

    //yes this is exactly the first one
    for (Player pl : Bukkit.getServer().getOnlinePlayers()) {
        Players++;
    } //if u have a better way to do this: cool, I didn't

    s0 = obj.getScore(" ");
    s0.setScore(12);

    s1 = obj.getScore("§8x §bPlayers:");
    s1.setScore(11);

    s2 = obj.getScore("§8» §d" + Players + "/" + fr.getMaxPlayers());
    s2.setScore(10);

    s3 = obj.getScore("  ");
    s3.setScore(9);

    s4 = obj.getScore("§8x §bStarting in:");
    s4.setScore(8);

    s5 = obj.getScore("§8» §d" + count);
    s5.setScore(7);

    s6 = obj.getScore("   ");
    s6.setScore(6);

    s7 = obj.getScore("§8x §bMap:");
    s7.setScore(5);

    s8 = obj.getScore("§8» §d" + fr.getLevelName());
    s8.setScore(4);

    s9 = obj.getScore("    ");
    s9.setScore(3);

    s10 = obj.getScore("§8x §bDrc:");
    s10.setScore(2);

    s11 = obj.getScore("§8» §d" + "§r§7§oPlaceholder");
    s11.setScore(1);

    player.setScoreboard(board);
}

    public  void scAfterStart() {
        scAfterStart("...");
    }

    public void scAfterStart(String count) {
        for(Player pl : Bukkit.getServer().getOnlinePlayers()) {
            scAfterStart(count, pl);
        }
    }

    public void scAfterStart(String count, Player player) {

        Scoreboard board = manager.getNewScoreboard();
        Objective obj = board.registerNewObjective("69420", "dummy");
        Players = 0;

        obj.setDisplaySlot(DisplaySlot.SIDEBAR);
        obj.setDisplayName("§8» §b§lOlympMC §8x §dSkywars "); //title

        //yes this is exactly the first one
        for (Player pl : Bukkit.getServer().getOnlinePlayers()) {
            if(pl.getGameMode() != GameMode.ADVENTURE) {
                Players++;
            }
        }

        s0 = obj.getScore(" ");
        s0.setScore(12);

        s1 = obj.getScore("§8x §bAlive:");
        s1.setScore(11);

        s2 = obj.getScore("§8» §d" + Players + "/" + fr.getMaxPlayers());
        s2.setScore(10);

        s3 = obj.getScore("  ");
        s3.setScore(9);

        s4 = obj.getScore("§8x §bRefill in:");
        s4.setScore(8);

        s5 = obj.getScore("§8» §d" + count);
        s5.setScore(7);

        s6 = obj.getScore("   ");
        s6.setScore(6);

        s7 = obj.getScore("§8x §bMap:");
        s7.setScore(5);

        s8 = obj.getScore("§8» §d" + fr.getLevelName());
        s8.setScore(4);

        s9 = obj.getScore("    ");
        s9.setScore(3);

        s10 = obj.getScore("§8x §bKills:");
        s10.setScore(2);

        s11 = obj.getScore("§8» §d" + pd.getKillcount(player));
        s11.setScore(1);

        player.setScoreboard(board);

    }



    public void sendTitle(Player player, String title) {
        PlayerConnection connection = ((CraftPlayer) player.getPlayer()).getHandle().playerConnection;
        IChatBaseComponent text = IChatBaseComponent.ChatSerializer.a("{'text': '" + title + "'}");

        PacketPlayOutTitle packet = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, text, 1, 80, 1);
        connection.sendPacket(packet);
    }

    public void sendSubTitle(Player player, String title) {
        PlayerConnection connection = ((CraftPlayer) player.getPlayer()).getHandle().playerConnection;
        IChatBaseComponent subText = IChatBaseComponent.ChatSerializer.a("{'text': '" + title + "'}");

        PacketPlayOutTitle packet = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.SUBTITLE, subText, 1, 80, 1);
        connection.sendPacket(packet);
    }

    private void openTeleportGui(Player player) {

        int slots = Bukkit.getOnlinePlayers().size();
        while (slots % 9 != 0) {
            slots++;
        }

        int slot = 0;

        Inventory playerSelector = Bukkit.createInventory(null, slots + 9, "§lTeleport Menu");

        for(Player pl: Bukkit.getOnlinePlayers()) {
            ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal());
            SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
            skullMeta.setDisplayName("§r§l" + pl.getDisplayName());
            skullMeta.setOwner(pl.getDisplayName());
            List<String> lore = Collections.singletonList("§8" + pl.getUniqueId().toString());
            skullMeta.setLore(lore);
            skull.setItemMeta(skullMeta);

            playerSelector.setItem(slot, skull);
            slot++;
        }

        ItemStack itemStack = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) DyeColor.LIME.ordinal());
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName("§a§lCenter");
        List<String> lore = Collections.singletonList("§8Teleport to Center");
        itemMeta.setLore(lore);
        itemStack.setItemMeta(itemMeta);

        playerSelector.setItem(slots + 8, itemStack);

        int i = 0;
        while (i < 8) {

            itemStack = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) DyeColor.BLACK.ordinal());
            itemMeta = itemStack.getItemMeta();
            itemMeta.setDisplayName("§r");
            itemStack.setItemMeta(itemMeta);
            playerSelector.setItem(slots + i, itemStack);
            i++;
        }

        player.openInventory(playerSelector);
    }

    private void openKitGui(Player player) {

        Inventory kitGui = Bukkit.createInventory(null, 27, "§lChange Kit");

        ItemStack itemStack = new ItemStack(Material.IRON_CHESTPLATE);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName("§5Tank");
        List<String> lore = Collections.singletonList("§8Prot 6 iron chestplate & boots");
        itemMeta.setLore(lore);
        itemStack.setItemMeta(itemMeta);
        kitGui.setItem(10, itemStack);

        itemStack = new ItemStack(Material.SNOW_BALL);
        itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName("§5Snow ball");
        lore = Collections.singletonList("§816 Snow balls");
        itemMeta.setLore(lore);
        itemStack.setItemMeta(itemMeta);
        kitGui.setItem(11, itemStack);

        itemStack = new ItemStack(Material.SLIME_BALL);
        itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName("§5Slime");
        lore = Collections.singletonList("§8Jump boost potion");
        itemMeta.setLore(lore);
        itemStack.setItemMeta(itemMeta);
        kitGui.setItem(12, itemStack);

        itemStack = new ItemStack(Material.FLINT_AND_STEEL);
        itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName("§5Flint and Steel");
        lore = Collections.singletonList("§8Flint and Steel");
        itemMeta.setLore(lore);
        itemStack.setItemMeta(itemMeta);
        kitGui.setItem(13, itemStack);

        itemStack = new ItemStack(Material.BARRIER);
        itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName("§c§lNone");
        lore = Collections.singletonList("§8Default Kit");
        itemMeta.setLore(lore);
        itemStack.setItemMeta(itemMeta);
        kitGui.setItem(15, itemStack);


        player.openInventory(kitGui);
    }

    public boolean guiSwitch(Player player, ItemStack itemStack, Inventory inventory) {

        switch (inventory.getName().toLowerCase()) {
            case "§lteleport menu":

                Location location;
                String lore;

                try {
                    lore = itemStack.getItemMeta().getLore().get(0).substring(2);
                } catch (StringIndexOutOfBoundsException | NullPointerException e) {
                    return true;
                }

                if(lore.equals("Teleport to Center")) {
                    location = fr.getCenter();
                    player.sendMessage("§aTeleported to Center");
                } else {
                    Player target = Bukkit.getPlayer(UUID.fromString(lore));
                    location = target.getLocation();
                    player.sendMessage("§aTeleported to " + target.getDisplayName());
                }

                player.teleport(location);
                break;

            case "§lchange kit":
                ItemStack kit = getItem("kit");
                ItemMeta kitItemMeta = kit.getItemMeta();
                List<String> kitLore;
                PlayerInventory playerInventory = player.getInventory();
                String name;

                try {
                    name = itemStack.getItemMeta().getDisplayName();
                } catch (NullPointerException e) {
                    name = "--";
                }
                switch (name.substring(2)) {

                    case "Slime":
                        kitLore = Collections.singletonList("§r§8Selected Kit: §a§lSlime");
                        kitItemMeta.setLore(kitLore);
                        kit.setItemMeta(kitItemMeta);
                        playerInventory.setItem(4, kit);
                        break;

                    case "Tank":
                        kitLore = Collections.singletonList("§r§8Selected Kit: §a§lTank");
                        kitItemMeta.setLore(kitLore);
                        kit.setItemMeta(kitItemMeta);
                        playerInventory.setItem(4, kit);
                        break;

                    case "Flint and Steel":
                        kitLore = Collections.singletonList("§r§8Selected Kit: §a§lFlint and Steel");
                        kitItemMeta.setLore(kitLore);
                        kit.setItemMeta(kitItemMeta);
                        playerInventory.setItem(4, kit);
                        break;

                    case "Snow ball":
                        kitLore = Collections.singletonList("§r§8Selected Kit: §a§lSnow ball");
                        kitItemMeta.setLore(kitLore);
                        kit.setItemMeta(kitItemMeta);
                        playerInventory.setItem(4, kit);
                        break;

                    default:
                        playerInventory.setItem(4, kit);
                        break;
                }

                break;

            default:
                return false;
        }
        return true;
    }

    public void itemSwitch(Player player, ItemStack itemStack) {
        String itemName = itemStack.getItemMeta().getDisplayName();
        switch (itemName) {
            case "§r§c§lBack to Hub":
                msg.connect(player, fr.getHubName());
                break;

            case "§r§a§lTeleport":
                openTeleportGui(player);
                break;

            case "§r§a§lChange Kit":
                openKitGui(player);
                break;
        }
    }

    public ItemStack getItem(String name) {

        ItemStack itemStack = null;
        ItemMeta itemMeta;

        switch (name) {
            case "tp":
                itemStack = new ItemStack(Material.COMPASS);
                itemMeta = itemStack.getItemMeta();
                itemMeta.setDisplayName("§r§a§lTeleport");
                List<String> lore = Collections.singletonList("§r§8Teleport around the map");
                itemMeta.setLore(lore);
                itemStack.setItemMeta(itemMeta);
                break;

            case "kit":
                itemStack = new ItemStack(Material.CHEST);
                itemMeta = itemStack.getItemMeta();
                itemMeta.setDisplayName("§r§a§lChange Kit");
                lore = Collections.singletonList("§r§8Change your Kit");
                itemMeta.setLore(lore);
                itemStack.setItemMeta(itemMeta);
                break;

            case "hub":
                itemStack = new ItemStack(Material.REDSTONE_BLOCK);
                itemMeta = itemStack.getItemMeta();
                itemMeta.setDisplayName("§r§c§lBack to Hub");
                lore = Collections.singletonList("§r§8Return to the Lobby/Hub");
                itemMeta.setLore(lore);
                itemStack.setItemMeta(itemMeta);
                break;
        }

        return itemStack;

    }

}
