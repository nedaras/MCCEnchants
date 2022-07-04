package app.vercel.minecraftcustoms.mccenchants.managers;

import app.vercel.minecraftcustoms.mccenchants.api.helpers.MCCInventory;
import app.vercel.minecraftcustoms.mccenchants.lib.MCCAnvil;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class Anvil extends CorrespondingInventory {

    private static final Set<String> states = new HashSet<>();

    private int upgradeSlot;
    private int sacrificeSlot;
    private int combineItemSlot;
    private int outputSlot;

    static {

        states.add("upgrade_valid");
        states.add("sacrifice_valid");
        states.add("combinable_items");
        states.add("level_to_high");
        states.add("incombinable_items");
        states.add("missing_levels");

    }

    public Anvil(@NotNull JavaPlugin plugin) {
        super(plugin, states, Material.ANVIL);

        if (this.getCheckupSlots().size() >= 2) {

            upgradeSlot = this.getCheckupSlots().get(0);
            sacrificeSlot = this.getCheckupSlots().get(1);

            return;
        }
        this.getPlugin().getLogger().warning("You need least two empty slots, so anvil could work correctly!");

    }

    @Override
    public void registerFunction(@NotNull String function, int slot) {
        if (function.equals("combine_items")) combineItemSlot = slot;
        if (function.equals("display_item")) outputSlot = slot;

    }
    
    @Override
    public void onInventoryClickEvent(InventoryClickEvent event, ItemStack item) {

        if (event.getClickedInventory() == null) return;
        if (event.getClickedInventory().getType() == InventoryType.PLAYER) return;
        if (item == null) return;

        Player player = (Player) event.getWhoClicked();

        event.setCancelled(!this.getCheckupSlots().contains(event.getSlot()));

        ItemStack upgradeItem = event.getInventory().getItem(upgradeSlot);
        ItemStack sacrificeItem = event.getInventory().getItem(sacrificeSlot);

        if (this.isPlace(event)) {

            if (event.getSlot() == upgradeSlot) upgradeItem = item;
            if (event.getSlot() == sacrificeSlot) sacrificeItem = item;

            if (upgradeItem != null && sacrificeItem != null) {

                if (MCCAnvil.isCompatible(upgradeItem, sacrificeItem)) {

                    HashMap<String, String> placeholders = new HashMap<>();
                    placeholders.put("level", MCCAnvil.getUseCost(upgradeItem, sacrificeItem) + "");

                    if (player.getGameMode() != GameMode.CREATIVE && MCCAnvil.getUseCost(upgradeItem, sacrificeItem) >= MCCAnvil.MAX_REPAIR_COST) {

                        this.setState(event, "level_to_high", placeholders);

                        this.setIndividualState(event, "upgrade_valid");
                        this.setIndividualState(event, "sacrifice_valid");

                        event.getInventory().setItem(outputSlot, getUpgradedItem(upgradeItem, sacrificeItem));

                        return;

                    }

                    if (player.getGameMode() != GameMode.CREATIVE && MCCAnvil.getUseCost(upgradeItem, sacrificeItem) > player.getLevel()) {

                        this.setState(event, "missing_levels", placeholders);

                        this.setIndividualState(event, "upgrade_valid");
                        this.setIndividualState(event, "sacrifice_valid");

                        event.getInventory().setItem(outputSlot, getUpgradedItem(upgradeItem, sacrificeItem));

                        return;

                    }

                    this.setState(event, "combinable_items", placeholders);

                    this.setIndividualState(event, "upgrade_valid");
                    this.setIndividualState(event, "sacrifice_valid");

                    event.getInventory().setItem(outputSlot, getUpgradedItem(upgradeItem, sacrificeItem));

                    return;

                }

                this.clearIndividualState(event, "upgrade_valid");
                this.clearIndividualState(event, "sacrifice_valid");

                this.setState(event, "incombinable_items");

                return;

            }

            if (upgradeItem != null) this.setIndividualState(event, "upgrade_valid");
            if (sacrificeItem != null) this.setIndividualState(event, "sacrifice_valid");

            return;

        }

        if (event.getSlot() == upgradeSlot) {

            this.clearState(event);
            this.clearIndividualState(event, "upgrade_valid");

            if (sacrificeItem != null) this.setIndividualState(event, "sacrifice_valid");

            return;

        }

        if (event.getSlot() == sacrificeSlot) {

            this.clearState(event);
            this.clearIndividualState(event, "sacrifice_valid");

            if (upgradeItem != null) this.setIndividualState(event, "upgrade_valid");

            return;

        }

        if (event.getSlot() != combineItemSlot) return;
        if (!Objects.equals(this.getState(event), "combinable_items")) return;
        if (upgradeItem == null || sacrificeItem == null) return;

        MCCInventory.saveAddItemToInventory(player, getUpgradedItem(upgradeItem, sacrificeItem));

        event.getInventory().setItem(outputSlot, this.getInitialItem(outputSlot));
        event.getInventory().setItem(upgradeSlot, null);
        event.getInventory().setItem(sacrificeSlot, null);

        if (player.getGameMode() != GameMode.CREATIVE) player.setLevel(player.getLevel() - MCCAnvil.getUseCost(upgradeItem, sacrificeItem));
        player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_USE, 1f, 1f);

        this.clearState(event);

    }

    private @NotNull ItemStack getUpgradedItem(@NotNull ItemStack upgradeItem, @NotNull ItemStack sacrificeItem) {

        ItemStack item = MCCAnvil.combineItems(upgradeItem, sacrificeItem);
        MCCAnvil.repairItem(item, sacrificeItem);

        return item;

    }

}