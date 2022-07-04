package app.vercel.minecraftcustoms.mccenchants.api.helpers;

import app.vercel.minecraftcustoms.mccenchants.api.enchantments.MCCEnchantment;
import app.vercel.minecraftcustoms.mccenchants.enchantments.CraftMCCEnchantment;
import app.vercel.minecraftcustoms.mccenchants.utils.Utils;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Objects;

public class MCCEnchanting {

    public static void setEnchantment(@NotNull ItemStack target, @NotNull Enchantment enchantment, int level) {
        setEnchantment(target, MCCEnchantment.toMCCEnchantment(enchantment), level);

    }

    public static void setEnchantment(@NotNull ItemStack target, @NotNull MCCEnchantment enchantment, int level) {

        boolean isEnchantingBook = target.getType() == Material.ENCHANTED_BOOK;
        Enchantment craftEnchantment = MCCEnchantment.toEnchantment(enchantment);

        Map<Enchantment, Integer> enchantments = getEnchantments(target);
        Utils.convertEnchantsToLore(target);

        if (enchantments.containsKey(craftEnchantment)) {

            if (isEnchantingBook) {

                EnchantmentStorageMeta meta = (EnchantmentStorageMeta) Objects.requireNonNull(target.getItemMeta());

                meta.removeStoredEnchant(craftEnchantment);
                target.setItemMeta(meta);

            }
            else target.removeEnchantment(craftEnchantment);

            Utils.removeEnchantmentLore(target, enchantment, enchantments.get(craftEnchantment));

        }

        if (isEnchantingBook) {

            EnchantmentStorageMeta meta = (EnchantmentStorageMeta) Objects.requireNonNull(target.getItemMeta());

            meta.addStoredEnchant(craftEnchantment, level, false);
            meta.addItemFlags(ItemFlag.values());

            target.setItemMeta(meta);

            Utils.addEnchantmentLore(target, enchantment, level);

            return;

        }

        target.addEnchantment(craftEnchantment, level);
        ItemMeta meta = Objects.requireNonNull(target.getItemMeta());

        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

        target.setItemMeta(meta);

        Utils.addEnchantmentLore(target, enchantment, level);

    }

    public static void removeEnchantment(@NotNull ItemStack target, @NotNull Enchantment enchantment) {
        removeEnchantment(target, MCCEnchantment.toMCCEnchantment(enchantment));

    }

    public static void removeEnchantment(@NotNull ItemStack target, @NotNull MCCEnchantment enchantment) {

        boolean isEnchantingBook = target.getType() == Material.ENCHANTED_BOOK;
        Enchantment craftEnchantment = CraftMCCEnchantment.toEnchantment(enchantment);

        Map<Enchantment, Integer> enchantments = getEnchantments(target);
        Utils.convertEnchantsToLore(target);

        if (isEnchantingBook) {

            EnchantmentStorageMeta meta = (EnchantmentStorageMeta) Objects.requireNonNull(target.getItemMeta());

            meta.removeStoredEnchant(craftEnchantment);
            target.setItemMeta(meta);

        }
        else target.removeEnchantment(craftEnchantment);

        Utils.removeEnchantmentLore(target, enchantment, enchantments.get(craftEnchantment));

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
