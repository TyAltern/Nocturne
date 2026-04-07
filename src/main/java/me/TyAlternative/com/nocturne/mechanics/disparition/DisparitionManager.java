package me.TyAlternative.com.nocturne.mechanics.disparition;

import me.TyAlternative.com.nocturne.core.round.RoundContext;
import me.TyAlternative.com.nocturne.elimination.EliminationManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;

/**
 * Gestionnaire des disparitions actives pour une manche de jeu.
 *
 * <p>Une disparition représente l'élimination d'un joueur non par Embrasement,
 * mais par une mécanique annexe (ex : Solitude Mortelle).
 * Elle est traitée en fin de phase de Gameplay par l' {@link EliminationManager}.
 *
 * <p>Une instance par manche, créée dans le {@link RoundContext}.
 */
@SuppressWarnings("unused")
public final class DisparitionManager {

    /** Map UUID joueur → cause de la disparition. */
    private final Map<UUID, DisparitionCause> disappeared = new LinkedHashMap<>();

    // -------------------------------------------------------------------------
    // Disparition
    // -------------------------------------------------------------------------

    /**
     * Enregistre la disparition du joueur identifié par {@code playerId}.
     *
     * <p>Si le joueur est déjà marqué pour disparition, l'entrée est mise à jour
     * avec la nouvelle cause.
     *
     * @param playerId UUID du joueur
     * @param cause    raison de la disparition
     */
    public void markForDisparition(@NotNull UUID playerId, @NotNull DisparitionCause cause) {
        disappeared.put(playerId, cause);
    }

    /**
     * Retire la disparition du joueur identifié par {@code playerId}.
     *
     * @param playerId UUID du joueur
     */
    public void removeDisparition(@NotNull UUID playerId) {
        disappeared.remove(playerId);
    }


    // -------------------------------------------------------------------------
    // Lecture
    // -------------------------------------------------------------------------

    /** {@code true} si le joueur est marqué pour disparition. */
    public boolean isMarked(@NotNull UUID playerId) {
        return disappeared.containsKey(playerId);
    }

    /**
     * Retourne la cause de la disparition du joueur, ou {@code null}
     * s'il n'est pas marqué.
     */
    public @Nullable DisparitionCause getCause (@NotNull UUID playerId) {
        return disappeared.get(playerId);
    }

    /**
     * Retourne une vue non-modifiable de toutes les disparitions actives
     * (UUID joueur → cause).
     */
    public @NotNull @Unmodifiable Map<UUID, DisparitionCause> getAll() {
        return Collections.unmodifiableMap(disappeared);
    }

    /** Nombre de joueurs marqués pour disparition. */
    public int count() {
        return disappeared.size();
    }

    /** Vide toutes les disparitions. Appelé via le {@code RoundContext} en début de manche. */
    public void clear() {
        disappeared.clear();
    }




}
