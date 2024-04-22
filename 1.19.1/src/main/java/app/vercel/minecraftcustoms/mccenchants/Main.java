package app.vercel.minecraftcustoms.mccenchants;

import app.vercel.minecraftcustoms.mccenchants.api.enchantments.MCCEnchantment;
import app.vercel.minecraftcustoms.mccenchants.commands.Commands;
import app.vercel.minecraftcustoms.mccenchants.configs.MenuConfig;
import app.vercel.minecraftcustoms.mccenchants.events.InventoryListener;
import app.vercel.minecraftcustoms.mccenchants.events.PlayerListener;
import app.vercel.minecraftcustoms.mccenchants.packets.PacketHandler;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    private static JavaPlugin INSTANCE;

    @Override
    public void onEnable() {

        INSTANCE = this;
        // TODO: we need to rewrite the enchantment table manager cuz code in there is just too complicated
        //Version.setVersion(this); // we will use reflection

        MCCEnchantment.registerEnchantment(new CustomEnchantment());
        // TODO: make like config to decide if this inv should be enabled
        //InventoryManager.registerInventories(this);

        PacketHandler.init();

        MenuConfig menuConfig = new MenuConfig(this);

        this.getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        this.getServer().getPluginManager().registerEvents(new InventoryListener(menuConfig), this);

        this.getCommand("mccenchants").setExecutor(new Commands(menuConfig));

    }


    @Override
    public void onDisable() {
        PacketHandler.deinit();
    }

    public static JavaPlugin getInstance() {
        return INSTANCE;
    }
}