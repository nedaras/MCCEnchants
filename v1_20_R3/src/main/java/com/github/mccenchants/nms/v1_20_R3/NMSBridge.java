package com.github.mccenchants.nms.v1_20_R3;

import com.github.mccenchants.enchantments.MCCEnchantment;
import com.github.mccenchants.nms.Bridge;
import com.github.mccenchants.nms.v1_20_R3.enchantments.NMSEnchantment;
import com.github.mccenchants.utils.Utils;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import org.bukkit.entity.Player;

import java.util.IdentityHashMap;

public class NMSBridge implements Bridge {

    @Override
    public void registerEnchantment(MCCEnchantment enchantment) {
        Utils.setFieldValue(BuiltInRegistries.ENCHANTMENT, "l", false);
        Utils.setFieldValue(BuiltInRegistries.ENCHANTMENT, "m", new IdentityHashMap<>());

        NMSEnchantment nmsEnchantment = new NMSEnchantment(enchantment);

        // TODO: make them idk override if already existing
        Registry.register(BuiltInRegistries.ENCHANTMENT, enchantment.getKey().toString(), nmsEnchantment);

        BuiltInRegistries.ENCHANTMENT.freeze();
    }

    @Override
    public void hookPlayerPackets(Player player) {
        PacketHandler.addPlayer(player);
    }

    @Override
    public void unhookPlayerPackets(Player player) {
        PacketHandler.removePlayer(player);
    }

}
