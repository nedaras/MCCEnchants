package app.vercel.minecraftcustoms.mccenchants.events;

import app.vercel.minecraftcustoms.mccenchants.Main;
import app.vercel.minecraftcustoms.mccenchants.configs.YamlConfig;
import app.vercel.minecraftcustoms.mccenchants.lib.MCCEnchantingTable;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class InventoryListener implements Listener {


    // TODO: using names to identify and inventory is nono
    // TODO: add a check if event is already canceled
    // TODO: check a way to like make slots not interactive


    // first integer is task second is how many books are around
    private static final Map<UUID, Data> playerData = new HashMap<>();
    private static YamlConfig config;

    private static ItemStack[] content;
    private static int inputSlot;
    private static int lapisSlot;

    public InventoryListener(JavaPlugin plugin) {
        config = new YamlConfig(plugin, "menu");

        content = new ItemStack[config.getConfig().getInt("size", 9)];

        // TODO: throw errors and stuff cuz like if slots are equal or what if its out of bounds and all
        inputSlot = config.getConfig().getInt("input_slot");
        lapisSlot = config.getConfig().getInt("lapis_slot");


        // no error handling
        for (String key : config.getConfig().getConfigurationSection("items").getKeys(false)) {
            ItemStack itemStack = getItemStack("items." + key);
            for (Integer i : getSlots("items." + key)) {
                content[i] = itemStack;
            }
        }

        content[inputSlot] = null;
        content[lapisSlot] = null;

    }

    // TODO: debug errors and move all of this shit out of this
    private @NotNull ItemStack getItemStack(@NotNull String path) {
        String material = config.getConfig().getString(path + ".material", "STONE");

        try {

            ItemStack itemStack = new ItemStack(Material.valueOf(material));
            ItemMeta meta = itemStack.getItemMeta();

            if (meta == null) {
                // print err or sum
                return itemStack;
            }

            String displayName = config.getConfig().getString(path + ".display_name");
            List<String> lore = config.getConfig().getStringList(path + ".lore");

            if (displayName != null) meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', displayName));
            meta.setLore(lore.stream().map((line) -> ChatColor.translateAlternateColorCodes('&', line)).toList());

            itemStack.setItemMeta(meta);

            return itemStack;

        } catch (IllegalArgumentException __) {
            // would be cool thing to like set error message in lore.
            return new ItemStack(Material.STONE);

        }
    }

    private @NotNull Set<Integer> getSlots(@NotNull String path) {

        Set<Integer> slots = new HashSet<>();

        if (config.getConfig().contains(path + ".slot")) slots.add(config.getConfig().getInt(path + ".slot"));
        for (String i : config.getConfig().getStringList(path + ".slots")) {

            try {
                slots.add(Integer.parseInt(i));
            } catch (IllegalArgumentException __) {
                try {
                    String[] range = i.split("-");
                    // TODO: throw error or sum
                    if (range.length != 2) continue;

                    int a = Integer.parseInt(range[0]);
                    int b = Integer.parseInt(range[1]);

                    // throw error
                    if (a > b) continue;

                    for (int j = a; j <= b; j++) {
                        slots.add(j);
                    }

                } catch (IllegalArgumentException ___) {
                    return slots;

                }
            }
        }

        return slots;

    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getClickedInventory() == null) return;
        if (!event.getView().getTitle().equals("Enchanting Table")) return;
        Data data = playerData.get(event.getWhoClicked().getUniqueId());
        // this so we would not race events
        if (data.taskId != null && Bukkit.getScheduler().isQueued(data.taskId)) {
            event.setCancelled(true);
            return;
        }

        NamespacedKey key = new NamespacedKey(Main.getInstance(), "enchanting_slot");
//        for (Integer i : RESULT_SLOTS) {
//            if (event.getSlot() == i) {
//
//                ItemStack itemStack = event.getInventory().getItem(i);
//                if (itemStack == null) break;
//                ItemMeta meta = itemStack.getItemMeta();
//                if (meta == null) break;
//
//                Byte b = meta.getPersistentDataContainer().get(key, PersistentDataType.BYTE);
//                if (b == null) break;
//
//                ItemStack inputItem = event.getInventory().getItem(ITEM_SLOT);
//                if (inputItem == null) break;
//
//                Random random = new Random(MCCEnchantingTable.getEnchantingSeed((Player) event.getWhoClicked()) + b);
//                int cost = MCCEnchantingTable.getEnchantingCost(random, b + 1, data.bookshelves, inputItem);
//                if (cost < b + 1) break;
//
//                for (EnchantmentInstance enchantmentInstance : MCCEnchantingTable.getEnchantments(random, cost, inputItem)) {
//                    event.getWhoClicked().sendMessage(CraftMCCEnchantment.minecraftToCustoms(enchantmentInstance.enchantment).getKey() + " " + enchantmentInstance.level);
//                }
//
//                event.setCancelled(true);
//                return;
//            }
//
//        }

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
            if (invalidInventory(event.getInventory())) {
                event.getView().getTopInventory().setContents(top);
                event.getView().getBottomInventory().setContents(bottom);
                event.getView().setCursor(cursor);
                return;
            }

            // call this only if top inventory updated
            contentUpdated(event.getInventory(), (Player) event.getWhoClicked(), data.bookshelves);
        }, 0).getTaskId();
    }


    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (!event.getView().getTitle().equals("Enchanting Table")) return;

        Data data = playerData.get(event.getWhoClicked().getUniqueId());
        if (data == null) return;

        if (data.taskId != null && Bukkit.getScheduler().isQueued(data.taskId)) {
            event.setCancelled(true);
            return;
        }

        ItemStack[] top = cloneContent(event.getView().getTopInventory().getContents());
        ItemStack[] bottom = cloneContent(event.getView().getBottomInventory().getContents());

        data.taskId = Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
            if (invalidInventory(event.getInventory())) {
                event.getView().getTopInventory().setContents(top);
                event.getView().getBottomInventory().setContents(bottom);
                event.getView().setCursor(event.getOldCursor());
                return;
            }

            contentUpdated(event.getInventory(), (Player) event.getWhoClicked(), data.bookshelves);
        }, 0).getTaskId();
    }

    // this is bad rl bad
    private boolean invalidInventory(Inventory inventory) {
        return false;
    }

    private void contentUpdated(Inventory inventory, Player player, int bookshelves) {

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
    public void onInventoryMoveItem(InventoryMoveItemEvent event) {
        //System.out.println(event.getClass().getSimpleName());

    }

    @EventHandler
    public void onInventoryOpen(PlayerInteractEvent event) {
        if (event.getClickedBlock() == null) return;
        if (event.getClickedBlock().getType() != Material.ENCHANTING_TABLE) return;

        boolean hasItemInHand = event.getPlayer().getInventory().getItemInMainHand().getType() != Material.AIR || event.getPlayer().getInventory().getItemInOffHand().getType() != Material.AIR;
        if (event.getPlayer().isSneaking() && hasItemInHand) return;

        Inventory inventory =  Bukkit.createInventory(event.getPlayer(), content.length, "Enchanting Table");
        inventory.setContents(content);

        playerData.put(event.getPlayer().getUniqueId(), new Data(MCCEnchantingTable.getSurroundingBookshelves(event.getClickedBlock())));

        event.getPlayer().openInventory(inventory);
        event.setCancelled(true);
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!event.getView().getTitle().equals("Enchanting Table")) return;
        playerData.remove(event.getPlayer().getUniqueId());

    }

    private static @Nullable EnchantmentInstance getFirstEnchant(@NotNull Random random, @NotNull ItemStack item, int cost) {
        List<EnchantmentInstance> enchantments = MCCEnchantingTable.getEnchantments(random, cost, item);
        return enchantments.isEmpty() ? null : enchantments.get(0);

    }

}

class Data { public @Nullable Integer taskId;
    public int bookshelves;

    Data(int bookshelves) {
        this.taskId = null;
        this.bookshelves = bookshelves;
    }
}
