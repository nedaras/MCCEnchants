package app.vercel.minecraftcustoms.mccenchants.events;

import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.craftbukkit.v1_20_R3.enchantments.CraftEnchantment;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.PrepareInventoryResultEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OnPrepareInventoryResultEvent implements Listener {

    // cuz we need to modify it first I guess
    @EventHandler(priority = EventPriority.LOWEST)
    void onPrepareInventoryResultEvent(PrepareInventoryResultEvent event) {
        if (!(event.getInventory().getType() == InventoryType.ANVIL || event.getInventory().getType() == InventoryType.GRINDSTONE)) return;

        ItemStack craftItemStack = event.getResult();
        if (craftItemStack == null) return;

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
