package me.TyAlternative.com.nocturne.mechanics.vent;

import me.TyAlternative.com.nocturne.core.NocturneGame;
import me.TyAlternative.com.nocturne.player.NocturnePlayer;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class VentManager {

    private final List<Location> ventLocations;
    private final Map<UUID, Integer> ventPlayers;
    private final NocturneGame game;

    public VentManager(@NotNull NocturneGame game) {
        this.game = game;
        this.ventLocations = game.getSettings().getVentLocations();
        this.ventPlayers = new HashMap<>();
    }

    public void enterVent(@NotNull NocturnePlayer nocturnePlayer) {
        if (!tryEnteringVent(nocturnePlayer)) return;
        Player player = nocturnePlayer.getPlayer();
        if (player == null) return;

        int ventNumber = ventPlayers.get(nocturnePlayer.getPlayerId());
        tpPlayerToVentNumber(nocturnePlayer, ventNumber);
    }

    public void exitVent(@NotNull NocturnePlayer nocturnePlayer) {
        ventPlayers.remove(nocturnePlayer.getPlayerId());
    }

    private boolean tryEnteringVent(@NotNull NocturnePlayer nocturnePlayer) {
        Player player = nocturnePlayer.getPlayer();
        if (player == null) return false;
        for (Location ventLocation : game.getSettings().getVentLocations()) {
            if (ventLocation.equals(player.getLocation().getBlock().getLocation())) {
                ventPlayers.put(nocturnePlayer.getPlayerId(), getVentNumber(ventLocation));
                return true;
            }
        }
        return false;

    }

    public void cycleVentLeft(@NotNull NocturnePlayer nocturnePlayer) {
        int ventNumber = ventPlayers.get(nocturnePlayer.getPlayerId()) - 1;
        if (ventNumber < 0) ventNumber = ventLocations.size()-1;

        tpPlayerToVentNumber(nocturnePlayer, ventNumber);
    }

    public void cycleVentRight(@NotNull NocturnePlayer nocturnePlayer) {
        int ventNumber = ventPlayers.get(nocturnePlayer.getPlayerId()) + 1;
        if (ventNumber == ventLocations.size()) ventNumber = 0;

        tpPlayerToVentNumber(nocturnePlayer, ventNumber);
    }

    private void tpPlayerToVentNumber(@NotNull NocturnePlayer nocturnePlayer, int ventNumber) {
        Player player = nocturnePlayer.getPlayer();
        if (player == null) return;
        Location ventLoc = ventLocations.get(ventNumber);

        player.teleport(ventLoc.clone().subtract(-0.5, 1, -0.5));
        ventPlayers.put(nocturnePlayer.getPlayerId(), ventNumber);
    }


    private int getVentNumber(@NotNull Location location) {
        for (int i = 0; i < ventLocations.size(); i++) {
            Location ventLocation = ventLocations.get(i);
            if (ventLocation.equals(location)) return i;
        }

        return 0;
    }


    public boolean isPlayerInVent(@NotNull NocturnePlayer nocturnePlayer) {
        return ventPlayers.containsKey(nocturnePlayer.getPlayerId());
    }

    public boolean isPlayerInVent(@NotNull Player player) {
        return ventPlayers.containsKey(player.getUniqueId());
    }
}
