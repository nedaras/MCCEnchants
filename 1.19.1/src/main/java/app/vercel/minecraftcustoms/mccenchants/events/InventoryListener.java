package app.vercel.minecraftcustoms.mccenchants.events;

import app.vercel.minecraftcustoms.mccenchants.Main;
import net.minecraft.world.inventory.ChestMenu;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_20_R3.inventory.CraftInventoryView;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public class InventoryListener implements Listener {


    // TODO: using names to identify and inventory is nono
    // TODO: add a check if event is already canceled

    private static @Nullable BukkitTask task;

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getClickedInventory() == null) return;
        if (!event.getView().getTitle().equals("Enchanting Table")) return;
        // this so we would not race events
        if (task != null && Bukkit.getScheduler().isQueued(task.getTaskId())) {
            event.setCancelled(true);
            return;
        }


        ItemStack[] top = event.getView().getTopInventory().getContents();
        ItemStack[] bottom = event.getView().getBottomInventory().getContents();

        for (int i = 0; i < top.length; i++) {
            if (top[i] != null) top[i] = top[i].clone();
        }

        for (int i = 0; i < bottom.length; i++) {
            if (bottom[i] != null) bottom[i] = bottom[i].clone();
        }

        ItemStack item = event.getView().getCursor() == null ? null : event.getView().getCursor().clone();

        // check in nms for function that would calculate the inventory output
        // that way we will like filter if only that items change which we allow
        // as a workaround i can store player content too and if we need to cancel event we can like reverse both content or sum
        // this mb will be even better cuz we could leave enchanting slots
        // bad cuz we can get new event for click when u know we have runtasklater or sum
        task = Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
            //if (!Arrays.equals(top, event.getView().getTopInventory().getContents())) {
                //event.getView().getTopInventory().setContents(top);
                //event.getView().getBottomInventory().setContents(bottom);
            //}

            if (event.getClickedInventory().getType() == InventoryType.CHEST) {
                event.getView().getTopInventory().setContents(top);
                event.getView().getBottomInventory().setContents(bottom);
                event.getView().setCursor(item);
            }

            //System.out.println(Arrays.toString(top));
            //System.out.println(Arrays.toString(bottom));

        }, 0);


    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (!event.getView().getTitle().equals("Enchanting Table")) return;
        System.out.println(Arrays.toString(event.getInventory().getContents()));

        Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
            System.out.println(Arrays.toString(event.getInventory().getContents()));
        }, 0);
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

        Inventory inventory =  Bukkit.createInventory(event.getPlayer(), 9, "Enchanting Table");

        ItemStack[] content = new ItemStack[inventory.getSize()];
        Arrays.fill(content, new ItemStack(Material.YELLOW_STAINED_GLASS_PANE));

        inventory.setContents(content);

        event.getPlayer().openInventory(inventory);
        event.setCancelled(true);

    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        //System.out.println(event.getClass().getSimpleName());

    }
}
