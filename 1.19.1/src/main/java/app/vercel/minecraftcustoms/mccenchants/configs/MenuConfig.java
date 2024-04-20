package app.vercel.minecraftcustoms.mccenchants.configs;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MenuConfig {

    private final YamlConfig config;

    private ContentItem[] content;

    private @NotNull String title;
    private int size;
    private int inputSlot;
    private int lapisSlot;


    private static class ContentItem {
        final @NotNull ItemStack itemStack;
        final @Nullable ItemStack[] states;

        public ContentItem(@NotNull ItemStack itemStack) {
            this.itemStack = itemStack;
            this.states = null;

        }

        public ContentItem(@NotNull ItemStack itemStack, @Nullable ItemStack[] states) {
            this.itemStack = itemStack;
            this.states = states;

        }

    }
    public MenuConfig(JavaPlugin plugin) {
        this.config = new YamlConfig(plugin, "menu");
        reload();


    }

    public void reload() {
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

    public int getInputSlot() {
        return inputSlot;
    }

    public int getLapisSlot() {
        return lapisSlot;
    }

    private ItemStack[] getStates(String path) {
        ConfigurationSection states = config.getConfig().getConfigurationSection(path + ".states");
        if (states == null) return null;

        ItemStack[] result = new ItemStack[4];

        for (String key : states.getKeys(false)) {
            int i = switch (key) {
                case "enchant_item" -> 0;
                case "missing_levels" -> 1;
                case "unenchantable_item" -> 2;
                case "missing_lapis_lazuli" -> 3;
                default -> -1;
            };

            if (i == -1) {
                // TODO: throw error
                continue;
            }

            // TODO: put placeholders
            result[i] = getItemStack(path + ".states." + key);

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
