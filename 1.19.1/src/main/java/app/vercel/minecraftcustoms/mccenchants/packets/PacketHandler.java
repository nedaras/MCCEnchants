package app.vercel.minecraftcustoms.mccenchants.packets;

import app.vercel.minecraftcustoms.mccenchants.Main;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundContainerSetContentPacket;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.network.protocol.game.ClientboundMerchantOffersPacket;
import net.minecraft.network.protocol.game.ServerboundSetCreativeModeSlotPacket;
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
import java.util.List;
import java.util.Map;

public class PacketHandler extends ChannelDuplexHandler {

    private static Field connectionField;

    static { // well cool and all but when we will try to add reflection and all we will want some error handling and like init function
        try {
            connectionField = ServerCommonPacketListenerImpl.class.getField("c");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
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

        if (channel.pipeline().get("mcc_enchants") != null) {
            Main.getInstance().getLogger().info("yo");
            return;
        }

        channel.pipeline().addBefore("packet_handler", "mcc_enchants", new PacketHandler());
    }

    public static void removePlayer(@NotNull Player player) {
        ServerPlayer minecraftPlayer = ((CraftPlayer) player).getHandle();
        Channel channel = getConnection(minecraftPlayer.connection).channel;


        if (channel.pipeline().get("mcc_enchants") == null) {
            Main.getInstance().getLogger().info("yo");
            return;
        }

        channel.pipeline().remove("mcc_enchants");
    }

    // we need some kind of event bus
    @Override
    public void channelRead(ChannelHandlerContext context, Object packet) throws Exception {
        if (packet instanceof ServerboundSetCreativeModeSlotPacket slotPacket) {
            System.out.println("pizda nx");
        }
        super.channelRead(context, packet);
    }

    @Override
    public void write(ChannelHandlerContext context, Object packet, ChannelPromise promise) throws Exception {
        if (packet instanceof ClientboundContainerSetSlotPacket slotPacket) enchantsToLore(slotPacket.getItem());
        if (packet instanceof ClientboundContainerSetContentPacket contentPacket) {
            for (net.minecraft.world.item.ItemStack itemStack : contentPacket.getItems()) enchantsToLore(itemStack);
        }
        if (packet instanceof ClientboundMerchantOffersPacket merchantOffersPacket) {
            for (MerchantOffer offer : merchantOffersPacket.getOffers()) {
                enchantsToLore(offer.getBaseCostA());
                enchantsToLore(offer.getCostB());
                enchantsToLore(offer.getResult());
            }
        }
        super.write(context, packet, promise);
    }


    private static void enchantsToLore(@NotNull net.minecraft.world.item.ItemStack itemStack) {
        enchantsToLore(CraftItemStack.asCraftMirror(itemStack));
    }

    private static void enchantsToLore(@NotNull ItemStack itemStack) {
        ItemMeta meta = itemStack.getItemMeta();

        if (itemStack.getType() == Material.AIR) return;
        if (itemStack.getEnchantments().isEmpty()) return;
        if (meta == null) return;
        if (meta.hasItemFlag(ItemFlag.HIDE_ENCHANTS)) return;

        List<String> lore = meta.getLore();
        if (lore == null) lore = new ArrayList<>();

        for (Map.Entry<Enchantment, Integer> entry : itemStack.getEnchantments().entrySet()) {
            lore.add(entry.getKey().getKey() + " " + entry.getValue());
        }

        meta.setLore(lore);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

        itemStack.setItemMeta(meta);

    }

}
