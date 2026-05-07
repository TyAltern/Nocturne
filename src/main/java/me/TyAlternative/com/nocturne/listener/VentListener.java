package me.TyAlternative.com.nocturne.listener;

import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import me.TyAlternative.com.nocturne.core.NocturneGame;
import me.TyAlternative.com.nocturne.mechanics.vent.VentManager;
import me.TyAlternative.com.nocturne.player.NocturnePlayer;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.NotNull;

public class VentListener implements Listener {

    private final NocturneGame game;
    private final VentManager ventManager;

    public VentListener(@NotNull NocturneGame game) {
        this.game = game;
        this.ventManager = game.getVentManager();
    }


    @EventHandler
    public void onPlayerSneak(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        NocturnePlayer nocturnePlayer = game.getPlayerManager().get(player);
        if (nocturnePlayer == null) return;

        if (ventManager.isPlayerInVent(nocturnePlayer)) return;
        if (event.isSneaking()) return;
//        if (!game.isGameRunning()) return;

        boolean isAVent = false;
        for (Location ventLocation : game.getSettings().getVentLocations()) {
            if (ventLocation.equals(player.getLocation().getBlock().getLocation())) {
                isAVent = true;
            }
        }
        if (!isAVent) return;
//        event.setCancelled(true);
//        player.setSneaking(false);

        ventManager.enterVent(nocturnePlayer);
        player.sendMessage("§eEnter a vent");

    }
    @EventHandler
    public void onPlayerJump(PlayerJumpEvent event) {
        Player player = event.getPlayer();
        NocturnePlayer nocturnePlayer = game.getPlayerManager().get(player);
        if (nocturnePlayer == null) return;

        if (!ventManager.isPlayerInVent(nocturnePlayer)) return;
//        if (!game.isGameRunning()) return;

        ventManager.exitVent(nocturnePlayer);

        player.sendMessage("§eExit a vent");


    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;
        Player player = event.getPlayer();
        if (!ventManager.isPlayerInVent(player)) return;

        NocturnePlayer nocturnePlayer = game.getPlayerManager().get(player);
        if (nocturnePlayer == null) return;

        if (event.getAction().isLeftClick()) {
            onLeftCLick(nocturnePlayer, event);
            player.sendMessage("§eLeft");
        }
        if (event.getAction().isRightClick()) {
            onRightCLick(nocturnePlayer, event);
            player.sendMessage("§eRight");
        }

    }

    public void onLeftCLick(@NotNull NocturnePlayer nocturnePlayer, @NotNull PlayerInteractEvent event) {
        ventManager.cycleVentLeft(nocturnePlayer);
    }

    public void onRightCLick(@NotNull NocturnePlayer nocturnePlayer, @NotNull PlayerInteractEvent event) {
        ventManager.cycleVentRight(nocturnePlayer);
    }
}
