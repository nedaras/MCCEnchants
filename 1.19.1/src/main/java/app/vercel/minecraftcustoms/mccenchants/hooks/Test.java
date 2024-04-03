package app.vercel.minecraftcustoms.mccenchants.hooks;
import app.vercel.minecraftcustoms.mccenchants.enchantments.NMSEnchantment;
import net.minecraft.nbt.Tag;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.craftbukkit.v1_20_R3.enchantments.CraftEnchantment;
import org.bukkit.craftbukkit.v1_20_R3.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class Test {

    // TODO: we need some kind of flag like is mcc enchants hidden this is for later and another thing we would then need to track flag changes

    // we will just match the strings cuz its most stable way fuck u spigot for removing hidden lore
    // some nbt data could be used but fuck that
    public void enchant(net.minecraft.world.item.ItemStack itemStack, net.minecraft.world.item.enchantment.Enchantment enchantment, int level) { // THIS MUST BE HOOKED
        // well this is one way to getName idk why but i cant use NMSEnchantment in this code it will break it some like class not found exception
        // well i guess what i can do is load the class no? using current loader but tbh this way is cool too i mean its probably the same thing even in case of efficiency
        Method method = null;
        try { // catch is not computed or sum
            method = enchantment.getClass().getMethod("getName");
            //if (getName.getReturnType().equals(String.class)) {
                //System.out.println((String) getName.invoke(enchantment));
            //}
            System.out.println("get name is");
        } catch (Exception e) {
            System.out.println("no get name");
        }

        System.out.println(method);

        CraftItemStack craftItemStack = CraftItemStack.asCraftMirror(itemStack);
        ItemMeta meta = craftItemStack.getItemMeta();

        if (meta == null) return;
        List<String> lore = meta.getLore();
        List<String> newLore = new ArrayList<>();

        if (lore == null) lore = new ArrayList<>();

        // cuz like enchantments has to be first but this is stupid we need to implement some other way like leave enchantments where there are in lore
        // and we need a way to like add new enchantments
        for (Map.Entry<Enchantment, Integer> entry : craftItemStack.getEnchantments().entrySet()) {
            newLore.add(entry.getKey().getKey() + " " + entry.getValue());
        }

        for (String line : lore) {
            String stripped = ChatColor.stripColor(line);
            String enchantmentString = stripped.split(" ", 1)[0];

            NamespacedKey namespacedKey = NamespacedKey.fromString(enchantmentString);
            if (namespacedKey == null) continue;
            if (Registry.ENCHANTMENT.get(namespacedKey) != null) continue;

            newLore.add(line);
        }

        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.setLore(newLore);
        craftItemStack.setItemMeta(meta);
    }

    // this one works with anvil and grindstone
    public void addTagElement(net.minecraft.world.item.ItemStack itemStack, String key, Tag value) { // THIS MUST BE HOOKED
        if (!key.equals("Enchantments")) return;

        CraftItemStack craftItemStack = CraftItemStack.asCraftMirror(itemStack);
        ItemMeta meta = craftItemStack.getItemMeta();

        if (meta == null) return;
        List<String> lore = meta.getLore();
        List<String> newLore = new ArrayList<>();

        if (lore == null) lore = new ArrayList<>();

        for (Map.Entry<Enchantment, Integer> entry : craftItemStack.getEnchantments().entrySet()) {
            newLore.add(entry.getKey().getKey() + " " + entry.getValue());
        }

        for (String line : lore) {
            String stripped = ChatColor.stripColor(line);
            String enchantmentString = stripped.split(" ", 1)[0];

            NamespacedKey namespacedKey = NamespacedKey.fromString(enchantmentString);
            if (namespacedKey == null) continue;
            if (Registry.ENCHANTMENT.get(namespacedKey) != null) continue;

            newLore.add(line);
        }

        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.setLore(newLore);
        craftItemStack.setItemMeta(meta);
    }

    // this one works with enchantment table and if bukkit api would enchant the item
    // to note enchantments are stored in alphabetic order so i mena i can make these algorithms faster but im lazy for now
    public void addUnsafeEnchantment(CraftItemStack craftItemStack, Enchantment enchantment, int level) { // THIS MUST BE HOOKED
        ItemMeta meta = craftItemStack.getItemMeta();

        if (meta == null) return;
        List<String> lore = meta.getLore();
        List<String> newLore = new ArrayList<>();

        if (lore == null) lore = new ArrayList<>();

        // cuz like enchantments has to be first but this is stupid we need to implement some other way like leave enchantments where there are in lore
        // and we need a way to like add new enchantments
        for (Map.Entry<Enchantment, Integer> entry : craftItemStack.getEnchantments().entrySet()) {
            newLore.add(entry.getKey().getKey() + " " + entry.getValue());
        }

        for (String line : lore) {
            String stripped = ChatColor.stripColor(line);
            String enchantmentString = stripped.split(" ", 1)[0];

            NamespacedKey namespacedKey = NamespacedKey.fromString(enchantmentString);
            if (namespacedKey == null) continue;
            if (Registry.ENCHANTMENT.get(namespacedKey) != null) continue;

            newLore.add(line);
        }

        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.setLore(newLore);
        craftItemStack.setItemMeta(meta);
    }
}
