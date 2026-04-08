package me.TyAlternative.com.nocturne.listener;

import me.TyAlternative.com.nocturne.core.NocturneGame;
import me.TyAlternative.com.nocturne.elimination.EliminationCause;
import me.TyAlternative.com.nocturne.player.NocturnePlayer;
import me.TyAlternative.com.nocturne.player.PlayerState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Gère les connexions et déconnexions de joueurs.
 *
 * <p>À la connexion : crée les données Nocturne si nécessaire.
 * À la déconnexion pendant une partie : marque le joueur comme déconnecté
 * et vérifie si cela déclenche une condition de victoire.
 */
@SuppressWarnings("unused")
public final class ConnectionListener implements Listener {

    private final NocturneGame game;

    public ConnectionListener(NocturneGame game) {
        this.game = game;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        // Crée les données Nocturne pour le nouveau joueur (état LOBBY)
        game.getPlayerManager().getOrCreate(player);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (!game.isGameRunning()) return;

        NocturnePlayer nocturnePlayer = game.getPlayerManager().get(player);
        if (nocturnePlayer == null || !nocturnePlayer.isAlive()) return;

        // Traiter la déconnexion comme une élimination silencieuse
        nocturnePlayer.setState(PlayerState.DISCONNECTED);
        game.getEliminationManager().eliminate(
                nocturnePlayer.getPlayerId(),
                EliminationCause.DISPARITION
        );

        game.broadcast("§e" + player.getName() + " §7s'est déconnecté.");

        // La déconnexion peut déclencher une victoire
        game.getVictoryManager().checkVictory();
    }
}
