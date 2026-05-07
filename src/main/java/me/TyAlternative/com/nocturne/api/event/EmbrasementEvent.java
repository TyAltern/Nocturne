package me.TyAlternative.com.nocturne.api.event;

import me.TyAlternative.com.nocturne.mechanics.embrasement.EmbrasementCause;
import me.TyAlternative.com.nocturne.mechanics.embrasement.EmbrasementManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * Événement déclenché lorsqu'un joueur est sur le point d'être embrasé.
 *
 * <p>Les capacités peuvent :
 * <ul>
 *   <li>Annuler l'embrasement (le joueur n'est pas embrasé).</li>
 *   <li>Rediriger la cible via {@link #redirectTarget(UUID)} — un autre joueur
 *       est embrasé à la place.</li>
 *   <li>Modifier la cause via {@link #setCause(EmbrasementCause)}.</li>
 * </ul>
 *
 * <h2>Exemple — absorber un embrasement</h2>
 * <pre>{@code
 * @Override
 * public void onEmbrasement(EmbrasementEvent event, Player self, NocturnePlayer np) {
 *     if (!event.getTargetId().equals(protectedPlayerId)) return;
 *     // Absorber : l'embrasement vise maintenant le porteur de la capacité
 *     event.redirectTarget(np.getPlayerId());
 * }
 * }</pre>
 *
 * <h2>Différence avec Murmuration</h2>
 * Murmuration agit en {@code onGameplayPhaseEnd} sur les embrasements déjà enregistrés.
 * Cet événement intercepte <em>avant</em> l'enregistrement dans l'
 * {@link EmbrasementManager}.
 */
public final class EmbrasementEvent extends GameEvent<UUID> {

    private @NotNull UUID targetId;
    private final @Nullable UUID casterId;
    private @NotNull EmbrasementCause cause;

    /**
     * @param targetId UUID du joueur qui va être embrasé
     * @param casterId UUID du joueur à l'origine de l'embrasement, ou {@code null}
     * @param cause    source de l'embrasement
     */
    public EmbrasementEvent(
            @NotNull UUID targetId,
            @Nullable UUID casterId,
            @NotNull EmbrasementCause cause
    ) {
        this.targetId = targetId;
        this.casterId = casterId;
        this.cause    = cause;
    }

    // -------------------------------------------------------------------------
    // Valeur principale
    // -------------------------------------------------------------------------

    /** UUID courant de la cible de l'embrasement. */
    @Override
    public @NotNull UUID getValue() {
        return targetId;
    }

    // -------------------------------------------------------------------------
    // Cible
    // -------------------------------------------------------------------------

    /** UUID courant du joueur qui sera embrasé. */
    public @NotNull UUID getTargetId() {
        return targetId;
    }

    /**
     * Redirige l'embrasement vers un autre joueur.
     *
     * @param newTarget UUID du nouveau joueur ciblé, jamais {@code null}
     */
    public void redirectTarget(@NotNull UUID newTarget) {
        this.targetId = newTarget;
    }

    // -------------------------------------------------------------------------
    // Lanceur
    // -------------------------------------------------------------------------

    /**
     * UUID du joueur à l'origine de l'embrasement, ou {@code null} si la cause
     * est automatique (Murmuration, mécanique interne).
     */
    public @Nullable UUID getCasterId() {
        return casterId;
    }

    // -------------------------------------------------------------------------
    // Cause
    // -------------------------------------------------------------------------

    /** Cause courante de l'embrasement. */
    public @NotNull EmbrasementCause getCause() {
        return cause;
    }

    /**
     * Modifie la cause de l'embrasement.
     * Utile pour masquer l'origine réelle (ex: faire croire à une Torche plutôt qu'une Étincelle).
     *
     * @param cause nouvelle cause, jamais {@code null}
     */
    public void setCause(@NotNull EmbrasementCause cause) {
        this.cause = cause;
    }
}
