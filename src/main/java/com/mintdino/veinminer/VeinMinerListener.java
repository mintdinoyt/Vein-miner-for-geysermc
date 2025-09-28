package com.mintdino.veinminer;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class VeinMinerListener implements Listener {

    @EventHandler
    public void onPrepareEnchant(PrepareItemEnchantEvent event) {
        ItemStack item = event.getItem();
        if (!VeinMinerPlugin.VEIN_MINER.canEnchantItem(item)) return;
        if (new Random().nextInt(4) == 0) {
            event.getOffers()[0].setEnchantment(VeinMinerPlugin.VEIN_MINER);
            event.getOffers()[0].setCost(15);
            event.getOffers()[0].setEnchantmentLevel(1);
        }
    }

    @EventHandler
    public void onEnchantItem(EnchantItemEvent event) {
        if (event.getEnchantsToAdd().containsKey(VeinMinerPlugin.VEIN_MINER)) {
            ItemStack item = event.getItem();
            ItemMeta meta = item.getItemMeta();
            List<String> lore = meta.hasLore() ? meta.getLore() : new ArrayList<>();
            lore.add(ChatColor.GRAY + "Vein Miner I");
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        ItemStack tool = player.getInventory().getItemInMainHand();
        if (tool == null || !tool.containsEnchantment(VeinMinerPlugin.VEIN_MINER)) return;

        Block block = event.getBlock();
        Material type = block.getType();
        if (!isOre(type)) return;

        Set<Block> vein = getConnectedVein(block, type, 32);
        for (Block ore : vein) {
            if (!ore.equals(block)) {
                ore.breakNaturally(tool);
                tool.setDurability((short) (tool.getDurability() + 1));
            }
        }
    }

    private boolean isOre(Material mat) {
        return switch (mat) {
            case COAL_ORE, DEEPSLATE_COAL_ORE,
                 IRON_ORE, DEEPSLATE_IRON_ORE,
                 COPPER_ORE, DEEPSLATE_COPPER_ORE,
                 GOLD_ORE, DEEPSLATE_GOLD_ORE,
                 REDSTONE_ORE, DEEPSLATE_REDSTONE_ORE,
                 LAPIS_ORE, DEEPSLATE_LAPIS_ORE,
                 DIAMOND_ORE, DEEPSLATE_DIAMOND_ORE,
                 EMERALD_ORE, DEEPSLATE_EMERALD_ORE,
                 NETHER_QUARTZ_ORE, NETHER_GOLD_ORE -> true;
            default -> false;
        };
    }

    private Set<Block> getConnectedVein(Block start, Material type, int limit) {
        Set<Block> result = new HashSet<>();
        Queue<Block> queue = new LinkedList<>();
        queue.add(start);

        while (!queue.isEmpty() && result.size() < limit) {
            Block b = queue.poll();
            if (b.getType() != type || result.contains(b)) continue;
            result.add(b);
            for (int x = -1; x <= 1; x++) {
                for (int y = -1; y <= 1; y++) {
                    for (int z = -1; z <= 1; z++) {
                        if (Math.abs(x) + Math.abs(y) + Math.abs(z) == 1) {
                            Block neighbor = b.getRelative(x, y, z);
                            if (!result.contains(neighbor) && neighbor.getType() == type) {
                                queue.add(neighbor);
                            }
                        }
                    }
                }
            }
        }
        return result;
    }
}
