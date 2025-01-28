package com.github.mccenchants.nms.v1_20_R4.enchantments;

import com.github.mccenchants.nms.v1_20_R4.api.enchantments.EnchantmentRarity;
import com.github.mccenchants.nms.v1_20_R4.api.enchantments.MCCEnchantment;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.enchantment.Enchantment;
import org.apache.commons.lang.WordUtils;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.craftbukkit.v1_20_R3.CraftRegistry;
import org.bukkit.craftbukkit.v1_20_R3.enchantments.CraftEnchantment;
import org.bukkit.craftbukkit.v1_20_R3.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_20_R3.util.Handleable;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class CraftMCCEnchantment extends MCCEnchantment implements Handleable<Enchantment> {

    private final NamespacedKey key;
    private final Enchantment handle;

    public static MCCEnchantment minecraftToCustoms(@NotNull Enchantment minecraft) {
        org.bukkit.enchantments.Enchantment enchantment = CraftRegistry.minecraftToBukkit(minecraft, Registries.ENCHANTMENT, Registry.ENCHANTMENT);
        return new CraftMCCEnchantment(enchantment.getKey(), minecraft);
    }

    public static Enchantment customsToMinecraft(@NotNull MCCEnchantment bukkit) {
        return CraftRegistry.bukkitToMinecraft(bukkit);
    }

    public static org.bukkit.enchantments.Enchantment customsToBukkit(@NotNull MCCEnchantment enchantment) {
        return new CraftEnchantment(enchantment.getKey(), ((CraftMCCEnchantment) enchantment).getHandle());
    }

    public static MCCEnchantment bukkitToCustoms(@NotNull org.bukkit.enchantments.Enchantment enchantment) {
        return new CraftMCCEnchantment(enchantment.getKey(), ((CraftEnchantment) enchantment).getHandle());
    }

    public CraftMCCEnchantment(NamespacedKey key, net.minecraft.world.item.enchantment.Enchantment handle) {
        this.handle = handle;
        this.key = key;
    }

    public net.minecraft.world.item.enchantment.Enchantment getHandle() {
        return this.handle;
    }

    @NotNull
    public NamespacedKey getKey() {
        return this.key;
    }

    @Override
    @NotNull
    public String getName() {
        if (handle instanceof NMSEnchantment nms) return nms.getName();
        return switch (key.toString()) {
            case "minecraft:protection" -> "Protection";
            case "minecraft:fire_protection" -> "Fire Protection";
            case "minecraft:feather_falling" -> "Feather Falling";
            case "minecraft:blast_protection" -> "Blast Protection";
            case "minecraft:projectile_protection" -> "Projectile Protection";
            case "minecraft:respiration" -> "Respiration";
            case "minecraft:aqua_affinity" -> "Aqua Affinity";
            case "minecraft:thorns" -> "Thorns";
            case "minecraft:depth_strider" -> "Depth Strider";
            case "minecraft:frost_walker" -> "Frost Walker";
            case "minecraft:binding_curse" -> "Curse of Binding";
            case "minecraft:soul_speed"  -> "Soul Speed";
            case "minecraft:swift_sneak" -> "Swift Sneak";
            case "minecraft:sharpness" -> "Sharpness";
            case "minecraft:smite" -> "Smite";
            case "minecraft:bane_of_arthropods" -> "Bane of Arthropods";
            case "minecraft:knockback" -> "Knockback";
            case "minecraft:fire_aspect" -> "Fire Aspect";
            case "minecraft:looting" -> "Looting";
            case "minecraft:sweeping" -> "Sweeping Edge";
            case "minecraft:efficiency"-> "Efficiency";
            case "minecraft:silk_touch" -> "Silk Touch";
            case "minecraft:unbreaking" -> "Unbreaking";
            case "minecraft:fortune" -> "Fortune";
            case "minecraft:power" -> "Power";
            case "minecraft:punch" -> "Punch";
            case "minecraft:flame" -> "Flame";
            case "minecraft:infinity" -> "Infinity";
            case "minecraft:luck_of_the_sea"-> "Luck of the Sea";
            case "minecraft:lure" -> "Lure";
            case "minecraft:loyalty" -> "Loyalty";
            case "minecraft:impaling" -> "Impaling";
            case "minecraft:riptide" -> "Riptide";
            case "minecraft:channeling" -> "Channeling";
            case "minecraft:multishot" -> "Multishot";
            case "minecraft:quick_charge" -> "Quick Charge";
            case "minecraft:piercing" -> "Piercing";
            case "minecraft:mending" -> "Mending";
            case "minecraft:vanishing_curse" -> "Curse of Vanishing";
            default -> WordUtils.capitalizeFully(key.getKey().replaceAll("_", " "));
        };
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
            return other instanceof CraftMCCEnchantment && this.getKey().equals(((MCCEnchantment) other).getKey());
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