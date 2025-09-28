package com.mintdino.veinminer;

import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.plugin.java.JavaPlugin;

public class VeinMinerPlugin extends JavaPlugin {

    public static Enchantment VEIN_MINER;

    @Override
    public void onEnable() {
        VEIN_MINER = new VeinMinerEnchant(new NamespacedKey(this, "vein_miner"));
        try {
            Enchantment.registerEnchantment(VEIN_MINER);
        } catch (IllegalArgumentException ignored) {}
        getServer().getPluginManager().registerEvents(new VeinMinerListener(), this);
        getLogger().info("VeinMiner enabled. Compatible with Java & Bedrock (GeyserMC).");
    }
}
