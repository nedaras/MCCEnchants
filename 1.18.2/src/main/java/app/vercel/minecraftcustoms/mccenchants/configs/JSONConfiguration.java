package app.vercel.minecraftcustoms.mccenchants.configs;

import com.google.gson.*;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.util.*;

public class JSONConfiguration {

    private final @NotNull Gson gson;
    private final @Nullable JsonObject json;
    private final @NotNull File file;

    private JSONConfiguration(@NotNull Gson gson, @NotNull File file) throws IOException {

        this.json = gson.fromJson(new FileReader(file), JsonObject.class);
        this.gson = gson;
        this.file = file;

    }

    public static @NotNull JSONConfiguration loadConfiguration(@NotNull File file, boolean prettier) throws IOException {

        Gson gson = prettier ? new GsonBuilder().setPrettyPrinting().create() : new Gson();

        return new JSONConfiguration(gson, file);

    }

    private @NotNull JsonElement getValueFromPath(@NotNull String path) throws IllegalArgumentException {

        String[] segments = path.split("\\.");
        JsonObject json = this.json;

        for (int i = 0; i < segments.length; i++) {
            if (json == null) throw new IllegalArgumentException("Value of " + path + " is undefined.");

            String segment = segments[i];
            JsonElement element = json.get(segment);

            if (i == segments.length - 1) {
                if (element != null) return element;
                throw new IllegalArgumentException("Value of " + path + " is undefined.");

            }

            json = element.getAsJsonObject();

        }

        throw new IllegalArgumentException("Value of " + path + " is undefined.");

    }

    public void setDefaults() {


    }

    public @Nullable Set<String> getKeys(@NotNull String path) {

        Set<String> keys = new HashSet<>();

        for (Map.Entry<String, JsonElement> entry : getElement(path).getAsJsonObject().entrySet()) {
            keys.add(entry.getKey());

        }

        return keys;

    }

    public @NotNull Set<String> getDefaultKeys() {

        Set<String> keys = new HashSet<>();
        if (json == null) return keys;

        json.entrySet().forEach((entry) -> keys.add(entry.getKey()));

        return keys;

    }

    public boolean containsKey(String key) {
        return getDefaultKeys().contains(key);

    }

    public boolean contains(String path) {

        try {

            getElement(path);
            return true;

        } catch (NullPointerException | IllegalArgumentException __) {

            return false;

        }

    }

    public boolean isNull(String path) {
        if (!contains(path)) return true;
        return getElement(path).isJsonNull();

    }

    public @Nullable String getString(@NotNull String path) {

        JsonElement element = getValueFromPath(path);

        if (!element.isJsonNull()) return ChatColor.translateAlternateColorCodes('&', element.getAsString());
        return null;

    }

    public @NotNull String getString(@NotNull String path, @NotNull String defaultString) {

        if (!isNull(path)) return getString(path);
        return ChatColor.translateAlternateColorCodes('&', defaultString);

    }

    public @NotNull JsonElement getElement(@NotNull String path) throws NullPointerException {
        return getValueFromPath(path);

    }

    public boolean getBoolean(@NotNull String path) {

        if (!isNull(path)) return getElement(path).getAsBoolean();
        return false;

    }

    public @NotNull JsonArray getJsonList(@NotNull String path) {

        JsonElement element = getValueFromPath(path);
        return element.getAsJsonArray();

    }
    public @NotNull List<Integer> getIntegerList(@NotNull JsonElement array) {

        List<Integer> integerList = new ArrayList<>();
        array.getAsJsonArray().forEach(element -> integerList.add(element.getAsInt()));

        return integerList;

    }

    public @Nullable String[] getStringList(@NotNull String path) {
        JsonArray list = getValueFromPath(path).getAsJsonArray();
        String[] array = new String[list.size()];

        for (int i = 0; i < array.length; i++) {
            JsonElement element = list.get(i);

            if (element.isJsonNull()) array[i] = null;
            else array[i] = ChatColor.translateAlternateColorCodes('&', element.getAsString());

        }

        return array;

    }

    public @NotNull String[] getStringList(@NotNull String path, String[] defaultStringList) {
        if (!isNull(path)) return getStringList(path);
        return defaultStringList;

    }

    public @NotNull String getDataFolder() {
        return file.getPath();

    }

    public void save(@NotNull JSONConfiguration config) throws IOException {
        Writer writer = new FileWriter(file);

        gson.toJson(config.json, writer);

        writer.flush();
        writer.close();

    }

    public void save(@NotNull Object object) throws IOException {
        Writer writer = new FileWriter(file);

        gson.toJson(object, writer);

        writer.flush();
        writer.close();

    }


}
