package app.vercel.minecraftcustoms.mccenchants;

import com.google.gson.Gson;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.logging.Level;

public class Version {

    private static @NotNull String version = "N/A";
    private static @Nullable JavaPlugin plugin;
    private static boolean alreadySet = false;

    public static void setVersion(JavaPlugin plugin) {
        if (alreadySet) throw new IllegalArgumentException("Version is already set.");

        try {

            version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];

            // why should this be synced?
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> checkForUpdates(plugin));

        } catch (ArrayIndexOutOfBoundsException error) {
            plugin.getLogger().log(Level.SEVERE, "Could not get minecraft version, are you sure that this is spigot/paper?", error.getMessage());

        }

        Version.plugin = plugin;
        alreadySet = true;

    }

    // IT needs to check better.
    // for pre versions, that 1.19 is technically the latest, but 1.18.2 is the latest for the 1.18.2 server
    private static void checkForUpdates(JavaPlugin plugin) {

        try {

            String currentVersion = "1.19-R0.1-SNAPSHOT";

            URL url = new URL("https://minecraftcustoms.vercel.app/api/versions/" + plugin.getName().toLowerCase());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            if (connection.getResponseCode() != 200) throw new Exception("failed fetch");

            StringBuilder stringBuilder = new StringBuilder();

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;

            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);

            }

            reader.close();
            connection.disconnect();

            List<String> versions = new Gson().fromJson(stringBuilder.toString(), Plugin.class).versions;

            if (versions.get(0).equals(currentVersion)) {
                plugin.getLogger().info(plugin.getName() + " plugin is up to date!");
                return;

            }

            plugin.getLogger().info( plugin.getName() + " plugin is outdated, update here: https://minecraftcustoms.vercel.app/download/custom-enchants");

        } catch (Exception __) {
            plugin.getLogger().warning("Could fetch latest version of " + plugin.getName());

        }
    }

    public static boolean isSupported() {
        if (plugin == null) throw new IllegalArgumentException("Could not get version: Version is not set.");

        boolean supported = "v1_19_R1".equals(version);
        if (!supported) plugin.getLogger().severe("You minecraft server does not support MCCEnchants plugin.");

        return supported;

    }

    private static class Plugin {

        List<String> versions;

    }

}
