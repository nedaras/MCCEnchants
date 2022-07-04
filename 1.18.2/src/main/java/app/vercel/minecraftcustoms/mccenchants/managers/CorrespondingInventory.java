package app.vercel.minecraftcustoms.mccenchants.managers;

import app.vercel.minecraftcustoms.mccenchants.configs.JSONConfig;
import app.vercel.minecraftcustoms.mccenchants.lib.State;
import app.vercel.minecraftcustoms.mccenchants.utils.InventoryManagerUtils;
import app.vercel.minecraftcustoms.mccenchants.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public abstract class CorrespondingInventory implements Listener {

    private final @NotNull Map<@NotNull String, @NotNull Set<InventoryItemStack>> savedItems = new HashMap<>();
    private final @NotNull HashMap<UUID, State<@NotNull String>> playerInventoryStates = new HashMap<>();
    private final JavaPlugin plugin;
    private final Material correspondingBlock;
    private final @NotNull List<Integer> checkupSlots = new ArrayList<>();
    private final @NotNull Set<String> states;
    private final @NotNull Inventory inventory;
    private final @NotNull String inventoryName;

    private int closeSlot = -1;

    public CorrespondingInventory(@NotNull JavaPlugin plugin, @NotNull Set<String> states, @NotNull Material correspondingBlock) {
        this.plugin = plugin;
        this.correspondingBlock = correspondingBlock;
        this.states = states;

        JSONConfig config = new JSONConfig(plugin, "inventories\\" + correspondingBlock.toString().toLowerCase());

        this.inventoryName = Objects.requireNonNull(config.getConfig().getString("inventory_name"));
        this.inventory = createInventory(config);

    }

    private @NotNull Inventory createInventory(@NotNull JSONConfig config) {

        String[] items = Objects.requireNonNull(config.getConfig().getStringList("inventory"));
        Inventory inventory = Bukkit.createInventory(null, items.length, "");

        Set<InventoryItemStack> inventoryItems = new HashSet<>();

        for (int i = 0; i < inventory.getSize(); i++) {

            if (items[i] == null) {
                inventory.setItem(i, null);
                checkupSlots.add(i);
                continue;

            }

            if (InventoryManagerUtils.fixeName(items[i]) == null && Utils.containsMaterial(items[i])) inventory.setItem(i, Utils.hiddenItemStackName(Objects.requireNonNull(Material.matchMaterial(items[i]))));
            else {

                ItemStack item = InventoryManagerUtils.itemStackFromConfig(plugin, config, "custom_items." + InventoryManagerUtils.fixeName(items[i]));
                String amount = config.getConfig().getString("custom_items." + InventoryManagerUtils.fixeName(items[i]) + ".amount", "1");

                inventory.setItem(i, item);
                inventoryItems.add(new InventoryItemStack(item, InventoryManagerUtils.getFunction(items[i]), i, amount));


            }

            saveStates(config, items[i], i);
            String function = InventoryManagerUtils.getFunction(items[i]);

            if (Objects.equals(function, "close")) closeSlot = i;
            else if (function != null) registerFunction(function, i);

        }

        savedItems.put("default", inventoryItems);

        return inventory;

    }

    public void registerFunction(@NotNull String function, int slot) {}

    private void saveStates(@NotNull JSONConfig config, @Nullable String name, int slot) {

        String itemName = InventoryManagerUtils.fixeName(name);

        if (itemName == null) return;
        if (config.getConfig().isNull("custom_items." + itemName + ".states")) return;

        for (String state : states) {

            if (config.getConfig().isNull("custom_items." + itemName + ".states." + state)) continue;
            ItemStack item = InventoryManagerUtils.itemStackFromConfig(plugin, config, "custom_items." + itemName + ".states." + state);
            String amount = config.getConfig().getString("custom_items." + itemName + ".states." + state + ".amount", "1");

            Set<@Nullable InventoryItemStack> items = savedItems.get(state);
            if (items == null) items = new HashSet<>();

            items.add(new InventoryItemStack(item, InventoryManagerUtils.getFunction(name), slot, amount));

            savedItems.put(state, items);

        }

    }

    public void onInventoryOpen(Player player, Block block) {}

    public void onInventoryClose(Player player) {}

    protected @NotNull Inventory openInitialInventory(Player player) {

        Inventory inventory = Bukkit.createInventory(player, this.inventory.getSize(), inventoryName);

        for (int i = 0; i < inventory.getSize(); i++) {
            inventory.setItem(i, this.inventory.getItem(i));

        }

        State<String> state = new State<>("default");
        playerInventoryStates.put(player.getUniqueId(), state);

        player.openInventory(inventory);

        return inventory;

    }

    protected void flushPlayer(Player player) {
        playerInventoryStates.remove(player.getUniqueId());


    }

    public int getCloseSlot() {
        return closeSlot;

    }

    public abstract void onInventoryClickEvent(InventoryClickEvent event, ItemStack item);

    public @NotNull Material getCorrespondingBlock() {
        return correspondingBlock;

    }

    public @NotNull List<Integer> getCheckupSlots() {
        return checkupSlots;

    }

    public @Nullable InventoryItemStack getSavedItemsWithFunction(@Nullable String state, @NotNull String function) {
        if (state == null) state = "default";

        for (InventoryItemStack item : savedItems.get(state)) {
            if (item.getFunction() == null) continue;
            if (!item.getFunction().equals(function)) continue;

            return item;

        }

        return null;

    }

    public void setState(@NotNull InventoryClickEvent event, @NotNull String state) {
        if (!state.equals("default") && getState(event) != null) updateStatedItemStacks(event, "default");
        updateStatedItemStacks(event, state);
        playerInventoryStates.get(event.getWhoClicked().getUniqueId()).setState(state);

    }

    public void setState(@NotNull InventoryClickEvent event, @NotNull String state, Map<String, String> placeholders) {
        if (!state.equals("default") && getState(event) != null) updateStatedItemStacks(event, "default");
        updateStatedItemStacks(event, state, placeholders);
        playerInventoryStates.get(event.getWhoClicked().getUniqueId()).setState(state);

    }

    public void clearState(@NotNull InventoryClickEvent event) {
        setState(event, "default");

    }

    public void setIndividualState(@NotNull InventoryClickEvent event, @NotNull String state) {
        updateStatedItemStacks(event, state);

    }

    public void clearIndividualState(@NotNull InventoryClickEvent event, @NotNull String state) {
        if (!savedItems.containsKey(state)) return;

        for (InventoryItemStack item : savedItems.get(state)) {
            event.getInventory().setItem(item.getSlot(), inventory.getItem(item.getSlot()));

        }

    }

    private void updateStatedItemStacks(@NotNull InventoryClickEvent event, @NotNull String state) {
        if (!savedItems.containsKey(state)) return;

        for (InventoryItemStack item : savedItems.get(state)) {
            event.getInventory().setItem(item.getSlot(), item.getItemStack());

        }

    }

    private void updateStatedItemStacks(@NotNull InventoryClickEvent event, @NotNull String state, Map<String, String> placeholders) {
        if (!savedItems.containsKey(state)) return;

        for (InventoryItemStack item : savedItems.get(state)) {
            event.getInventory().setItem(item.getSlot(), Utils.setPlaceholders(item, placeholders));

        }

    }

    public @Nullable String getState(@NotNull InventoryClickEvent event) {
        String state = playerInventoryStates.get(event.getWhoClicked().getUniqueId()).getState();
        return state.equals("default") ? null : state;

    }

    public boolean isPickup(InventoryClickEvent event) {
        InventoryAction inventoryAction = event.getAction();
        return inventoryAction == InventoryAction.PICKUP_ALL || inventoryAction == InventoryAction.PICKUP_HALF || inventoryAction == InventoryAction.PICKUP_ONE || inventoryAction == InventoryAction.PICKUP_SOME;

    }

    public boolean isPlace(InventoryClickEvent event) {
        InventoryAction inventoryAction = event.getAction();
        return inventoryAction == InventoryAction.PLACE_ALL || inventoryAction == InventoryAction.PLACE_ONE || inventoryAction == InventoryAction.PLACE_SOME || inventoryAction == InventoryAction.SWAP_WITH_CURSOR;

    }

    public @NotNull JavaPlugin getPlugin() {
        return plugin;

    }

    public @Nullable ItemStack getInitialItem(int slot) {
        return inventory.getItem(slot);

    }

}
