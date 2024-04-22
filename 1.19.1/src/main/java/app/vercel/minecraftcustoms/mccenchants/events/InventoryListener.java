package app.vercel.minecraftcustoms.mccenchants.events;

import app.vercel.minecraftcustoms.mccenchants.Main;
import app.vercel.minecraftcustoms.mccenchants.api.helpers.MCCInventory;
import app.vercel.minecraftcustoms.mccenchants.configs.MenuConfig;
import app.vercel.minecraftcustoms.mccenchants.lib.MCCEnchantingTable;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_20_R3.enchantments.CraftEnchantment;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class InventoryListener implements Listener {


    // TODO: someday add PAPI support
    // TODO: add a check if event is already canceled
    // TODO: check a way to like make slots not interactive

    private static final Map<UUID, Data> playerData = new HashMap<>();
    private final MenuConfig config;

    public InventoryListener(MenuConfig config) {
        this.config = config;
    }


    // TODO: check if clieked on invalid slit if it is event set cancel early cuz if not we can like swap simular items
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getClickedInventory() == null) return;
        if (!event.getView().getTitle().equals(config.getTitle())) return;
        Data data = playerData.get(event.getWhoClicked().getUniqueId());
        // this so we would not race events
        if (data.taskId != null && Bukkit.getScheduler().isQueued(data.taskId)) {
            System.out.println("cancel");
            event.setCancelled(true);
            return;
        }

        if (event.getClickedInventory().getType() != InventoryType.PLAYER) {
           if (event.getSlot() != config.getInputSlot() && event.getSlot() != config.getLapisSlot()) {
               event.setCancelled(true);
               return;
           }
        }

        ItemStack[] top = cloneContent(event.getView().getTopInventory().getContents());
        ItemStack[] bottom = cloneContent(event.getView().getBottomInventory().getContents());

        ItemStack cursor = event.getView().getCursor() == null ? null : event.getView().getCursor().clone();
        ItemStack inputItem = event.getInventory().getItem(config.getInputSlot()) == null ? null : event.getInventory().getItem(config.getInputSlot()).clone();

        data.taskId = Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
            ItemStack[] content2 = config.getContents(data.bookshelves, inputItem);
            for (int i = 0; i < event.getInventory().getContents().length; i++) {
                if (i == config.getInputSlot()) continue;
                if (i == config.getLapisSlot()) continue;
                if (!eq(event.getInventory().getItem(i), content2[i])) {
                    event.getView().getTopInventory().setContents(top);
                    event.getView().getBottomInventory().setContents(bottom);
                    event.getView().setCursor(cursor);

                    return;
                }
            }

            // TODO: add early returns with click actions

            ItemStack itemStack = event.getInventory().getItem(config.getInputSlot());
            if (itemStack == null || itemStack.getType() == Material.AIR) {
                ItemStack[] content = config.getContents(data.bookshelves, null);
                for (int i = 0; i < event.getInventory().getContents().length; i++) {
                    if (i == config.getInputSlot()) continue;
                    if (i == config.getLapisSlot()) continue;
                    event.getInventory().setItem(i, content[i]);
                }
                return;
            };

            ItemStack[] content = config.getContents(data.bookshelves, itemStack);
            for (int i = 0; i < event.getInventory().getContents().length; i++) {
                if (i == config.getInputSlot()) continue;
                if (i == config.getLapisSlot()) continue;
                event.getInventory().setItem(i, content[i]);
            }

        }, 0).getTaskId();
    }

    private boolean eq(@Nullable ItemStack a, @Nullable ItemStack b) {
        if (a == null || b == null) return a == b;
        return a.equals(b);
    }

    private void enchantItem(@NotNull Inventory inventory, @NotNull HumanEntity player, int bookshelves, int slot) {

         ItemStack itemStack = inventory.getItem(config.getInputSlot());
         if (itemStack == null) return;

        Random random = new Random(MCCEnchantingTable.getEnchantingSeed(player) + slot);
        int cost = MCCEnchantingTable.getEnchantingCost(random, slot + 1, bookshelves, itemStack);

        if (cost < slot + 1) return;

        for (EnchantmentInstance enchantmentInstance : MCCEnchantingTable.getEnchantments(random, cost, itemStack)) {
            itemStack.addUnsafeEnchantment(CraftEnchantment.minecraftToBukkit(enchantmentInstance.enchantment), enchantmentInstance.level);

        }

        MCCInventory.saveAddItemToInventory(player, itemStack);
        inventory.setItem(config.getInputSlot(), null);

        MCCEnchantingTable.updateEnchantingSeed(player);

    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (!event.getView().getTitle().equals(config.getTitle())) return;

        Data data = playerData.get(event.getWhoClicked().getUniqueId());
        if (data == null) return;

        if (data.taskId != null && Bukkit.getScheduler().isQueued(data.taskId)) {
            event.setCancelled(true);
            return;
        }

        ItemStack[] top = cloneContent(event.getView().getTopInventory().getContents());
        ItemStack[] bottom = cloneContent(event.getView().getBottomInventory().getContents());

        data.taskId = Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
        }, 0).getTaskId();
    }

    private ItemStack[] cloneContent(ItemStack[] itemStacks) {
        ItemStack[] arr = new ItemStack[itemStacks.length];
        for (int i = 0; i < itemStacks.length; i++) {
            if (itemStacks[i] != null) arr[i] = itemStacks[i].clone();
        }

        return arr;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (event.getClickedBlock() == null) return;
        if (event.getClickedBlock().getType() != Material.ENCHANTING_TABLE) return;

        boolean hasItemInHand = event.getPlayer().getInventory().getItemInMainHand().getType() != Material.AIR || event.getPlayer().getInventory().getItemInOffHand().getType() != Material.AIR;
        if (event.getPlayer().isSneaking() && hasItemInHand) return;

        playerData.put(event.getPlayer().getUniqueId(), new Data(MCCEnchantingTable.getSurroundingBookshelves(event.getClickedBlock())));

        event.getPlayer().openInventory(config.getInventory(event.getPlayer()));
        event.setCancelled(true);
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!event.getView().getTitle().equals(config.getTitle())) return;

        MCCInventory.saveAddItemToInventory(event.getPlayer(), event.getInventory().getItem(config.getInputSlot()));
        MCCInventory.saveAddItemToInventory(event.getPlayer(), event.getInventory().getItem(config.getLapisSlot()));

        playerData.remove(event.getPlayer().getUniqueId());

    }

    private static @Nullable EnchantmentInstance getFirstEnchant(@NotNull Random random, @NotNull ItemStack item, int cost) {
        List<EnchantmentInstance> enchantments = MCCEnchantingTable.getEnchantments(random, cost, item);
        return enchantments.isEmpty() ? null : enchantments.get(0);

    }

}

class Data {
    public @Nullable Integer taskId;
    public int bookshelves;

    Data(int bookshelves) {
        this.taskId = null;
        this.bookshelves = bookshelves;
    }
}
