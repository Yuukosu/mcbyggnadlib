package net.yuukosu;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class EventListener implements Listener {

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Block clickedBlock = event.getClickedBlock();
        ItemStack item = event.getItem();

        if (item != null && clickedBlock != null) {
            if (item.getType() == Material.GOLD_AXE) {
                event.setCancelled(true);

                switch (event.getAction()) {
                    case LEFT_CLICK_BLOCK -> {
                        ByggnadCommand.setPos1(player, clickedBlock.getLocation());
                        player.sendMessage("§aPosition 1 selected!");
                    }
                    case RIGHT_CLICK_BLOCK -> {
                        ByggnadCommand.setPos2(player, clickedBlock.getLocation());
                        player.sendMessage("§aPosition 2 selected!");
                    }
                }
            }
        }
    }
}