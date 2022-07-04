package app.vercel.minecraftcustoms.mccenchants;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.logging.Level;

public class Version {

    private static @NotNull String version = "N/A";
    private static @Nullable JavaPlugin plugin;
    private static boolean alreadySet = false;

    public static void setVersion(JavaPlugin plugin) {
        if (alreadySet) throw new IllegalArgumentException("Version is already set.");

        try {

            version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];

        } catch (ArrayIndexOutOfBoundsException error) {
            plugin.getLogger().log(Level.SEVERE, "Could not get minecraft version, are you sure that this is spigot/paper?", error.getMessage());

        }

        Version.plugin = plugin;
        alreadySet = true;

    }

    public static boolean isSupported() {
        if (plugin == null) throw new IllegalArgumentException("Could not get version: Version is not set.");

        boolean supported = "v1_18_R2".equals(version);
        if (!supported) plugin.getLogger().severe("You minecraft server does not support MCCEnchants plugin.");

        return supported;

    }

}
