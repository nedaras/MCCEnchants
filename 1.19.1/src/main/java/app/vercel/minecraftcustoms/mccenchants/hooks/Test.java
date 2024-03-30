package app.vercel.minecraftcustoms.mccenchants.hooks;
import org.bukkit.craftbukkit.v1_20_R3.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.ItemMeta;

public class Test {

    // NOTE: Here can can only use net and bukkit function, we cant just modify the plugin simply or reference some variables, like this plugin's logger
    // NOTE: Don not overuse this functionality and add an option to disable it
    // PROBLEM: addUnsafeEnchantment is only called when we enchant with enchantment table, we need a function that likes changes item metas with enchantments
    // PROBLEM: new it override the function, we need some kind of debugging the check how the code looks, it pob gets overrides cuz last operation is return by default
    public void addUnsafeEnchantment(CraftItemStack itemStack, Enchantment enchantment, int level) {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) return;
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.setDisplayName("Zdarowa senis!");
        itemStack.setItemMeta(meta);
        System.out.println("display name: " + meta.getDisplayName());
    }
}
