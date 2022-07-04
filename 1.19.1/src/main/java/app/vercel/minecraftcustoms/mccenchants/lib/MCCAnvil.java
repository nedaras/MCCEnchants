package app.vercel.minecraftcustoms.mccenchants.lib;

import app.vercel.minecraftcustoms.mccenchants.api.helpers.MCCEnchanting;
import net.minecraft.world.inventory.ContainerAnvil;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_19_R1.enchantments.CraftEnchantment;
import org.bukkit.craftbukkit.v1_19_R1.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.enchantments.EnchantmentWrapper;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.Repairable;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Objects;

// TODO: make more readable and more modern
public class MCCAnvil {

    public static int MAX_REPAIR_COST = 40;

    public static void repairItem(@NotNull ItemStack firstItem, @NotNull ItemStack secondItem) {

        if (!EnchantmentTarget.BREAKABLE.includes(firstItem)) return;

        net.minecraft.world.item.ItemStack firstCraftItem = CraftItemStack.asNMSCopy(firstItem);
        net.minecraft.world.item.ItemStack secondCraftItem = CraftItemStack.asNMSCopy(secondItem);

        Damageable firstItemMeta = (Damageable) Objects.requireNonNull(firstItem.getItemMeta());
        boolean needsRepair = getRepairCost(firstItem, secondItem) > 0;

        if ((firstCraftItem.c().a(firstCraftItem, secondCraftItem))) {

            int repairedDurability = Math.min(firstItemMeta.getDamage(), firstItem.getType().getMaxDurability() / 4);
            if (repairedDurability <= 0) return;

            for (int i = 0; repairedDurability > 0 && i < secondCraftItem.J(); i++) {
                int damage = firstItemMeta.getDamage() - repairedDurability;
                firstItemMeta.setDamage(damage);

                repairedDurability = Math.min(firstItemMeta.getDamage(), firstItem.getType().getMaxDurability() / 4);

            }

        }

        if (EnchantmentTarget.BREAKABLE.includes(secondItem) && firstItem.getType() == secondItem.getType()) {

            Damageable secondItemMeta = (Damageable) Objects.requireNonNull(secondItem.getItemMeta());

            int firstItemDurability = firstItem.getType().getMaxDurability() - firstItemMeta.getDamage();
            int secondDurability = secondItem.getType().getMaxDurability() - secondItemMeta.getDamage();

            int bonus = secondDurability + firstItem.getType().getMaxDurability() * 12 / 100;
            int damage = Math.max(firstItem.getType().getMaxDurability() - (firstItemDurability + bonus), 0);

            if (damage < firstItemMeta.getDamage()) firstItemMeta.setDamage(damage);

        }

        firstItem.setItemMeta(firstItemMeta);

        if (!needsRepair) return;

        Repairable repairable = (Repairable) firstItem.getItemMeta();

        int repairCost = ContainerAnvil.e(Math.max(firstCraftItem.H(), secondCraftItem.H()));
        repairable.setRepairCost(repairCost);

        firstItem.setItemMeta(repairable);

    }

    public static ItemStack combineItems(@NotNull ItemStack primaryItem, @NotNull ItemStack secondaryItem) {
        primaryItem = primaryItem.clone();
        secondaryItem = secondaryItem.clone();

        if (!EnchantmentTarget.BREAKABLE.includes(primaryItem) && primaryItem.getType() != Material.ENCHANTED_BOOK) return primaryItem;

        Map<Enchantment, Integer> enchantments = MCCEnchanting.getEnchantments(secondaryItem);
        Map<Enchantment, Integer> primaryEnchantments = MCCEnchanting.getEnchantments(primaryItem);

        for (Map.Entry<Enchantment, Integer> entries : enchantments.entrySet()) {

            Enchantment enchantment = entries.getKey();
            if (primaryItem.getType() != Material.ENCHANTED_BOOK && !enchantment.canEnchantItem(primaryItem)) continue;

            int level = entries.getValue();
            int otherLevel = primaryEnchantments.getOrDefault(enchantment, 0);

            int combineLevel = Math.min(level == otherLevel ? level + 1 : Math.max(level, otherLevel), enchantment.getMaxLevel());

            MCCEnchanting.setEnchantment(primaryItem, enchantment, combineLevel);

        }

        return primaryItem;

    }

    public static int getRepairCost(@NotNull ItemStack primaryItem, @NotNull ItemStack secondaryItem) {

        net.minecraft.world.item.ItemStack craftPrimaryItem = CraftItemStack.asNMSCopy(primaryItem);
        net.minecraft.world.item.ItemStack craftSecondaryItem = CraftItemStack.asNMSCopy(secondaryItem);

        if (!craftPrimaryItem.h()) return 0;

        if (craftPrimaryItem.c().a(craftPrimaryItem, craftSecondaryItem)) {

            int repairCost = 0;

            int repairedDurability = Math.min(craftPrimaryItem.j(), craftPrimaryItem.k() / 4);
            if (repairedDurability <= 0) return 0;

            while (repairedDurability > 0 && repairCost < craftSecondaryItem.K()){

                int durability = craftPrimaryItem.j() - repairedDurability;
                craftPrimaryItem.b(durability);

                repairedDurability = Math.min(craftPrimaryItem.j(), craftPrimaryItem.k() / 4);
                repairCost++;

            }

            return repairCost;

        }

        if (primaryItem.getType() == secondaryItem.getType()) {

            int repairedDurability = craftPrimaryItem.k() - craftPrimaryItem.j();
            int repairCost = craftSecondaryItem.k() - craftSecondaryItem.j();

            int l = repairCost + craftPrimaryItem.k() * 12 / 100;
            int ji = repairedDurability + l;
            int k1 = craftPrimaryItem.k() - ji;

            if (k1 < 0) k1 = 0;
            if (k1 < craftPrimaryItem.j()) {
                craftPrimaryItem.b(k1);
                return 2;


            }
        }

        return  0;

    }

    public static int getUseCost(@NotNull ItemStack firstItem, @NotNull ItemStack secondItem) {

        net.minecraft.world.item.ItemStack firstCraftItem = CraftItemStack.asNMSCopy(firstItem);
        net.minecraft.world.item.ItemStack secondCraftItem = CraftItemStack.asNMSCopy(secondItem);

        int repairCost = getRepairCost(firstItem, secondItem);
        int extraCost = firstCraftItem.H() + secondCraftItem.H();

        Map<Enchantment, Integer> firstsItemsEnchantments = MCCEnchanting.getEnchantments(firstItem);
        Map<Enchantment, Integer> secondsItemsEnchantments = MCCEnchanting.getEnchantments(secondItem);

        if (secondsItemsEnchantments.size() <= 0) return repairCost + extraCost;

        for (Map.Entry<Enchantment, Integer> entry : secondsItemsEnchantments.entrySet()) {

            Enchantment enchantment = entry.getKey();

            int level = entry.getValue();
            int otherLevel = firstsItemsEnchantments.getOrDefault(enchantment, 0);

            level = Math.min(level == otherLevel ? level + 1 : Math.max(level, otherLevel), enchantment.getMaxLevel());

            int enchantmentsCost;

            net.minecraft.world.item.enchantment.Enchantment craftEnchantment = toNMSCopy(enchantment);

            switch (craftEnchantment.d().a()) {

                case 10:
                    enchantmentsCost = 1;
                    break;
                case 5:
                    enchantmentsCost = 2;
                    break;
                case 2:
                    enchantmentsCost = 4;
                    break;
                default: enchantmentsCost = 8;

            }

            if (firstItem.getType() == Material.ENCHANTED_BOOK) enchantmentsCost = Math.max(1, enchantmentsCost / 2);

            repairCost += enchantmentsCost * level;

            if (firstCraftItem.K() > 1) repairCost = 40;

        }

        return repairCost + extraCost;

    }

    private static net.minecraft.world.item.enchantment.Enchantment toNMSCopy(Enchantment enchantment) {

        if (enchantment instanceof EnchantmentWrapper) enchantment = ((EnchantmentWrapper) enchantment).getEnchantment();
        return ((CraftEnchantment) enchantment).getHandle();

    }

    // TODO: make code smarter, like da fuck is this bullshit?
    // TODO: prevent from stupid enchants like enchanting unbreaking 3 with unbreaking 3
    public static boolean isCompatible(ItemStack primaryItem, ItemStack secondaryItem) {

        net.minecraft.world.item.ItemStack craftPrimaryItem = CraftItemStack.asNMSCopy(primaryItem);
        net.minecraft.world.item.ItemStack craftSecondaryItem = CraftItemStack.asNMSCopy(secondaryItem);

        boolean isEnchantmentBook = secondaryItem.getType() == Material.ENCHANTED_BOOK;
        boolean foundAtLeastOne = false;

        for (Map.Entry<Enchantment, Integer> entry : MCCEnchanting.getEnchantments(secondaryItem).entrySet()) {

            Enchantment enchantment = entry.getKey();

            if (!foundAtLeastOne && enchantment.canEnchantItem(primaryItem)) foundAtLeastOne = true;

            for (Map.Entry<Enchantment, Integer> otherEntry : MCCEnchanting.getEnchantments(primaryItem).entrySet()) {

                Enchantment otherEnchantment = otherEntry.getKey();

                if (enchantment != otherEnchantment && enchantment.conflictsWith(otherEnchantment)) return false;

            }

        }

        if (isEnchantmentBook && EnchantmentTarget.BREAKABLE.includes(primaryItem) && !foundAtLeastOne) return false;
        if (!EnchantmentTarget.BREAKABLE.includes(primaryItem)) return primaryItem.getType() == Material.ENCHANTED_BOOK  && isEnchantmentBook;
        if (isEnchantmentBook) return true;
        if (foundAtLeastOne && EnchantmentTarget.BREAKABLE.includes(primaryItem) && EnchantmentTarget.BREAKABLE.includes(secondaryItem)) return true;

        if (craftPrimaryItem.c().a(craftPrimaryItem, craftSecondaryItem)) {

            int repairedDurability = Math.min(craftPrimaryItem.J(), craftPrimaryItem.K() / 4);
            return repairedDurability > 0;

        }

        if (primaryItem.getType() == secondaryItem.getType()) {

            int repairedDurability = craftPrimaryItem.k() - craftPrimaryItem.j();
            int repairCost = craftSecondaryItem.k() - craftSecondaryItem.j();

            int l = repairCost + craftPrimaryItem.k() * 12 / 100;
            int ji = repairedDurability + l;
            int k1 = craftPrimaryItem.k() - ji;

            if (k1 < 0) k1 = 0;
            if (k1 < craftPrimaryItem.j()) {
                craftPrimaryItem.b(k1);
                return true;


            }
        }

        return  false;

    }

}
