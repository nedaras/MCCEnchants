package app.vercel.minecraftcustoms.mccenchants.managers;

import app.vercel.minecraftcustoms.mccenchants.api.helpers.MCCInventory;
import app.vercel.minecraftcustoms.mccenchants.lib.MCCGrindstone;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class Grindstone extends CorrespondingInventory {

    private static final @NotNull Set<String> states = new HashSet<>();
    private static final @NotNull Map<UUID, Location> storedBlockLocations = new HashMap<>();

    private int firstSlot;
    private int secondSlot;
    private int combineItemSlot;
    private int outputSlot;

    static {

        states.add("valid_items");
        states.add("invalid_items");
        states.add("first_item_valid");
        states.add("second_item_valid");

    }

    public Grindstone(@NotNull JavaPlugin plugin) {
        super(plugin, states, Material.GRINDSTONE);

        if (this.getCheckupSlots().size() >= 2) {

            firstSlot = this.getCheckupSlots().get(0);
            secondSlot = this.getCheckupSlots().get(1);

            return;
        }
        this.getPlugin().getLogger().warning("You need least two empty slots, so anvil could work correctly!");

    }

    @Override
    public void registerFunction(@NotNull String function, int slot) {
        if (function.equals("combine_items")) combineItemSlot = slot;
        if (function.equals("display_item")) outputSlot = slot;

    }

    @Override
    public void onInventoryOpen(Player player, Block block) {
        storedBlockLocations.put(player.getUniqueId(), block.getLocation());

    }

    @Override
    public void onInventoryClose(Player player) {
        storedBlockLocations.remove(player.getUniqueId());

    }

    @Override
    public void onInventoryClickEvent(InventoryClickEvent event, ItemStack item) {

        if (event.getClickedInventory() == null) return;
        if (event.getClickedInventory().getType() == InventoryType.PLAYER) return;
        if (item == null) return;

        Player player = (Player) event.getWhoClicked();

        event.setCancelled(!this.getCheckupSlots().contains(event.getSlot()));

        ItemStack firstItem = event.getInventory().getItem(firstSlot);
        ItemStack secondItem = event.getInventory().getItem(secondSlot);

        if (this.isPlace(event)) {

            if (event.getSlot() == firstSlot) firstItem = item;
            if (event.getSlot() == secondSlot) secondItem = item;

            if (MCCGrindstone.isCompatible(firstItem, secondItem)) {

                this.setState(event, "valid_items");

                if (firstItem != null) this.setIndividualState(event, "first_item_valid");
                if (secondItem != null) this.setIndividualState(event, "second_item_valid");

                event.getInventory().setItem(outputSlot, MCCGrindstone.combineItems(firstItem, secondItem));

                return;

            }

            this.setState(event, "invalid_items");

            if (firstItem != null) this.clearIndividualState(event, "first_item_valid");
            if (secondItem != null) this.clearIndividualState(event, "second_item_valid");

            return;

        }

        if (event.getSlot() == firstSlot) {

            if (MCCGrindstone.isCompatible(null, secondItem)) {

                this.setState(event, "valid_items");

                this.setIndividualState(event, "second_item_valid");

                event.getInventory().setItem(outputSlot, MCCGrindstone.combineItems(null, secondItem));

                return;

            }

            this.setState(event, "invalid_items");

            this.clearIndividualState(event, "second_item_valid");

            return;

        }

        if (event.getSlot() == secondSlot) {

            if (MCCGrindstone.isCompatible(firstItem, null)) {

                this.setState(event, "valid_items");

                this.setIndividualState(event, "first_item_valid");

                event.getInventory().setItem(outputSlot, MCCGrindstone.combineItems(firstItem, null));

                return;

            }

            this.setState(event, "invalid_items");

            this.clearIndividualState(event, "first_item_valid");

            return;

        }

        if (event.getSlot() != combineItemSlot) return;
        if (!MCCGrindstone.isCompatible(firstItem, secondItem)) return;

        MCCInventory.saveAddItemToInventory(player, MCCGrindstone.combineItems(firstItem, secondItem));

        event.getInventory().setItem(outputSlot, this.getInitialItem(outputSlot));
        event.getInventory().setItem(firstSlot, null);
        event.getInventory().setItem(secondSlot, null);

        MCCGrindstone.dropExperienceOrbs(getBlockLocation(player), firstItem, secondItem);
        player.playSound(player.getLocation(), Sound.BLOCK_GRINDSTONE_USE, 1f, 1f);

        this.clearState(event);

        this.clearIndividualState(event, "first_item_valid");
        this.clearIndividualState(event, "second_item_valid");

    }

    private @NotNull Location getBlockLocation(@NotNull Player player) {
        return storedBlockLocations.get(player.getUniqueId());

    }

}
