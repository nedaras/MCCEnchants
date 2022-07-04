package app.vercel.minecraftcustoms.mccenchants.utils;

import app.vercel.minecraftcustoms.mccenchants.configs.JSONConfig;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class InventoryManagerUtils {

    public static @NotNull ItemStack itemStackFromConfig(@NotNull JavaPlugin plugin, @NotNull JSONConfig config, @NotNull String path) {

        String itemName = path.split("\\.")[path.split("\\.").length - 1];

        if (config.getConfig().isNull(path)) {
            plugin.getLogger().warning("Custom item not found: " + itemName + " - " + config.getDataFolder());
            return new ItemStack(Material.STONE);
        }

        Material material = getMaterial(plugin, config, path);
        int amount = Integer.parseInt(getAmount(config, path));

        String displayName = config.getConfig().getString(path + ".display_name", "");
        List<String> lore = getLore(config, path);
        boolean glow = config.getConfig().getBoolean(path + ".glow");

        ItemStack item = new ItemStack(material, amount);
        ItemMeta meta = Objects.requireNonNull(item.getItemMeta());

        meta.setDisplayName(ChatColor.RESET + displayName);
        meta.setLore(lore);

        if (glow) {

            meta.addEnchant(Enchantment.DIG_SPEED, 1, false);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

        }

        item.setItemMeta(meta);

        return item;

    }

     private static @NotNull Material getMaterial(@NotNull JavaPlugin plugin, @NotNull JSONConfig config, @NotNull String path) {

        String material = config.getConfig().getString(path + ".material", "STONE");
        if (Utils.containsMaterial(material)) return Material.matchMaterial(material);

        String itemName = path.split("\\.")[path.split("\\.").length - 1];

        plugin.getLogger().warning("Material doesn't exist: " + itemName + " - " + config.getDataFolder());

        return Material.STONE;

     }

    private static @NotNull String getAmount(@NotNull JSONConfig config, @NotNull String path) {

        String amount = config.getConfig().getString(path + ".amount", "1");
        if (Utils.isInteger(amount)) return amount;

        return "1";

    }

    private static @NotNull List<String> getLore(@NotNull JSONConfig config, @NotNull String path) {

        String[] lore = config.getConfig().getStringList(path + ".lore", new String[]{});
        return Arrays.stream(lore).collect(Collectors.toList());

    }

    public static @Nullable String fixeName(@Nullable String name) {

        if (name == null) return null;
        if (!(name.charAt(0) == '&' || name.charAt(0) == '§')) return null;
        if (name.split(" ").length < 1) return null;

        name = name.split(" ")[0];
        name = name.substring(1);

        return name.charAt(name.length() - 1) == '~' ? name.substring(0, name.length() - 1) : name;

    }

    public static @Nullable String getFunction(@Nullable String name) {

        if (name == null) return null;
        if (fixeName(name) != null && name.charAt(name.length() - 1) == '~') return fixeName(name);
        if (name.split(" ").length < 2) return null;

        return name.split(" ")[1];

    }

}
