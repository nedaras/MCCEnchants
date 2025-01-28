package com.github.mccenchants.nms.v1_20_R4.api.enchantments;

import com.github.mccenchants.nms.v1_20_R4.enchantments.CraftMCCEnchantment;
import com.github.mccenchants.nms.v1_20_R4.enchantments.NMSEnchantment;
import net.minecraft.core.registries.BuiltInRegistries;
import com.google.common.base.Preconditions;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.Translatable;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.*;

public abstract class MCCEnchantment implements Keyed, Translatable {

    public MCCEnchantment() {}

    @NotNull
    private static MCCEnchantment getMCCEnchantment(@NotNull String key) {
        NamespacedKey namespacedKey = NamespacedKey.minecraft(key);
        Enchantment enchantment = Registry.ENCHANTMENT.get(namespacedKey);
        Preconditions.checkNotNull(enchantment, "No Enchantment found for %s. This is a bug.", namespacedKey);
        return CraftMCCEnchantment.bukkitToCustoms(enchantment);
    }

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

    private static Field getField(@NotNull Class<?> clazz, @NotNull String fieldName) {
        try {
            return clazz.getDeclaredField(fieldName);
        }
        catch (NoSuchFieldException e) {
            Class<?> superClass = clazz.getSuperclass();
            return superClass == null ? null : getField(superClass, fieldName);
        }
    }

    private static boolean setFieldValue(@NotNull Object of, @NotNull String fieldName, @Nullable Object value) {
        try {
            boolean isStatic = of instanceof Class;
            Class<?> clazz = isStatic ? (Class<?>) of : of.getClass();

            Field field = getField(clazz, fieldName);
            if (field == null) return false;

            field.setAccessible(true);
            field.set(isStatic ? null : of, value);
            return true;
        }
        catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Move to bridge
    public static void registerEnchantment(@NotNull MCCEnchantment enchantment) {
        // TODO: do ac check if like enchantment does not exist
        // TODO: we need to understand that are these l and m values cuz its weird to register like that
        setFieldValue(BuiltInRegistries.ENCHANTMENT, "l", false);
        setFieldValue(BuiltInRegistries.ENCHANTMENT, "m", new IdentityHashMap<>());

        NMSEnchantment nmsEnchantment = new NMSEnchantment(enchantment);

        net.minecraft.core.Registry.register(BuiltInRegistries.ENCHANTMENT, enchantment.getKey().toString(), nmsEnchantment);

        BuiltInRegistries.ENCHANTMENT.freeze();

    }

    @Deprecated
    @Contract("null -> null")
    @Nullable
    public static MCCEnchantment getByKey(@Nullable NamespacedKey key) {
        if (key == null) return null;
        Enchantment enchantment =  Registry.ENCHANTMENT.get(key);
        return enchantment == null ? null : CraftMCCEnchantment.bukkitToCustoms(enchantment);
    }

    /** @deprecated */
    @Deprecated
    @Contract("null -> null")
    @Nullable
    public static MCCEnchantment getByName(@Nullable String name) {
        Enchantment enchantment = Enchantment.getByName(name);
        return enchantment == null ? null : CraftMCCEnchantment.bukkitToCustoms(enchantment);
    }

    @NotNull
    private static MCCEnchantment getEnchantment(@NotNull String key) {
        NamespacedKey namespacedKey = NamespacedKey.minecraft(key);
        org.bukkit.enchantments.Enchantment enchantment = org.bukkit.Registry.ENCHANTMENT.get(namespacedKey);
        Preconditions.checkNotNull(enchantment, "No Enchantment found for %s. This is a bug.", namespacedKey);
        return CraftMCCEnchantment.bukkitToCustoms(enchantment);
    }


}
