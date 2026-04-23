package me.TyAlternative.com.nocturne.api.role;

import me.TyAlternative.com.nocturne.api.ability.Ability;
import me.TyAlternative.com.nocturne.api.ability.AbilityContext;
import me.TyAlternative.com.nocturne.api.ability.AbilityResult;
import me.TyAlternative.com.nocturne.core.phase.PhaseContext;
import me.TyAlternative.com.nocturne.core.phase.PhaseManager;
import me.TyAlternative.com.nocturne.elimination.EliminationCause;
import me.TyAlternative.com.nocturne.mechanics.vote.VoteEntry;
import me.TyAlternative.com.nocturne.player.NocturnePlayer;
import me.TyAlternative.com.nocturne.role.RoleRegistry;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;
import java.util.UUID;

/**
 * Contrat public de tout rôle pouvant être assigné à un joueur.
 *
 * <p>Chaque implémentation de {@code Role} est instanciée une fois par joueur (via
 * {@link RoleFactory}), garantissant l'isolation totale de l'état entre joueurs.
 *
 * <p>Les hooks d'événements ({@code onAssigned}, {@code onGameplayPhaseStart}, etc.)
 * sont appelés par {@link PhaseManager} et
 * délégués aux {@link Ability} enregistrées. Chaque hook est isolé par un try/catch
 * individuel afin qu'une erreur dans une ability n'empêche pas les autres de s'exécuter.
 */
@SuppressWarnings("unused")
public interface Role {

    // -------------------------------------------------------------------------
    // Identité du rôle
    // -------------------------------------------------------------------------

    /**
     * Identifiant unique du rôle, en majuscules et sans espaces (ex : {@code "ETINCELLE"}).
     * Utilisé comme clé dans le {@link RoleRegistry}.
     */
    @NotNull String getId();

    /** Nom affiché aux joueurs, avec codes couleur Minecraft inclus. */
    @NotNull String getDisplayName();

    /** Description de l'objectif du rôle, affichée à l'assignation. */
    @NotNull String getDescription();

    /** Catégorie du rôle (Flamme, Bâton, Neutre). */
    @NotNull RoleType getType();

    /** Équipe du rôle, déterminant la condition de victoire partagée. */
    @NotNull RoleTeam getTeam();

    /** Icône affichée dans le GUI de composition. */
    @NotNull Material getGuiIcon();


    // -------------------------------------------------------------------------
    // Capacités
    // -------------------------------------------------------------------------

    /**
     * Retourne une vue non-modifiable de toutes les capacités de ce rôle,
     * y compris les capacités invisibles et "drunk".
     */
    @Unmodifiable @NotNull List<Ability> getAbilities();

    /**
     * Retourne la capacité correspondant à l'identifiant donné, ou {@code null}
     * si ce rôle ne possède pas cette capacité.
     *
     * @param abilityId identifiant de la capacité (ex: {@code AbilityIds.EMBRASEMENT})
     */
    @Nullable Ability getAbility(@NotNull String abilityId);

    /** {@code true} si ce rôle possède la capacité identifiée par {@code abilityId}. */
    boolean hasAbility(@NotNull String abilityId);

    /** Supprime une ability dont l'id est {@code abilityId} et retourne {@code true} si cela a fonctionné, {@code false} sinon. */
    boolean removeAbility(@NotNull String abilityId);

    // -------------------------------------------------------------------------
    // Équipe (mutable pour les mécaniques d'alignement dynamique)
    // -------------------------------------------------------------------------

    /**
     * Modifie l'équipe du rôle pendant la partie.
     * Utilisé par les mécaniques changeant l'allégeance d'un joueur.
     */
    void setTeam(@NotNull RoleTeam team);


    // -------------------------------------------------------------------------
    // Hooks d'événements de jeu
    // -------------------------------------------------------------------------

    /**
     * Appelé une fois, immédiatement après que ce rôle a été assigné au joueur.
     * C'est ici que doit être envoyé le message de présentation du rôle.
     */
    void onAssigned(@NotNull Player player, @NotNull NocturnePlayer nocturnePlayer);

    /** Appelé au début de chaque phase de Gameplay. */
    void onGameplayPhaseStart(@NotNull Player player, @NotNull NocturnePlayer nocturnePlayer, @NotNull PhaseContext phaseContext);

    /** Appelé à la fin de chaque phase de Gameplay, avant le traitement des éliminations. */
    void onGameplayPhaseEnd(@NotNull Player player, @NotNull NocturnePlayer nocturnePlayer, @NotNull PhaseContext phaseContext);

    /** Appelé au début de chaque phase de Vote. */
    void onVotePhaseStart(@NotNull Player player, @NotNull NocturnePlayer nocturnePlayer, @NotNull PhaseContext phaseContext);

    /** Appelé à la fin de chaque phase de Vote, avant le traitement du résultat. */
    void onVotePhaseEnd(@NotNull Player player, @NotNull NocturnePlayer nocturnePlayer, @NotNull PhaseContext phaseContext);

    /**
     * Appelé après le calcul du vote, pour chaque joueur encore en vie.
     *
     * @param votedPlayerId UUID du joueur éliminé par le vote, ou {@code null} si aucun
     * @param allVotes      liste complète des votes de cette manche
     */
    void afterVoteCalculation(
            @NotNull Player player,
            @NotNull NocturnePlayer nocturnePlayer,
            @Nullable UUID votedPlayerId,
            @NotNull List<VoteEntry> allVotes
    );

    /**
     * Appelé lorsque ce joueur interagit physiquement avec un autre joueur.
     *
     * @param caster           joueur qui interagit
     * @param nocturneCaster   données Nocturne du lanceur
     * @param receiver         joueur cible de l'interaction
     * @param nocturneReceiver données Nocturne de la cible
     * @param emptyHand        {@code true} si le lanceur avait la main principale vide
     */
    void onPlayerInteract(
            @NotNull Player caster,
            @NotNull NocturnePlayer nocturneCaster,
            @NotNull Player receiver,
            @NotNull NocturnePlayer nocturneReceiver,
            boolean emptyHand
    );

    /**
     * Appelé sur tous les joueurs vivants lorsque l'un d'eux utilise une capacité active.
     *
     * @param caster         joueur ayant utilisé la capacité
     * @param nocturneCaster données Nocturne du lanceur
     * @param context        contexte d'exécution de la capacité
     * @param result         résultat de l'exécution
     */
    void onActiveAbilityUsed(
            @NotNull Player caster,
            @NotNull NocturnePlayer nocturneCaster,
            @NotNull AbilityContext context,
            @NotNull AbilityResult result
    );

    /**
     * Appelé lorsque ce joueur est éliminé.
     *
     * @param cause raison de l'élimination
     */
    void onEliminated(
            @NotNull Player player,
            @NotNull NocturnePlayer nocturnePlayer,
            @NotNull EliminationCause cause
    );


    /**
     * Appelé sur tous les joueurs vivants lorsqu'un autre joueur est éliminé.
     *
     * @param eliminated         joueur éliminé
     * @param nocturneEliminated données Nocturne du joueur éliminé
     * @param cause              raison de l'élimination
     */
    void onOtherEliminated(
            @NotNull Player self,
            @NotNull Player eliminated,
            @NotNull NocturnePlayer nocturneEliminated,
            @NotNull EliminationCause cause
    );

}
