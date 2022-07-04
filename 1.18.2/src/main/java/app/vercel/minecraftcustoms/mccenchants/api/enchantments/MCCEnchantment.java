package app.vercel.minecraftcustoms.mccenchants.api.enchantments;

import app.vercel.minecraftcustoms.mccenchants.Main;
import app.vercel.minecraftcustoms.mccenchants.Version;
import app.vercel.minecraftcustoms.mccenchants.configs.YamlConfig;
import app.vercel.minecraftcustoms.mccenchants.enchantments.CraftMCCEnchantment;
import app.vercel.minecraftcustoms.mccenchants.enchantments.MCCEnchantmentWrapper;
import app.vercel.minecraftcustoms.mccenchants.utils.Utils;
import com.mojang.serialization.Lifecycle;
import net.minecraft.core.IRegistry;
import net.minecraft.core.RegistryMaterials;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.enchantment.Enchantment;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_18_R2.enchantments.CraftEnchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.*;

public abstract class MCCEnchantment {

    public static final MCCEnchantment PROTECTION_ENVIRONMENTAL = new MCCEnchantmentWrapper("protection");
    public static final MCCEnchantment PROTECTION_FIRE = new MCCEnchantmentWrapper("fire_protection");
    public static final MCCEnchantment PROTECTION_FALL = new MCCEnchantmentWrapper("feather_falling");
    public static final MCCEnchantment PROTECTION_EXPLOSIONS = new MCCEnchantmentWrapper("blast_protection");
    public static final MCCEnchantment PROTECTION_PROJECTILE = new MCCEnchantmentWrapper("projectile_protection");
    public static final MCCEnchantment OXYGEN = new MCCEnchantmentWrapper("respiration");
    public static final MCCEnchantment WATER_WORKER = new MCCEnchantmentWrapper("aqua_affinity");
    public static final MCCEnchantment THORNS = new MCCEnchantmentWrapper("thorns");
    public static final MCCEnchantment DEPTH_STRIDER = new MCCEnchantmentWrapper("depth_strider");
    public static final MCCEnchantment FROST_WALKER = new MCCEnchantmentWrapper("frost_walker");
    public static final MCCEnchantment BINDING_CURSE = new MCCEnchantmentWrapper("binding_curse");
    public static final MCCEnchantment DAMAGE_ALL = new MCCEnchantmentWrapper("sharpness");
    public static final MCCEnchantment DAMAGE_UNDEAD = new MCCEnchantmentWrapper("smite");
    public static final MCCEnchantment DAMAGE_ARTHROPODS = new MCCEnchantmentWrapper("bane_of_arthropods");
    public static final MCCEnchantment KNOCKBACK = new MCCEnchantmentWrapper("knockback");
    public static final MCCEnchantment FIRE_ASPECT = new MCCEnchantmentWrapper("fire_aspect");
    public static final MCCEnchantment LOOT_BONUS_MOBS = new MCCEnchantmentWrapper("looting");
    public static final MCCEnchantment SWEEPING_EDGE = new MCCEnchantmentWrapper("sweeping");
    public static final MCCEnchantment DIG_SPEED = new MCCEnchantmentWrapper("efficiency");
    public static final MCCEnchantment SILK_TOUCH = new MCCEnchantmentWrapper("silk_touch");
    public static final MCCEnchantment DURABILITY = new MCCEnchantmentWrapper("unbreaking");
    public static final MCCEnchantment LOOT_BONUS_BLOCKS = new MCCEnchantmentWrapper("fortune");
    public static final MCCEnchantment ARROW_DAMAGE = new MCCEnchantmentWrapper("power");
    public static final MCCEnchantment ARROW_KNOCKBACK = new MCCEnchantmentWrapper("punch");
    public static final MCCEnchantment ARROW_FIRE = new MCCEnchantmentWrapper("flame");
    public static final MCCEnchantment ARROW_INFINITE = new MCCEnchantmentWrapper("infinity");
    public static final MCCEnchantment LUCK = new MCCEnchantmentWrapper("luck_of_the_sea");
    public static final MCCEnchantment LURE = new MCCEnchantmentWrapper("lure");
    public static final MCCEnchantment LOYALTY = new MCCEnchantmentWrapper("loyalty");
    public static final MCCEnchantment IMPALING = new MCCEnchantmentWrapper("impaling");
    public static final MCCEnchantment RIPTIDE = new MCCEnchantmentWrapper("riptide");
    public static final MCCEnchantment CHANNELING = new MCCEnchantmentWrapper("channeling");
    public static final MCCEnchantment MULTISHOT = new MCCEnchantmentWrapper("multishot");
    public static final MCCEnchantment QUICK_CHARGE = new MCCEnchantmentWrapper("quick_charge");
    public static final MCCEnchantment PIERCING = new MCCEnchantmentWrapper("piercing");
    public static final MCCEnchantment MENDING = new MCCEnchantmentWrapper("mending");
    public static final MCCEnchantment VANISHING_CURSE = new MCCEnchantmentWrapper("vanishing_curse");
    public static final MCCEnchantment SOUL_SPEED = new MCCEnchantmentWrapper("soul_speed");

    private static final Map<NamespacedKey, CraftMCCEnchantment> byKey = new HashMap<>();
    private static final Map<NamespacedKey, String> displayNames = new HashMap<>();
    private static final Map<String, CraftMCCEnchantment> byName = new HashMap<>();
    //private static YamlConfig names = new YamlConfig(Main.getInstance(), "names");

    private static boolean registered = false;

    private final NamespacedKey key;

    public MCCEnchantment(@NotNull NamespacedKey key) {
        this.key = key;

    }

    public @NotNull String getDisplayName() {
        return displayNames.get(key);

    }

    public @NotNull String getDisplayName(int level) {
        String romeNumber = getMaxLevel() > 1 ? " " + Utils.toRomeNumber(level) : "";
        return getDisplayName() + romeNumber;

    }

    public @NotNull NamespacedKey getKey() {
        return this.key;
    }

    public abstract @NotNull String getName();

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

    public boolean equals(Object obj) {

        if (obj == null) {
            return false;
        } else if (!(obj instanceof MCCEnchantment)) {
            return false;
        } else {
            MCCEnchantment other = (MCCEnchantment)obj;
            return this.key.equals(other.key);
        }
    }

    public int hashCode() {
        return this.key.hashCode();
    }

    public @NotNull String toString() {
        return "MCCEnchantment[" + this.key + ", " + this.getName() + "]";
    }

    public static void registerEnchantment(@NotNull MCCEnchantment enchantment) {

        String prefix = new YamlConfig(Main.getInstance(), "names").getConfig().getString("prefix");

        CraftMCCEnchantment craftMCCEnchantment = new CraftMCCEnchantment(enchantment);

        int id = IRegistry.V.a(CraftEnchantment.getRaw(org.bukkit.enchantments.Enchantment.getByKey(enchantment.key)));
        if (id == -1) id = IRegistry.V.a(craftMCCEnchantment.getHandle());

        if (id == -1) {

            IRegistry.a(IRegistry.V, enchantment.key.getKey(), craftMCCEnchantment.getHandle());

            byKey.put(enchantment.key, craftMCCEnchantment);
            byName.put(enchantment.getName(), craftMCCEnchantment);

            displayNames.put(enchantment.key, ChatColor.translateAlternateColorCodes('&', prefix + enchantment.getDisplayName()));

            org.bukkit.enchantments.Enchantment.registerEnchantment(new CraftEnchantment(craftMCCEnchantment.getHandle()));

            Main.getInstance().getLogger().info("Successfully registered enchantment: " + enchantment.getName());

            return;

        }

        try {

            RegistryMaterials<Enchantment> enchantments = ((RegistryMaterials<Enchantment>) IRegistry.V);
            ResourceKey<Enchantment> resourceKey = ResourceKey.a(IRegistry.V.m(), new MinecraftKey(enchantment.key.getKey()));

            enchantments.a(OptionalInt.of(id), resourceKey, craftMCCEnchantment.getHandle(), Lifecycle.stable());

            Field byKeyField = org.bukkit.enchantments.Enchantment.class.getDeclaredField("byKey");
            Field byNameField = org.bukkit.enchantments.Enchantment.class.getDeclaredField("byName");

            byKeyField.setAccessible(true);
            byNameField.setAccessible(true);

            Map<NamespacedKey, org.bukkit.enchantments.Enchantment> byKey = (Map) byKeyField.get(null);
            Map<String, org.bukkit.enchantments.Enchantment> byName = (Map) byNameField.get(null);

            CraftEnchantment craftEnchantment = new CraftEnchantment(craftMCCEnchantment.getHandle());

            byKey.put(enchantment.getKey(), craftEnchantment);
            byName.put(enchantment.getName(), craftEnchantment);

            MCCEnchantment.byKey.put(enchantment.key, craftMCCEnchantment);
            MCCEnchantment.byName.put(enchantment.getName(), craftMCCEnchantment);

            displayNames.put(enchantment.key, ChatColor.translateAlternateColorCodes('&', prefix + enchantment.getDisplayName()));

            Main.getInstance().getLogger().info("Successfully registered enchantment: " + enchantment.getName());

        } catch (NoSuchFieldException | IllegalAccessException error) {
            error.printStackTrace();

        }

    }

    public static void registerMinecraftEnchantments(JavaPlugin plugin) {

        if (!Version.isSupported()) return;
        if (registered) throw new IllegalArgumentException("Minecraft Enchantments already registered, don't you want to overwrite them?");

        try {

            if (!CraftEnchantment.isAcceptingRegistrations()) {

                Field field = org.bukkit.enchantments.Enchantment.class.getDeclaredField("acceptingNew");

                field.setAccessible(true);
                field.set(null, true);

            }

            RegistryMaterials<Enchantment> enchantments = ((RegistryMaterials<Enchantment>) IRegistry.V);
            Field isFrozen = enchantments.getClass().getDeclaredField("bL");
            isFrozen.setAccessible(true);
            isFrozen.set(enchantments, false);

            YamlConfiguration yamlConfig = new YamlConfig(plugin, "names").getConfig();
//            JSONConfiguration jsonConfig = new JSONConfig(plugin, "enchantments").getConfig();
//
//            List<String> keys = jsonConfig.getDefaultKeys();

            for (org.bukkit.enchantments.Enchantment enchantment : org.bukkit.enchantments.Enchantment.values()) {
                if (enchantment.getName().startsWith("UNKNOWN_ENCHANT_")) continue;

                CraftEnchantment craftEnchantment = (CraftEnchantment) enchantment;
                CraftMCCEnchantment craftMCCEnchantment = new CraftMCCEnchantment(craftEnchantment.getHandle());

                byKey.put(enchantment.getKey(), craftMCCEnchantment);
                byName.put(enchantment.getName(), craftMCCEnchantment);

//                if (keys.contains(enchantment.getName())) {
//
//                    int id = IRegistry.V.a(CraftEnchantment.getRaw(enchantment));
//                    if (id < 0) throw new IllegalArgumentException("Enchantment not found, cannot overwrite: " + enchantment.getKey());
//
//                    ConfigEnchantment configEnchantment = new ConfigEnchantment(craftMCCEnchantment, jsonConfig);
//                    craftMCCEnchantment = new CraftMCCEnchantment(configEnchantment);
//
//                    try {
//
//                        RegistryMaterials<Enchantment> registry = ((RegistryMaterials<Enchantment>) IRegistry.V);
//                        ResourceKey<Enchantment> resourceKey = ResourceKey.a(IRegistry.V.m(), new MinecraftKey(enchantment.getKey().getKey()));
//
//                        registry.a(OptionalInt.of(id), resourceKey, craftMCCEnchantment.getHandle(), Lifecycle.stable());
//
//                        Field byKeyField = org.bukkit.enchantments.Enchantment.class.getDeclaredField("byKey");
//                        Field byNameField = org.bukkit.enchantments.Enchantment.class.getDeclaredField("byName");
//
//                        byKeyField.setAccessible(true);
//                        byNameField.setAccessible(true);
//
//                        Map<NamespacedKey, org.bukkit.enchantments.Enchantment> byKey = (Map) byKeyField.get(null);
//                        Map<String, org.bukkit.enchantments.Enchantment> byName = (Map) byNameField.get(null);
//
//                        byKey.put(configEnchantment.getKey(), new CraftEnchantment(craftMCCEnchantment.getHandle()));
//                        byName.put(configEnchantment.getName(), new CraftEnchantment(craftMCCEnchantment.getHandle()));
//
//                        enchantment = Objects.requireNonNull(org.bukkit.enchantments.Enchantment.getByKey(configEnchantment.getKey()));
//
//                        MCCEnchantment.byKey.put(enchantment.getKey(), craftMCCEnchantment);
//                        MCCEnchantment.byName.put(enchantment.getName(), craftMCCEnchantment);
//
//                    } catch (NoSuchFieldException | IllegalAccessException error) {
//                        plugin.getLogger().severe("Please restart the server when changing enchantment.json file: /stop");
//
//                    }
//
//                }

                String displayName =  Objects.requireNonNull(yamlConfig.getString(enchantment.getName()));
                String prefix = Objects.requireNonNull(yamlConfig.getString("prefix"));

                displayNames.put(enchantment.getKey(), ChatColor.translateAlternateColorCodes('&', prefix + displayName));

            }

            registered = true;

        } catch (Exception e) {
            e.printStackTrace();

        }

    }

    @Nullable
    public static MCCEnchantment getByKey(@Nullable NamespacedKey key) {
        return byKey.get(key);
    }

    @Nullable
    public static MCCEnchantment getByName(@Nullable String name) {
        return byName.get(name);
    }

    @NotNull
    public static MCCEnchantment[] values() {
        return byName.values().toArray(new MCCEnchantment[0]);
    }

    public static @NotNull org.bukkit.enchantments.Enchantment toEnchantment(MCCEnchantment enchantment) {
        if (!byKey.containsKey(enchantment.key)) throw new IllegalArgumentException("Enchantment not registered: " + enchantment.key);
        return new CraftEnchantment(byKey.get(enchantment.key).getHandle());

    }

    public static @NotNull MCCEnchantment toMCCEnchantment(org.bukkit.enchantments.Enchantment enchantment) {
        return new CraftMCCEnchantment(CraftEnchantment.getRaw(enchantment));

    }

}
