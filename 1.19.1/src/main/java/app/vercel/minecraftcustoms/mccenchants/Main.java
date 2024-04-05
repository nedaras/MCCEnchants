package app.vercel.minecraftcustoms.mccenchants;

import app.vercel.minecraftcustoms.mccenchants.events.*;
import app.vercel.minecraftcustoms.mccenchants.hooks.Hooks;
import app.vercel.minecraftcustoms.mccenchants.api.enchantments.MCCEnchantment;
import app.vercel.minecraftcustoms.mccenchants.managers.InventoryManager;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin implements Listener {

    private static JavaPlugin INSTANCE;
    private static boolean IS_HOOKED = false; // we need this

    @Override
    public void onEnable() {

        INSTANCE = this;
        //Version.setVersion(this);

        MCCEnchantment.registerEnchantment(new CustomEnchantment());
        Hooks.init();
        InventoryManager.registerInventories(this); // we will need them inventories

        registerEvents();
    }

    private void registerEvents() {

        // we should like handle other events if Hooks failed
        this.getServer().getPluginManager().registerEvents(new OnPrepareInventoryResultEvent(), this);
        //this.getServer().getPluginManager().registerEvents(new OnChestOpenEvent(), this);
        //this.getServer().getPluginManager().registerEvents(new OnVillagerInteractEvent(), this);
        //this.getServer().getPluginManager().registerEvents(new OnPlayerPickupItemEvent(), this);

    }

    public static JavaPlugin getInstance() {
        return INSTANCE;
    }
}