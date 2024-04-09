package app.vercel.minecraftcustoms.mccenchants.utils;

import app.vercel.minecraftcustoms.mccenchants.managers.InventoryItemStack;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class Utils {

    public static ItemStack hiddenItemStackName(Material material) {
        ItemStack item = new ItemStack(material);
        if (material == Material.AIR) return item;

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;

        meta.setDisplayName(ChatColor.RESET + "");

        item.setItemMeta(meta);

        return item;

    }

    public static String toRomeNumber(int number) {
        return switch (number) {
            case 1 -> "I";
            case 2 -> "II";
            case 3 -> "III";
            case 4 -> "IV";
            case 5 -> "V";
            case 6 -> "VI";
            default -> number + "";
        };
    }

    public static boolean containsMaterial(@Nullable String material) {
        if (material == null) return false;
        return Material.matchMaterial(material) != null;

    }

    public static boolean isInteger(String number) {

        try {

            Integer.parseInt(number);
            return true;

        } catch (NumberFormatException __) {
            return false;

        }

    }

    public static @NotNull String setPlaceholders(@NotNull String input, Map<String, String> placeholders) {

        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            input = input.replace("%" + entry.getKey() + "%", entry.getValue());

        }

        return input;

    }

    public static @Nullable ItemStack setPlaceholders(@NotNull InventoryItemStack item, Map<String, String> placeholders) {

        if (item.getItemStack() == null) return null;

        ItemStack itemStack = item.getItemStack().clone();
        ItemMeta meta = Objects.requireNonNull(itemStack.getItemMeta());

        meta.setDisplayName(setPlaceholders(meta.getDisplayName(), placeholders));

        if (meta.hasLore()) {

            List<String> lore = Objects.requireNonNull(meta.getLore());

            lore.replaceAll(input -> setPlaceholders(input, placeholders));

            meta.setLore(lore);

        }

        String amount = setPlaceholders(item.getAmount(), placeholders);
        if (Utils.isInteger(amount)) itemStack.setAmount(Integer.parseInt(amount));

        itemStack.setItemMeta(meta);

        return itemStack;

    }

}
