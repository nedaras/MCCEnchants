package app.vercel.minecraftcustoms.mccenchants.lib;

import app.vercel.minecraftcustoms.mccenchants.enchantments.CraftMCCEnchantment;
import app.vercel.minecraftcustoms.mccenchants.api.enchantments.MCCEnchantment;
import app.vercel.minecraftcustoms.mccenchants.utils.MCCEnchantmentInstance;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.item.enchantment.EnchantmentManager;
import net.minecraft.world.item.enchantment.WeightedRandomEnchant;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.BlockEnchantmentTable;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_18_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_18_R2.inventory.CraftItemStack;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
        List<WeightedRandomEnchant> enchantments = EnchantmentManager.b(random, CraftItemStack.asNMSCopy(item), enchantingCost, false);

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
        int cost = EnchantmentManager.a(random, slot - 1, bookshelves, CraftItemStack.asNMSCopy(item));
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
        return craftPlayer.getHandle().fy();

    }

    public static void updateEnchantingSeed(@NotNull Player player) {
        CraftPlayer craftPlayer = (CraftPlayer) player;
        craftPlayer.getHandle().a(CraftItemStack.asNMSCopy(new ItemStack(Material.AIR)), 0);

    }

}
