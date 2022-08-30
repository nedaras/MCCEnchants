package app.vercel.minecraftcustoms.mccenchants.events;

import app.vercel.minecraftcustoms.mccenchants.api.helpers.MCCEnchanting;
import app.vercel.minecraftcustoms.mccenchants.utils.Utils;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.entity.TileEntityChest;
import net.minecraft.world.level.chunk.Chunk;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.craftbukkit.v1_19_R1.CraftWorld;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class OnChestOpenEvent implements Listener {

    // TODO: we need to check what else generates loot tables and then add them

    @EventHandler
    public void onPlayerInteractEvent(PlayerInteractEvent event) {

        if (!event.hasBlock()) return;
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (event.getClickedBlock().getType() != Material.CHEST) return;
        if (isChestAlreadyGenerated(event.getClickedBlock())) return;

        Chest chest = (Chest) event.getClickedBlock().getState();
        Inventory inventory = chest.getBlockInventory();

        for (int i = 0; i < inventory.getSize(); i++) {

            ItemStack item = inventory.getItem(i);

            if (item == null) continue;
            if (MCCEnchanting.getEnchantments(item).isEmpty()) continue;

            Utils.convertEnchantsToLore(item);

        }

    }

    private boolean isChestAlreadyGenerated(@NotNull Block block) {

        World world = ((CraftWorld) block.getWorld()).getHandle();
        Chunk chunk = world.d(block.getWorld().getChunkAt(block).getX(), block.getWorld().getChunkAt(block).getZ());

        TileEntityChest tileEntityChest = (TileEntityChest) chunk.c_(new BlockPosition(block.getX(), block.getY(), block.getZ()));

        return tileEntityChest.g == null;

    }

}
