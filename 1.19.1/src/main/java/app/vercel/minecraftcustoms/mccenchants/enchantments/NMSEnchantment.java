package app.vercel.minecraftcustoms.mccenchants.enchantments;

import app.vercel.minecraftcustoms.mccenchants.api.enchantments.EnchantmentRarity;
import app.vercel.minecraftcustoms.mccenchants.api.enchantments.MCCEnchantment;
import net.minecraft.world.entity.EnumItemSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentSlotType;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_19_R1.inventory.CraftItemStack;
import org.bukkit.enchantments.EnchantmentTarget;
import org.jetbrains.annotations.NotNull;

public class NMSEnchantment extends Enchantment {

    private final MCCEnchantment enchantment;

    private static @NotNull Rarity toNMSRarity(@NotNull EnchantmentRarity rarity) {

        switch (rarity) {
            case COMMON: return Rarity.a;
            case UNCOMMON: return Rarity.b;
            case RARE: return Rarity.c;
            default: return Rarity.d;

        }

    }

    private static @NotNull EnchantmentSlotType toNMSEnchantmentSlotType(@NotNull EnchantmentTarget target) {

        switch (target) {
            case ARMOR: return EnchantmentSlotType.a;
            case ARMOR_FEET: return EnchantmentSlotType.b;
            case ARMOR_LEGS: return EnchantmentSlotType.c;
            case ARMOR_TORSO: return EnchantmentSlotType.d;
            case ARMOR_HEAD: return EnchantmentSlotType.e;
            case WEAPON: return  EnchantmentSlotType.f;
            case TOOL: return EnchantmentSlotType.g;
            case FISHING_ROD: return EnchantmentSlotType.h;
            case TRIDENT: return EnchantmentSlotType.i;
            case BREAKABLE: return EnchantmentSlotType.j;
            case BOW: return EnchantmentSlotType.k;
            case WEARABLE: return EnchantmentSlotType.l;
            case CROSSBOW: return EnchantmentSlotType.m;
            default: return EnchantmentSlotType.n;

        }

    }

    public NMSEnchantment(MCCEnchantment enchantment) {
        super(toNMSRarity(enchantment.getRarity()), toNMSEnchantmentSlotType(enchantment.getItemTarget()), EnumItemSlot.values());
        this.enchantment = enchantment;
    }

    public @NotNull String getName() {
        return enchantment.getName();

    }
    public @NotNull NamespacedKey getKey() {
        return enchantment.getKey();

    }


    @Override
    public int e() {
        return enchantment.getStartLevel();

    }

    @Override
    public int a() {
        return enchantment.getMaxLevel();

    }

    @Override
    public int a(int level) {
        return enchantment.getMaxCost(level);
    }

    @Override
    public int b(int level) {
        return enchantment.getMinCost(level);
    }

    @Override
    public boolean a(Enchantment other) {
        CraftMCCEnchantment enchantment = new CraftMCCEnchantment(other);
        return !enchantment.equals(this.enchantment) && !this.enchantment.conflictsWith(enchantment);

    }

    @Override
    public boolean b() {
        return enchantment.isTreasure();

    }

    @Override
    public boolean c() {
        return enchantment.isCursed();

    }

    @Override
    public boolean h() {
        return enchantment.isTradable();

    }

    @Override
    public boolean i() {
        return enchantment.isDiscoverable();

    }

    @Override
    public boolean a(ItemStack item) {
        return enchantment.canEnchantItem(CraftItemStack.asBukkitCopy(item));

    }

}
