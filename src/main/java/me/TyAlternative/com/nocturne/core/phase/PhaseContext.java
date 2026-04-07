package me.TyAlternative.com.nocturne.core.phase;

import me.TyAlternative.com.nocturne.api.phase.GamePhase;
import me.TyAlternative.com.nocturne.core.round.RoundContext;
import org.jetbrains.annotations.NotNull;

/**
 * Contexte immuable d'une phase de jeu en cours.
 *
 * <p>Créé par le {@link PhaseManager} au démarrage de chaque phase et passé
 * aux méthodes {@link GamePhase#onStart} et
 * {@link GamePhase#onEnd}.
 *
 * <p>Regroupe les informations de timing ainsi que le {@link RoundContext}
 * portant l'état de la manche courante (embrasements, protections, disparitions).
 */
@SuppressWarnings({"unused", "ClassCanBeRecord"})
public final class PhaseContext {

    private final long startTimeMs;
    private final long durationMs;
    private final RoundContext roundContext;

    /**
     * @param startTimeMs  timestamp de démarrage de la phase en millisecondes ({@link System#currentTimeMillis()})
     * @param durationMs   durée totale de la phase en millisecondes
     * @param roundContext contexte de la manche encapsulant les managers de mécaniques
     */
    public PhaseContext(long startTimeMs, long durationMs, RoundContext roundContext) {
        this.startTimeMs = startTimeMs;
        this.durationMs = durationMs;
        this.roundContext = roundContext;
    }

    // -------------------------------------------------------------------------
    // Timing
    // -------------------------------------------------------------------------

    /** Timestamp de démarrage de la phase en millisecondes. */
    public long getStartTimeMs() {
        return startTimeMs;
    }
    /** Durée totale configurée pour cette phase en millisecondes. */
    public long getDurationMs() {
        return durationMs;
    }

    /**
     * Temps restant avant la fin de la phase, en millisecondes.
     * Retourne {@code 0} si la phase est déjà terminée.
     */
    public long getRemainingMs() {
        long elapsed = System.currentTimeMillis() - startTimeMs;
        return Math.max(0L, durationMs - elapsed);
    }


    /**
     * Temps restant arrondi à la seconde supérieure.
     * Pratique pour l'affichage dans la BossBar et l'ActionBar.
     */
    public int getRemainingSeconds() {
        return (int) Math.ceil(getRemainingMs() / 1000.0);
    }

    /**
     * Progression de la phase entre {@code 0.0} (début) et {@code 1.0} (fin).
     * Toujours dans l'intervalle [0.0, 1.0].
     */
    public float getProgress() {
        if (durationMs <= 0) return 1.0f;
        long elapsed = System.currentTimeMillis() - startTimeMs;
        return Math.min(1.0f, Math.max(0.0f, (float) elapsed / durationMs));
    }


    // -------------------------------------------------------------------------
    // Manche
    // -------------------------------------------------------------------------

    /** Contexte de la manche courante, portant les managers de mécaniques. */
    public @NotNull RoundContext getRoundContext() {
        return roundContext;
    }

    /** Numéro de la manche courante. */
    public int getRoundNumber() {
        return roundContext.getRoundNumber();
    }
}
