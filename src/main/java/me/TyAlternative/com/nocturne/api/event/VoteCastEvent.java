package me.TyAlternative.com.nocturne.api.event;

import me.TyAlternative.com.nocturne.player.NocturnePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * Événement déclenché lorsqu'un joueur tente de nocturneVoter contre une cible.
 *
 * <p>Les capacités peuvent :
 * <ul>
 *   <li>Annuler le vote (le joueur ne vote pour personne).</li>
 *   <li>Rediriger la cible vers un autre UUID via {@link #redirectTarget(UUID)}.</li>
 *   <li>Modifier le poids du vote via {@link #setWeight(int)}.</li>
 * </ul>
 *
 * <h2>Exemple d'utilisation — rediriger un vote</h2>
 * <pre>{@code
 * @Override
 * public void onVoteCast(VoteCastEvent event, Player self, NocturnePlayer nocturnePlayer) {
 *     if (!event.getNocturneVoter().getUniqueId().equals(markedPlayerId)) return;
 *     // Rediriger le vote du joueur marqué vers une autre cible
 *     event.redirectTarget(alternativeTargetId);
 * }
 * }</pre>
 */
public final class VoteCastEvent extends GameEvent<UUID> {

    private final NocturnePlayer nocturneVoter;
    private final UUID voterId;
    private @Nullable UUID targetId;
    private int weight;

    /**
     * @param nocturneVoter joueur qui tente de nocturneVoter
     * @param targetId      UUID de la cible initiale
     * @param weight        poids de vote initial du votant
     */
    public VoteCastEvent(
            @NotNull NocturnePlayer nocturneVoter,
            @Nullable UUID targetId,
            int weight
    ) {
        this.nocturneVoter = nocturneVoter;
        this.voterId  = nocturneVoter.getPlayerId();
        this.targetId = targetId;
        this.weight   = weight;
    }

    // -------------------------------------------------------------------------
    // Valeur principale
    // -------------------------------------------------------------------------

    /** UUID de la cible courante du vote (peut avoir été redirigée). */
    @Override
    public @Nullable UUID getValue() {
        return targetId;
    }

    // -------------------------------------------------------------------------
    // Votant
    // -------------------------------------------------------------------------

    /** Joueur Nocturne qui émet le vote. */
    public @NotNull NocturnePlayer getVoter() {
        return nocturneVoter;
    }

    /** UUID du votant. */
    public @NotNull UUID getVoterId() {
        return voterId;
    }

    // -------------------------------------------------------------------------
    // Cible
    // -------------------------------------------------------------------------

    /**
     * UUID courant de la cible du vote, ou {@code null} si abstention
     * (vote annulé ou sans cible initiale).
     */
    public @Nullable UUID getTargetId() {
        return targetId;
    }

    /**
     * Redirige le vote vers une nouvelle cible.
     * Passer {@code null} transforme le vote en abstention.
     *
     * @param newTarget UUID du nouveau joueur ciblé, ou {@code null}
     */
    public void redirectTarget(@Nullable UUID newTarget) {
        this.targetId = newTarget;
    }

    // -------------------------------------------------------------------------
    // Poids
    // -------------------------------------------------------------------------

    /** Poids courant du vote (peut avoir été modifié). */
    public int getWeight() {
        return weight;
    }

    /**
     * Modifie le poids de ce vote pour ce tour uniquement.
     * N'affecte pas le {@link NocturnePlayer#getVoteWeight()} permanent.
     *
     * @param weight nouveau poids (peut être négatif)
     */
    public void setWeight(int weight) {
        this.weight = weight;
    }
}
