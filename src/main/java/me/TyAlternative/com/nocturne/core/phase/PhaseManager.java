package me.TyAlternative.com.nocturne.core.phase;

import me.TyAlternative.com.nocturne.Nocturne;
import me.TyAlternative.com.nocturne.api.phase.GamePhase;
import me.TyAlternative.com.nocturne.api.phase.PhaseType;
import me.TyAlternative.com.nocturne.core.NocturneGame;
import me.TyAlternative.com.nocturne.core.phase.impl.EndPhase;
import me.TyAlternative.com.nocturne.core.phase.impl.GameplayPhase;
import me.TyAlternative.com.nocturne.core.phase.impl.LobbyPhase;
import me.TyAlternative.com.nocturne.core.phase.impl.VotePhase;
import me.TyAlternative.com.nocturne.core.round.RoundContext;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Orchestre le cycle de vie des phases de jeu.
 *
 * <p>Maintient le registre des phases disponibles, gère les transitions et la
 * planification de la fin de chaque phase via le scheduler Bukkit.
 *
 * <h2>Garanties de transition</h2>
 * <ul>
 *   <li>{@code onEnd} de la phase sortante est <strong>toujours</strong> appelé
 *       avant {@code onStart} de la phase entrante.</li>
 *   <li>La tâche de fin de phase est annulée avant toute transition manuelle
 *       ({@link #skipCurrentPhase()}).</li>
 *   <li>Un seul {@link PhaseContext} est actif à la fois.</li>
 * </ul>
 *
 * <h2>Transitions standards</h2>
 * <pre>
 * LOBBY → GAMEPLAY → VOTE → GAMEPLAY → VOTE → ... → END → LOBBY
 * </pre>
 *
 * <p>La logique de transition (vérification de victoire, incrémentation de manche)
 * est déléguée au {@link NocturneGame} via le
 * {@link PhaseTransitionHandler}.
 */
@SuppressWarnings("unused")
public final class PhaseManager {

    // -------------------------------------------------------------------------
    // Registre des phases
    // -------------------------------------------------------------------------

    private final Map<PhaseType, GamePhase> phases = new EnumMap<>(PhaseType.class);

    // -------------------------------------------------------------------------
    // État courant
    // -------------------------------------------------------------------------

    private GamePhase currentPhase;
    private @Nullable PhaseContext currentContext;
    private @Nullable BukkitTask endTask;

    // -------------------------------------------------------------------------
    // Dépendances
    // -------------------------------------------------------------------------
    private final PhaseTransitionHandler transitionHandler;
    private final Logger logger;

    /**
     * @param transitionHandler gestionnaire appelé lors des transitions de phase
     * @param logger            logger du plugin
     */
    public PhaseManager(PhaseTransitionHandler transitionHandler, Logger logger) {
        this.transitionHandler = transitionHandler;
        this.logger = logger;

        // Enregistrement de toutes les phases
        register(new LobbyPhase());
        register(new GameplayPhase());
        register(new VotePhase());
        register(new EndPhase());

        this.currentPhase = phases.get(PhaseType.LOBBY);

    }

    private void register(@NotNull GamePhase phase) {
        phases.put(phase.getType(), phase);
    }

    // -------------------------------------------------------------------------
    // Transitions
    // -------------------------------------------------------------------------

    /**
     * Démarre la phase identifiée par {@code type} avec le {@link RoundContext} donné.
     *
     * <p>Si une phase est en cours, sa tâche de fin est annulée.
     * Le {@code onEnd} de la phase précédente doit avoir été appelé <strong>avant</strong>
     * cet appel (responsabilité de l'appelant, typiquement {@link #endCurrentPhase()}).
     *
     * @param type         type de la phase à démarrer
     * @param roundContext contexte de la manche courante
     * @throws IllegalArgumentException si le type de phase est inconnu
     */
    public void startPhase(@NotNull PhaseType type, @NotNull RoundContext roundContext) {
        // Annuler la tâche de fin éventuelle
        cancelEndTask();

        GamePhase phase = phases.get(type);
        if (phase == null) throw new IllegalArgumentException("Phase inconnue : " + type);

        currentPhase = phase;

        // Créer le contexte avec le timestamp actuel
        long now = System.currentTimeMillis();
        long duration = phase.getDurationMs(
                new PhaseContext(now, 0, roundContext) // dure calculée avec le contexte minimal
        );
        currentContext = new PhaseContext(now, duration, roundContext);
        logger.info("[Nocturne] Démarrage de la phase : %s (manche %d)"
                .formatted(type.getDisplayName(), roundContext.getRoundNumber()));

        // Démarrer la phase
        phase.onStart(currentContext);

        // Planifier la fin uniquement pour les phases in-game
        if (type.isInGame()) {
            long durationTicks = duration / 50L;
            endTask = Nocturne.getInstance()
                    .getServer()
                    .getScheduler()
                    .runTaskLater(
                            Nocturne.getInstance(),
                            this::endCurrentPhase,
                            durationTicks
                    );
        }

    }

    /**
     * Termine la phase courante et délègue la transition au {@link PhaseTransitionHandler}.
     *
     * <p>Appelé automatiquement à la fin du timer ou manuellement via {@link #skipCurrentPhase()}.
     */
    public void endCurrentPhase() {
        if (currentPhase == null || currentContext == null) return;

        cancelEndTask();

        logger.info("[Nocturne] Fin de la phase : " + currentPhase.getType().getDisplayName());
        currentPhase.onEnd(currentContext);

        // Déléguer la logique de transition (victoire, nouvelle manche...)
        transitionHandler.onPhaseEnded(currentPhase.getType());
    }


    /**
     * Force la fin de la phase courante immédiatement.
     * Même comportement que la fin naturelle, mais déclenché manuellement.
     */
    public void skipCurrentPhase() {
        endCurrentPhase();
    }

    /**
     * Arrête complètement le cycle de phases et retourne en LOBBY.
     * Appelé par {@link NocturneGame#stopGame}.
     */
    public void stop() {
        cancelEndTask();
        currentPhase = phases.get(PhaseType.LOBBY);
        currentContext = null;
    }

    // -------------------------------------------------------------------------
    // Accesseurs
    // -------------------------------------------------------------------------

    /** Type de la phase actuellement active. */
    public @NotNull PhaseType getCurrentType() {
        return currentPhase != null ? currentPhase.getType() : PhaseType.LOBBY;
    }

    /**
     * Contexte de la phase courante, ou {@code null} si aucune phase active
     * (état LOBBY sans partie en cours).
     */
    public @Nullable PhaseContext getCurrentContext() {
        return currentContext;
    }

    /** Temps restant en secondes dans la phase courante, ou {@code 0} si non applicable. */
    public int getRemainingSeconds() {
        return currentContext != null ? currentContext.getRemainingSeconds() : 0;
    }

    // -------------------------------------------------------------------------
    // Utilitaires privés
    // -------------------------------------------------------------------------

    private void cancelEndTask() {
        if (endTask != null && !endTask.isCancelled()) {
            endTask.cancel();
            endTask = null;
        }
    }

    // -------------------------------------------------------------------------
    // Interface de callback pour les transitions
    // -------------------------------------------------------------------------

    /**
     * Interface de callback appelée par le {@link PhaseManager} lors des transitions.
     *
     * <p>Implémentée par {@link NocturneGame} pour
     * encapsuler la logique de transition (vérification de victoire, incrémentation
     * de manche, démarrage de la phase suivante).
     */
    @FunctionalInterface
    public interface PhaseTransitionHandler {
        /**
         * Appelé après la fin d'une phase, avant le démarrage de la suivante.
         *
         * @param endedPhase type de la phase qui vient de se terminer
         */
        void onPhaseEnded(@NotNull PhaseType endedPhase);
    }
}
