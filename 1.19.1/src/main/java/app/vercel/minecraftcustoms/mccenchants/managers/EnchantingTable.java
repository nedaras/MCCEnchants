package app.vercel.minecraftcustoms.mccenchants.managers;

import app.vercel.minecraftcustoms.mccenchants.api.helpers.MCCInventory;
import app.vercel.minecraftcustoms.mccenchants.lib.MCCEnchantingTable;
import app.vercel.minecraftcustoms.mccenchants.utils.MCCEnchantmentInstance;
import app.vercel.minecraftcustoms.mccenchants.utils.Utils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public final class EnchantingTable extends CorrespondingInventory {

    private static final @NotNull Set<String> states = new HashSet<>();

    private static final @NotNull Map<UUID, Integer> storedBookshelves = new HashMap<>();
    private static final @NotNull Map<UUID, Map<Integer, Integer>> storedEnchantableSlots = new HashMap<>();
    private static int inputSlot = -1;
    private static int lapisLazuliSlot = -1;

    static {

        states.add("enchant_item");
        states.add("missing_levels");
        states.add("unenchantable_item");
        states.add("missing_lapis_lazuli");

    }

    public EnchantingTable(@NotNull JavaPlugin plugin) {
        super(plugin, states, Material.ENCHANTING_TABLE);

    }

    @Override
    public void registerFunction(@NotNull String function, int slot) {

        if (function.equals("input")) {

            inputSlot = slot;
            this.getCheckupSlots().add(inputSlot);

        }
        if (function.equals("lapis_lazuli")) {

            lapisLazuliSlot = slot;
            this.getCheckupSlots().add(lapisLazuliSlot);

        }

    }

    @Override
    public void onInventoryOpen(Player player, Block block) {
        storedBookshelves.put(player.getUniqueId(), MCCEnchantingTable.getSurroundingBookshelves(block));
        storedEnchantableSlots.put(player.getUniqueId(), new HashMap<>());

    }

    @Override
    public void onInventoryClose(Player player) {
        storedBookshelves.remove(player.getUniqueId());
        storedEnchantableSlots.remove(player.getUniqueId());

    }

    @Override
    public void onInventoryClickEvent(InventoryClickEvent event) {

        Player player = (Player) event.getWhoClicked();

        ItemStack inputItem = event.getInventory().getItem(inputSlot);
        ItemStack lapisItem = lapisLazuliSlot >= 0 ? event.getInventory().getItem(lapisLazuliSlot) : null;

        if (event.getClickedInventory() == null) return;
        if (event.getClickedInventory().getType() == InventoryType.PLAYER && !event.isShiftClick()) return;

        if (isPickup(event) && inputItem == null) {

            storedEnchantableSlots.put(player.getUniqueId(), new HashMap<>());
            this.clearState(event);

            return;

        }

        if (!event.isCancelled() && inputItem != null) {

            if (MCCEnchantingTable.canEnchantItem(inputItem)) {

                updateEnchants(event);
                return;

            }

            this.setState(event, "unenchantable_item");
            return;

        }

        Map<Integer, Integer> enchantableSlots = storedEnchantableSlots.get(player.getUniqueId());

        if (inputItem == null) return;
        if (lapisLazuliSlot > 0 && player.getGameMode() != GameMode.CREATIVE && lapisItem == null) return;
        if (this.isPlace(event)) return;
        if (!enchantableSlots.containsKey(event.getSlot())) return;

        int enchantingSlot = enchantableSlots.get(event.getSlot());
        int bookshelves = getBookshelves(player);

        Random random = new Random(MCCEnchantingTable.getEnchantingSeed(player) + (enchantingSlot - 1));

        int cost = MCCEnchantingTable.getEnchantingCost(random, enchantingSlot, bookshelves, inputItem);
        ItemStack enchantedItem = MCCEnchantingTable.enchantItem(random, cost, inputItem);

        if (player.getGameMode() != GameMode.CREATIVE) player.setLevel(player.getLevel() - enchantingSlot);

        if (inputItem.getAmount() == 1) event.getInventory().setItem(inputSlot, null);
        else inputItem.setAmount(inputItem.getAmount() - 1);

        if (player.getGameMode() != GameMode.CREATIVE && lapisLazuliSlot > 0) {

            if (lapisItem.getAmount() == enchantingSlot) event.getInventory().setItem(lapisLazuliSlot, null);
            else lapisItem.setAmount(lapisItem.getAmount() - enchantingSlot);

        }

        MCCInventory.saveAddItemToInventory(player, enchantedItem);
        player.playSound(player.getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1f, 1f);
        MCCEnchantingTable.updateEnchantingSeed(player);

        if (event.getInventory().getItem(inputSlot) != null) {

            updateEnchants(event);
            return;

        }

        storedEnchantableSlots.put(player.getUniqueId(), new HashMap<>());
        this.clearState(event);

    }

    private void updateEnchants(InventoryClickEvent event) {

        Player player = (Player) event.getWhoClicked();

        ItemStack inputItem = event.getInventory().getItem(inputSlot);
        ItemStack lapisItem = lapisLazuliSlot >= 0 ? event.getInventory().getItem(lapisLazuliSlot) : null;

        int bookshelves = getBookshelves(player);

        boolean isLapisLazuli = lapisItem != null && lapisItem.getType() == Material.LAPIS_LAZULI;

        for (int enchantingSlot = 1; enchantingSlot <= 3; enchantingSlot++) {

            Random random = new Random(MCCEnchantingTable.getEnchantingSeed(player) + (enchantingSlot - 1));

            int cost = MCCEnchantingTable.getEnchantingCost(random, enchantingSlot, bookshelves, inputItem);
            if (cost < enchantingSlot) continue;

            MCCEnchantmentInstance enchantment = getFirstEnchant(random, inputItem, cost);
            if (enchantment == null) continue;

            InventoryItemStack inventoryItem = this.getSavedItemsWithFunction("enchant_item", "enchant_" + enchantingSlot);
            if (inventoryItem == null) continue;

            Map<String, String> placeholders = new HashMap<>();

            placeholders.put("level", cost + "");
            placeholders.put("enchantment", enchantment.getEnchantment().getDisplayName(enchantment.getLevel()));

            if (player.getGameMode() != GameMode.CREATIVE && player.getLevel() < cost) {

                inventoryItem = this.getSavedItemsWithFunction("missing_levels", "enchant_" + enchantingSlot);
                if (inventoryItem == null) return;

                event.getInventory().setItem(inventoryItem.getSlot(), Utils.setPlaceholders(inventoryItem, placeholders));

                continue;

            }

            if (player.getGameMode() != GameMode.CREATIVE && lapisLazuliSlot > 0) {

                if (!isLapisLazuli || lapisItem.getAmount() < enchantingSlot) {

                    inventoryItem = this.getSavedItemsWithFunction("missing_lapis_lazuli", "enchant_" + enchantingSlot);
                    if (inventoryItem == null) continue;

                    event.getInventory().setItem(inventoryItem.getSlot(), Utils.setPlaceholders(inventoryItem, placeholders));

                    continue;

                }

            }

            Map<Integer, Integer> enchantableSlots_ = storedEnchantableSlots.get(player.getUniqueId());
            enchantableSlots_.put(inventoryItem.getSlot(), enchantingSlot);

            storedEnchantableSlots.put(player.getUniqueId(), enchantableSlots_);

            event.getInventory().setItem(inventoryItem.getSlot(), Utils.setPlaceholders(inventoryItem, placeholders));

        }

    }

    private int getBookshelves(Player player) {
        return storedBookshelves.get(player.getUniqueId());

    }

    private @Nullable MCCEnchantmentInstance getFirstEnchant(@NotNull Random random, @NotNull ItemStack item, int cost) {

        List<MCCEnchantmentInstance> enchantments = MCCEnchantingTable.getEnchantments(random, cost, item);
        return enchantments.isEmpty() ? null : enchantments.get(0);

    }

}
