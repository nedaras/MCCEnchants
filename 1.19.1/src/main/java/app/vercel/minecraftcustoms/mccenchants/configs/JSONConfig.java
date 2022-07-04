package app.vercel.minecraftcustoms.mccenchants.configs;

import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

// TODO: we're not using defaults!
// But there is no true use case yet.
public class JSONConfig {

    private final @NotNull JavaPlugin plugin;
    private final @NotNull String fileName;
    private final @NotNull File configFile;
    private final boolean prettier;

    private JSONConfiguration config;


    public JSONConfig(@NotNull JavaPlugin plugin, @NotNull String fileName, boolean prettier) {

        this.plugin = plugin;
        this.fileName = fileName + ".json";
        this.configFile = new File(plugin.getDataFolder(), this.fileName);
        this.prettier = prettier;

        reloadConfig();

    }

    public JSONConfig(@NotNull JavaPlugin plugin, @NotNull String fileName) {
        this(plugin, fileName, true);

    }

    public @NotNull String getDataFolder() {
        return config.getDataFolder();

    }

    public void saveConfig() {

        try {

            getConfig().save(config);
            reloadConfig();

        } catch (IOException error) {
            plugin.getLogger().log(Level.SEVERE, "Could not save: " + plugin.getDataFolder() + "\\" + this.fileName, error.getMessage());

        }


    }

    public void saveConfig(Object object) {

        try {

            getConfig().save(object);
            reloadConfig();

        } catch (IOException error) {
            plugin.getLogger().log(Level.SEVERE, "Could not save: " + plugin.getDataFolder() + "\\" + this.fileName, error.getMessage());

        }


    }

    public void reloadConfig() {

        try {

            if (!configFile.exists()) {
                plugin.getLogger().log(Level.INFO, "Creating file from template: " + plugin.getDataFolder() + "\\" + this.fileName);
                plugin.saveResource(this.fileName, false);

            }

            config = JSONConfiguration.loadConfiguration(configFile, prettier);

        } catch (IOException error) {
            plugin.getLogger().log(Level.SEVERE,  "Could not load: " + plugin.getDataFolder() + "\\" + this.fileName, error.getMessage());

        }

    }

    public @NotNull JSONConfiguration getConfig() {
        if (config == null) reloadConfig();
        return config;

    }

}
