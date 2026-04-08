package me.TyAlternative.com.nocturne.listener;

import me.TyAlternative.com.nocturne.Nocturne;
import me.TyAlternative.com.nocturne.config.GameSettings;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Détecte le double appui rapide sur la touche d'échange de main (F).
 *
 * <p>Quand un swap est détecté, il est mis en attente pendant
 * {@link GameSettings#getDoubleSwapMaxDelayMs()} millisecondes.
 * Si un second swap arrive dans cette fenêtre, c'est un double-swap.
 * Sinon, le simple swap est exécuté à l'expiration du délai.
 *
 * <p>Le résultat ({@code true} = double, {@code false} = simple) est retourné
 * à {@link PlayerInteractionListener} pour dispatcher vers le bon trigger.
 */
@SuppressWarnings("unused")
public final class DoubleSwapDetector {

    /** Timestamp du dernier swap par joueur. */
    private final Map<UUID, Long> lastSwapTime   = new HashMap<>();

    /** Flag indiquant qu'un simple swap est en attente d'exécution. */
    private final Map<UUID, Boolean> pendingSwap = new HashMap<>();

    private final GameSettings settings;

    /**
     * @param settings configuration contenant le délai de détection
     */
    public DoubleSwapDetector(@NotNull GameSettings settings) {
        this.settings = settings;
    }

    // -------------------------------------------------------------------------
    // Détection
    // -------------------------------------------------------------------------

    /**
     * Enregistre un swap et détermine s'il s'agit d'un simple ou double appui.
     *
     * @param playerId UUID du joueur ayant pressé la touche
     * @return {@code true} si double-swap, {@code false} si premier appui (simple en attente)
     */
    public boolean detect(@NotNull UUID playerId) {
        long now = System.currentTimeMillis();
        int maxDelay = settings.getDoubleSwapMaxDelayMs();
        Long last = lastSwapTime.get(playerId);

        if (last != null && (now - last) <= maxDelay) {
            // Double-swap confirmé
            lastSwapTime.remove(playerId);
            pendingSwap.remove(playerId);
            return true;
        }

        // Premier appui : enregistrer et planifier l'exécution du simple swap
        lastSwapTime.put(playerId, now);
        pendingSwap.put(playerId, true);

        long delayTicks = Math.max(1L, (maxDelay/50L) + 1L);
        Bukkit.getScheduler().runTaskLater(
                Nocturne.getInstance(),
                () -> pendingSwap.remove(playerId),
                delayTicks
        );

        return false;
    }

    /**
     * {@code true} si un simple swap est en attente de confirmation pour ce joueur.
     * Utilisé par le listener pour savoir si l'exécution différée doit encore avoir lieu.
     */
    public boolean hasPendingSwap(@NotNull UUID playerId) {
        return Boolean.TRUE.equals(pendingSwap.get(playerId));
    }

    /**
     * Nettoie les données d'un joueur (déconnexion, fin de partie).
     */
    public void clear(@NotNull UUID playerId) {
        lastSwapTime.remove(playerId);
        pendingSwap.remove(playerId);
    }

    /** Nettoie toutes les données. */
    public void clearAll() {
        lastSwapTime.clear();
        pendingSwap.clear();
    }
}
