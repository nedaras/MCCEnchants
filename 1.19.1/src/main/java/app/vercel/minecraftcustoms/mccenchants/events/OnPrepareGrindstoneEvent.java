package app.vercel.minecraftcustoms.mccenchants.events;

import app.vercel.minecraftcustoms.mccenchants.Main;
import app.vercel.minecraftcustoms.mccenchants.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

public class OnPrepareGrindstoneEvent implements Listener {

    @EventHandler
    public void onGrindstoneClickEvent(InventoryClickEvent event) {


        if (event.getClickedInventory() == null) return;
        if (event.getInventory().getType() != InventoryType.GRINDSTONE) return;

        Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> onPrepareGrindstoneEvent(event.getInventory().getItem(2)), 0);

    }

    // TODO: don't remove all lore just the enchantment lore
    private void onPrepareGrindstoneEvent(@Nullable ItemStack result) {

        if (result == null) return;
        if (result.getItemMeta() == null) return;
        if (!result.getItemMeta().hasLore()) return;

        ItemMeta meta = result.getItemMeta();

        meta.setLore(null);

        result.setItemMeta(meta);

        Utils.convertEnchantsToLore(result);

    }

}
