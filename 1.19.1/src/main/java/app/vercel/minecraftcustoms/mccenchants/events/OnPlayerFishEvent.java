package app.vercel.minecraftcustoms.mccenchants.events;

import app.vercel.minecraftcustoms.mccenchants.api.helpers.MCCEnchanting;
import app.vercel.minecraftcustoms.mccenchants.utils.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;

public class OnPlayerFishEvent implements Listener {

    @EventHandler
    public void onPlayerFishEvent(PlayerFishEvent event) {

        if (event.getCaught() == null) return;
        if (!(event.getCaught() instanceof Item)) return;

        ItemStack item = ((Item) event.getCaught()).getItemStack();

        if (item.getType() == Material.AIR) return;
        if (MCCEnchanting.getEnchantments(item).isEmpty()) return;

        Utils.convertEnchantsToLore(item);

    }

}
