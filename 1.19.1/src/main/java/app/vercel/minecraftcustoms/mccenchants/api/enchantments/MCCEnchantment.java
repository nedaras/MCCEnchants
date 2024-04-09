package app.vercel.minecraftcustoms.mccenchants.api.enchantments;

import app.vercel.minecraftcustoms.mccenchants.Main;
import app.vercel.minecraftcustoms.mccenchants.enchantments.CraftMCCEnchantment;
import app.vercel.minecraftcustoms.mccenchants.enchantments.NMSEnchantment;
import net.minecraft.core.registries.BuiltInRegistries;
import com.google.common.base.Preconditions;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.Translatable;
import org.bukkit.craftbukkit.v1_20_R3.enchantments.CraftEnchantment;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.*;

public abstract class MCCEnchantment implements Keyed, Translatable {

    public static @NotNull org.bukkit.enchantments.Enchantment toEnchantment(@NotNull MCCEnchantment enchantment) {
        return new CraftEnchantment(enchantment.getKey(), ((CraftMCCEnchantment) enchantment).getHandle());
    }

    public static @NotNull MCCEnchantment toMCCEnchantment(@NotNull org.bukkit.enchantments.Enchantment enchantment) {
        return new CraftMCCEnchantment(enchantment.getKey(), ((CraftEnchantment) enchantment).getHandle());
    }

    public MCCEnchantment() {}

    @NotNull
    private static MCCEnchantment getMCCEnchantment(@NotNull String key) {
        NamespacedKey namespacedKey = NamespacedKey.minecraft(key);
        Enchantment enchantment = Registry.ENCHANTMENT.get(namespacedKey);
        Preconditions.checkNotNull(enchantment, "No Enchantment found for %s. This is a bug.", namespacedKey);
        return toMCCEnchantment(enchantment);
    }

    public @NotNull String getName() {
        return WordUtils.capitalizeFully(this.getKey().getKey().replace("_", " "));

    }

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

    public static void registerEnchantment(@NotNull MCCEnchantment enchantment) {
        // TODO: we need to understand that are these l and m values cuz its weird to register like that
        setFieldValue(BuiltInRegistries.ENCHANTMENT, "l", false);
        setFieldValue(BuiltInRegistries.ENCHANTMENT, "m", new IdentityHashMap<>());

        NMSEnchantment nmsEnchantment = new NMSEnchantment(enchantment);

        net.minecraft.core.Registry.register(BuiltInRegistries.ENCHANTMENT, enchantment.getKey().toString(), nmsEnchantment);

        BuiltInRegistries.ENCHANTMENT.freeze();
        Main.getInstance().getLogger().info(enchantment.getKey().toString());

    }

    public static void registerMinecraftEnchantments(JavaPlugin plugin) {

    }

    @Deprecated
    @Contract("null -> null")
    @Nullable
    public static MCCEnchantment getByKey(@Nullable NamespacedKey key) {
        return key == null ? null : toMCCEnchantment(Registry.ENCHANTMENT.get(key));
    }

    /** @deprecated */
    @Deprecated
    @Contract("null -> null")
    @Nullable
    public static MCCEnchantment getByName(@Nullable String name) {
        return null; // TODO: implement
    }

    // TODO: implement
    ///** @deprecated */
    @Deprecated
    //@NotNull
    public static org.bukkit.enchantments.Enchantment[] values() {
        //return Lists.newArrayList(Registry.ENCHANTMENT).toArray(new org.bukkit.enchantments.Enchantment[0]);
        return null;
    }

    @NotNull
    private static MCCEnchantment getEnchantment(@NotNull String key) {
        NamespacedKey namespacedKey = NamespacedKey.minecraft(key);
        org.bukkit.enchantments.Enchantment enchantment = org.bukkit.Registry.ENCHANTMENT.get(namespacedKey);
        Preconditions.checkNotNull(enchantment, "No Enchantment found for %s. This is a bug.", namespacedKey);
        return toMCCEnchantment(enchantment);
    }


}
