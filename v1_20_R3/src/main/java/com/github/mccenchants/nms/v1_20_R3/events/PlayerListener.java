package com.github.mccenchants.nms.v1_20_R3.events;

import com.github.mccenchants.nms.v1_20_R3.packets.PacketHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        PacketHandler.addPlayer(event.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        PacketHandler.removePlayer(event.getPlayer());
    }

}
