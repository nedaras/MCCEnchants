package app.vercel.minecraftcustoms.mccenchants.enchantments;

import app.vercel.minecraftcustoms.mccenchants.Main;
import app.vercel.minecraftcustoms.mccenchants.api.enchantments.EnchantmentRarity;
import app.vercel.minecraftcustoms.mccenchants.api.enchantments.MCCEnchantment;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_20_R3.inventory.CraftItemStack;
import org.bukkit.enchantments.EnchantmentTarget;
import org.jetbrains.annotations.NotNull;

public class NMSEnchantment extends Enchantment {

    private final MCCEnchantment enchantment;

    private static @NotNull Rarity toNMSRarity(@NotNull EnchantmentRarity rarity) {

        switch (rarity) {
            case COMMON: return Rarity.COMMON;
            case UNCOMMON: return Rarity.UNCOMMON;
            case RARE: return Rarity.RARE;
            case VERY_RARE: return Rarity.VERY_RARE;
            default: return  Rarity.VERY_RARE; // TODO: print error
        }
    }

    private static @NotNull EnchantmentCategory toNMSEnchantmentSlotType(@NotNull EnchantmentTarget target) {

        switch (target) {
            case ARMOR: return EnchantmentCategory.ARMOR;
            case ARMOR_FEET: return EnchantmentCategory.ARMOR_FEET;
            case ARMOR_LEGS: return EnchantmentCategory.ARMOR_LEGS;
            case ARMOR_TORSO: return EnchantmentCategory.ARMOR_CHEST;
            case ARMOR_HEAD: return EnchantmentCategory.ARMOR_HEAD;
            case WEAPON: return  EnchantmentCategory.WEAPON;
            case TOOL: return EnchantmentCategory.DIGGER;
            case FISHING_ROD: return EnchantmentCategory.FISHING_ROD;
            case TRIDENT: return EnchantmentCategory.TRIDENT;
            case BREAKABLE: return EnchantmentCategory.BREAKABLE;
            case BOW: return EnchantmentCategory.BOW;
            case WEARABLE: return EnchantmentCategory.WEARABLE;
            case CROSSBOW: return EnchantmentCategory.CROSSBOW;
            case VANISHABLE: return EnchantmentCategory.VANISHABLE;
            default: return EnchantmentCategory.VANISHABLE; // TODO: log error
        }
    }

    public NMSEnchantment(MCCEnchantment enchantment) {
        super(toNMSRarity(enchantment.getRarity()), toNMSEnchantmentSlotType(enchantment.getItemTarget()), EquipmentSlot.values());
        this.enchantment = enchantment;
    }

    // TODO: these things aint even used
    public @NotNull String getName() {
        return enchantment.getName();

    }
    public @NotNull NamespacedKey getKey() {
        return enchantment.getKey();

    }

    @Override
    public int getMinLevel() {
        return enchantment.getStartLevel();

    }

    @Override
    public int getMaxLevel() {
        return enchantment.getMaxLevel();

    }

    @Override
    public int getMaxCost(int level) {
        return enchantment.getMaxCost(level);
    }

    @Override
    public int getMinCost(int level) {
        return enchantment.getMinCost(level);
    }

    @Override
    public boolean checkCompatibility(Enchantment other) {
        CraftMCCEnchantment enchantment = (CraftMCCEnchantment) CraftMCCEnchantment.minecraftToBukkit(other);
        return !enchantment.equals(this.enchantment) && !this.enchantment.conflictsWith(enchantment);

    }

    @Override
    public boolean isTreasureOnly() {
        return enchantment.isTreasure();

    }

    @Override
    public boolean isCurse() {
        return enchantment.isCursed();

    }

    // TODO: fix spelling mistakes
    @Override
    public boolean isTradeable() {
        return enchantment.isTradable();

    }

    @Override
    public boolean isDiscoverable() {
        return enchantment.isDiscoverable();

    }

    @Override
    public boolean canEnchant(ItemStack item) {
        return enchantment.canEnchantItem(CraftItemStack.asBukkitCopy(item));

    }

}
