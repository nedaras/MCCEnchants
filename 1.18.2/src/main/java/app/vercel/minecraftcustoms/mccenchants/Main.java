package app.vercel.minecraftcustoms.mccenchants;

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

    }

    public static JavaPlugin getInstance() {
        return INSTANCE;

    }

}