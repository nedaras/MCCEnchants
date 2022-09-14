package app.vercel.minecraftcustoms.mccenchants.events;

import app.vercel.minecraftcustoms.mccenchants.api.helpers.MCCEnchanting;
import app.vercel.minecraftcustoms.mccenchants.utils.Utils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupItemEvent;

public class OnPlayerPickupItemEvent implements Listener {

    @EventHandler
    public void onPlayerPickupItemEvent(PlayerPickupItemEvent event) {

        if (MCCEnchanting.getEnchantments(event.getItem().getItemStack()).isEmpty()) return;
        if (Utils.isUpToDate(event.getItem().getItemStack())) return;

        Utils.convertEnchantsToLore(event.getItem().getItemStack());

    }

}
