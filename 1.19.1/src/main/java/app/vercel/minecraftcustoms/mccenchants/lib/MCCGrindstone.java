package app.vercel.minecraftcustoms.mccenchants.lib;

import app.vercel.minecraftcustoms.mccenchants.api.enchantments.MCCEnchantment;
import app.vercel.minecraftcustoms.mccenchants.api.helpers.MCCEnchanting;
import app.vercel.minecraftcustoms.mccenchants.utils.Utils;
import net.minecraft.core.BlockPosition;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.EntityExperienceOrb;
import net.minecraft.world.phys.Vec3D;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_19_R1.CraftWorld;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Objects;

public class MCCGrindstone {

    public static boolean isCompatible(@Nullable ItemStack firstItem, @Nullable ItemStack secondItem) {
        if (firstItem == null && secondItem == null) return false;
        if (firstItem != null && secondItem != null) {
            return canBePlaced(firstItem) && canBePlaced(secondItem) && firstItem.getType() == secondItem.getType();

        }

        if (firstItem != null) return canBePlaced(firstItem) && !MCCEnchanting.getEnchantments(firstItem).isEmpty();
        return canBePlaced(secondItem) && !MCCEnchanting.getEnchantments(secondItem).isEmpty();

    }

    private static boolean canBePlaced(@NotNull ItemStack item) {
        return EnchantmentTarget.BREAKABLE.includes(item) || item.getType() == Material.ENCHANTED_BOOK;

    }

    public static @NotNull ItemStack combineItems(@Nullable ItemStack firstItem, @Nullable ItemStack secondItem) {

        if (firstItem != null && secondItem != null) {

            Damageable firstItemMeta = (Damageable) Objects.requireNonNull(firstItem.getItemMeta());
            Damageable secondItemMeta = (Damageable) Objects.requireNonNull(secondItem.getItemMeta());

            int firstItemDurability = firstItem.getType().getMaxDurability() - firstItemMeta.getDamage();
            int secondItemDurability = secondItem.getType().getMaxDurability() - secondItemMeta.getDamage();

            int damage = firstItemDurability + secondItemDurability + secondItem.getType().getMaxDurability() * 5 / 100;

            ItemStack item = removeEnchants(mergeItemStacks(firstItem, secondItem));
            Damageable meta = (Damageable) Objects.requireNonNull(item.getItemMeta());

            meta.setDamage(Math.max(secondItem.getType().getMaxDurability() - damage, 0));
            item.setItemMeta(meta);

            return item;

        }

        return firstItem != null ? removeEnchants(firstItem) : removeEnchants(Objects.requireNonNull(secondItem));

    }

    private static @NotNull ItemStack mergeItemStacks(@NotNull ItemStack firstItem, @NotNull ItemStack secondItem) {

        firstItem = firstItem.clone();

        for (Map.Entry<Enchantment, Integer> entry : MCCEnchanting.getEnchantments(secondItem).entrySet()) {

            int level = Math.max(entry.getValue(), getEnchantmentsLevel(firstItem, entry.getKey()));
            MCCEnchanting.setEnchantment(firstItem, entry.getKey(), level);

        }

        return firstItem;

    }

    private static int getEnchantmentsLevel(@NotNull ItemStack item, @NotNull Enchantment enchantment) {
        boolean isBook = item.getType() == Material.ENCHANTED_BOOK;

        if (isBook) {

            EnchantmentStorageMeta meta = (EnchantmentStorageMeta) Objects.requireNonNull(item.getItemMeta());
            if (meta.hasStoredEnchant(enchantment)) return meta.getEnchantLevel(enchantment);
            return 0;

        }

        if (item.containsEnchantment(enchantment)) return item.getEnchantmentLevel(enchantment);
        return 0;

    }

    private static @NotNull ItemStack removeEnchants(@NotNull ItemStack item) {
        item = item.clone();
        Utils.convertEnchantsToLore(item);

        for (Map.Entry<Enchantment, Integer> entry : MCCEnchanting.getEnchantments(item).entrySet())  {
            Enchantment enchantment = entry.getKey();
            // default Enchantment class will return true to binding_curse and vanishing_curse, not checking NMS curse status.
            if (MCCEnchantment.toMCCEnchantment(enchantment).isCursed()) continue;

            MCCEnchanting.removeEnchantment(item, enchantment);


        }

        if (item.getType() == Material.ENCHANTED_BOOK) {
            EnchantmentStorageMeta meta = (EnchantmentStorageMeta) Objects.requireNonNull(item.getItemMeta());
            if (meta.getStoredEnchants().isEmpty()) item.setType(Material.BOOK);

        }

        return item;

    }

    // we don't need nms for this!
    public static void dropExperienceOrbs(Location location, @Nullable ItemStack firstItem, @Nullable ItemStack secondItem) {

        WorldServer worldServer = ((CraftWorld) Objects.requireNonNull(location.getWorld())).getHandle();
        BlockPosition position = new BlockPosition(location.getBlockX(), location.getBlockY(), location.getBlockZ());

        EntityExperienceOrb.a(worldServer, Vec3D.a(position), getExperienceAmount(worldServer, firstItem, secondItem));


    }

    private static int getExperienceAmount(WorldServer world, @Nullable ItemStack firstItem, @Nullable ItemStack secondItem) {

        int experience = getExperienceFromItem(firstItem) + getExperienceFromItem(secondItem);
        if (experience > 0) {

            int k = (int) Math.ceil((double) experience / 2.0d);
            return k + world.w.f(); // gets world's random

        }

        return 0;

    }

    private static int getExperienceFromItem(@Nullable ItemStack item) {
        if (item == null) return 0;
        int i = 0;

        for (Map.Entry<Enchantment, Integer> entry : MCCEnchanting.getEnchantments(item).entrySet())  {
            MCCEnchantment enchantment = MCCEnchantment.toMCCEnchantment(entry.getKey());
            if (enchantment.isCursed()) continue;

            i += enchantment.getMinCost(entry.getValue());

        }

        return i;

    }

}
