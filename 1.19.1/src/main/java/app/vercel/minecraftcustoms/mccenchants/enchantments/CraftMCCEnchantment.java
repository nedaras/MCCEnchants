package app.vercel.minecraftcustoms.mccenchants.enchantments;

import app.vercel.minecraftcustoms.mccenchants.api.enchantments.EnchantmentRarity;
import app.vercel.minecraftcustoms.mccenchants.api.enchantments.MCCEnchantment;
import net.minecraft.core.registries.BuiltInRegistries;
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
    private final int id;

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
    @NotNull
    public String getName() {
        if (handle instanceof NMSEnchantment nms) return nms.getName();
        // TODO: im dumb why am i using ids i need to use namespace
        return switch (id) {
            case 0 -> "Protection";
            case 1 -> "Fire Protection";
            case 2 -> "Feather Falling";
            case 3 -> "Blast Protection";
            case 4 -> "Projectile Protection";
            case 5 -> "Respiration";
            case 6 -> "Aqua Affinity";
            case 7 -> "Thorns";
            case 8 -> "Depth Strider";
            case 9 -> "Frost Walker";
            case 10 -> "Curse of Binding";
            case 11 -> "Saul Speed";
            case 12 -> "Swift Sneak";
            case 13 -> "Sharpness";
            case 14 -> "Smite";
            case 15 -> "Bane of Arthropods";
            case 16 -> "Knockback";
            case 17 -> "Fire Aspect";
            case 18 -> "Looting";
            case 19 -> "Sweeping Edge";
            case 20 -> "Efficiency";
            case 21 -> "Silk Touch";
            case 22 -> "Unbreaking";
            case 23 -> "Fortune";
            case 24 -> "Power";
            case 25 -> "Punch";
            case 26 -> "Flame";
            case 27 -> "Infinity";
            case 28 -> "Luck";
            case 29 -> "Lure";
            case 30 -> "Loyalty";
            case 31 -> "Impaling";
            case 32 -> "Riptide";
            case 33 -> "Channeling";
            case 34 -> "Multishot";
            case 35 -> "Quick Charge";
            case 36 -> "Piercing";
            case 37 -> "Mending";
            case 38 -> "Curse of Vanishing";
            default -> WordUtils.capitalizeFully(getKey().getKey().replaceAll("_", " "));
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