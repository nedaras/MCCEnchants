package app.vercel.minecraftcustoms.mccenchants.api.enchantments;

import app.vercel.minecraftcustoms.mccenchants.Main;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.Objects;
import java.util.logging.Level;

public class MCCEnchantmentConfig {

    private final JavaPlugin plugin;
    private final YamlConfiguration config;

    public MCCEnchantmentConfig(NamespacedKey key, JavaPlugin plugin) {

        this.plugin = plugin;

        JavaPlugin MCCEnchants = Main.getInstance();
        File file = new File(MCCEnchants.getDataFolder(), "enchantments\\" + key.getKey() + ".yml");

        if (!file.exists()) {

            plugin.getLogger().log(Level.INFO, "Creating file from template: " + MCCEnchants.getDataFolder() + "\\enchantments\\" + key.getKey() + ".yml");
            saveResource(key.getKey() + ".yml", "enchantments\\", plugin);

        }

        this.config = YamlConfiguration.loadConfiguration(file);

        InputStream defaultStream = plugin.getResource(key.getKey() + ".yml");

        if (defaultStream != null) {

            YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defaultStream));
            config.setDefaults(defaultConfig);

        }

    }

    public @NotNull String getDisplayName() {
        String displayName = Objects.requireNonNull(getConfig().getString("displayName"));
        return ChatColor.translateAlternateColorCodes('&', displayName);

    }

    public int getMaxLevel() {
        return getConfig().getInt("maxLevel");

    }

    public @NotNull EnchantmentRarity getEnchantmentRarity() {

        switch (Objects.requireNonNull(getConfig().getString("rarity"))) {

            case "COMMON": return EnchantmentRarity.COMMON;
            case "UNCOMMON": return EnchantmentRarity.UNCOMMON;
            case "RARE": return EnchantmentRarity.RARE;
            case "VERY_RARE": return EnchantmentRarity.VERY_RARE;
            default: {
                plugin.getLogger().severe("Enchantment Rarity doesn't exist: " + getConfig().getString("rarity"));
                return EnchantmentRarity.VERY_RARE;

            }

        }

    }

    public int getMinCost(int level) {

        if (config.get("minCost") instanceof String) {

            MCCEnchantment enchantment = MCCEnchantment.getByName(getConfig().getString("minCost"));

            if (enchantment == null) {

                plugin.getLogger().severe("Enchantment not doesn't exist on minCost: " + getConfig().getString("minCost"));
                return 0;

            }

            return enchantment.getMinCost(level);

        }

        return getConfig().getIntegerList("minCost").get(level - 1);

    }

    public int getMaxCost(int level) {

        if (config.get("maxCost") instanceof String) {

            MCCEnchantment enchantment = MCCEnchantment.getByName(getConfig().getString("maxCost"));

            if (enchantment == null) {

                plugin.getLogger().severe("Enchantment not doesn't exist on minCost: " + getConfig().getString("maxCost"));
                return 0;

            }

            return enchantment.getMaxCost(level);

        }

        return getConfig().getIntegerList("maxCost").get(level - 1);

    }

    public boolean isTreasure() {
        return getConfig().getBoolean("treasure");

    }

    public boolean isCursed() {
        return getConfig().getBoolean("cursed");

    }

    public boolean isTradable() {
        return getConfig().getBoolean("tradable");

    }

    public @NotNull YamlConfiguration getConfig() {
        return config;

    }

    private void saveResource(@NotNull String resourcePath, @NotNull String to, JavaPlugin plugin) {

        resourcePath = resourcePath.replace('\\', '/');
        to = to.replace('\\', '/');
        InputStream in = plugin.getResource(resourcePath);

        if (in == null) {
            throw new IllegalArgumentException("The embedded resource '" + resourcePath + "' cannot be found in ");
        }

        File outFile = new File(Main.getInstance().getDataFolder(), to + resourcePath);
        int lastIndex = resourcePath.lastIndexOf('/');
        File outDir = new File(Main.getInstance().getDataFolder(), to + resourcePath.substring(0, Math.max(lastIndex, 0)));

        if (!outDir.exists()) outDir.mkdirs();

        try {
            if (!outFile.exists()) {
                OutputStream out = new FileOutputStream(outFile);
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                out.close();
                in.close();
            } else {
                plugin.getLogger().log(Level.WARNING, "Could not save " + outFile.getName() + " to " + outFile + " because " + outFile.getName() + " already exists.");
            }
        } catch (IOException ex) {
            plugin.getLogger().log(Level.SEVERE, "Could not save " + outFile.getName() + " to " + outFile, ex);
        }
    }

}
