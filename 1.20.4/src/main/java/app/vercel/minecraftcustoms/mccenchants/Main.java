package app.vercel.minecraftcustoms.mccenchants;

import app.vercel.minecraftcustoms.mccenchants.api.enchantments.MCCEnchantment;
import app.vercel.minecraftcustoms.mccenchants.debug.MyEnchantment;
import app.vercel.minecraftcustoms.mccenchants.events.PlayerListener;
import app.vercel.minecraftcustoms.mccenchants.packets.PacketHandler;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    @Override
    public void onEnable() {
        PacketHandler.init();

        // The problem we're having is we're doing two plugins in one
        //  * custom enchantments
        //  * enchantment gui
        // We should just focus on custom enchantments making them like vanilla
        // Our job is to add them lore to custom enchantments

        this.getServer().getPluginManager().registerEvents(new PlayerListener(), this);

        MCCEnchantment.registerEnchantment(new MyEnchantment());

    }

    @Override
    public void onDisable() {
        PacketHandler.deinit();
    }
}