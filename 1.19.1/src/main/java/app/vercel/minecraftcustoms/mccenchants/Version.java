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

    // if you change this to true you will get premium for free :D
    private static final boolean premium = false;

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

    private static void checkForUpdates(JavaPlugin plugin) {

        try {

            String currentVersion = "v1.0.1-RELEASE";

            URL url = new URL("https://minecraftcustoms.vercel.app/api/versions/" + plugin.getName().toLowerCase());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("GET");
            connection.setConnectTimeout(15000);
            connection.setReadTimeout(15000);

            if (connection.getResponseCode() != 200) throw new Exception("failed fetch");

            StringBuilder stringBuilder = new StringBuilder();

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;

            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);

            }

            reader.close();
            connection.disconnect();

            int amount = new Gson().fromJson(stringBuilder.toString(), Plugin.class).getOutdatedVersionAmount(currentVersion);

            if (amount == 0) {
                plugin.getLogger().info(plugin.getName() + " plugin is up to date!");
                return;

            }

            String downloadURL = premium ? "https://www.spigotmc.org/resources/mccenchants.104731/" : "https://github.com/nedaras/MCCEnchants";
            plugin.getLogger().info( plugin.getName() + " plugin is outdated by (" + amount + ") versions, update here: " + downloadURL);

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

        public int getOutdatedVersionAmount(@NotNull String current) {

            int i = 0;

            for (String version : versions) {
                if (version.equals(current)) return i;
                i++;

            }

            throw new IllegalStateException("Version not matched.");

        }

    }

}
