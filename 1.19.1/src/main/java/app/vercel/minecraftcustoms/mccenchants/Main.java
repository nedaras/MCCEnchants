package app.vercel.minecraftcustoms.mccenchants;

import app.vercel.minecraftcustoms.mccenchants.api.enchantments.MCCEnchantment;
import app.vercel.minecraftcustoms.mccenchants.events.PlayerListener;
import app.vercel.minecraftcustoms.mccenchants.packets.PacketHandler;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    private static JavaPlugin INSTANCE;

    @Override
    public void onEnable() {

        INSTANCE = this;
        //Version.setVersion(this);

        MCCEnchantment.registerEnchantment(new CustomEnchantment());
        //InventoryManager.registerInventories(this); // we will need them inventorie

        PacketHandler.init();
        this.getServer().getPluginManager().registerEvents(new PlayerListener(), this);
    }


    @Override
    public void onDisable() {
        PacketHandler.deinit();
    }

    public static JavaPlugin getInstance() {
        return INSTANCE;
    }
}