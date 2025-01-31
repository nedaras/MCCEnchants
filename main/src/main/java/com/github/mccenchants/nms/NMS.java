package com.github.mccenchants.nms;

import com.github.mccenchants.enchantments.MCCEnchantment;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;

public class NMS {

    final private Bridge bridge;

    private NMS(@NotNull Bridge bridge) {
        this.bridge = bridge;
    }

    public static NMS init(@NotNull String packageVersion) throws Exception {
        try {
            Object bridge = Class.forName("com.github.mccenchants.nms." + packageVersion + ".NMSBridge").getDeclaredConstructor().newInstance();
            return new NMS((Bridge) bridge);
        } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new Exception(e);
        }
    }

    public void registerEnchantment(MCCEnchantment enchantment) {
        bridge.registerEnchantment(enchantment);
    }

    public void hookPlayerPackets(Player player) {
        bridge.hookPlayerPackets(player);
    }

    public void unhookPlayerPackets(Player player) {
        bridge.unhookPlayerPackets(player);
    }

}
