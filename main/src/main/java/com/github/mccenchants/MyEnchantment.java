package com.github.mccenchants;

import com.github.mccenchants.enchantments.EnchantmentRarity;
import com.github.mccenchants.enchantments.MCCEnchantment;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class MyEnchantment extends MCCEnchantment {
    @Override
    public @NotNull String getName() {
        return "Test";
    }

    @Override
    public int getMaxLevel() {
        return 1;
    }

    @Override
    public int getStartLevel() {
        return getMaxLevel();
    }

    @Override
    public @NotNull EnchantmentRarity getRarity() {
        return EnchantmentRarity.COMMON;
    }

    @Override
    public int getMinCost(int level) {
        return level * 25;
    }

    @Override
    public int getMaxCost(int level) {
        return getMinCost(level) + 50;
    }

    @Override
    public @NotNull EnchantmentTarget getItemTarget() {
        return EnchantmentTarget.BREAKABLE;
    }

    @Override
    public boolean isTreasure() {
        return false;
    }

    @Override
    public boolean isCursed() {
        return false;
    }

    @Override
    public boolean isTradable() {
        return true;
    }

    @Override
    public boolean isDiscoverable() {
        return true;
    }

    @Override
    public boolean conflictsWith(@NotNull MCCEnchantment enchantment) {
        return false;
    }

    @Override
    public boolean canEnchantItem(@NotNull ItemStack item) {
        return getItemTarget().includes(item);
    }

    @Override
    public @NotNull NamespacedKey getKey() {
        return NamespacedKey.minecraft("test");
    }

    @Override
    public @NotNull String getTranslationKey() {
        return "test";
    }
}
