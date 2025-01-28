package com.github.mccenchants.nms.v1_20_R4.packets;

import com.github.mccenchants.nms.v1_20_R4.enchantments.CraftMCCEnchantment;
import com.github.mccenchants.nms.v1_20_R4.utils.Utils;
import com.mojang.datafixers.util.Pair;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.*;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerCommonPacketListenerImpl;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.trading.MerchantOffer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_20_R3.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PacketHandler extends ChannelDuplexHandler {

    private static Field connectionField;

    public static void init() {
        try {
            connectionField = ServerCommonPacketListenerImpl.class.getDeclaredField("c");
            for (Player player : Bukkit.getOnlinePlayers()) addPlayer(player);

        } catch (NoSuchFieldException e) {
            throw new RuntimeException();
        }
    }

    public static void deinit() {
        for (Player player : Bukkit.getOnlinePlayers()) removePlayer(player);

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
        if (packet instanceof ServerboundContainerClickPacket clickPacket) {
            reverseItemStack(clickPacket.getCarriedItem());
            clickPacket.getChangedSlots().forEach((i, item) -> {
                reverseItemStack(item);
            });
        }
        super.channelRead(context, packet);
    }

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
        //Set Equipment
        if (packet instanceof ClientboundSetEquipmentPacket equipmentPacket) {
            for (Pair<EquipmentSlot, net.minecraft.world.item.ItemStack> pair : equipmentPacket.getSlots()) {
                enchantsToLore(pair.getSecond());
            }
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
        if (!hasHideFlag(meta)) return;

        List<String> lore = meta.getLore();
        List<String> newLore = new ArrayList<>();

        if (lore == null) return;

        for (int i = 0; i < lore.size(); i++) {
            if (i < enchantments) continue;
            newLore.add(lore.get(i));
        }

        if (lore.size() == newLore.size()) return;

        meta.setLore(newLore);
        removeHideFlag(meta);

        itemStack.setItemMeta(meta);

    }

    private static void enchantsToLore(@NotNull net.minecraft.world.item.ItemStack minecraftItemStack) {
        ItemStack itemStack = (CraftItemStack.asCraftMirror(minecraftItemStack));
        ItemMeta meta = itemStack.getItemMeta();

        if (itemStack.getType() == Material.AIR) return;
        if (meta == null) return;
        if (hasHideFlag(meta)) {
            //CompoundTag tag = minecraftItemStack.getOrCreateTag();
            //tag.putShort("MCCEnchants", (short) 0);
            return;
        }
        if (getEnchantments(itemStack, meta).isEmpty()) return;


        List<String> lore = meta.getLore();
        List<String> newLore = new ArrayList<>();

        if (lore == null) lore = new ArrayList<>();

        for (Map.Entry<Enchantment, Integer> entry : getEnchantments(itemStack, meta).entrySet()) {
            ChatColor color = CraftMCCEnchantment.bukkitToCustoms(entry.getKey()).isCursed() ? ChatColor.RED : ChatColor.GRAY;
            newLore.add(color + Utils.getEnchantmentName(entry.getKey(), entry.getValue()));

        }

        newLore.addAll(lore);

        meta.setLore(newLore);
        addHideFlag(meta);

        itemStack.setItemMeta(meta);

        CompoundTag tag = minecraftItemStack.getOrCreateTag();
        tag.putShort("MCCEnchants", (short) getEnchantments(itemStack, meta).size());

    }

    private static Map<Enchantment, Integer> getEnchantments(ItemStack itemStack, ItemMeta meta) {
        if (meta instanceof EnchantmentStorageMeta storageMeta) return storageMeta.getStoredEnchants();
        return itemStack.getEnchantments();
    }

    private static boolean hasHideFlag(ItemMeta meta) {
        if (meta instanceof EnchantmentStorageMeta) return meta.hasItemFlag(ItemFlag.HIDE_POTION_EFFECTS);
        return meta.hasItemFlag(ItemFlag.HIDE_ENCHANTS);
    }

    private static void addHideFlag(ItemMeta meta) {
        if (meta instanceof EnchantmentStorageMeta) {
            meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
            return;
        }
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
    }

    private static void removeHideFlag(ItemMeta meta) {
        if (meta instanceof EnchantmentStorageMeta) {
            meta.removeItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
            return;
        }
        meta.removeItemFlags(ItemFlag.HIDE_ENCHANTS);
    }

}