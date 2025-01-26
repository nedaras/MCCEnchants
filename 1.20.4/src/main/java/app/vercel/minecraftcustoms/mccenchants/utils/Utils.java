package app.vercel.minecraftcustoms.mccenchants.utils;

import app.vercel.minecraftcustoms.mccenchants.enchantments.CraftMCCEnchantment;
import org.bukkit.enchantments.Enchantment;

public class Utils {

    public static String getEnchantmentName(Enchantment enchantment, int level) {
        return CraftMCCEnchantment.bukkitToCustoms(enchantment).getName() + " " + toRomeNumber(level);

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
}
