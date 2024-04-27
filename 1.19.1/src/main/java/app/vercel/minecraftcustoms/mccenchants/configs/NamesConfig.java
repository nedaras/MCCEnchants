package app.vercel.minecraftcustoms.mccenchants.configs;

import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class NamesConfig {

    // TODO: try ur best not to use config
    private final YamlConfig config;
    private final Map<Enchantment, String> names =  new HashMap<>();

    public NamesConfig(JavaPlugin plugin) {
        this.config = new YamlConfig(plugin, "names");
        reload();
    }

    public void reload() {
        config.reloadConfig();
        names.clear();

        for (String key : config.getConfig().getKeys(false)) {
             Enchantment enchantment = Enchantment.getByName(key);
             if (enchantment == null) continue;
             names.put(enchantment, ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(config.getConfig().getString(key))));
        }
    }

    public @NotNull String getEnchantmentName(Enchantment enchantment) {
        String name = names.get(enchantment);
        return name == null ? WordUtils.capitalizeFully(enchantment.getKey().getKey().replaceAll("_", " ")) : name;
    }

}
