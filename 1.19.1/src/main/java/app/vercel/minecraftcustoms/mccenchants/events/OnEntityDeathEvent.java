package app.vercel.minecraftcustoms.mccenchants.events;

import app.vercel.minecraftcustoms.mccenchants.api.helpers.MCCEnchanting;
import app.vercel.minecraftcustoms.mccenchants.utils.Utils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

public class OnEntityDeathEvent implements Listener {

    @EventHandler
    public void onEntityDeathEvent(EntityDeathEvent event) {

        event.getDrops().forEach((item) -> {

            if (MCCEnchanting.getEnchantments(item).isEmpty()) return;
            if (Utils.isItemStackSinged(item)) return;

            Utils.convertEnchantsToLore(item);

        });

    }

}
