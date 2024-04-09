package app.vercel.minecraftcustoms.mccenchants.enchantments;

import app.vercel.minecraftcustoms.mccenchants.api.enchantments.EnchantmentRarity;
import app.vercel.minecraftcustoms.mccenchants.api.enchantments.MCCEnchantment;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.enchantment.Enchantment;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.craftbukkit.v1_20_R3.CraftRegistry;
import org.bukkit.craftbukkit.v1_20_R3.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_20_R3.util.Handleable;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class CraftMCCEnchantment extends MCCEnchantment implements Handleable<Enchantment> {

    // TODO: would it not be better if we extended Enchantment and CraftEnchantment from bukkit, we're kinda the same just better
    private final NamespacedKey key;
    private final Enchantment handle;
    private final int id;

    public static MCCEnchantment minecraftToBukkit(Enchantment minecraft) {
        org.bukkit.enchantments.Enchantment enchantment = CraftRegistry.minecraftToBukkit(minecraft, Registries.ENCHANTMENT, Registry.ENCHANTMENT);
        return MCCEnchantment.toMCCEnchantment(enchantment);
    }

    public static Enchantment bukkitToMinecraft(MCCEnchantment bukkit) {
        return CraftRegistry.bukkitToMinecraft(bukkit);
    }

    public CraftMCCEnchantment(NamespacedKey key, net.minecraft.world.item.enchantment.Enchantment handle) {
        this.handle = handle;
        this.key = key;
        this.id = BuiltInRegistries.ENCHANTMENT.getId(handle);
    }

    public net.minecraft.world.item.enchantment.Enchantment getHandle() {
        return this.handle;
    }

    @NotNull
    public NamespacedKey getKey() {
        return this.key;
    }

    @Override
    public int getMaxLevel() {
        return this.handle.getMaxLevel();

    }

    @Override
    public int getStartLevel() {
        return this.handle.getMinLevel();
    }

    @Override
    public @NotNull EnchantmentRarity getRarity() {
        return switch (handle.getRarity().getWeight()) {
            case 10 -> EnchantmentRarity.COMMON;
            case 5 -> EnchantmentRarity.UNCOMMON;
            case 2 -> EnchantmentRarity.RARE;
            case 1 -> EnchantmentRarity.VERY_RARE;
            default ->
                    throw new IllegalArgumentException("Enchantment Rarity with weight doesn't exist: " + handle.getRarity().getWeight());
        };
    }

    @Override
    public int getMinCost(int level) {
        return handle.getMinCost(level);

    }

    @Override
    public int getMaxCost(int level) {
        return handle.getMaxCost(level);

    }

    @Override
    public @NotNull EnchantmentTarget getItemTarget() {

        return switch (handle.category.ordinal()) {
            case 0 -> EnchantmentTarget.ARMOR;
            case 1 -> EnchantmentTarget.ARMOR_FEET;
            case 2 -> EnchantmentTarget.ARMOR_LEGS;
            case 3 -> EnchantmentTarget.ARMOR_TORSO;
            case 4 -> EnchantmentTarget.ARMOR_HEAD;
            case 5 -> EnchantmentTarget.WEAPON;
            case 6 -> EnchantmentTarget.TOOL;
            case 7 -> EnchantmentTarget.FISHING_ROD;
            case 8 -> EnchantmentTarget.TRIDENT;
            case 9 -> EnchantmentTarget.BREAKABLE;
            case 10 -> EnchantmentTarget.BOW;
            case 11 -> EnchantmentTarget.WEARABLE;
            case 12 -> EnchantmentTarget.CROSSBOW;
            case 13 -> EnchantmentTarget.VANISHABLE;
            default -> throw new IncompatibleClassChangeError();
        };
    }

    @Override
    public boolean isTreasure() {
        return this.handle.isTreasureOnly();
    }

    @Override
    public boolean isCursed() {
        return this.handle.isCurse();
    }

    @Override
    public boolean isTradable() {
        return handle.isTradeable();

    }

    @Override
    public boolean isDiscoverable() {
        return handle.isDiscoverable();

    }

    @Override
    public boolean canEnchantItem(@NotNull ItemStack item) {
        return this.handle.canEnchant(CraftItemStack.asNMSCopy(item));
    }

    @Override
    public boolean conflictsWith(@NotNull MCCEnchantment other) {
        if (!(other instanceof CraftMCCEnchantment enchantment)) {
            return false;
        } else {
            return !handle.isCompatibleWith(enchantment.handle);
        }
    }


    @NotNull
    public String getTranslationKey() {
        return this.handle.getDescriptionId();
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        } else {
            return !(other instanceof CraftMCCEnchantment) ? false : this.getKey().equals(((MCCEnchantment) other).getKey());
        }
    }
   public int hashCode() {
        return this.key.hashCode();
   }

    @NotNull
    public String toString() {
        return "CraftEnchantment[" + this.getKey() + "]";
    }

}