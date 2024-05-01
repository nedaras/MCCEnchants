package app.vercel.minecraftcustoms.mccenchants.configs;

import app.vercel.minecraftcustoms.mccenchants.Main;
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
import java.util.logging.Level;

public class MenuConfig {

    private final JavaPlugin plugin;
    private final String fileName;
    private final YamlConfig config;
    private ContentItem[] content;
    private String title;
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

            NamespacedKey namespacedKey = new NamespacedKey(Main.getInstance(), "amount");
            String amount = meta.getPersistentDataContainer().get(namespacedKey, PersistentDataType.STRING);
            if (amount != null) {
                for (Map.Entry<String, String> entry : placeholders.entrySet()) {
                    amount = amount .replaceAll("%" + entry.getKey() + "%", entry.getValue());
                }
                try {
                    item.setAmount(Integer.parseInt(amount));
                } catch (IllegalArgumentException __) {
                    Main.getInstance().getLogger().log(Level.SEVERE, "[menu.yml] string: \"" + meta.getPersistentDataContainer().get(namespacedKey, PersistentDataType.STRING) + "\" can't be converted to integer.");
                }
                meta.getPersistentDataContainer().remove(namespacedKey);
            }

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
        this.plugin = plugin;
        this.fileName = "menu";
        this.config = new YamlConfig(plugin, this.fileName);
        this.slotNamespace = new NamespacedKey(plugin, "slot");
        reload();

    }

    public void reload() {
        config.reloadConfig();
        outputSlots.clear();

        int size = config.getConfig().getInt("size");
        if (size > 54) {
            plugin.getLogger().log(Level.WARNING, "[" + fileName + ".yml] size: " + size + " is too big the maximum is 54.");
            size = 54;
        }

        if (size % 9 != 0) {
            plugin.getLogger().log(Level.WARNING, "[" + fileName + ".yml] size: " + size + " is not divisible by 9.");
            size -= size % 9;
        }

        content = new ContentItem[size];

        // or idk add default name RED + NO NAME SET
        title = config.getConfig().getString("menu_title", "Enchantment Table");

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
        Inventory inventory = Bukkit.createInventory(owner, content.length, title);
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

            if (meta == null) {
                plugin.getLogger().log(Level.SEVERE, "[" + fileName + ".yml] Could not get item meta from material " + resultItem.getType() + ".");
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

            if (i == STATE.DEFAULT) {
                plugin.getLogger().log(Level.WARNING, "[" + fileName + ".yml] " + path + ".states: state with name " + key + " does not exist.");
                continue;
            }
            result[i.state] = getItemStack(path + ".states." + key);

        }
        return result;
    }

    // TODO: debug errors and move all of this shit out of this
    // TODO: add placeholder map
    private @NotNull ItemStack getItemStack(@NotNull String path) {
        String material = config.getConfig().getString(path + ".material", "STONE");
        String amount = config.getConfig().getString(path + ".amount", "1");

        try {
            // TODO: add sub id like for leather items and other stuff
            ItemStack itemStack = new ItemStack(Material.valueOf(material));
            ItemMeta meta = itemStack.getItemMeta();

            if (meta == null) {
                plugin.getLogger().log(Level.SEVERE, "[" + fileName + ".yml] Could not get item meta from material " + itemStack.getType() + ".");
                return itemStack;
            }

            try {
                itemStack.setAmount(Integer.parseInt(amount));
            } catch (IllegalArgumentException __) {
                if (amount.length() >= 3 && amount.charAt(0) == '%' && amount.charAt(amount.length() - 1) == '%') {
                    NamespacedKey namespacedKey = new NamespacedKey(plugin, "amount");
                    meta.getPersistentDataContainer().set(namespacedKey, PersistentDataType.STRING, amount);
                } else {
                    plugin.getLogger().log(Level.SEVERE, "[" + fileName + ".yml] " + path + ".amount: amount can only be an integer or a placeholder, got: " + amount + ".");
                }
            }

            String displayName = config.getConfig().getString(path + ".display_name");
            List<String> lore = config.getConfig().getStringList(path + ".lore");

            if (displayName != null) meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', displayName));
            meta.setLore(lore.stream().map((line) -> ChatColor.translateAlternateColorCodes('&', line)).toList());

            itemStack.setItemMeta(meta);

            return itemStack;

        } catch (IllegalArgumentException __) {
            // would be cool thing to like set error message in lore.
            plugin.getLogger().log(Level.SEVERE, "[" + fileName + ".yml] " + path + ".material: material named " + material + " does not exist.");
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
                    if (range.length != 2) {
                        plugin.getLogger().log(Level.SEVERE, "[" + fileName + ".yml] " + path + ".slots: invalid range syntax, expected {start}-{end}, got: " + i + ".");
                        continue;
                    };

                    int a = Integer.parseInt(range[0]);
                    int b = Integer.parseInt(range[1]);

                    if (a > b) {
                        plugin.getLogger().log(Level.WARNING, "[" + fileName + ".yml] " + path + ".slots: begin param is bigger then end param.");
                        continue;
                    }

                    for (int j = a; j <= b; j++) {
                        slots.add(j);
                    }

                } catch (IllegalArgumentException ___) {
                    plugin.getLogger().log(Level.SEVERE, "[" + fileName + ".yml] " + path + ".slots: invalid range syntax, ranges params can only have numbers.");
                    return slots;
                }
            }
        }

        return slots;

    }

}
