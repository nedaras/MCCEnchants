package com.github.mccenchants.nms.v1_20_R3.utils;

import com.github.mccenchants.nms.v1_20_R3.enchantments.CraftMCCEnchantment;
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
