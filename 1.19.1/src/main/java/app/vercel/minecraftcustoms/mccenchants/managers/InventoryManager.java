package app.vercel.minecraftcustoms.mccenchants.managers;

import app.vercel.minecraftcustoms.mccenchants.Main;
import app.vercel.minecraftcustoms.mccenchants.api.helpers.MCCInventory;
import app.vercel.minecraftcustoms.mccenchants.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class InventoryManager implements Listener {

    private static final Map<Integer, CorrespondingInventory> playerInventories = new HashMap<>();
    private static final Map<Material, CorrespondingInventory> correspondingInventories = new HashMap<>();

    public static void registerInventories(JavaPlugin plugin) {

        plugin.getServer().getPluginManager().registerEvents(new InventoryManager(), plugin);

        registerInventory(new EnchantingTable(plugin), plugin);

    }

    private static void registerInventory(CorrespondingInventory inventory, JavaPlugin plugin) {
        correspondingInventories.put(inventory.getCorrespondingBlock(), inventory);
        plugin.getServer().getPluginManager().registerEvents(inventory, plugin);

    }

    public static boolean isCustomInventory(@NotNull Inventory inventory) {
        return playerInventories.containsKey(inventory.hashCode());

    }

    @EventHandler
    public void onPlayerInteractEvent(PlayerInteractEvent event) {

        if (event.getClickedBlock() == null) return;
        if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) return;
        if (!event.hasBlock()) return;
        if (!correspondingInventories.containsKey(event.getClickedBlock().getType())) return;

        CorrespondingInventory correspondingInventory = correspondingInventories.get(event.getClickedBlock().getType());
        Player player = event.getPlayer();

        boolean hasItemInHand = player.getInventory().getItemInMainHand().getType() != Material.AIR || player.getInventory().getItemInOffHand().getType() != Material.AIR;

        if (event.getPlayer().isSneaking() && hasItemInHand) return;

        event.setCancelled(true);

        Inventory inventory = correspondingInventory.openInitialInventory(player);
        correspondingInventory.onInventoryOpen(player, event.getClickedBlock());

        playerInventories.put(inventory.hashCode(), correspondingInventory);

    }

    @EventHandler
    public void onInventoryClickEvent(InventoryClickEvent event) {

        if (event.getAction() == InventoryAction.NOTHING) return;
        if (event.getClickedInventory() == null) return;
        if (!isCustomInventory(event.getInventory())) return;

        CorrespondingInventory inventory = playerInventories.get(event.getInventory().hashCode());
        if (event.getSlot() == inventory.getCloseSlot()) {

            event.setCancelled(true);

            // Running later so, it would prevent from spawning ghost item in players inventory.
            Bukkit.getScheduler().runTaskLater(inventory.getPlugin(), () -> event.getWhoClicked().closeInventory(), 0);

            return;

        }
        // we can shift click glass
        // we need to fix MOVE_TO_ANOTHER_INVENTORY state
        event.setCancelled(event.getClickedInventory().getType() == InventoryType.CHEST && !inventory.getCheckupSlots().contains(getValidSlot(event)));
        Bukkit.getScheduler().runTaskLater(inventory.getPlugin(), () -> inventory.onInventoryClickEvent(event), 0);

    }

    private int getValidSlot(InventoryClickEvent event) {

        if (!event.isShiftClick() || event.getClickedInventory().getType() == InventoryType.CHEST) return event.getSlot();

        for (int i = 0; i < event.getInventory().getSize(); i++) {

            ItemStack inventoryItem = event.getInventory().getItem(i);

            if (inventoryItem == null) continue;
            if (!inventoryItem.isSimilar(event.getCurrentItem())) continue;
            if (inventoryItem.getAmount() >= inventoryItem.getMaxStackSize()) continue;

            return i;

        }

        return event.getInventory().firstEmpty() != -1 ? event.getInventory().firstEmpty() : event.getSlot();

    }

    @EventHandler
    public void onInventoryCloseEvent(InventoryCloseEvent event) {

        if (!isCustomInventory(event.getInventory())) return;

        CorrespondingInventory inventory = playerInventories.get(event.getInventory().hashCode());
        Player player = (Player) event.getPlayer();

        for (int slot : inventory.getCheckupSlots()) {

            ItemStack item = event.getInventory().getItem(slot);

            if (item != null && item.getType() != Material.AIR) {

                MCCInventory.saveAddItemToInventory(player, item);

            }

        }

        inventory.onInventoryClose((Player) event.getPlayer());
        inventory.flushPlayer((Player) event.getPlayer());
        playerInventories.remove(event.getInventory().hashCode());

    }

    // we need to convert drag event to click event

}
