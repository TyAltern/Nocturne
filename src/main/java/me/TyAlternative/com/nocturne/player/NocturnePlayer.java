package me.TyAlternative.com.nocturne.player;


import me.TyAlternative.com.nocturne.api.role.Role;
import me.TyAlternative.com.nocturne.api.role.RoleFactory;
import me.TyAlternative.com.nocturne.role.RoleDistributor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * Représentation d'un joueur pendant une partie Nocturne.
 *
 * <p>Cette classe centralise toutes les données de jeu propres à un joueur :
 * état, rôle assigné, système de vote, flèches spectrales.
 *
 * <p>Contrairement à l'ancienne implémentation, {@code NocturnePlayer} ne contient
 * <strong>pas</strong> de {@code customData} générique. L'état propre à chaque
 * capacité est stocké directement dans l'instance de la capacité, puisque les rôles
 * sont instanciés par joueur.
 *
 * <h2>Cycle de vie</h2>
 * <ul>
 *   <li>{@link #resetForNewRound()} — appelé entre chaque manche, réinitialise le vote.</li>
 *   <li>{@link #resetFull()} — appelé en fin de partie, remet tout à zéro.</li>
 * </ul>
 */
@SuppressWarnings("unused")
public final class NocturnePlayer {

    private final UUID playerId;

    // État général
    private PlayerState state;

    // Rôle — non null pendant une partie, null en lobby
    private @Nullable Role role;


    // Système de vote
    private @Nullable UUID votedPlayerId;
    private int voteWeight;
    private boolean canVote;
    private @Nullable TextColor voteGlowColor;

    // Flèches spectrales
    private int spectralArrowsRemaining;


    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    /**
     * @param playerId UUID du joueur Minecraft associé à cette entrée
     */
    public NocturnePlayer(@NotNull UUID playerId) {
        this.playerId = playerId;
        this.state = PlayerState.LOBBY;
        this.voteWeight = 1;
        this.canVote = true;
        this.voteGlowColor = TextColor.color(255,170,0); // doré par défaut
    }


    // -------------------------------------------------------------------------
    // Identité
    // -------------------------------------------------------------------------


    /** UUID Minecraft de ce joueur. */
    public @NotNull UUID getPlayerId() {
        return playerId;
    }

    /**
     * Instance {@link Player} Bukkit en ligne, ou {@code null} si le joueur est déconnecté.
     * Toujours vérifier la nullabilité (jsp si ça ce dit) avant utilisation.
     */
    public @Nullable Player getPlayer() {
        return Bukkit.getPlayer(playerId);
    }


    // -------------------------------------------------------------------------
    // State
    // -------------------------------------------------------------------------

    public @NotNull PlayerState getState() {
        return state;
    }

    public void setState(@NotNull PlayerState state) {
        this.state = state;
    }

    /** {@code true} si ce joueur est vivant et actif dans la partie. */
    public boolean isAlive() {
        return state.isAlive();
    }


    // -------------------------------------------------------------------------
    // Role
    // -------------------------------------------------------------------------

    /**
     * Rôle assigné à ce joueur, ou {@code null} si aucune partie n'est en cours
     * ou si le rôle n'a pas encore été distribué.
     */
    public @Nullable Role getRole() {
        return role;
    }

    /**
     * Assigne un rôle à ce joueur.
     * Doit être appelé uniquement par le {@link RoleDistributor}.
     *
     * @param role instance fraîche créée par la {@link RoleFactory}
     */
    public void setRole(@Nullable Role role) {
        this.role = role;
    }

    /** {@code true} si un rôle a été assigné à ce joueur. */
    public boolean hasRole() {
        return role != null;
    }


    // -------------------------------------------------------------------------
    // Flèches spectrales
    // -------------------------------------------------------------------------

    /** Nombre de flèches spectrales restantes pour ce joueur dans la partie en cours. */
    public int getSpectralArrowsRemaining() {
        return spectralArrowsRemaining;
    }

    public void setSpectralArrowsRemaining(int count) {
        this.spectralArrowsRemaining = Math.max(0,count);
    }

    /**
     * Tente de consommer une flèche spectrale.
     *
     * @return {@code true} si une flèche a été consommée, {@code false} si le joueur n'en avait plus
     */
    public boolean consumeSpectralArrow() {
        if (spectralArrowsRemaining <= 0) {
            return false;
        }
        spectralArrowsRemaining--;
        return true;
    }

    public boolean hasSpectralArrows() {
        return spectralArrowsRemaining > 0;
    }


    // -------------------------------------------------------------------------
    // Système de vote
    // -------------------------------------------------------------------------

    /** UUID du joueur ciblé par le vote, ou {@code null} si ce joueur n'a pas encore voté. */
    public @Nullable UUID getVotedPlayerId() {
        return votedPlayerId;
    }

    /** Enregistre le vote de ce joueur vers {@code targetId}. */
    public void voteFor(@NotNull UUID targetId) {
        this.votedPlayerId = targetId;
    }

    /** Annule le vote en cours de ce joueur. */
    public void clearVote() {
        this.votedPlayerId = null;
    }

    /** {@code true} si ce joueur a voté pour quelqu'un dans cette manche. */
    public boolean hasVoted() {
        return votedPlayerId != null;
    }

    /**
     * Poids de ce joueur dans le calcul du vote.
     * Valeur par défaut : {@code 1}. Peut être modifiée par certaines mécaniques.
     */
    public int getVoteWeight() {
        return voteWeight;
    }

    public void setVoteWeight(int weight) {
        this.voteWeight = weight;
    }

    /** {@code true} si ce joueur est autorisé à voter. */
    public boolean canVote() {return canVote;}

    public void setCanVote(boolean canVote) { this.canVote = canVote;}

    /**
     * Couleur de surbrillance (glowing) affichée sur le joueur ciblé lors du vote.
     * {@code null} si non défini (aucune surbrillance).
     */
    public @Nullable TextColor getVoteGlowColor() {return voteGlowColor;}

    public void setVoteGlowColor(@Nullable TextColor color) {this.voteGlowColor = color;}


    // -------------------------------------------------------------------------
    // Réinitialisations
    // -------------------------------------------------------------------------

    /**
     * Réinitialise les données dépendantes de la manche.
     * Appelé par le {@link PlayerManager} au début de chaque nouvelle manche.
     *
     * <p>Le rôle et les flèches spectrales sont conservés ; seul le vote est effacé.
     */
    public void resetForNewRound() {
        clearVote();
    }

    /**
     * Réinitialisation complète en fin de partie.
     * Remet le joueur dans son état initial de lobby.
     *
     * <p>Le rôle est dissocié ici : son état interne est géré par ses propres
     * instances de capacités, qui sont garbage-collectées avec le rôle.
     */
    public void resetFull() {
        state = PlayerState.LOBBY;
        role = null;
        votedPlayerId = null;
        voteWeight = 1;
        canVote = true;
        voteGlowColor = TextColor.color(255,170,0);
        spectralArrowsRemaining = 0;
    }


    // -------------------------------------------------------------------------
    // Object
    // -------------------------------------------------------------------------

    @Override
    public String toString() {
        Player player = getPlayer();
        String name = player != null ? player.getName() : Bukkit.getOfflinePlayer(playerId).getName() + " (offline)";
        String roleName = role != null ? role.getDisplayName() : "aucun";
        return "NocturnePlayer[%s, %s, rôle=%s]".formatted(name, state, roleName);
    }

}
