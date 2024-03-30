package app.vercel.minecraftcustoms.mccenchants.hooks;
import org.bukkit.craftbukkit.v1_20_R3.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.ItemMeta;

public class Test {

    public void addUnsafeEnchantment(CraftItemStack itemStack, Enchantment enchantment, int level) {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) return;
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.setDisplayName("Zdarowa senis!");
        itemStack.setItemMeta(meta);
        System.out.println("display name: " + meta.getDisplayName());
    }
}
