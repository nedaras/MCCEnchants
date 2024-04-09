package app.vercel.minecraftcustoms.mccenchants;

import app.vercel.minecraftcustoms.mccenchants.api.enchantments.MCCEnchantment;
import app.vercel.minecraftcustoms.mccenchants.packets.PacketHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public final class Main extends JavaPlugin implements Listener {

    private static JavaPlugin INSTANCE;

    @Override
    public void onEnable() {

        INSTANCE = this;
        //Version.setVersion(this);

        MCCEnchantment.registerEnchantment(new CustomEnchantment());
        //InventoryManager.registerInventories(this); // we will need them inventories

        this.getServer().getPluginManager().registerEvents(this, this);

        for (Player player : Bukkit.getOnlinePlayers()) {
            PacketHandler.addPlayer(player);
        }

        registerEvents();
    }

    @EventHandler
    public void playerJoin(PlayerJoinEvent event) {
        PacketHandler.addPlayer(event.getPlayer());
    }


    @EventHandler
    public void playerQuit(PlayerQuitEvent event) {
        PacketHandler.removePlayer(event.getPlayer());
    }

    @EventHandler
    public void inte(PlayerInteractEvent event) {
        if (event.getItem() == null || event.getItem().getType() == Material.AIR) return;

        ItemMeta meta = event.getItem().getItemMeta();

        if (meta == null) return;

        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GOLD + "we can even have lore");

        meta.setLore(lore);
        event.getItem().setItemMeta(meta);

        System.out.println(event.getItem());
    }

    @Override
    public void onDisable() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            PacketHandler.removePlayer(player);
        }
    }

    private void registerEvents() {

        // we should like handle other events if Hooks failed
        //this.getServer().getPluginManager().registerEvents(new OnPrepareInventoryResultEvent(), this);
        //this.getServer().getPluginManager().registerEvents(new OnChestOpenEvent(), this);
        //this.getServer().getPluginManager().registerEvents(new OnVillagerInteractEvent(), this);
        //this.getServer().getPluginManager().registerEvents(new OnPlayerPickupItemEvent(), this);

    }

    public static JavaPlugin getInstance() {
        return INSTANCE;
    }
}