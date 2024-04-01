package app.vercel.minecraftcustoms.mccenchants.hooks;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.craftbukkit.v1_20_R3.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Test {


    // NOTE: books dont work now
    // NOTE: Here can can only use net and bukkit function, we cant just modify the plugin simply or reference some variables, like this plugin's logger
    // NOTE: Don not overuse this functionality and add an option to disable it
    // NOTE: Lets try to redirect all these calls to like CraftBukkit addEnchantment or addUnsafeEnchantment to make it simplier
    // PROBLEM: addUnsafeEnchantment is only called when we enchant with enchantment table, we need a function that likes changes item metas with enchantments
    // PROBLEM: new it override the function, we need some kind of debugging the check how the code looks, it pob gets overrides cuz last operation is return by default

    // ItemStack enchant get called when loot tables generated, trades and on that /enchant call, only no EnchantmentTable, but there is event for that.
    public void enchant(net.minecraft.world.item.ItemStack nms, net.minecraft.world.item.enchantment.Enchantment enchantment, int level) { // THIS MUST BE HOOKED or mb tag.put
        ItemStack itemStack = CraftItemStack.asBukkitCopy(nms);
        ItemMeta meta = itemStack.getItemMeta();

        if (meta == null) return;

        System.out.println("enchantment: " + enchantment.getFullname(1).getString());
        System.out.println("display name: " + meta.getDisplayName());
    }

    // this one works with enchantment table and if bukkit api would enchant the item
    public void addUnsafeEnchantment(CraftItemStack itemStack, org.bukkit.enchantments.Enchantment enchantment, int level) {
        itemStack.removeEnchantments();
    }

    // this one works with anvil and grindstone
    public void addTagElement(net.minecraft.world.item.ItemStack itemStack, String key, Tag value) { // THIS MUST BE HOOKED
        if (!key.equals("Enchantments")) return;
        if (!(value instanceof ListTag list)) return;

        ItemStack bukkitItemStack = CraftItemStack.asBukkitCopy(itemStack);;

        for (Tag item : list) {

            CompoundTag tag = (CompoundTag) item;

            NamespacedKey namespacedKey = NamespacedKey.fromString(tag.getString("id")); // COULD BE GOOD TO USE CraftItemMeta
            int level = '\uffff' & tag.getShort("lvl");

            if (namespacedKey == null) continue;
            Enchantment enchantment = Registry.ENCHANTMENT.get(namespacedKey);
            if (enchantment == null) continue;

            // now at the end addUnsafeEnchantment will be called it should not call this function never
            // we can safely there like process our logic
            // but to say this is bad behavior and we should handle that logic down here
            bukkitItemStack.addEnchantment(enchantment, level);
        }
    }
}