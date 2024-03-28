package app.vercel.minecraftcustoms.mccenchants.events;

import app.vercel.minecraftcustoms.mccenchants.api.helpers.MCCEnchanting;
import app.vercel.minecraftcustoms.mccenchants.utils.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.inventory.EnchantmentMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.craftbukkit.v1_20_R3.CraftWorld;
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

        ServerLevel world = ((CraftWorld) block.getWorld()).getHandle();
        LevelChunk chunk = world.getChunk(block.getWorld().getChunkAt(block).getX(), block.getWorld().getChunkAt(block).getZ());

        ChestBlockEntity tileEntityChest = (ChestBlockEntity) chunk.getBlockEntity(new BlockPos(block.getX(), block.getY(), block.getZ()));

        if (tileEntityChest == null) return true;
        return tileEntityChest.lootTable == null;
    }
}
