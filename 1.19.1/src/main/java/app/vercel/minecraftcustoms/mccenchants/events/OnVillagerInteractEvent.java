package app.vercel.minecraftcustoms.mccenchants.events;

import app.vercel.minecraftcustoms.mccenchants.api.helpers.MCCEnchanting;
import app.vercel.minecraftcustoms.mccenchants.utils.Utils;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;

public class OnVillagerInteractEvent implements Listener {

    @EventHandler
    public void onPlayerInteractEntityEvent(PlayerInteractEntityEvent event) {

        if (!(event.getRightClicked() instanceof Villager)) return;
        if (!tradesEnchantedItems(event.getRightClicked())) return;

        Villager villager = (Villager) event.getRightClicked();

        for (int i = 0; i < villager.getRecipes().size(); i++) {

            MerchantRecipe recipe = villager.getRecipe(i);

            ItemStack item = recipe.getResult();

            if (MCCEnchanting.getEnchantments(item).isEmpty()) continue;
            if (Utils.isItemStackSinged(item)) continue;

            Utils.convertEnchantsToLore(item);

            MerchantRecipe newRecipe = new MerchantRecipe(item, recipe.getUses(), recipe.getMaxUses(), recipe.hasExperienceReward(), recipe.getVillagerExperience(), recipe.getPriceMultiplier(), recipe.getDemand(), recipe.getSpecialPrice());
            newRecipe.setIngredients(recipe.getIngredients());

            villager.setRecipe(i, newRecipe);

        }

    }

    private boolean tradesEnchantedItems(Entity entity) {

        Villager villager = (Villager) entity;

        switch (villager.getProfession()) {
            case FISHERMAN:
            case LIBRARIAN:
            case ARMORER:
            case TOOLSMITH:
            case WEAPONSMITH:
            case FLETCHER:
                return true;
            default:
                return false;
        }

    }

}
