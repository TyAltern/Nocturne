package me.TyAlternative.com.nocturne.mechanics.vote;


import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * Représentation immuable du vote d'un joueur lors d'une phase de Vote.
 *
 * <p>Un vote sans cible ({@link #getTargetId()} retourne {@code null}) signifie
 * que le joueur a choisi de ne pas voter, mais son poids de vote est toujours
 * comptabilisé dans les calculs si une mécanique le nécessite.
 */
public class VoteEntry {

    private final UUID voterId;
    private final  @Nullable UUID targetId;
    private final int weight;

    /**
     * @param voterId  UUID du joueur ayant voté
     * @param targetId UUID du joueur ciblé, ou {@code null} si abstention
     * @param weight   poids de ce vote (peut être négatif pour certaines mécaniques)
     */
    public VoteEntry(@NotNull UUID voterId, @Nullable UUID targetId, int weight) {
        this.voterId = voterId;
        this.targetId = targetId;
        this.weight = weight;
    }

    /** UUID du joueur ayant émis ce vote. */
    public @NotNull UUID getVoterId() {
        return voterId;
    }

    /**
     * UUID du joueur ciblé par ce vote, ou {@code null} si le joueur s'est abstenu.
     *
     * @see #hasTarget()
     */
    public @Nullable UUID getTargetId() {
        return targetId;
    }

    /** {@code true} si ce vote cible un joueur spécifique (non-abstention). */
    public boolean hasTarget() {
        return targetId != null;
    }

    /**
     * Poids de ce vote dans le calcul final.
     * La valeur par défaut est {@code 1}. Peut être modifiée par certaines mécaniques
     * (ex: Corrosion qui peut rendre le poids négatif).
     */
    public int getWeight() {
        return weight;
    }

    /** {@code true} si ce vote cible le joueur identifié par {@code playerId}. */
    public boolean targets(@NotNull UUID playerId) {
        return playerId.equals(targetId);
    }

}
