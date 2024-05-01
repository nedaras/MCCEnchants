package app.vercel.minecraftcustoms.mccenchants.configs;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;

public class YamlConfig {

    private final @NotNull JavaPlugin plugin;
    private final @NotNull String fileName;
    private final @NotNull File configFile;

    private YamlConfiguration config;

    public YamlConfig(@NotNull JavaPlugin plugin, @NotNull String fileName) {

        this.plugin = plugin;
        this.fileName = fileName.replace("/", "\\") + ".yml";
        this.configFile = new File(plugin.getDataFolder(), this.fileName);

        reloadConfig();

    }

    public void saveConfig() {

        if (config == null) return;

        try {

            config.save(configFile);
            reloadConfig();

        } catch (IOException error) {
            plugin.getLogger().log(Level.SEVERE, "Could not save: " + plugin.getDataFolder() + "\\" + this.fileName, error.getMessage());

        }


    }

    public void reloadConfig() {

        if (!configFile.exists()) {
            plugin.getLogger().log(Level.INFO, "Creating file from template: " + plugin.getDataFolder() + "\\" + this.fileName);
            plugin.saveResource(this.fileName, false);

        }

        config = YamlConfiguration.loadConfiguration(configFile);

        InputStream defaultStream = plugin.getResource(fileName);

        if (defaultStream != null) {

            YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defaultStream));
            config.setDefaults(defaultConfig);

        }

    }

    public @NotNull YamlConfiguration getConfig() {
        if (config == null) reloadConfig();
        return config;

    }

}
