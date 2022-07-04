package app.vercel.minecraftcustoms.mccenchants.enchantments;

import app.vercel.minecraftcustoms.mccenchants.api.enchantments.EnchantmentRarity;
import app.vercel.minecraftcustoms.mccenchants.api.enchantments.MCCEnchantment;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class MCCEnchantmentWrapper extends MCCEnchantment {

    public MCCEnchantmentWrapper(@NotNull String name) {
        super(NamespacedKey.minecraft(name));
    }

    @NotNull
    public MCCEnchantment getEnchantment() {
        return Objects.requireNonNull(MCCEnchantment.getByKey(this.getKey()));
    }

    @Override
    public int getMaxLevel() {
        return this.getEnchantment().getMaxLevel();
    }

    @Override
    public int getStartLevel() {
        return this.getEnchantment().getStartLevel();
    }

    @Override
    public @NotNull EnchantmentRarity getRarity() { return getEnchantment().getRarity(); }

    @Override
    public int getMinCost(int level) {
        return getEnchantment().getMinCost(level);
    }

    @Override
    public int getMaxCost(int level) {
        return getEnchantment().getMaxCost(level);
    }

    @Override
    @NotNull
    public EnchantmentTarget getItemTarget() {
        return this.getEnchantment().getItemTarget();
    }

    @Override
    public boolean canEnchantItem(@NotNull ItemStack item) {
        return this.getEnchantment().canEnchantItem(item);
    }

    @Override
    @NotNull
    public String getName() {
        return this.getEnchantment().getName();
    }

    @Override
    public boolean isTreasure() {
        return this.getEnchantment().isTreasure();
    }

    @Override
    public boolean isCursed() {
        return this.getEnchantment().isCursed();
    }

    @Override
    public boolean isTradable() {
        return getEnchantment().isTradable();
    }

    @Override
    public boolean isDiscoverable() {
        return getEnchantment().isDiscoverable();
    }

    @Override
    public boolean conflictsWith(@NotNull MCCEnchantment other) {
        return this.getEnchantment().conflictsWith(other);
    }
}
