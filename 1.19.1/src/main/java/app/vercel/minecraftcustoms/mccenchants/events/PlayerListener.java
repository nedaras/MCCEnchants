package app.vercel.minecraftcustoms.mccenchants.events;

import app.vercel.minecraftcustoms.mccenchants.packets.PacketHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerExpChangeEvent;
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

    @EventHandler
    public void a(PlayerExpChangeEvent event) {
        //System.out.println("xp xp xp");
    }

}
