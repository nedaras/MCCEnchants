package com.github.mccenchants.enchantments;

import org.bukkit.Keyed;
import org.bukkit.Translatable;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public abstract class MCCEnchantment implements Keyed, Translatable  {

    @NotNull
    public abstract String getName();

    public abstract int getMaxLevel();

    public abstract int getStartLevel();

    @NotNull
    public abstract EnchantmentRarity getRarity();

    public abstract int getMinCost(int level);

    public abstract int getMaxCost(int level);

    @NotNull
    public abstract EnchantmentTarget getItemTarget();

    public abstract boolean isTreasure();

    public abstract boolean isCursed();

    public abstract boolean isTradable();

    public abstract boolean isDiscoverable();

    public abstract boolean conflictsWith(@NotNull MCCEnchantment enchantment);

    public abstract boolean canEnchantItem(@NotNull ItemStack item);

}
