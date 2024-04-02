package app.vercel.minecraftcustoms.mccenchants;

import app.vercel.minecraftcustoms.mccenchants.events.*;
import app.vercel.minecraftcustoms.mccenchants.hooks.Hooks;
import app.vercel.minecraftcustoms.mccenchants.api.enchantments.MCCEnchantment;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_20_R3.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    private static JavaPlugin INSTANCE;

    @Override
    public void onEnable() {

        INSTANCE = this;
        //Version.setVersion(this);

        //MCCEnchantment.registerMinecraftEnchantments(this);
        MCCEnchantment.registerEnchantment(new CustomEnchantment());
        Hooks.init();
        //InventoryManager.registerInventories(this); // we will need them inventories

        //registerEvents();

    }

    private void registerEvents() {

        this.getServer().getPluginManager().registerEvents(new OnPrepareAnvilEvent(), this);
        this.getServer().getPluginManager().registerEvents(new OnPrepareGrindstoneEvent(), this);
        this.getServer().getPluginManager().registerEvents(new OnChestOpenEvent(), this);
        this.getServer().getPluginManager().registerEvents(new OnVillagerInteractEvent(), this);
        this.getServer().getPluginManager().registerEvents(new OnPlayerPickupItemEvent(), this);

    }

    public static JavaPlugin getInstance() {
        return INSTANCE;

    }

}