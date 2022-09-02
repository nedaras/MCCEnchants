package app.vercel.minecraftcustoms.mccenchants.utils;

import app.vercel.minecraftcustoms.mccenchants.api.enchantments.MCCEnchantment;
import app.vercel.minecraftcustoms.mccenchants.api.helpers.MCCEnchanting;
import app.vercel.minecraftcustoms.mccenchants.managers.InventoryItemStack;
import net.minecraft.nbt.NBTTagCompound;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_19_R1.inventory.CraftItemStack;
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
        ItemMeta meta = Objects.requireNonNull(item.getItemMeta());

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

        System.out.println(isItemStackSinged(item));

    }

    private static ItemMeta signItemStack(@NotNull ItemStack item) {

        net.minecraft.world.item.ItemStack craftItem = CraftItemStack.asNMSCopy(item);
        NBTTagCompound tag = craftItem.v();

        tag.a("MCCEnchantment", true);

        craftItem.c(tag);

        return CraftItemStack.asBukkitCopy(craftItem).getItemMeta();

    }

    public static boolean isItemStackSinged(@NotNull ItemStack item) {

        net.minecraft.world.item.ItemStack craftItem = CraftItemStack.asNMSCopy(item);

        if (!craftItem.t()) return false;

        NBTTagCompound tag = craftItem.u();

        return tag.e("MCCEnchantment");

    }

    public static void addEnchantmentLore(@NotNull ItemStack item, @NotNull MCCEnchantment enchantment, int level) {
        ItemMeta meta = Objects.requireNonNull(item.getItemMeta());

        List<String> lore = meta.hasLore() ? Objects.requireNonNull(meta.getLore()) : new ArrayList<>();

        lore.add(MCCEnchanting.getEnchantments(item).size() - 1, enchantment.getDisplayName(level));

        meta.setLore(lore);

        item.setItemMeta(meta);

    }

    public static void removeEnchantmentLore(@NotNull ItemStack item, @NotNull MCCEnchantment enchantment, int level) {

        ItemMeta meta = Objects.requireNonNull(item.getItemMeta());

        if (!meta.hasLore()) return;

        List<String> lore = Objects.requireNonNull(meta.getLore());

        if (!lore.contains(enchantment.getDisplayName(level))) return;

        lore.remove(enchantment.getDisplayName(level));
        meta.setLore(lore);

        item.setItemMeta(meta);

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

    public static List<String> translateAlternateColorCodes(char altColorChar, List<String> list) {

        List<String> stringList = new ArrayList<>();
        list.forEach((string) -> stringList.add(ChatColor.translateAlternateColorCodes(altColorChar, string)));

        return stringList;

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
