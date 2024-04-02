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

        String HEADER = "" + ChatColor.RESET + ChatColor.UNDERLINE + ChatColor.RESET;
        String FOOTER = "" + ChatColor.RESET + ChatColor.STRIKETHROUGH + ChatColor.RESET;

        // we will remove r encoding cus it will just be removed
        char[] codes = new char[] {'0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f','k','l','m','n','o','r' };

        List<String> lore = new ArrayList<>();

        for (Map.Entry<Enchantment, Integer> entry : craftItemStack.getEnchantments().entrySet()) {
            net.minecraft.world.item.enchantment.Enchantment nmsEnchantment = CraftEnchantment.bukkitToMinecraft(entry.getKey());
            int id = BuiltInRegistries.ENCHANTMENT.getId(nmsEnchantment);
            StringBuilder encoded = new StringBuilder();
            while (id != 0) {
                int i = id % codes.length;
                encoded.insert(0, ChatColor.getByChar(codes[i]));
                id /= codes.length;
            }
            // spigot is removing my encoding they prob are doing to that to clear space
            // we could try to use persistent data prob or idk somehow set the lore with encoding
            String mustHaveEncodin = HEADER + encoded + FOOTER;

            // MAKE SO GOLD WOULD BE THERE AAA
            String l = entry.getKey().getKey() + " " + entry.getValue();

            MutableComponent currentComp = Component.empty();

            // will try to make my own component I fucking hope that spigot leaved the component away
            Style style = Style.EMPTY;

            // i think 3 color codes would be enough for storing &{1-2}&r
            // cuz like if ur adding lore ur self and before ur string there is that &r it will just be removed

            currentComp.append(Component.literal("").withStyle(style.withColor(ChatFormatting.RED)));
            currentComp.append(Component.literal("").withStyle(style.withUnderlined(Boolean.TRUE)));
            currentComp.append(Component.literal("").withStyle(style.withColor(ChatFormatting.RED)));

            // we will encode some shit here


            currentComp.append(Component.literal("").withStyle(style.withColor(ChatFormatting.RED)));
            currentComp.append(Component.literal("").withStyle(style.withStrikethrough(Boolean.TRUE)));
            currentComp.append(Component.literal("").withStyle(style.withItalic(Boolean.FALSE).withColor(ChatFormatting.RESET)));
            currentComp.append(Component.literal(l).withStyle(style.withItalic(Boolean.FALSE)));

            //currentComp.append(CraftChatMessage.fromString(l)[0]); // it appends the text with extra its bad we only need extras inside to append we will fix it in first place were looping in wrong position

            // we will get current lores component and then append

            CompoundTag compoundTag = itemStack.getTagElement("display"); // we need to create orCreate crashed idk why
            if (compoundTag == null) {
                lore.add(l);
                continue;
            }
            ListTag listTag = compoundTag.getList("Lore", 8);
            if (listTag == null) {
                System.out.println("we're null");
                listTag = new ListTag();
                compoundTag.put("Lore", listTag);
            }

            StringTag stringTag = StringTag.valueOf(CraftChatMessage.toJSON(currentComp));
            listTag.add(stringTag);

            CompoundTag tag = itemStack.getTag();
            if (tag == null) return;

            //System.out.println(listTag.getAsString());

            // when we will set the tag we need to pray that when we will recv it from meta the encoding will still be there
            //lore.add(CraftChatMessage.fromComponent(currentComp));
        }


        ItemMeta meta = craftItemStack.getItemMeta();
        if (!lore.isEmpty()) {
            meta.setLore(lore);
            craftItemStack.setItemMeta(meta);
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