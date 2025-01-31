package com.github.mccenchants.nms;

import com.github.mccenchants.enchantments.MCCEnchantment;
import org.bukkit.entity.Player;

public interface Bridge  {

    void registerEnchantment(MCCEnchantment enchantment);

    void hookPlayerPackets(Player player);

    void unhookPlayerPackets(Player player);

}
