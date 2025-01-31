package com.github.mccenchants;

import com.github.mccenchants.nms.NMS;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nullable;

public class MCCEnchants extends JavaPlugin implements Listener  {

    @Nullable NMS nms = null;

    @Override
    public void onEnable() {

        String packageVersion = Bukkit.getUnsafe().getClass().getName();
        packageVersion = packageVersion.replace("org.bukkit.craftbukkit.", "");
        packageVersion = packageVersion.replace(".util.CraftMagicNumbers", "");

        try {
            nms = NMS.init(packageVersion);
        } catch (Exception e) {
            this.getLogger().severe(this.getDescription().getVersion() + " is not compatible with Minecraft " + packageVersion + ".");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            nms.hookPlayerPackets(player);
        }

        this.getServer().getPluginManager().registerEvents(this, this);

        nms.registerEnchantment(new MyEnchantment());

    }

    @Override
    public void onDisable() {
        if (nms == null) return;

        for (Player player : Bukkit.getOnlinePlayers()) {
            nms.unhookPlayerPackets(player);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (nms == null) return;
        nms.hookPlayerPackets(event.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        if (nms == null) return;
        nms.unhookPlayerPackets(event.getPlayer());
    }

}
