package app.vercel.minecraftcustoms.mccenchants.events;

import app.vercel.minecraftcustoms.mccenchants.Main;
import app.vercel.minecraftcustoms.mccenchants.api.helpers.MCCInventory;
import app.vercel.minecraftcustoms.mccenchants.configs.MenuConfig;
import app.vercel.minecraftcustoms.mccenchants.lib.MCCEnchantingTable;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_20_R3.enchantments.CraftEnchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class InventoryListener implements Listener {


    // TODO: someday add PAPI support
    // TODO: add a check if event is already canceled
    // TODO: check a way to like make slots not interactive
    // TODO: add a check when player gets exp and update inv
    // TODO: handle distance check for when inv should be closed and if other player breaks the inv or we die and more shit

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
            event.setCancelled(true);
            return;
        }

        for (Integer i : config.getOutputSlots()) {
            if (i == event.getSlot()) {
                ItemStack itemStack = event.getInventory().getItem(i);
                ItemStack inputItem = event.getInventory().getItem(config.getInputSlot());
                ItemStack lapisItem = event.getInventory().getItem(config.getLapisSlot());

                if (itemStack == null || inputItem == null) break;

                ItemMeta meta = itemStack.getItemMeta();
                if (meta == null) break;

                Byte slot = meta.getPersistentDataContainer().get(config.getSlotNamespace(), PersistentDataType.BYTE);
                if (slot == null) break;

                Random random = new Random(MCCEnchantingTable.getEnchantingSeed(event.getWhoClicked()) + slot - 1);
                int cost = MCCEnchantingTable.getEnchantingCost(random, slot, data.bookshelves, inputItem);

                for (EnchantmentInstance instance : MCCEnchantingTable.getEnchantments(random, cost, inputItem)) {
                    inputItem.addUnsafeEnchantment(CraftEnchantment.minecraftToBukkit(instance.enchantment), instance.level);
                }

                if (event.getWhoClicked().getGameMode() != GameMode.CREATIVE && lapisItem != null) {
                    if (lapisItem.getAmount() > slot) {
                        lapisItem.setAmount(lapisItem.getAmount() - slot);
                    } else {
                        lapisItem = null;
                    }
                }

                // TODO: update seed and handle books stored enchants and like not make null item remove one item from it and remove levels, play sounds
                MCCInventory.saveAddItemToInventory(event.getWhoClicked(), inputItem);

                //pitch calculated: world.random.nextFloat() * 0.1F + 0.9F
                ((Player) event.getWhoClicked()).playSound(data.blockLocation, Sound.BLOCK_ENCHANTMENT_TABLE_USE, SoundCategory.BLOCKS, 1.0f, 1.0f);

                event.getInventory().setItem(config.getInputSlot(), null);
                event.getInventory().setItem(config.getLapisSlot(), lapisItem);

                ItemStack[] content = config.getContents(event.getWhoClicked(), data.bookshelves, null, lapisItem);

                for (int j = 0; j < event.getInventory().getContents().length; j++) {
                    if (j == config.getInputSlot()) continue;
                    if (j == config.getLapisSlot()) continue;
                    event.getInventory().setItem(j, content[j]);
                }

                event.setCancelled(true);
                return;

            }
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
        ItemStack lapisItem = event.getInventory().getItem(config.getLapisSlot()) == null ? null : event.getInventory().getItem(config.getLapisSlot()).clone();

        data.taskId = Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
            ItemStack[] content2 = config.getContents(event.getWhoClicked(), data.bookshelves, inputItem, lapisItem);
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
            ItemStack lapis = event.getInventory().getItem(config.getLapisSlot());
            if (itemStack == null || itemStack.getType() == Material.AIR) {
                ItemStack[] content = config.getContents(event.getWhoClicked(), data.bookshelves, null, lapis);
                for (int i = 0; i < event.getInventory().getContents().length; i++) {
                    if (i == config.getInputSlot()) continue;
                    if (i == config.getLapisSlot()) continue;
                    event.getInventory().setItem(i, content[i]);
                }
                return;
            };

            ItemStack[] content = config.getContents(event.getWhoClicked(), data.bookshelves, itemStack, lapis);
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

        event.setCancelled(true);
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

        playerData.put(event.getPlayer().getUniqueId(), new Data(event.getClickedBlock()));

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

}

class Data {
    public @Nullable Integer taskId;
    public final int bookshelves;
    public final Location blockLocation;

    Data(Block block) {
        this.taskId = null;
        this.bookshelves = MCCEnchantingTable.getSurroundingBookshelves(block);
        this.blockLocation = block.getLocation();
    }
}
