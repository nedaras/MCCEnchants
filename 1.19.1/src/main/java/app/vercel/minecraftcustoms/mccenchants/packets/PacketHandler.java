package app.vercel.minecraftcustoms.mccenchants.packets;

import app.vercel.minecraftcustoms.mccenchants.Main;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.*;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerCommonPacketListenerImpl;
import net.minecraft.world.item.trading.MerchantOffer;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_20_R3.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PacketHandler extends ChannelDuplexHandler {

    private final static Field connectionField;
    private static final Map<String, Integer> map = new HashMap<>();

    static { // well cool and all but when we will try to add reflection and all we will want some error handling and like init function
        try {
            connectionField = ServerCommonPacketListenerImpl.class.getField("c");
        } catch (NoSuchFieldException e) {
            throw new RuntimeException();
        }
    }

    private static Connection getConnection(ServerCommonPacketListenerImpl packetListener) {
        connectionField.setAccessible(true);
        try {
            Connection connection = (Connection) connectionField.get(packetListener);
            connectionField.setAccessible(false);
            return connection;
        } catch (IllegalAccessException e) {
            connectionField.setAccessible(false);
            throw new RuntimeException(e);
        }
    }

    public static void addPlayer(@NotNull Player player) {
        ServerPlayer minecraftPlayer = ((CraftPlayer) player).getHandle();
        Channel channel = getConnection(minecraftPlayer.connection).channel;

        if (channel.pipeline().get("mcc_enchants") != null) return;
        channel.pipeline().addBefore("packet_handler", "mcc_enchants", new PacketHandler());
    }

    public static void removePlayer(@NotNull Player player) {
        ServerPlayer minecraftPlayer = ((CraftPlayer) player).getHandle();
        Channel channel = getConnection(minecraftPlayer.connection).channel;

        if (channel.pipeline().get("mcc_enchants") == null) return;
        channel.pipeline().remove("mcc_enchants");
    }

    @Override
    public void channelRead(ChannelHandlerContext context, Object packet) throws Exception {
        if (packet instanceof ServerboundSetCreativeModeSlotPacket slotPacket) reverseItemStack(slotPacket.getItem());
        super.channelRead(context, packet);
    }

    // if we in inventory click item then exit the inventory and put item in main hand it will not update
    // idk why
    @Override
    public void write(ChannelHandlerContext context, Object packet, ChannelPromise promise) throws Exception {
        if (packet instanceof ClientboundContainerSetSlotPacket slotPacket) enchantsToLore(slotPacket.getItem());
        if (packet instanceof ClientboundContainerSetContentPacket contentPacket) {
            enchantsToLore(contentPacket.getCarriedItem());
            for (net.minecraft.world.item.ItemStack itemStack : contentPacket.getItems()) enchantsToLore(itemStack);
        }
        if (packet instanceof ClientboundMerchantOffersPacket merchantOffersPacket) {
            for (MerchantOffer offer : merchantOffersPacket.getOffers()) {
                enchantsToLore(offer.getBaseCostA());
                enchantsToLore(offer.getCostB());
                enchantsToLore(offer.getResult());
            }
        }


        if (map.containsKey(packet.getClass().getSimpleName())) {
            int times = map.get(packet.getClass().getSimpleName());

            map.put(packet.getClass().getSimpleName(), times + 1);

        } else {
            map.put(packet.getClass().getSimpleName(), 0);
            System.out.println(packet.getClass().getSimpleName());
        }

        super.write(context, packet, promise);
    }

    private static void reverseItemStack(@NotNull net.minecraft.world.item.ItemStack minecraftItemStack) {
        CompoundTag tag = minecraftItemStack.getTag();

        if (tag == null) return;

        int enchantments = tag.getShort("MCCEnchants");
        tag.remove("MCCEnchants");

        if (enchantments <= 0) return;

        ItemStack itemStack = CraftItemStack.asCraftMirror(minecraftItemStack);
        ItemMeta meta = itemStack.getItemMeta();

        if (meta == null) return;
        if (!meta.hasItemFlag(ItemFlag.HIDE_ENCHANTS)) return;

        List<String> lore = meta.getLore();
        List<String> newLore = new ArrayList<>();

        if (lore == null) return;

        for (int i = 0; i < lore.size(); i++) {
            if (i < enchantments) continue;
            newLore.add(lore.get(i));
        }

        if (lore.size() == newLore.size()) return;

        meta.removeItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.setLore(newLore);

        itemStack.setItemMeta(meta);

    }

    private static void enchantsToLore(@NotNull net.minecraft.world.item.ItemStack minecraftItemStack) {
        ItemStack itemStack = (CraftItemStack.asCraftMirror(minecraftItemStack));
        ItemMeta meta = itemStack.getItemMeta();

        if (itemStack.getType() == Material.AIR) return;
        if (itemStack.getEnchantments().isEmpty()) return;
        if (meta == null) return;
        if (meta.hasItemFlag(ItemFlag.HIDE_ENCHANTS)) {
            CompoundTag tag = minecraftItemStack.getOrCreateTag();
            tag.putShort("MCCEnchants", (short) 0);
            return;
        };

        List<String> lore = meta.getLore();
        List<String> newLore = new ArrayList<>();

        if (lore == null) lore = new ArrayList<>();

        for (Map.Entry<Enchantment, Integer> entry : itemStack.getEnchantments().entrySet()) {
            newLore.add(entry.getKey().getKey() + " " + entry.getValue());
        }

        newLore.addAll(lore);

        meta.setLore(newLore);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

        itemStack.setItemMeta(meta);

        CompoundTag tag = minecraftItemStack.getOrCreateTag();
        tag.putShort("MCCEnchants", (short) itemStack.getEnchantments().size());

    }
}
