package com.github.mccenchants.enchantments;

public enum EnchantmentRarity {
    COMMON(10),
    UNCOMMON(5),
    RARE(2),
    VERY_RARE(1);

    private final int weight;

    EnchantmentRarity(int var2) {
        this.weight = var2;
    }

    public int getWeight() {
        return weight;
    }
}
