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

    // TODO: refactor code there are a lot of functions repeating it self

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
    public void onInventoryClickEvent(InventoryClickEvent event, ItemStack item) {

        // TODO: we need shift click checks

        if (event.getClickedInventory() == null) return;
        if (event.getClickedInventory().getType() == InventoryType.PLAYER) return;
        if (item == null) return;

        Player player = (Player) event.getWhoClicked();

        event.setCancelled(!this.getCheckupSlots().contains(event.getSlot()));

        if (event.getSlot() == inputSlot) {

            if (isPickup(event)) {

                storedEnchantableSlots.put(player.getUniqueId(), new HashMap<>());
                this.clearState(event);

                return;

            }

            if (MCCEnchantingTable.canEnchantItem(item)) {

                int bookshelves = getBookshelves(player);

                ItemStack lapisLazuli = event.getInventory().getItem(lapisLazuliSlot);
                boolean isLapisLazuli = lapisLazuli != null && lapisLazuli.getType() == Material.LAPIS_LAZULI;

                for (int enchantingSlot = 1; enchantingSlot <= 3; enchantingSlot++) {

                    Random random = new Random(MCCEnchantingTable.getEnchantingSeed(player) + (enchantingSlot - 1));

                    int cost = MCCEnchantingTable.getEnchantingCost(random, enchantingSlot, bookshelves, item);
                    if (cost < enchantingSlot) continue;

                    MCCEnchantmentInstance enchantment = getFirstEnchant(random, item, cost);
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

                    if (player.getGameMode() != GameMode.CREATIVE && lapisLazuliSlot > 1) {

                        if (!isLapisLazuli || lapisLazuli.getAmount() < enchantingSlot) {

                            inventoryItem = this.getSavedItemsWithFunction("missing_lapis_lazuli", "enchant_" + enchantingSlot);
                            if (inventoryItem == null) continue;

                            event.getInventory().setItem(inventoryItem.getSlot(), Utils.setPlaceholders(inventoryItem, placeholders));

                            continue;

                        }

                    }

                    Map<Integer, Integer> enchantableSlots = storedEnchantableSlots.get(player.getUniqueId());
                    enchantableSlots.put(inventoryItem.getSlot(), enchantingSlot);

                    storedEnchantableSlots.put(player.getUniqueId(), enchantableSlots);

                    event.getInventory().setItem(inventoryItem.getSlot(), Utils.setPlaceholders(inventoryItem, placeholders));

                }

                return;

            }

            this.setState(event, "unenchantable_item");
            return;

        }

        if (event.getSlot() == lapisLazuliSlot) {

            ItemStack itemToEnchant = event.getInventory().getItem(inputSlot);

            if (itemToEnchant == null || !MCCEnchantingTable.canEnchantItem(itemToEnchant)) return;

            if (isPickup(event)) {

                int bookshelves = getBookshelves(player);

                for (int enchantingSlot = 1; enchantingSlot <= 3; enchantingSlot++) {

                    Random random = new Random(MCCEnchantingTable.getEnchantingSeed(player) + (enchantingSlot - 1));

                    int cost = MCCEnchantingTable.getEnchantingCost(random, enchantingSlot, bookshelves, itemToEnchant);
                    if (cost < enchantingSlot) continue;

                    if (player.getGameMode() != GameMode.CREATIVE && player.getLevel() < cost) return;

                    MCCEnchantmentInstance enchantment = getFirstEnchant(random, itemToEnchant, cost);
                    if (enchantment == null) continue;

                    InventoryItemStack inventoryItem = this.getSavedItemsWithFunction("enchant_item", "enchant_" + enchantingSlot);
                    if (inventoryItem == null) continue;

                    Map<String, String> placeholders = new HashMap<>();

                    placeholders.put("level", cost + "");
                    placeholders.put("enchantment", enchantment.getEnchantment().getDisplayName(enchantment.getLevel()));

                    inventoryItem = this.getSavedItemsWithFunction("missing_lapis_lazuli", "enchant_" + enchantingSlot);
                    if (inventoryItem == null) continue;

                    event.getInventory().setItem(inventoryItem.getSlot(), Utils.setPlaceholders(inventoryItem, placeholders));

                }

                return;

            }

            int bookshelves = getBookshelves(player);

            int placedLapisAmount = event.getInventory().getItem(lapisLazuliSlot) != null && event.getInventory().getItem(lapisLazuliSlot).getType() == Material.LAPIS_LAZULI ? event.getInventory().getItem(lapisLazuliSlot).getAmount() : 0;
            int amount = event.getAction() == InventoryAction.PLACE_ONE ? placedLapisAmount + 1 : item.getAmount() + placedLapisAmount;

            for (int enchantingSlot = 1; enchantingSlot <= 3; enchantingSlot++) {

                Random random = new Random(MCCEnchantingTable.getEnchantingSeed(player) + (enchantingSlot - 1));

                int cost = MCCEnchantingTable.getEnchantingCost(random, enchantingSlot, bookshelves, itemToEnchant);
                if (cost < enchantingSlot) continue;

                if (player.getGameMode() != GameMode.CREATIVE && player.getLevel() < cost) return;

                MCCEnchantmentInstance enchantment = getFirstEnchant(random, itemToEnchant, cost);
                if (enchantment == null) continue;

                InventoryItemStack inventoryItem = this.getSavedItemsWithFunction("enchant_item", "enchant_" + enchantingSlot);
                if (inventoryItem == null) continue;

                Map<String, String> placeholders = new HashMap<>();

                placeholders.put("level", cost + "");
                placeholders.put("enchantment", enchantment.getEnchantment().getDisplayName(enchantment.getLevel()));

                if (player.getGameMode() != GameMode.CREATIVE) {

                    if (item.getType() != Material.LAPIS_LAZULI || amount < enchantingSlot) {

                        inventoryItem = this.getSavedItemsWithFunction("missing_lapis_lazuli", "enchant_" + enchantingSlot);
                        if (inventoryItem == null) continue;

                        event.getInventory().setItem(inventoryItem.getSlot(), Utils.setPlaceholders(inventoryItem, placeholders));

                        continue;

                    }

                }

                Map<Integer, Integer> enchantableSlots = storedEnchantableSlots.get(player.getUniqueId());
                enchantableSlots.put(inventoryItem.getSlot(), enchantingSlot);

                storedEnchantableSlots.put(player.getUniqueId(), enchantableSlots);

                event.getInventory().setItem(inventoryItem.getSlot(), Utils.setPlaceholders(inventoryItem, placeholders));

            }

            return;

        }

        Map<Integer, Integer> enchantableSlots = storedEnchantableSlots.get(player.getUniqueId());
        ItemStack itemStack = event.getInventory().getItem(inputSlot);

        if (itemStack == null) return;
        if (this.isPlace(event)) return;
        if (itemStack.getAmount() > 1) return;
        if (!enchantableSlots.containsKey(event.getSlot())) return;

        int enchantingSlot = enchantableSlots.get(event.getSlot());
        int bookshelves = getBookshelves(player);

        Random random = new Random(MCCEnchantingTable.getEnchantingSeed(player) + (enchantingSlot - 1));

        int cost = MCCEnchantingTable.getEnchantingCost(random, enchantingSlot, bookshelves, itemStack);
        ItemStack enchantedItem = MCCEnchantingTable.enchantItem(random, cost, itemStack);

        if (player.getGameMode() != GameMode.CREATIVE) player.setLevel(player.getLevel() - enchantingSlot);
        event.getInventory().setItem(inputSlot, null);

        if (lapisLazuliSlot > 1) {

            ItemStack lapis = event.getInventory().getItem(lapisLazuliSlot);

            if (lapis.getAmount() == enchantingSlot) event.getInventory().setItem(lapisLazuliSlot, null);
            else lapis.setAmount(lapis.getAmount() - enchantingSlot);

        }

        MCCInventory.saveAddItemToInventory(player, enchantedItem);
        player.playSound(player.getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1f, 1f);
        MCCEnchantingTable.updateEnchantingSeed(player);

        storedEnchantableSlots.put(player.getUniqueId(), new HashMap<>());
        this.clearState(event);

    }

    private int getBookshelves(Player player) {
        return storedBookshelves.get(player.getUniqueId());

    }

    private @Nullable MCCEnchantmentInstance getFirstEnchant(@NotNull Random random, @NotNull ItemStack item, int cost) {

        List<MCCEnchantmentInstance> enchantments = MCCEnchantingTable.getEnchantments(random, cost, item);
        return enchantments.isEmpty() ? null : enchantments.get(0);

    }

}
