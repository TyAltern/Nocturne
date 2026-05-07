package me.TyAlternative.com.nocturne.api.event;

import me.TyAlternative.com.nocturne.elimination.EliminationCause;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;


/**
 * Événement déclenché lorsqu'un joueur est sur le point d'être éliminé.
 *
 * <p>Les capacités peuvent :
 * <ul>
 *   <li>Annuler l'élimination (le joueur survit).</li>
 *   <li>Rediriger l'élimination vers un autre joueur via {@link #redirectTarget(UUID)}.</li>
 *   <li>Modifier la cause via {@link #setCause(EliminationCause)}.</li>
 * </ul>
 *
 * <h2>Exemple — annuler une élimination une fois par partie</h2>
 * <pre>{@code
 * @Override
 * public void onElimination(EliminationEvent event, Player self, NocturnePlayer np) {
 *     if (!event.getTargetId().equals(np.getPlayerId())) return;
 *     if (alreadyUsed) return;
 *     alreadyUsed = true;
 *     event.cancel("§aVotre capacité vous a sauvé de l'élimination !");
 * }
 * }</pre>
 */

public final class EliminationEvent extends GameEvent<UUID>{

    private @NotNull UUID targetId;
    private @NotNull EliminationCause cause;

    /**
     * @param targetId UUID du joueur qui va être éliminé
     * @param cause    cause de l'élimination
     */
    public EliminationEvent(
            @NotNull UUID targetId,
            @NotNull EliminationCause cause
    ) {
        this.targetId = targetId;
        this.cause    = cause;
    }

    // -------------------------------------------------------------------------
    // Valeur principale
    // -------------------------------------------------------------------------

    /** UUID courant du joueur qui sera éliminé. */
    @Override
    public @NotNull UUID getValue() {
        return targetId;
    }

    // -------------------------------------------------------------------------
    // Cible
    // -------------------------------------------------------------------------

    /** UUID courant du joueur ciblé par l'élimination. */
    public @NotNull UUID getTargetId() {
        return targetId;
    }

    /**
     * Redirige l'élimination vers un autre joueur.
     *
     * @param newTarget UUID du nouveau joueur ciblé, jamais {@code null}
     */
    public void redirectTarget(@NotNull UUID newTarget) {
        this.targetId = newTarget;
    }

    // -------------------------------------------------------------------------
    // Cause
    // -------------------------------------------------------------------------

    /** Cause courante de l'élimination. */
    public @NotNull EliminationCause getCause() {
        return cause;
    }

    /**
     * Modifie la cause de l'élimination.
     *
     * @param cause nouvelle cause, jamais {@code null}
     */
    public void setCause(@NotNull EliminationCause cause) {
        this.cause = cause;
    }
}
