package app.vercel.minecraftcustoms.mccenchants.enchantments;

import app.vercel.minecraftcustoms.mccenchants.api.enchantments.EnchantmentRarity;
import app.vercel.minecraftcustoms.mccenchants.api.enchantments.MCCEnchantment;
import app.vercel.minecraftcustoms.mccenchants.configs.JSONConfiguration;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

// TODO: override like fr, wtf is this bullshit
public class ConfigEnchantment extends MCCEnchantment {

    private @NotNull final NamespacedKey key;
    private final int maxLevel;

    public ConfigEnchantment(@NotNull MCCEnchantment enchantment, @NotNull JSONConfiguration config) {
        super(enchantment.getKey());
        this.key = enchantment.getKey();

        // TODO: make it smarter

        if (config.containsKey(enchantment.getName() + ".maxLevel")) {
            this.maxLevel = Integer.parseInt(config.getString(enchantment.getName() + ".maxLevel"));
            return;

        }

        this.maxLevel = enchantment.getMaxLevel();

//
//        List<int[]> levels = new ArrayList<>();
//        boolean isCursed = config.getBoolean(enchantment.getName() + ".isCursed");
//
//        JsonArray array = config.getJsonList(enchantment.getName() + ".costs");
//
//        for (int i = 0; i < array.size(); i++) {
//
//            JsonPrimitive primitive = array.get(i).isJsonPrimitive() ? array.get(i).getAsJsonPrimitive() : null;
//
//            if (primitive != null && primitive.isString() && primitive.getAsString().equals("generated")) {
//                levels.add(new int[]{ enchantment.getMinCost(i + 1), enchantment.getMaxCost(i + 1) });
//                continue;
//            }
//
//            List<Integer> storedArray = config.getIntegerList(array.get(i));
//            levels.add(new int[]{ storedArray.get(0), storedArray.get(1) });
//
//        }
//
//        this.levels = levels;
//        this.isCursed = isCursed;

    }

    private @NotNull MCCEnchantment getEnchantment() {
        return Objects.requireNonNull(MCCEnchantment.getByKey(key));

    }

    @Override
    public @NotNull String getName() {
        return getEnchantment().getName();
    }

    @Override
    public int getMaxLevel() {
        return 2;
    }

    @Override
    public int getStartLevel() {
        return 1;
    }

    @Override
    public @NotNull EnchantmentRarity getRarity() {
        return getEnchantment().getRarity();
    }

    @Override
    public int getMinCost(int level) {
        return getEnchantment().getMinCost(level);
    }

    @Override
    public int getMaxCost(int level) {
        return getEnchantment().getMaxCost(level);
    }

    @Override
    public @NotNull EnchantmentTarget getItemTarget() {
        return getEnchantment().getItemTarget();
    }

    @Override
    public boolean isTreasure() {
        return getEnchantment().isTreasure();
    }

    @Override
    public boolean isCursed() {
        return getEnchantment().isCursed();
    }

    @Override
    public boolean isTradable() {
        return getEnchantment().isTradable();
    }

    @Override
    public boolean isDiscoverable() {
        return getEnchantment().isDiscoverable();
    }

    @Override
    public boolean conflictsWith(@NotNull MCCEnchantment enchantment) {
        return getEnchantment().conflictsWith(enchantment);
    }

    @Override
    public boolean canEnchantItem(@NotNull ItemStack item) {
        return getEnchantment().canEnchantItem(item);
    }
}
