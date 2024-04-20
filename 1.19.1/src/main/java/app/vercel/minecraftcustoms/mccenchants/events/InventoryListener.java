package app.vercel.minecraftcustoms.mccenchants.events;

import app.vercel.minecraftcustoms.mccenchants.Main;
import app.vercel.minecraftcustoms.mccenchants.api.helpers.MCCEnchanting;
import app.vercel.minecraftcustoms.mccenchants.api.helpers.MCCInventory;
import app.vercel.minecraftcustoms.mccenchants.configs.MenuConfig;
import app.vercel.minecraftcustoms.mccenchants.enchantments.CraftMCCEnchantment;
import app.vercel.minecraftcustoms.mccenchants.lib.MCCEnchantingTable;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_20_R3.enchantments.CraftEnchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class InventoryListener implements Listener {


    // TODO: someday add PAPI support
    // TODO: using names to identify and inventory is nono
    // TODO: add a check if event is already canceled
    // TODO: check a way to like make slots not interactive


    // first integer is task second is how many books are around
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

        // need a check if state is good
        int b = -1;
        for (Integer i : config.getOutputSlots()) {
            b++;
            if (event.getSlot() == i) {

                ItemStack itemStack = event.getInventory().getItem(i);
                if (itemStack == null) break;
                ItemMeta meta = itemStack.getItemMeta();
                if (meta == null) break;

                ItemStack inputItem = event.getInventory().getItem(config.getInputSlot());
                if (inputItem == null) break;

                Random random = new Random(MCCEnchantingTable.getEnchantingSeed((Player) event.getWhoClicked()) + b);
                System.out.println(b);
                int cost = MCCEnchantingTable.getEnchantingCost(random, b + 1, data.bookshelves, inputItem);
                if (cost < b + 1) break;

                System.out.println(cost);
                for (EnchantmentInstance enchantmentInstance : MCCEnchantingTable.getEnchantments(random, cost, inputItem)) {
                    inputItem.addEnchantment(CraftEnchantment.minecraftToBukkit(enchantmentInstance.enchantment), enchantmentInstance.level);
                }

                MCCInventory.saveAddItemToInventory(event.getWhoClicked(), inputItem);
                event.getInventory().setItem(config.getInputSlot(), null);

                MCCEnchantingTable.updateEnchantingSeed((Player) event.getWhoClicked());

                event.setCancelled(true);
                return;
            }

        }

        // we need early returns lots of them

        ItemStack[] top = cloneContent(event.getView().getTopInventory().getContents());
        ItemStack[] bottom = cloneContent(event.getView().getBottomInventory().getContents());

        ItemStack cursor = event.getView().getCursor() == null ? null : event.getView().getCursor().clone();

        // check in nms for function that would calculate the inventory output
        // that way we will like filter if only that items change which we allow
        // as a workaround i can store player content too and if we need to cancel event we can like reverse both content or sum
        // this mb will be even better cuz we could leave enchanting slots
        // bad cuz we can get new event for click when u know we have runtasklater or sum
        data.taskId = Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
            if (invalidInventory(event.getInventory(), data.state)) {
                event.getView().getTopInventory().setContents(top);
                event.getView().getBottomInventory().setContents(bottom);
                event.getView().setCursor(cursor);
                return;
            }

            // call this only if top inventory updated
            contentUpdated(event.getInventory(), (Player) event.getWhoClicked(), data);
        }, 0).getTaskId();
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
            if (invalidInventory(event.getInventory(), data.state)) {
                event.getView().getTopInventory().setContents(top);
                event.getView().getBottomInventory().setContents(bottom);
                event.getView().setCursor(event.getOldCursor());
                return;
            }

            contentUpdated(event.getInventory(), (Player) event.getWhoClicked(), data);
        }, 0).getTaskId();
    }

    // how we will check placeholders? we need to pass placeholder too
    private boolean invalidInventory(Inventory inventory, @Nullable Integer state) {

        ItemStack[] content = config.getContents(state); // pass here placeholders

        for (int i = 0; i < content.length; i++) {
            if (i == config.getInputSlot() || i == config.getLapisSlot()) continue;
            if (!content[i].equals(inventory.getContents()[i])) return true;
        }

        return false;
    }

    private void contentUpdated(Inventory inventory, Player player, Data data) {
        if (isItemStackEmpty(inventory.getItem(config.getInputSlot()))) {
            data.state = null;
        } else {
            data.state = 0;
        }

        ItemStack[] content = config.getContents(data.state);

        for (int i = 0; i < content.length; i++) {
            if (i == config.getInputSlot() || i == config.getLapisSlot()) continue;
            inventory.setItem(i, content[i]);
        }

//        if (isItemStackEmpty(inventory.getItem(ITEM_SLOT))) {
//            for (Integer i : RESULT_SLOTS) {
//                inventory.setItem(i, new ItemStack(Material.EXPERIENCE_BOTTLE));
//            }
//            return;
//        }
//
//        ItemStack inputItem = Objects.requireNonNull(inventory.getItem(ITEM_SLOT));
//        for (int i = 0; i < RESULT_SLOTS.length; i++) {
//
//            Random random = new Random(MCCEnchantingTable.getEnchantingSeed(player) + i);
//            int cost = MCCEnchantingTable.getEnchantingCost(random, i + 1, bookshelves, inputItem);
//            if (cost < i + 1) {
//                inventory.setItem(RESULT_SLOTS[i], new ItemStack(Material.EXPERIENCE_BOTTLE));
//                continue;
//            }
//
//            EnchantmentInstance enchantmentInstance = getFirstEnchant(random, inputItem, cost);
//            if (enchantmentInstance == null) {
//                inventory.setItem(RESULT_SLOTS[i], new ItemStack(Material.EXPERIENCE_BOTTLE));
//                continue;
//            }
//
//            ItemStack itemStack = new ItemStack(Material.EXPERIENCE_BOTTLE);
//            ItemMeta meta = itemStack.getItemMeta();
//
//            PersistentDataContainer container = meta.getPersistentDataContainer();
//            NamespacedKey key = new NamespacedKey(Main.getInstance(), "enchanting_slot");
//
//            container.set(key, PersistentDataType.BYTE, (byte) i);
//
//            // we will add nbt data that will have like stored enchantment
//            meta.setDisplayName(CraftMCCEnchantment.minecraftToCustoms(enchantmentInstance.enchantment).getKey() + " " + enchantmentInstance.level);
//            itemStack.setItemMeta(meta);
//
//            inventory.setItem(RESULT_SLOTS[i], itemStack);
//        }
    }

    private static boolean isItemStackEmpty(@Nullable ItemStack itemStack) {
        return itemStack == null || itemStack.getType() == Material.AIR;
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
    public @Nullable Integer state;
    public int bookshelves;

    Data(int bookshelves) {
        this.taskId = null;
        this.state = null;
        this.bookshelves = bookshelves;
    }
}
