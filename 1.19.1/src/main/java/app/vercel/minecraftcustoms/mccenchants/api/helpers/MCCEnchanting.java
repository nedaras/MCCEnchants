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

    public static @NotNull Map<Enchantment, Integer> getEnchantments(@NotNull ItemStack target) {

        boolean isEnchantingBook = target.getType() == Material.ENCHANTED_BOOK;
        return isEnchantingBook ? Objects.requireNonNull(((EnchantmentStorageMeta) target.getItemMeta())).getStoredEnchants() :  target.getEnchantments();

    }

}
