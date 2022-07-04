package app.vercel.minecraftcustoms.mccenchants.enchantments;

import app.vercel.minecraftcustoms.mccenchants.api.enchantments.EnchantmentRarity;
import app.vercel.minecraftcustoms.mccenchants.api.enchantments.MCCEnchantment;
import net.minecraft.core.IRegistry;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_18_R2.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_18_R2.util.CraftNamespacedKey;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Objects;

public class CraftMCCEnchantment extends MCCEnchantment {

    private final net.minecraft.world.item.enchantment.Enchantment target;

    private static final HashMap<NamespacedKey, String> names = new HashMap<>();

    public CraftMCCEnchantment(net.minecraft.world.item.enchantment.Enchantment target) {
        super(CraftNamespacedKey.fromMinecraft(Objects.requireNonNull(IRegistry.V.b(target))));
        this.target = target;
        if (target instanceof NMSEnchantment) names.put(((NMSEnchantment) target).getKey(), ((NMSEnchantment) target).getName());
    }

    public CraftMCCEnchantment(MCCEnchantment target) {
        super(target.getKey());
        this.target = new NMSEnchantment(target);
        if (!names.containsKey(target.getKey())) names.put(target.getKey(), target.getName());
    }

    @Override
    public int getMaxLevel() {
        return this.target.a();

    }

    @Override
    public int getStartLevel() {
        return this.target.e();
    }

    @Override
    public @NotNull EnchantmentRarity getRarity() {

        switch (target.d().a()) {
            case 10: return EnchantmentRarity.COMMON;
            case 5: return EnchantmentRarity.UNCOMMON;
            case 2: return EnchantmentRarity.RARE;
            case 1: return EnchantmentRarity.VERY_RARE;
            default: throw new IllegalArgumentException("Enchantment Rarity with weight doesn't exist: " + target.d().a());

        }

    }

    @Override
    public int getMinCost(int level) {
        return target.a(level);

    }

    @Override
    public int getMaxCost(int level) {
        return target.b(level);

    }

    @Override
    public @NotNull EnchantmentTarget getItemTarget() {

        switch(target.e.ordinal()) {
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
            default: throw new IllegalStateException("Could not found EnchantmentTarget with id: " + target.e.ordinal());
        }
    }

    @Override
    public boolean isTreasure() {
        return this.target.b();
    }

    @Override
    public boolean isCursed() {
        return this.target.c();
    }

    @Override
    public boolean isTradable() {
        return target.h();

    }

    @Override
    public boolean isDiscoverable() {
        return target.i();

    }

    @Override
    public boolean canEnchantItem(@NotNull ItemStack item) {
        return this.target.a(CraftItemStack.asNMSCopy(item));
    }

    @Override
    public @NotNull String getName() {

        if (names.containsKey(getKey())) return names.get(getKey());

        switch(IRegistry.V.a(target)) {
            case 0:
                return "PROTECTION_ENVIRONMENTAL";
            case 1:
                return "PROTECTION_FIRE";
            case 2:
                return "PROTECTION_FALL";
            case 3:
                return "PROTECTION_EXPLOSIONS";
            case 4:
                return "PROTECTION_PROJECTILE";
            case 5:
                return "OXYGEN";
            case 6:
                return "WATER_WORKER";
            case 7:
                return "THORNS";
            case 8:
                return "DEPTH_STRIDER";
            case 9:
                return "FROST_WALKER";
            case 10:
                return "BINDING_CURSE";
            case 11:
                return "SOUL_SPEED";
            case 12:
                return "DAMAGE_ALL";
            case 13:
                return "DAMAGE_UNDEAD";
            case 14:
                return "DAMAGE_ARTHROPODS";
            case 15:
                return "KNOCKBACK";
            case 16:
                return "FIRE_ASPECT";
            case 17:
                return "LOOT_BONUS_MOBS";
            case 18:
                return "SWEEPING_EDGE";
            case 19:
                return "DIG_SPEED";
            case 20:
                return "SILK_TOUCH";
            case 21:
                return "DURABILITY";
            case 22:
                return "LOOT_BONUS_BLOCKS";
            case 23:
                return "ARROW_DAMAGE";
            case 24:
                return "ARROW_KNOCKBACK";
            case 25:
                return "ARROW_FIRE";
            case 26:
                return "ARROW_INFINITE";
            case 27:
                return "LUCK";
            case 28:
                return "LURE";
            case 29:
                return "LOYALTY";
            case 30:
                return "IMPALING";
            case 31:
                return "RIPTIDE";
            case 32:
                return "CHANNELING";
            case 33:
                return "MULTISHOT";
            case 34:
                return "QUICK_CHARGE";
            case 35:
                return "PIERCING";
            case 36:
                return "MENDING";
            case 37:
                return "VANISHING_CURSE";
            default:
                return  "UNKNOWN_ENCHANT_" + IRegistry.V.a(target);
        }
    }

    public static @Nullable net.minecraft.world.item.enchantment.Enchantment getRaw(MCCEnchantment enchantment) {
        if (enchantment instanceof MCCEnchantmentWrapper) {
            enchantment = ((MCCEnchantmentWrapper)enchantment).getEnchantment();
        }

        return enchantment instanceof CraftMCCEnchantment ? ((CraftMCCEnchantment)enchantment).target : null;
    }

    @Override
    public boolean conflictsWith(@NotNull MCCEnchantment other) {
        if (other instanceof MCCEnchantmentWrapper) {
            other = ((MCCEnchantmentWrapper) other).getEnchantment();
        }

        if (!(other instanceof CraftMCCEnchantment)) {
            return false;
        } else {
            CraftMCCEnchantment enchantment = (CraftMCCEnchantment) other;
            return !target.b(enchantment.target);
        }
    }

    public net.minecraft.world.item.enchantment.Enchantment getHandle() {
        return this.target;
    }

}
