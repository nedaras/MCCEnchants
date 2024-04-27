package app.vercel.minecraftcustoms.mccenchants.configs;

import app.vercel.minecraftcustoms.mccenchants.utils.MCCEnchantingTable;
import app.vercel.minecraftcustoms.mccenchants.utils.Utils;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class MenuConfig {

    private final YamlConfig config;
    private ContentItem[] content;
    private String title;
    private int size;
    private int inputSlot;
    private int lapisSlot;
    private final Set<@NotNull Integer> outputSlots = new TreeSet<>();
    private final @NotNull NamespacedKey slotNamespace;

    enum STATE {
        DEFAULT(-1),
        ENCHANT(0),
        UNENCHANTABLE(1),
        MISSING_LEVELS(2),
        MISSING_LAPIS(3);

        public final int state;

        STATE(int state) {
            this.state = state;

        }

    }

    // TODO: add amount placeholders
    // TODO: we need to rewrite for them string amounts
    private static class ContentItem {
        final @NotNull ItemStack itemStack;
        final @Nullable ItemStack[] states;

        public ContentItem(@NotNull ItemStack itemStack, @Nullable ItemStack[] states) {
            this.itemStack = itemStack;
            this.states = states;

        }

        public @NotNull ItemStack getItemStack(@NotNull STATE state, @Nullable Map<String, String> placeholders) {
            ItemStack item = null;

            if (states == null || state == STATE.DEFAULT) item = itemStack;

            if (item == null) {
                item = states[state.state];
                item = item != null ? item : itemStack;
            }

            item = item.clone();

            if (placeholders == null) return item;

            ItemMeta meta = item.getItemMeta();
            if (meta == null) return item;

            String displayName = meta.getDisplayName();
            List<String> lore = meta.getLore();

            if (!displayName.isEmpty()) {
                for (Map.Entry<String, String> entry : placeholders.entrySet()) {
                    displayName = displayName.replaceAll("%" + entry.getKey() + "%", entry.getValue());
                }
                meta.setDisplayName(displayName);
            }

            if (lore != null) {
                for (int i = 0; i < lore.size(); i++) {
                    String out = lore.get(i);
                    for (Map.Entry<String, String> entry : placeholders.entrySet()) {
                        out = out.replaceAll("%" + entry.getKey() + "%", entry.getValue());
                    }
                    lore.set(i, out);
                }
                meta.setLore(lore);
            }

            item.setItemMeta(meta);
            return item;
        }

    }
    public MenuConfig(JavaPlugin plugin) {
        this.config = new YamlConfig(plugin, "menu");
        this.slotNamespace = new NamespacedKey(plugin, "slot");
        reload();

    }

    public void reload() {

        config.reloadConfig();
        outputSlots.clear();

        // TODO: check if size is even logical
        content = new ContentItem[config.getConfig().getInt("size")];

        // or idk add default name RED + NO NAME SET
        title = config.getConfig().getString("menu_title", "Enchantment Table");
        size = config.getConfig().getInt("size");

        inputSlot = config.getConfig().getInt("input_slot");
        lapisSlot = config.getConfig().getInt("lapis_slot");

        for (String key : config.getConfig().getConfigurationSection("items").getKeys(false)) {
            ItemStack itemStack = getItemStack("items." + key);
            ItemStack[] states = getStates("items." + key);

            for (Integer i : getSlots("items." + key)) {
                if (states != null && states[0] != null) {
                    outputSlots.add(i);
                }
                content[i] = new ContentItem(itemStack, states);
            }
        }

        content[inputSlot] = null;
        content[lapisSlot] = null;
    }

    public @NotNull Inventory getInventory(Player owner) {
        // TODO: when using papi we will need to update placeholders
        Inventory inventory = Bukkit.createInventory(owner, size, title);
        for (int i = 0; i < content.length; i++) {
            inventory.setItem(i, content[i] == null ? null : content[i].itemStack);
        }
        return inventory;
    }

    public @NotNull ItemStack[] getContents(HumanEntity player, int bookshelves, @Nullable ItemStack inputItem, @Nullable ItemStack lapisItem) {
        ItemStack[] result = new ItemStack[content.length];

        int slot = 0;
        for (int i = 0; i < content.length; i++) {
            if (content[i] == null) continue;
            if (inputItem == null || content[i].states == null || content[i].states[STATE.ENCHANT.state] == null) {
                result[i] = content[i].getItemStack(STATE.DEFAULT, null);
                continue;
            }

            // TODO: increase slot only for state that has STATE.ENCHANT and display all items to its state
            // Make so other way to find output slots prob somewhere in config
            Random random = new Random(MCCEnchantingTable.getEnchantingSeed(player) + slot++);
            int cost = MCCEnchantingTable.getEnchantingCost(random, slot, bookshelves, inputItem);

            if (!MCCEnchantingTable.canEnchantItem(inputItem)) {
                result[i] = content[i].getItemStack(STATE.UNENCHANTABLE, null);
                continue;
            }

            if (cost < slot) {
                result[i] = content[i].getItemStack(STATE.DEFAULT, null);
                continue;
            }

            EnchantmentInstance enchantmentInstance = getFirstEnchant(random, inputItem, cost);
            if (enchantmentInstance == null) {
                result[i] = content[i].getItemStack(STATE.DEFAULT, null);
                continue;
            }

            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("enchantment", Utils.getEnchantmentName(enchantmentInstance.enchantment, enchantmentInstance.level));
            placeholders.put("levels", slot + "");
            placeholders.put("level", cost + "");

            if (player.getGameMode() != GameMode.CREATIVE) {
                if (cost > ((Player) player).getLevel()) {
                    result[i] = content[i].getItemStack(STATE.MISSING_LEVELS, placeholders);
                    continue;
                }

                if (lapisItem == null || lapisItem.getType() != Material.LAPIS_LAZULI || lapisItem.getAmount() < slot) {
                    result[i] = content[i].getItemStack(STATE.MISSING_LAPIS, placeholders);
                    continue;
                }
            }

            ItemStack resultItem = content[i].getItemStack(STATE.ENCHANT, placeholders);
            ItemMeta meta = resultItem.getItemMeta();

            // TODO: idk throw error??
            if (meta == null) {
                result[i] = content[i].getItemStack(STATE.DEFAULT, null);
                continue;
            }

            meta.getPersistentDataContainer().set(slotNamespace, PersistentDataType.BYTE, (byte) slot);
            resultItem.setItemMeta(meta);

            result[i] = resultItem;

        }
        return result;
    }

    private static @Nullable EnchantmentInstance getFirstEnchant(@NotNull Random random, @NotNull ItemStack item, int cost) {
        List<EnchantmentInstance> enchantments = MCCEnchantingTable.getEnchantments(random, cost, item);
        return enchantments.isEmpty() ? null : enchantments.get(0);

    }

    public @NotNull String getTitle() {
        return title;
    }

    public @NotNull NamespacedKey getSlotNamespace() {
        return slotNamespace;
    }

    public int getInputSlot() {
        return inputSlot;
    }

    public int getLapisSlot() {
        return lapisSlot;
    }

    public Set<@NotNull Integer> getOutputSlots() {
        return outputSlots;
    }

    private ItemStack[] getStates(String path) {
        ConfigurationSection states = config.getConfig().getConfigurationSection(path + ".states");
        if (states == null) return null;

        ItemStack[] result = new ItemStack[4];

        for (String key : states.getKeys(false)) {
            STATE i = switch (key) {
                case "enchant" -> STATE.ENCHANT;
                case "unenchantable" -> STATE.UNENCHANTABLE;
                case "missing_levels" -> STATE.MISSING_LEVELS;
                case "missing_lapis" -> STATE.MISSING_LAPIS;
                default -> STATE.DEFAULT;
            };

            // throw error
            if (i == STATE.DEFAULT) continue;
            result[i.state] = getItemStack(path + ".states." + key);

        }
        return result;
    }

    // TODO: debug errors and move all of this shit out of this
    // TODO: add placeholder map
    private @NotNull ItemStack getItemStack(@NotNull String path) {
        String material = config.getConfig().getString(path + ".material", "STONE");
        int amount = config.getConfig().getInt(path + ".amount", 1);

        try {
            // TODO: add sub id like for leather items and other stuff
            ItemStack itemStack = new ItemStack(Material.valueOf(material), amount);
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

}
