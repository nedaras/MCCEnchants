package app.vercel.minecraftcustoms.mccenchants.events;

import app.vercel.minecraftcustoms.mccenchants.api.helpers.MCCEnchanting;
import app.vercel.minecraftcustoms.mccenchants.utils.Utils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;

public class OnPrepareAnvilEvent implements Listener {

    @EventHandler
    public void onPrepareAnvilEvent(PrepareAnvilEvent event) {
        if (event.getResult() == null) return;
        if (event.getResult().getItemMeta() == null) return;
        if (MCCEnchanting.getEnchantments(event.getResult()).isEmpty()) return;

        Utils.convertEnchantsToLore(event.getResult());

    }

}
