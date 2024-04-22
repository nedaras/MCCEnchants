package app.vercel.minecraftcustoms.mccenchants.lib;

import com.google.common.collect.Lists;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.WeightedRandom;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.level.block.EnchantmentTableBlock;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_20_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_20_R3.enchantments.CraftEnchantment;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_20_R3.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_20_R3.util.RandomSourceWrapper;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class MCCEnchantingTable {

    // TODO: books ye
    public static @NotNull ItemStack enchantItem(@NotNull Random random, int enchantingCost, @NotNull ItemStack item) {
        item = item.clone();
        item.setAmount(1);

        List<EnchantmentInstance> enchantments = getEnchantments(random, enchantingCost, item);

        if (item.getType() == Material.BOOK) {
            item.setType(Material.ENCHANTED_BOOK);

            ItemMeta meta = item.getItemMeta();
            EnchantmentStorageMeta storageMeta = (EnchantmentStorageMeta) meta;

            if (meta == null) return item;

            for (EnchantmentInstance instance : enchantments) {
                storageMeta.addStoredEnchant(CraftEnchantment.minecraftToBukkit(instance.enchantment), instance.level, true);

            }

            item.setItemMeta(storageMeta);

            return item;
        }

        for (EnchantmentInstance instance : enchantments) {
            item.addUnsafeEnchantment(CraftEnchantment.minecraftToBukkit(instance.enchantment), instance.level);

        }

        return item;

    }

    public static boolean canEnchantItem(@NotNull ItemStack item) {
        if (!item.getEnchantments().isEmpty()) return false;
        if (CraftItemStack.asNMSCopy(item).getItem().getEnchantmentValue() <= 0) return false;

        return item.getType() == Material.BOOK || EnchantmentTarget.BREAKABLE.includes(item);

    }

    public static List<EnchantmentInstance> getEnchantments(@NotNull Random random, int enchantingCost, @NotNull ItemStack item) {
        List<EnchantmentInstance> enchantments = b(random, CraftItemStack.asNMSCopy(item), enchantingCost, false);

        if (item.getType() == Material.BOOK && enchantments.size() > 1) {
            enchantments.remove(random.nextInt(enchantments.size()));
        }

        return enchantments;
    }

    public static int getEnchantingCost(@NotNull Random random, int slot, int bookshelves, @NotNull ItemStack item) {
        int cost = EnchantmentHelper.getEnchantmentCost(new RandomSourceWrapper(random), slot - 1, bookshelves, CraftItemStack.asNMSCopy(item));
        return cost < slot ? 0 : cost;

    }

    public static int getSurroundingBookshelves(@NotNull Block block) {
        int bookshelves = 0;

        List<BlockPos> offsets = EnchantmentTableBlock.BOOKSHELF_OFFSETS;
        ServerLevel world = ((CraftWorld) block.getWorld()).getHandle();

        Location location = block.getLocation();
        BlockPos position = new BlockPos(location.getBlockX(), location.getBlockY(), location.getBlockZ());

        for (BlockPos offset : offsets) {
            if (EnchantmentTableBlock.isValidBookShelf(world, position, offset)) bookshelves++;
        }

        return Math.min(bookshelves, 15);

    }

    public static int getEnchantingSeed(@NotNull HumanEntity player) {
        CraftPlayer craftPlayer = (CraftPlayer) player;
        return craftPlayer.getHandle().getEnchantmentSeed();

    }

    // TODO: pass item stack that we're enchanting.
    public static void updateEnchantingSeed(@NotNull HumanEntity player) {
        CraftPlayer craftPlayer = (CraftPlayer) player;
        craftPlayer.getHandle().onEnchantmentPerformed(CraftItemStack.asNMSCopy(new ItemStack(Material.AIR)), 0);

    }

    // Fixing canEnchantItem bug

    public static List<EnchantmentInstance> b(Random var0, net.minecraft.world.item.ItemStack var1, int var2, boolean var3) {
        RandomSource randomSource = new RandomSourceWrapper(var0);
        List<EnchantmentInstance> var4 = Lists.newArrayList();
        Item var5 = var1.getItem();
        int var6 = var5.getEnchantmentValue();
        if (var6 <= 0) {
            return var4;
        } else {
            var2 += 1 + randomSource.nextInt(var6 / 4 + 1) + randomSource.nextInt(var6 / 4 + 1);
            float var7 = (randomSource.nextFloat() + randomSource.nextFloat() - 1.0F) * 0.15F;
            var2 = Mth.clamp(Math.round((float)var2 + (float)var2 * var7), 1, Integer.MAX_VALUE);
            List<EnchantmentInstance> var8 = a(var2, var1, var3);
            if (!var8.isEmpty()) {
                Optional<EnchantmentInstance> var10000 = WeightedRandom.getRandomItem(randomSource, var8);
                Objects.requireNonNull(var4);
                var10000.ifPresent(var4::add);

                while(randomSource.nextInt(50) <= var2) {
                    if (!var4.isEmpty()) {
                        EnchantmentHelper.filterCompatibleEnchantments(var8, Util.lastOf(var4));

                    }

                    if (var8.isEmpty()) {
                        break;
                    }

                    var10000 = WeightedRandom.getRandomItem(randomSource, var8);
                    Objects.requireNonNull(var4);
                    var10000.ifPresent(var4::add);
                    var2 /= 2;
                }
            }

            return var4;
        }
    }

    public static List<EnchantmentInstance> a(int var0, net.minecraft.world.item.ItemStack var1, boolean var2) {
        List<EnchantmentInstance> var3 = Lists.newArrayList();
        boolean var5 = var1.is(Items.BOOK);
        Iterator<Enchantment> var6 = BuiltInRegistries.ENCHANTMENT.iterator();

        while(true) {
            Enchantment var7;
            do {
                do {
                    do {
                        if (!var6.hasNext()) {
                            return var3;
                        }

                        var7 = var6.next();
                    } while(var7.isTreasureOnly() && !var2);
                } while(!var7.isDiscoverable());
            } while(!var7.canEnchant(var1) && !var5); // here

            for(int var8 = var7.getMaxLevel(); var8 > var7.getMinLevel() - 1; --var8) {
                if (var0 >= var7.getMinCost(var8) && var0 <= var7.getMaxCost(var8)) {
                    var3.add(new EnchantmentInstance(var7, var8));
                    break;
                }
            }
        }
    }

}
