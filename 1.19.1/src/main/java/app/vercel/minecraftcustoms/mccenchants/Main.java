package app.vercel.minecraftcustoms.mccenchants;

import app.vercel.minecraftcustoms.mccenchants.events.OnChestOpenEvent;
import app.vercel.minecraftcustoms.mccenchants.events.OnPrepareAnvilEvent;
import app.vercel.minecraftcustoms.mccenchants.events.OnPrepareGrindstoneEvent;
import app.vercel.minecraftcustoms.mccenchants.events.OnVillagerInteractEvent;
import app.vercel.minecraftcustoms.mccenchants.managers.InventoryManager;
import app.vercel.minecraftcustoms.mccenchants.api.enchantments.MCCEnchantment;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    private static JavaPlugin INSTANCE;

    @Override
    public void onEnable() {

        INSTANCE = this;
        Version.setVersion(this);

        MCCEnchantment.registerMinecraftEnchantments(this);
        InventoryManager.registerInventories(this);

        registerEvents();

    }

    private void registerEvents() {

        this.getServer().getPluginManager().registerEvents(new OnPrepareAnvilEvent(), this);
        this.getServer().getPluginManager().registerEvents(new OnPrepareGrindstoneEvent(), this);
        this.getServer().getPluginManager().registerEvents(new OnChestOpenEvent(), this);
        this.getServer().getPluginManager().registerEvents(new OnVillagerInteractEvent(), this);

    }

    public static JavaPlugin getInstance() {
        return INSTANCE;

    }

}