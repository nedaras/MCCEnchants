package app.vercel.minecraftcustoms.mccenchants;

import app.vercel.minecraftcustoms.mccenchants.events.*;
import app.vercel.minecraftcustoms.mccenchants.hooks.Hooks;
import app.vercel.minecraftcustoms.mccenchants.api.enchantments.MCCEnchantment;
import app.vercel.minecraftcustoms.mccenchants.managers.InventoryManager;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerCommonPacketListenerImpl;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.server.network.ServerPlayerConnection;
import net.minecraft.world.item.ItemStack;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_20_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

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
        //Hooks.init();
        InventoryManager.registerInventories(this); // we will need them inventories


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
        this.getServer().getPluginManager().registerEvents(new OnPrepareInventoryResultEvent(), this);
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
        if (packet.getClass().getSimpleName().equals("PacketPlayOutSetSlot")) {
            System.out.println("found smth cool");
        }
        super.channelRead(ctx, packet);
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if (!msg.getClass().getSimpleName().equals("PacketPlayOutSetSlot")) {
            super.write(ctx, msg, promise);
            return;
        }
        System.out.println("packet sent " + msg.getClass().getSimpleName());
        System.out.println(ClientboundContainerSetSlotPacket.class.getSimpleName());

        ClientboundContainerSetSlotPacket packet = (ClientboundContainerSetSlotPacket) msg;

        // itemstack -> f

        ItemStack itemStack = packet.getItem();
        CraftItemStack craftItemStack = CraftItemStack.asCraftMirror(itemStack);

        System.out.println(packet.getSlot());
        System.out.println(craftItemStack);

        super.write(ctx, msg, promise);
    }

}