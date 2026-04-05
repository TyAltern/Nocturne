package me.TyAlternative.com.nocturne.api.phase;

import me.TyAlternative.com.nocturne.core.phase.PhaseContext;
import me.TyAlternative.com.nocturne.core.phase.PhaseManager;
import org.jetbrains.annotations.NotNull;

/**
 * Contrat d'une phase de jeu gérée par le {@link PhaseManager}.
 *
 * <p>Chaque phase est responsable de son propre cycle de vie via {@link #onStart} et {@link #onEnd}.
 * Elle ne doit contenir aucune logique de transition : c'est le {@link PhaseManager}
 * qui détermine la phase suivante via {@link PhaseType}.
 *
 * <p>Les implémentations concrètes se trouvent dans {@code me.tyalternative.nocturne.core.phase.impl}.
 */
public interface GamePhase {

    /**
     * Appelé lorsque cette phase démarre.
     *
     * <p>La phase doit initialiser ses ressources, notifier les joueurs
     * et démarrer les tâches nécessaires.
     *
     * @param context contexte complet de la phase (manche, timing, données de manche)
     */
    void onStart(@NotNull PhaseContext context);

    /**
     * Appelé lorsque cette phase se termine, avant le démarrage de la suivante.
     *
     * <p>La phase doit traiter ses résultats (éliminations, votes, etc.),
     * arrêter ses tâches et libérer ses ressources.
     *
     * @param context contexte complet de la phase, identique à celui passé à {@link #onStart}
     */
    void onEnd(@NotNull PhaseContext context);

    /** Type identifiant cette phase, utilisé par le {@link PhaseManager}. */
    @NotNull PhaseType getType();

    /**
     * Durée de la phase en millisecondes.
     * Retourner {@link Long#MAX_VALUE} pour une phase de durée infinie (ex : Lobby).
     *
     * @param context contexte disponible si la durée dépend de la configuration ou de la manche
     */
    long getDurationMs(@NotNull PhaseContext context);
}
