package app.vercel.minecraftcustoms.mccenchants.hooks;

import com.sun.tools.attach.VirtualMachine;
import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.craftbukkit.v1_20_R3.enchantments.CraftEnchantment;
import org.bukkit.craftbukkit.v1_20_R3.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Hooks {

    public static void init()
    {

        try {

            String pid = ManagementFactory.getRuntimeMXBean().getName();
            pid = pid.substring(0, pid.indexOf("@"));

            VirtualMachine  vm = VirtualMachine.attach(pid);
            File thiz = new File(Hooks.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
            vm.loadAgent(thiz.getAbsolutePath());
            vm.detach();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    // TODO: we need some kind of flag like is mcc enchants hidden this is for later and another thing we would then need to track flag changes

    // we will just match the strings cuz its most stable way fuck u spigot for removing hidden lore
    // some nbt data could be used but fuck that
    public void enchant(net.minecraft.world.item.ItemStack itemStack, net.minecraft.world.item.enchantment.Enchantment enchantment, int level) { // THIS MUST BE HOOKED
        CraftItemStack craftItemStack = CraftItemStack.asCraftMirror(itemStack);
        ItemMeta meta = craftItemStack.getItemMeta();

        if (meta == null) return;
        List<String> lore = meta.getLore();
        List<String> newLore = new ArrayList<>();

        if (lore == null) lore = new ArrayList<>();

        for (Map.Entry<Enchantment, Integer> entry : craftItemStack.getEnchantments().entrySet()) {
            Enchantment bukkitEnchantment = entry.getKey();
            net.minecraft.world.item.enchantment.Enchantment minecraftEnchantment = CraftEnchantment.bukkitToMinecraft(bukkitEnchantment);

            ChatColor color = minecraftEnchantment.isCurse() ? ChatColor.RED : ChatColor.GRAY;
            // this is bad like curse of vanishing is vanishing curse and binding curse luck of the sea is wrong sweeping edge for now f it
            // they have to be stored somewhere cuz like console knows the names
            String enchantmentName = WordUtils.capitalizeFully(bukkitEnchantment.getKey().getKey().replace("_", " "));
            String levelName = "";

            if (entry.getValue() != 1 || minecraftEnchantment.getMaxLevel() != 1) {
                levelName = switch (entry.getValue()) {
                    case 1 -> " " + ChatColor.GRAY + "I";
                    case 2 -> " " + ChatColor.GRAY + "II";
                    case 3 -> " " + ChatColor.GRAY + "III";
                    case 4 -> " " + ChatColor.GRAY + "IV";
                    case 5 -> " " + ChatColor.GRAY + "V";
                    case 6 -> " " + ChatColor.GRAY + "VI";
                    default -> " " + ChatColor.GRAY + "net.minecraft.level." + entry.getValue();
                };
            }
            // wait this work so mb we can encode with color... like if it would ahh it would be a dream come true
            newLore.add(color + enchantmentName + levelName);
        }

        for (String line : lore) {
            String enchantmentString = "minecraft:" + ChatColor.stripColor(line.split(" " + ChatColor.GRAY)[0]).toLowerCase().replace(" ", "_");

            NamespacedKey namespacedKey = NamespacedKey.fromString(enchantmentString);
            if (namespacedKey != null && Registry.ENCHANTMENT.get(namespacedKey) != null) continue;

            newLore.add(line);
        }

        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.setLore(newLore);
        craftItemStack.setItemMeta(meta);
    }

    // this one works with enchantment table and if bukkit api would enchant the item
    // to note enchantments are stored in alphabetic order so i mena i can make these algorithms faster but im lazy for now

    // do we need the remove enchantment shit?
    public void addUnsafeEnchantment(CraftItemStack craftItemStack, Enchantment enchantment, int level) { // THIS MUST BE HOOKED
        ItemMeta meta = craftItemStack.getItemMeta();

        if (meta == null) return;
        List<String> lore = meta.getLore();
        List<String> newLore = new ArrayList<>();

        if (lore == null) lore = new ArrayList<>();

        for (Map.Entry<Enchantment, Integer> entry : craftItemStack.getEnchantments().entrySet()) {
            Enchantment bukkitEnchantment = entry.getKey();
            net.minecraft.world.item.enchantment.Enchantment minecraftEnchantment = CraftEnchantment.bukkitToMinecraft(bukkitEnchantment);

            ChatColor color = minecraftEnchantment.isCurse() ? ChatColor.RED : ChatColor.GRAY;
            String enchantmentName = WordUtils.capitalizeFully(bukkitEnchantment.getKey().getKey().replace("_", " "));
            String levelName = "";

            if (entry.getValue() != 1 || minecraftEnchantment.getMaxLevel() != 1) {
                levelName = switch (entry.getValue()) {
                    case 1 -> " " + ChatColor.GRAY + "I";
                    case 2 -> " " + ChatColor.GRAY + "II";
                    case 3 -> " " + ChatColor.GRAY + "III";
                    case 4 -> " " + ChatColor.GRAY + "IV";
                    case 5 -> " " + ChatColor.GRAY + "V";
                    case 6 -> " " + ChatColor.GRAY + "VI";
                    default -> " " + ChatColor.GRAY + "net.minecraft.level." + entry.getValue();
                };
            }
            // wait this work so mb we can encode with color... like if it would ahh it would be a dream come true
            newLore.add(color + enchantmentName + levelName);
        }

        for (String line : lore) {
            String enchantmentString = "minecraft:" + ChatColor.stripColor(line.split(" " + ChatColor.GRAY)[0]).toLowerCase().replace(" ", "_");

            NamespacedKey namespacedKey = NamespacedKey.fromString(enchantmentString);
            if (namespacedKey != null && Registry.ENCHANTMENT.get(namespacedKey) != null) continue;

            newLore.add(line);
        }

        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.setLore(newLore);
        craftItemStack.setItemMeta(meta);
    }

}
