package app.vercel.minecraftcustoms.mccenchants.lib;

import app.vercel.minecraftcustoms.mccenchants.enchantments.CraftMCCEnchantment;
import app.vercel.minecraftcustoms.mccenchants.api.enchantments.MCCEnchantment;
import app.vercel.minecraftcustoms.mccenchants.utils.MCCEnchantmentInstance;
import com.google.common.collect.Lists;
import net.minecraft.SystemUtils;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.IRegistry;
import net.minecraft.util.MathHelper;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.WeightedRandom2;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentManager;
import net.minecraft.world.item.enchantment.WeightedRandomEnchant;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.BlockEnchantmentTable;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_19_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_19_R1.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_19_R1.util.RandomSourceWrapper;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class MCCEnchantingTable {

    public static @NotNull ItemStack enchantItem(@NotNull Random random, int enchantingCost, @NotNull ItemStack item) {
        item = item.clone();

        List<MCCEnchantmentInstance> enchantments = getEnchantments(random, enchantingCost, item);
        if (item.getType() == Material.BOOK) item.setType(Material.ENCHANTED_BOOK);

        for (MCCEnchantmentInstance instance : enchantments) {

            app.vercel.minecraftcustoms.mccenchants.api.helpers.MCCEnchanting.setEnchantment(item, instance.getEnchantment(), instance.getLevel());

        }

        return item;

    }

    public static boolean canEnchantItem(@NotNull ItemStack item) {
        if (item.getEnchantments().size() > 0) return false;
        if (CraftItemStack.asNMSCopy(item).c().c() <= 0) return false;

        return item.getType() == Material.BOOK || EnchantmentTarget.BREAKABLE.includes(item);

    }

    public static List<MCCEnchantmentInstance> getEnchantments(@NotNull Random random, int enchantingCost, @NotNull ItemStack item) {

        List<MCCEnchantmentInstance> mccEnchantments = new ArrayList<>();
        List<WeightedRandomEnchant> enchantments = b(random, CraftItemStack.asNMSCopy(item), enchantingCost, false);

        if (item.getType() == Material.BOOK && enchantments.size() > 1) {
            enchantments.remove(random.nextInt(enchantments.size()));

        }

        enchantments.forEach((enchantment) -> {
            MCCEnchantment mccEnchantment = new CraftMCCEnchantment(enchantment.a);
            mccEnchantments.add(new MCCEnchantmentInstance(mccEnchantment, enchantment.b));

        });

        return mccEnchantments;

    }

    public static int getEnchantingCost(@NotNull Random random, int slot, int bookshelves, @NotNull ItemStack item) {
        int cost = EnchantmentManager.a(new RandomSourceWrapper(random), slot - 1, bookshelves, CraftItemStack.asNMSCopy(item));
        return cost < slot ? 0 : cost;

    }

    public static int getSurroundingBookshelves(@NotNull Block block) {

        int bookshelves = 0;

        List<BlockPosition> offsets = BlockEnchantmentTable.b;
        World world = ((CraftWorld) block.getWorld()).getHandle();

        Location location = block.getLocation();
        BlockPosition position = new BlockPosition(location.getBlockX(), location.getBlockY(), location.getBlockZ());

        for (BlockPosition offset : offsets) {
            if (BlockEnchantmentTable.a(world, position, offset)) bookshelves++;

        }

        return Math.min(bookshelves, 15);

    }

    public static int getEnchantingSeed(@NotNull Player player) {
        CraftPlayer craftPlayer = (CraftPlayer) player;
        return craftPlayer.getHandle().fI();

    }

    public static void updateEnchantingSeed(@NotNull Player player) {
        CraftPlayer craftPlayer = (CraftPlayer) player;
        craftPlayer.getHandle().a(CraftItemStack.asNMSCopy(new ItemStack(Material.AIR)), 0);

    }

    // Fixing canEnchantItem bug

    public static List<WeightedRandomEnchant> b(Random var0, net.minecraft.world.item.ItemStack var1, int var2, boolean var3) {
        RandomSource randomSource = new RandomSourceWrapper(var0);
        List<WeightedRandomEnchant> var4 = Lists.newArrayList();
        Item var5 = var1.c();
        int var6 = var5.c();
        if (var6 <= 0) {
            return var4;
        } else {
            var2 += 1 + randomSource.a(var6 / 4 + 1) + randomSource.a(var6 / 4 + 1);
            float var7 = (randomSource.i() + randomSource.i() - 1.0F) * 0.15F;
            var2 = MathHelper.a(Math.round((float)var2 + (float)var2 * var7), 1, Integer.MAX_VALUE);
            List<WeightedRandomEnchant> var8 = a(var2, var1, var3);
            if (!var8.isEmpty()) {
                Optional<WeightedRandomEnchant> var10000 = WeightedRandom2.a(randomSource, var8);
                Objects.requireNonNull(var4);
                var10000.ifPresent(var4::add);

                while(randomSource.a(50) <= var2) {
                    if (!var4.isEmpty()) {
                        EnchantmentManager.a(var8, SystemUtils.a(var4));
                    }

                    if (var8.isEmpty()) {
                        break;
                    }

                    var10000 = WeightedRandom2.a(randomSource, var8);
                    Objects.requireNonNull(var4);
                    var10000.ifPresent(var4::add);
                    var2 /= 2;
                }
            }

            return var4;
        }
    }

    public static List<WeightedRandomEnchant> a(int var0, net.minecraft.world.item.ItemStack var1, boolean var2) {
        List<WeightedRandomEnchant> var3 = Lists.newArrayList();
        boolean var5 = var1.a(Items.oY);
        Iterator<Enchantment> var6 = IRegistry.W.iterator();

        while(true) {
            Enchantment var7;
            do {
                do {
                    do {
                        if (!var6.hasNext()) {
                            return var3;
                        }

                        var7 = var6.next();
                    } while(var7.b() && !var2);
                } while(!var7.i());
            } while(!var7.a(var1) && !var5); // here

            for(int var8 = var7.a(); var8 > var7.e() - 1; --var8) {
                if (var0 >= var7.a(var8) && var0 <= var7.b(var8)) {
                    var3.add(new WeightedRandomEnchant(var7, var8));
                    break;
                }
            }
        }
    }

}
