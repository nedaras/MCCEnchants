package app.vercel.minecraftcustoms.mccenchants.managers;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class InventoryItemStack {

    private final @Nullable ItemStack itemStack;
    private final @Nullable String function;
    private final int slot;
    private final String amount;

    public InventoryItemStack(@Nullable ItemStack itemStack, @Nullable String function, int slot, @NotNull String amount) {
        this.itemStack = itemStack;
        this.function = function;
        this.slot = slot;
        this.amount = amount;

    }

    public @Nullable ItemStack getItemStack() {
        return itemStack;

    }

    public @Nullable String getFunction() {
        return function;

    }

    public int getSlot() {
        return slot;

    }

    public @NotNull String getAmount() {
        return amount;

    }

}
