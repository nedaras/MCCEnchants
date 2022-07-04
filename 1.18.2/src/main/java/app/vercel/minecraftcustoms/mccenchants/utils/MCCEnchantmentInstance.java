package app.vercel.minecraftcustoms.mccenchants.utils;

import app.vercel.minecraftcustoms.mccenchants.api.enchantments.MCCEnchantment;

public class MCCEnchantmentInstance {

    private final MCCEnchantment enchantment;
    private final int level;

    public MCCEnchantmentInstance(MCCEnchantment enchantment, int level) {
        this.enchantment = enchantment;
        this.level = level;

    }

    public MCCEnchantment getEnchantment() {
        return enchantment;

    }

    public int getLevel() {
        return level;

    }

}
