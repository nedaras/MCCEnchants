package app.vercel.minecraftcustoms.mccenchants.hooks;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.*;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_20_R3.enchantments.CraftEnchantment;
import org.bukkit.craftbukkit.v1_20_R3.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_20_R3.util.CraftChatMessage;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Test {

    // NOTE: books dont work now
    // NOTE: Here can can only use net and bukkit function, we cant just modify the plugin simply or reference some variables, like this plugin's logger
    // NOTE: Don not overuse this functionality and add an option to disable it
    // NOTE: Lets try to redirect all these calls to like CraftBukkit addEnchantment or addUnsafeEnchantment to make it simplier
    // PROBLEM: addUnsafeEnchantment is only called when we enchant with enchantment table, we need a function that likes changes item metas with enchantments
    // PROBLEM: new it override the function, we need some kind of debugging the check how the code looks, it pob gets overrides cuz last operation is return by default

    // ItemStack enchant get called when loot tables generated, trades and on that /enchant call, only no EnchantmentTable, but there is event for that.
    public void enchant(net.minecraft.world.item.ItemStack itemStack, net.minecraft.world.item.enchantment.Enchantment enchantment, int level) { // THIS MUST BE HOOKED
        // we will use color codes to store some data, why?
        // future problem how would we get custom enchant names?
        CraftItemStack craftItemStack = CraftItemStack.asCraftMirror(itemStack);

        // we need to add those letter too so we could store more enchantment
        // now we can only store 100 enchantment 10^2 why we cant store letter for some reason the &r removed these letters but numbers can stack
        // we should not leave this to base 10 like cmon we could use base 22 or atleast base 21 from 100 enchants we could store about 500
        char[] codes = new char[] {'0','1','2','3','4','5','6','7','8','9', 'a','b','c','d','e','f','k','l','m','n','o', 'r' };

        for (Map.Entry<Enchantment, Integer> entry : craftItemStack.getEnchantments().entrySet()) {
            net.minecraft.world.item.enchantment.Enchantment nmsEnchantment = CraftEnchantment.bukkitToMinecraft(entry.getKey());
            int id = BuiltInRegistries.ENCHANTMENT.getId(nmsEnchantment);

            StringBuilder encoded = new StringBuilder();

            //ChatFormatting[] encoded = new ChatFormatting[2];
            for (char pos = 0; pos < 2; pos++) {
                int i = id % codes.length;
                encoded.insert(0, ChatColor.getByChar(codes[i]));
                //encoded[encoded.length - 1 - pos] = ChatFormatting.getByCode(codes[i]);
                id /= codes.length;
            }

            String l = entry.getKey().getKey() + " " + entry.getValue();

            MutableComponent component = Component.empty();
            Style style = Style.EMPTY;

            //component.append(Component.literal("").withStyle(style.withColor(encoded[0])));
            //component.append(Component.literal("").withStyle(style.withColor(encoded[1])));

            // why the fuck spigot makes it into that three then? like this way we're storing so much less data
            // should we use italic false cuz thats how spigot does it.
            component.append(Component.literal(encoded.toString() + ChatColor.RESET).withStyle(style));
            //component.append(Component.literal("").withStyle(style.withColor(ChatFormatting.RESET)));
            //component.append(Component.literal(l).withStyle(style)); // this is shit
            component.append(CraftChatMessage.fromString(l)[0]); // I mean it works but this aint the spigot way it has that extra in extra

            CompoundTag display = itemStack.getOrCreateTagElement("display");
            if (!display.contains("Lore")) display.put("Lore", new ListTag());

            ListTag lore = display.getList("Lore", ListTag.TAG_STRING);
            lore.add(StringTag.valueOf(CraftChatMessage.toJSON(component)));
        }

        if (!craftItemStack.getItemMeta().hasLore()) return;
        List<String> newLore = craftItemStack.getItemMeta().getLore();
        for (String l : newLore) {
            System.out.println(l);
        }
        CompoundTag tag = itemStack.getTag();
        if (tag == null) return;
        System.out.println(tag.getAsString());
    }

    // this one works with anvil and grindstone
    public void addTagElement(net.minecraft.world.item.ItemStack itemStack, String key, Tag value) { // THIS MUST BE HOOKED
/*
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
            bukkitItemStack.addEnchantment(enchantment, level); // we will remove this black magic
        }
*/
    }

    // this one works with enchantment table and if bukkit api would enchant the item
    public void addUnsafeEnchantment(CraftItemStack itemStack, org.bukkit.enchantments.Enchantment enchantment, int level) { // THIS MUST BE HOOKED
        itemStack.removeEnchantments();
    }
}