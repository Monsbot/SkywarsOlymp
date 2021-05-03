package com.monsbot.skywarsolymp.utilitys;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.monsbot.skywarsolymp.SkywarsOlymp;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

//the pluginMessage stuff
public class Messages implements PluginMessageListener {


    private SkywarsOlymp plugin = SkywarsOlymp.getInstance();

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {

        if(!channel.equals("BungeeCord")) {return;} //check if it's for Bungeecord

        /*ByteArrayDataInput input = ByteStreams.newDataInput(message);
        String subchannel = input.readUTF(); */
        //removed, wasn't really used anywhere
    }

    //connect a player to a server
    public void connect(Player player, String server) {
        connect(player, server, plugin);
    }

    private void connect(Player player, String server, SkywarsOlymp plugin) {
        ByteArrayDataOutput output = ByteStreams.newDataOutput();
        output.writeUTF("Connect");
        output.writeUTF(server);

        //tbh I have no Idea what's going on here
        player.sendPluginMessage(plugin, "BungeeCord", output.toByteArray());
    }


}
