package app.vercel.minecraftcustoms.mccenchants.api.helpers;

import org.bukkit.Location;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class MCCInventory {

    public static void saveAddItemToInventory(@NotNull HumanEntity player, @Nullable ItemStack item) {
        if  (item == null) return;
        if (addedItemToOffHand(player, item)) return;

        Map<Integer, ItemStack> remainingItems = player.getInventory().addItem(item);

        for (Map.Entry<Integer, ItemStack> entry : remainingItems.entrySet()) {

            Vector velocity = player.getLocation().getDirection().multiply(0.25).setY(0);
            Item droppedItem = player.getWorld().dropItem(player.getLocation().add(0, 1.5f, 0), entry.getValue());

            droppedItem.setVelocity(velocity);

        }

    }

    public static void saveAddItemToInventory(@NotNull HumanEntity player, @NotNull Location location, @Nullable ItemStack item) {
        if (item == null) return;
        if (addedItemToOffHand(player, item)) return;

        Map<Integer, ItemStack> remainingItems = player.getInventory().addItem(item);

        for (Map.Entry<Integer, ItemStack> entry : remainingItems.entrySet()) {

            player.getWorld().dropItemNaturally(location, entry.getValue());

        }

    }

    private static boolean addedItemToOffHand(@NotNull HumanEntity player, @NotNull ItemStack item) {

        if (!player.getInventory().getItemInOffHand().isSimilar(item)) return false;

        int amount = player.getInventory().getItemInOffHand().getAmount();
        if (amount >= item.getMaxStackSize()) return false;

        player.getInventory().getItemInOffHand().setAmount(amount + 1);

        return true;

    }

}
