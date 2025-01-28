package com.github.mccenchants.nms.v1_20_R4;

import com.github.mccenchants.nms.v1_20_R4.api.enchantments.MCCEnchantment;
import com.github.mccenchants.nms.v1_20_R4.debug.MyEnchantment;
import com.github.mccenchants.nms.v1_20_R4.events.PlayerListener;
import com.github.mccenchants.nms.v1_20_R4.packets.PacketHandler;
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