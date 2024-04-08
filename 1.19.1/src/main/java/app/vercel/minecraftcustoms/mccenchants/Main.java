package app.vercel.minecraftcustoms.mccenchants;

import app.vercel.minecraftcustoms.mccenchants.events.*;
import app.vercel.minecraftcustoms.mccenchants.api.enchantments.MCCEnchantment;
import app.vercel.minecraftcustoms.mccenchants.managers.InventoryManager;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.*;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerCommonPacketListenerImpl;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.server.network.ServerPlayerConnection;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.MerchantOffer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_20_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import javax.xml.transform.stream.StreamSource;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public final class Main extends JavaPlugin implements Listener {

    private static JavaPlugin INSTANCE;
    private static boolean IS_HOOKED = false; // we need this

    @Override
    public void onEnable() {

        INSTANCE = this;
        //Version.setVersion(this);

        MCCEnchantment.registerEnchantment(new CustomEnchantment());
        InventoryManager.registerInventories(this); // we will need them inventories

        this.getServer().getPluginManager().registerEvents(this, this);

        for (Player player : Bukkit.getOnlinePlayers()) {

            ServerPlayer serverPlayer = ((CraftPlayer) player).getHandle();
            ServerCommonPacketListenerImpl listener = serverPlayer.connection;

            try {
                Field f = listener.getClass().getField("c");
                f.setAccessible(true);

                Connection connection = (Connection) f.get(listener);
                Channel channel = connection.channel;

                channel.pipeline().addBefore("packet_handler", "MCCEnchants", new PacketHandler());

                f.setAccessible(false);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        registerEvents();
    }

    @EventHandler
    public void playerJoin(PlayerJoinEvent event) {

        ServerPlayer serverPlayer = ((CraftPlayer) event.getPlayer()).getHandle();
        ServerCommonPacketListenerImpl listener = serverPlayer.connection;

        try {
            Field f = listener.getClass().getField("c");
            f.setAccessible(true);

            Connection connection = (Connection) f.get(listener);
            Channel channel = connection.channel;

            channel.pipeline().addBefore("packet_handler", "MCCEnchants", new PacketHandler());

            f.setAccessible(false);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }


    @EventHandler
    public void playerQuit(PlayerQuitEvent event) {

        ServerPlayer serverPlayer = ((CraftPlayer) event.getPlayer()).getHandle();
        ServerCommonPacketListenerImpl listener = serverPlayer.connection;

        try {
            Field f = listener.getClass().getField("c");
            f.setAccessible(true);

            Connection connection = (Connection) f.get(listener);
            Channel channel = connection.channel;

            if (channel.pipeline().get("MCCEnchants") == null) return;
            channel.pipeline().remove("MCCEnchants");

            f.setAccessible(false);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @EventHandler
    public void inte(PlayerInteractEvent event) {
        if (event.getItem() == null || event.getItem().getType() == Material.AIR) return;

        System.out.println(event.getItem());
    }

    @Override
    public void onDisable() {

        for (Player player : Bukkit.getOnlinePlayers()) {

            ServerPlayer serverPlayer = ((CraftPlayer) player).getHandle();
            ServerCommonPacketListenerImpl listener = serverPlayer.connection;

            try {
                Field f = listener.getClass().getField("c");
                f.setAccessible(true);

                Connection connection = (Connection) f.get(listener);
                Channel channel = connection.channel;

                if (channel.pipeline().get("MCCEnchants") == null) continue;
                channel.pipeline().remove("MCCEnchants");

                f.setAccessible(false);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    private void registerEvents() {

        // we should like handle other events if Hooks failed
        //this.getServer().getPluginManager().registerEvents(new OnPrepareInventoryResultEvent(), this);
        //this.getServer().getPluginManager().registerEvents(new OnChestOpenEvent(), this);
        //this.getServer().getPluginManager().registerEvents(new OnVillagerInteractEvent(), this);
        //this.getServer().getPluginManager().registerEvents(new OnPlayerPickupItemEvent(), this);

    }

    public static JavaPlugin getInstance() {
        return INSTANCE;
    }
}

class PacketHandler extends ChannelDuplexHandler {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object packet) throws Exception {
        // this thing like resets our items i guess
        // so we can add an tag for mccenchants and like reset the item
        if (packet.getClass().getSimpleName().equals(ServerboundSetCreativeModeSlotPacket.class.getSimpleName())) {
            ServerboundSetCreativeModeSlotPacket slotPacket = (ServerboundSetCreativeModeSlotPacket) packet;
            CraftItemStack craftItemStack = CraftItemStack.asCraftMirror(slotPacket.getItem());
            ItemMeta meta = craftItemStack.getItemMeta();

            if (meta != null) {
                if (meta.getDisplayName().equals("packet item!")) {
                    meta.setDisplayName(null);
                    craftItemStack.setItemMeta(meta);
                }
            }
            System.out.println(craftItemStack);
        }
        super.channelRead(ctx, packet);
    }

    // when crafting in survival i got not updated item i guess it is prob cuz that item did not have itemmeta for some reason
    // i cant get that bug again
    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {

        String packet = msg.getClass().getSimpleName();

        // this just like sets item in slot like when pickup or smth like that
        if (packet.equals(ClientboundContainerSetSlotPacket.class.getSimpleName())) {

            ClientboundContainerSetSlotPacket slotPacket = (ClientboundContainerSetSlotPacket) msg;
            CraftItemStack craftItemStack = CraftItemStack.asCraftMirror(slotPacket.getItem());

            if (craftItemStack.getType() != Material.AIR) {
                System.out.println(craftItemStack);
                ItemMeta meta = craftItemStack.getItemMeta();
                if (meta != null) {
                    meta.setDisplayName("packet item!");
                    craftItemStack.setItemMeta(meta);
                }
            }

            System.out.println("#0" + packet);
        }

        // this for content like in chests or anvils with grindstone
        if (packet.equals(ClientboundContainerSetContentPacket.class.getSimpleName())) {

            ClientboundContainerSetContentPacket contentPacket = (ClientboundContainerSetContentPacket) msg;

            for (ItemStack itemStack : contentPacket.getItems())  { // if i press e items gets overwritten even on the server
                CraftItemStack craftItemStack = CraftItemStack.asCraftMirror(itemStack);
                ItemMeta meta = craftItemStack.getItemMeta();
                if (craftItemStack.getType() == Material.AIR) continue;
                if (meta == null) continue;

                meta.setDisplayName("packet item!");
                craftItemStack.setItemMeta(meta);

                System.out.println(craftItemStack);
            }

            System.out.println("#1" + packet);

        }

        if (packet.equals(ClientboundContainerSetDataPacket.class.getSimpleName())) {
            System.out.println("#2" + packet);
        }

        if (packet.equals(ClientboundContainerClosePacket.class.getSimpleName())) {
            System.out.println("#3" + packet);
        }

        if (packet.equals(ClientboundMerchantOffersPacket.class.getSimpleName())) {
            ClientboundMerchantOffersPacket offersPacket = (ClientboundMerchantOffersPacket) msg;

            for (MerchantOffer offer : offersPacket.getOffers()) {

                // System.out.println(offer.getBaseCostA()); idk if we should mod this if we mod this then getCostA if auto modified

                CraftItemStack a = CraftItemStack.asCraftMirror(offer.getBaseCostA());
                CraftItemStack b = CraftItemStack.asCraftMirror(offer.getCostB());
                CraftItemStack c  = CraftItemStack.asCraftMirror(offer.getResult());

                ItemMeta aa = a.getItemMeta();
                ItemMeta cc = c.getItemMeta();

                if (aa != null) {
                    aa.setDisplayName("packet item!");
                    a.setItemMeta(aa);
                }

                if (cc != null) {
                    cc.setDisplayName("packet item!");
                    c.setItemMeta(cc);
                }

                if (b.getType() != Material.AIR) {

                    ItemMeta bb = b.getItemMeta();

                    if (bb != null) {
                        bb.setDisplayName("packet item!");
                        b.setItemMeta(bb);
                    }

                }

            }

        }

        super.write(ctx, msg, promise);
    }

}