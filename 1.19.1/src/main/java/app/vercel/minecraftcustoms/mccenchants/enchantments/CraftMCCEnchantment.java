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
        //super(CraftNamespacedKey.fromMinecraft(Objects.requireNonNull(BuiltInRegistries.ENCHANTMENT.getKey(target))));
        this.handle = handle;
        this.key = key;
        this.id = BuiltInRegistries.ENCHANTMENT.getId(handle);
        //if (target instanceof NMSEnchantment) names.put(((NMSEnchantment) target).getKey(), ((NMSEnchantment) target).getName());
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

        switch (handle.getRarity().getWeight()) {
            case 10: return EnchantmentRarity.COMMON;
            case 5: return EnchantmentRarity.UNCOMMON;
            case 2: return EnchantmentRarity.RARE;
            case 1: return EnchantmentRarity.VERY_RARE;
            default: throw new IllegalArgumentException("Enchantment Rarity with weight doesn't exist: " + handle.getRarity().getWeight());

        }

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

        switch(handle.category.ordinal()) {
            case 0:
                return EnchantmentTarget.ARMOR;
            case 1:
                return EnchantmentTarget.ARMOR_FEET;
            case 2:
                return EnchantmentTarget.ARMOR_LEGS;
            case 3:
                return EnchantmentTarget.ARMOR_TORSO;
            case 4:
                return EnchantmentTarget.ARMOR_HEAD;
            case 5:
                return EnchantmentTarget.WEAPON;
            case 6:
                return EnchantmentTarget.TOOL;
            case 7:
                return EnchantmentTarget.FISHING_ROD;
            case 8:
                return EnchantmentTarget.TRIDENT;
            case 9:
                return EnchantmentTarget.BREAKABLE;
            case 10:
                return EnchantmentTarget.BOW;
            case 11:
                return EnchantmentTarget.WEARABLE;
            case 12:
                return EnchantmentTarget.CROSSBOW;
            case 13:
                return EnchantmentTarget.VANISHABLE;
            default:
                throw new IncompatibleClassChangeError();
        }
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
    public @NotNull String getName() {
        switch(this.id) {
            case 1:
                return "PROTECTION_ENVIRONMENTAL";
            case 2:
                return "PROTECTION_FIRE";
            case 3:
                return "PROTECTION_FALL";
            case 4:
                return "PROTECTION_EXPLOSIONS";
            case 5:
                return "PROTECTION_PROJECTILE";
            case 6:
                return "OXYGEN";
            case 7:
                return "WATER_WORKER";
            case 8:
                return "THORNS";
            case 9:
                return "DEPTH_STRIDER";
            case 10:
                return "FROST_WALKER";
            case 11:
                return "BINDING_CURSE";
            case 12:
                return "SOUL_SPEED";
            case 13:
                return "DAMAGE_ALL";
            case 14:
                return "DAMAGE_UNDEAD";
            case 15:
                return "DAMAGE_ARTHROPODS";
            case 16:
                return "KNOCKBACK";
            case 17:
                return "FIRE_ASPECT";
            case 18:
                return "LOOT_BONUS_MOBS";
            case 19:
                return "SWEEPING_EDGE";
            case 20:
                return "DIG_SPEED";
            case 21:
                return "SILK_TOUCH";
            case 22:
                return "DURABILITY";
            case 23:
                return "LOOT_BONUS_BLOCKS";
            case 24:
                return "ARROW_DAMAGE";
            case 25:
                return "ARROW_KNOCKBACK";
            case 26:
                return "ARROW_FIRE";
            case 27:
                return "ARROW_INFINITE";
            case 28:
                return "LUCK";
            case 29:
                return "LURE";
            case 30:
                return "LOYALTY";
            case 31:
                return "IMPALING";
            case 32:
                return "RIPTIDE";
            case 33:
                return "CHANNELING";
            case 34:
                return "MULTISHOT";
            case 35:
                return "QUICK_CHARGE";
            case 36:
                return "PIERCING";
            case 37:
                return "MENDING";
            case 38:
                return "VANISHING_CURSE";
            default:
                return  "UNKNOWN_ENCHANT_" + BuiltInRegistries.ENCHANTMENT.getId(handle);
        }
    }

    @Override
    public boolean conflictsWith(@NotNull MCCEnchantment other) {
        if (!(other instanceof CraftMCCEnchantment)) {
            return false;
        } else {
            CraftMCCEnchantment enchantment = (CraftMCCEnchantment) other;
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