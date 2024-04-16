package app.vercel.minecraftcustoms.mccenchants.events;

import app.vercel.minecraftcustoms.mccenchants.Main;
import net.minecraft.world.item.Item;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public class InventoryListener implements Listener {


    // TODO: using names to identify and inventory is nono
    // TODO: add a check if event is already canceled
    // TODO: check a way to like make slots not interactive

    private static @Nullable BukkitTask task;
    private static final ItemStack[] content = new ItemStack[9 * 6];
    private static final int ITEM_SLOT = 12;

    private static final int LAPIS_SLOT = 14;
    private static final int[] RESULT_SLOTS = { 29, 31, 33 };

    static {
        Arrays.fill(content, new ItemStack(Material.YELLOW_STAINED_GLASS_PANE));

        content[ITEM_SLOT] = null;
        content[LAPIS_SLOT] = null;

        for (int slot : RESULT_SLOTS) {
            content[slot] = null;
        }

    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getClickedInventory() == null) return;
        if (!event.getView().getTitle().equals("Enchanting Table")) return;
        // this so we would not race events
        if (task != null && Bukkit.getScheduler().isQueued(task.getTaskId())) {
            event.setCancelled(true);
            return;
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
        task = Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
            if (invalidInventory(event.getInventory())) {
                event.getView().getTopInventory().setContents(top);
                event.getView().getBottomInventory().setContents(bottom);
                event.getView().setCursor(cursor);
                return;
            }

            contentUpdated(event.getInventory());
        }, 0);
    }


    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (!event.getView().getTitle().equals("Enchanting Table")) return;
        if (task != null && Bukkit.getScheduler().isQueued(task.getTaskId())) {
            event.setCancelled(true);
            return;
        }

        ItemStack[] top = cloneContent(event.getView().getTopInventory().getContents());
        ItemStack[] bottom = cloneContent(event.getView().getBottomInventory().getContents());

        task = Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
            if (invalidInventory(event.getInventory())) {
                event.getView().getTopInventory().setContents(top);
                event.getView().getBottomInventory().setContents(bottom);
                event.getView().setCursor(event.getOldCursor());
                return;
            }

            contentUpdated(event.getInventory());
        }, 0);
    }

    private boolean invalidInventory(Inventory inventory) {
        if (inventory.getContents().length != content.length) return true;

        for (int i = 0; i < inventory.getContents().length; i++) {
            if (content[i] == null) continue;
            if (!content[i].equals(inventory.getContents()[i])) return true;
        }

        return false;
    }

    private void contentUpdated(Inventory inventory) {
        System.out.println(Arrays.toString(inventory.getContents()));

        if (isItemStackEmpty(inventory.getItem(ITEM_SLOT))) {
            inventory.setItem(RESULT_SLOTS[0], null);
            return;
        }

        inventory.setItem(RESULT_SLOTS[0], new ItemStack(Material.BOOK));

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
    public void onInventoryMoveItem(InventoryMoveItemEvent event) {
        //System.out.println(event.getClass().getSimpleName());

    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        //System.out.println(event.getClass().getSimpleName());

        if (event.isCancelled()) return;
        if (event.getInventory().getType() != InventoryType.ENCHANTING) return;

        Inventory inventory =  Bukkit.createInventory(event.getPlayer(), content.length, "Enchanting Table");
        inventory.setContents(content);

        event.getPlayer().openInventory(inventory);
        event.setCancelled(true);

    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        //System.out.println(event.getClass().getSimpleName());

    }
}
