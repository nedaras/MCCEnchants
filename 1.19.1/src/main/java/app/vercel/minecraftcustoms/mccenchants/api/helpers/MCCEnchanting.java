package app.vercel.minecraftcustoms.mccenchants.api.helpers;

import app.vercel.minecraftcustoms.mccenchants.api.enchantments.MCCEnchantment;
import app.vercel.minecraftcustoms.mccenchants.utils.Utils;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Objects;

public class MCCEnchanting {

    public static void setEnchantment(@NotNull ItemStack target, @NotNull MCCEnchantment enchantment, int level) {
        setEnchantment(target, MCCEnchantment.toEnchantment(enchantment), level);

    }

    public static void setEnchantment(@NotNull ItemStack target, @NotNull Enchantment enchantment, int level) {

        boolean isEnchantingBook = target.getType() == Material.ENCHANTED_BOOK;

        if (isEnchantingBook) {

            EnchantmentStorageMeta meta = (EnchantmentStorageMeta) target.getItemMeta();
            meta.addStoredEnchant(enchantment, level, false);

            target.setItemMeta(meta);
            Utils.convertEnchantsToLore(target);

            return;

        }

        target.addEnchantment(enchantment, level);
        Utils.convertEnchantsToLore(target);

    }

    public static void removeEnchantment(@NotNull ItemStack target, @NotNull Enchantment enchantment) {

        boolean isEnchantingBook = target.getType() == Material.ENCHANTED_BOOK;

        if (isEnchantingBook) {

            EnchantmentStorageMeta meta = (EnchantmentStorageMeta) target.getItemMeta();
            meta.removeStoredEnchant(enchantment);

            target.setItemMeta(meta);
            Utils.convertEnchantsToLore(target);

            return;

        }

        target.removeEnchantment(enchantment);
        Utils.convertEnchantsToLore(target);

    }

    public static void removeEnchantment(@NotNull ItemStack target, @NotNull MCCEnchantment enchantment) {
        removeEnchantment(target, MCCEnchantment.toEnchantment(enchantment));

    }

    public static @NotNull Map<Enchantment, Integer> getEnchantments(@NotNull ItemStack target) {

        boolean isEnchantingBook = target.getType() == Material.ENCHANTED_BOOK;
        return isEnchantingBook ? Objects.requireNonNull(((EnchantmentStorageMeta) target.getItemMeta())).getStoredEnchants() :  target.getEnchantments();

    }

    public static boolean containsEnchantment(ItemStack item, MCCEnchantment enchantment) {
        return containsEnchantment(item, MCCEnchantment.toEnchantment(enchantment));

    }

    public static boolean containsEnchantment(ItemStack item, Enchantment enchantment) {
        Map<Enchantment, Integer> enchantments = getEnchantments(item);
        return enchantments.containsKey(enchantment);

    }

}
