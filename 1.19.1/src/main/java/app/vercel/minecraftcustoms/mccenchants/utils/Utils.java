package app.vercel.minecraftcustoms.mccenchants.utils;

import app.vercel.minecraftcustoms.mccenchants.api.enchantments.MCCEnchantment;
import app.vercel.minecraftcustoms.mccenchants.api.helpers.MCCEnchanting;
import app.vercel.minecraftcustoms.mccenchants.managers.InventoryItemStack;
import net.minecraft.nbt.CompoundTag;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_20_R3.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
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

    public static void convertEnchantsToLore(@NotNull ItemStack item) {

        if (MCCEnchanting.getEnchantments(item).isEmpty()) return;

        ItemMeta meta = isItemStackSinged(item) ? item.getItemMeta() : signItemStack(item);
        Map<Enchantment, Integer> enchants = MCCEnchanting.getEnchantments(item);

        boolean isEnchantmentBook = item.getType() == Material.ENCHANTED_BOOK;
        List<String> lore = new ArrayList<>();

        for (Map.Entry<Enchantment, Integer> entry : enchants.entrySet()) {

            MCCEnchantment mccEnchantment = MCCEnchantment.toMCCEnchantment(entry.getKey());

            if (lore.contains(mccEnchantment.getDisplayName(entry.getValue()))) continue;
            lore.add(mccEnchantment.getDisplayName(entry.getValue()));

        }

        if (isEnchantmentBook) {

            EnchantmentStorageMeta bookMeta = (EnchantmentStorageMeta) meta;

            bookMeta.addItemFlags(ItemFlag.values());
            bookMeta.setLore(lore);

            item.setItemMeta(bookMeta);

            return;

        }

        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.setLore(lore);

        item.setItemMeta(meta);

    }

    private static ItemMeta signItemStack(@NotNull ItemStack item) {
        net.minecraft.world.item.ItemStack craftItem = CraftItemStack.asNMSCopy(item);
        CompoundTag tag = craftItem.getTag();

        // NOTE: WARNINGS
        if (tag != null) {
            tag.putBoolean("MCCEnchantment", true);
            craftItem.setTag(tag);
        }

        return CraftItemStack.asBukkitCopy(craftItem).getItemMeta();
    }

    public static boolean isUpToDate(@NotNull ItemStack item) {

        if (!isItemStackSinged(item)) return false;
        if (!item.getItemMeta().hasLore()) return false;

        for (Map.Entry<Enchantment, Integer> entry : MCCEnchanting.getEnchantments(item).entrySet()) {
            MCCEnchantment enchantment = MCCEnchantment.toMCCEnchantment(entry.getKey());

            if (!item.getItemMeta().getLore().contains(enchantment.getDisplayName(entry.getValue()))) return false;

        }

        return true;

    }

    public static boolean isItemStackSinged(@NotNull ItemStack item) {

        net.minecraft.world.item.ItemStack craftItem = CraftItemStack.asNMSCopy(item);

        if (!craftItem.hasTag()) return false;

        CompoundTag tag = craftItem.getTag();
        // TODO: refactor check if hasTag and tag == != null is same thing
        if (tag == null) return false;

        return tag.contains("MCCEnchantment");

    }


    public static String toRomeNumber(int number) {

        switch (number) {
            case 1: return "I";
            case 2: return "II";
            case 3: return "III";
            case 4: return "IV";
            case 5: return "V";
            case 6: return "VI";
            case 7: return "VII";
            case 8: return "VIII";
            case 9: return "IX";
            case 10: return "X";
            default: return number + "";

        }

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
