package me.TyAlternative.com.nocturne.api.ability;

import me.TyAlternative.com.nocturne.ability.AbilityIds;
import me.TyAlternative.com.nocturne.ability.AbilityManager;
import me.TyAlternative.com.nocturne.api.role.Role;
import me.TyAlternative.com.nocturne.elimination.EliminationCause;
import me.TyAlternative.com.nocturne.mechanics.vote.VoteEntry;
import me.TyAlternative.com.nocturne.player.NocturnePlayer;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

/**
 * Contrat public de toute capacité pouvant être enregistrée sur un {@link Role}.
 *
 * <h2>Cycle d'exécution</h2>
 * <ol>
 *   <li>Le listener détecte un événement correspondant au {@link #getTrigger()} de la capacité.</li>
 *   <li>{@link #canExecute} est appelé : si {@code false}, un message d'erreur est éventuellement envoyé.</li>
 *   <li>{@link #execute} est appelé et retourne un {@link AbilityResult}.</li>
 *   <li>Si {@link AbilityResult#countsAsUse()}, l'utilisation est enregistrée dans l'
 *       {@link AbilityManager}.</li>
 * </ol>
 *
 * <h2>Mode "drunk"</h2>
 * Une capacité peut être enregistrée comme "drunk" via
 * {@link me.TyAlternative.com.nocturne.api.role.Role#getAbilities()}.
 * Dans ce cas, {@link #isDrunk()} retourne {@code true} et {@link #execute} doit
 * produire un comportement erroné ou aléatoire. Surcharger {@link #supportsDrunk()}
 * pour activer cette fonctionnalité.
 *
 * <h2>Capacités à ticks</h2>
 * Pour les capacités avec {@link AbilityTrigger#TICKS}, surcharger {@link #getTickInterval()}
 * pour définir la fréquence d'exécution en ticks (20 ticks = 1 seconde).
 */
public interface Ability {

    // -------------------------------------------------------------------------
    // Identité
    // -------------------------------------------------------------------------


    /**
     * Identifiant unique de la capacité, en majuscules (ex: {@code "EMBRASEMENT"}).
     * Doit correspondre à une constante dans {@link AbilityIds}.
     */
    @NotNull String getId();

    /** Nom affiché aux joueurs dans la présentation du rôle et l'action bar. */
    @NotNull String getDisplayName();

    /** Description de la capacité affichée à l'assignation du rôle. */
    @NotNull String getDescription();

    /** Catégorie visuelle (Capacité, Effet, Malédiction). */
    @NotNull AbilityCategory getCategory();

    /** Mode d'utilisation (Actif, Passif, Toggle, Vote). */
    @NotNull AbilityUseType getUseType();

    /** Action joueur déclenchant cette capacité. */
    @NotNull AbilityTrigger getTrigger();


    // -------------------------------------------------------------------------
    // Configuration
    // -------------------------------------------------------------------------

    /**
     * Intervalle d'exécution en ticks pour les capacités avec {@link AbilityTrigger#TICKS}.
     * Ignoré pour les autres triggers. Valeur par défaut : {@code -1} (désactivé).
     */
    default int getTickInterval() {
        return -1;
    }

    /**
     * {@code true} si cette capacité doit être masquée dans la présentation du rôle.
     * Utilisé pour les capacités internes ou les malédictions cachées.
     */
    boolean isHidden();


    /** {@code true} si cette capacité supporte le mode "drunk" (comportement erroné). */
    default boolean supportsDrunk() {
        return false;
    }

    /** {@code true} si cette capacité est actuellement en mode "drunk". */
    boolean isDrunk();

    /** Active ou désactive le mode "drunk" sur cette capacité. */
    void setDrunk(boolean drunk);


    // -------------------------------------------------------------------------
    // Logique d'exécution
    // -------------------------------------------------------------------------


    /**
     * Vérifie si la capacité peut être exécutée dans le contexte donné.
     *
     * <p>Cette méthode ne doit <strong>pas</strong> vérifier les limites d'utilisation
     * ni la phase courante : ces vérifications sont effectuées en amont par le
     * {@link AbilityManager}.
     *
     * @param player         joueur tentant d'utiliser la capacité
     * @param nocturnePlayer données Nocturne du joueur
     * @param context        contexte de l'exécution (cible, main vide, etc.)
     * @return {@code true} si l'exécution peut avoir lieu
     */
    boolean canExecute(
            @NotNull Player player,
            @NotNull NocturnePlayer nocturnePlayer,
            @NotNull AbilityContext context
    );

    /**
     * Exécute la capacité et retourne son résultat.
     *
     * <p>Si {@link #isDrunk()} est {@code true} et {@link #supportsDrunk()} est {@code true},
     * l'implémentation doit produire un comportement erroné ou aléatoire.
     *
     * @param player         joueur utilisant la capacité
     * @param nocturnePlayer données Nocturne du joueur
     * @param context        contexte de l'exécution
     * @return résultat de l'exécution, jamais {@code null}
     */
    @NotNull AbilityResult execute(
            @NotNull Player player,
            @NotNull NocturnePlayer nocturnePlayer,
            @NotNull AbilityContext context
    );


    /**
     * Message envoyé au joueur lorsque {@link #canExecute} retourne {@code false}.
     * Retourner {@code null} pour ne rien afficher.
     */
    @Nullable Component getCannotExecuteMessage(
            @NotNull Player player,
            @NotNull NocturnePlayer nocturnePlayer
    );

    // -------------------------------------------------------------------------
    // Hooks d'événements (tous optionnels via implémentation par défaut vide)
    // -------------------------------------------------------------------------

    /** Appelé une fois, immédiatement après l'assignation du rôle au joueur. */
    default void onAssigned(@NotNull Player player, @NotNull NocturnePlayer nocturnePlayer) {}

    /** Appelé au début de chaque phase de Gameplay. */
    default void onGameplayPhaseStart(@NotNull Player player, @NotNull NocturnePlayer nocturnePlayer) {}

    /** Appelé à la fin de chaque phase de Gameplay. */
    default void onGameplayPhaseEnd(@NotNull Player player, @NotNull NocturnePlayer nocturnePlayer) {}

    /** Appelé au début de chaque phase de Vote. */
    default void onVotePhaseStart(@NotNull Player player, @NotNull NocturnePlayer nocturnePlayer) {}

    /** Appelé à la fin de chaque phase de Vote. */
    default void onVotePhaseEnd(@NotNull Player player, @NotNull NocturnePlayer nocturnePlayer) {}

    /**
     * Appelé après le calcul du résultat du vote.
     *
     * @param votedPlayerId UUID du joueur éliminé, ou {@code null} si aucun
     * @param allVotes      liste complète des votes de la manche
     */
    default void afterVoteCalculation(
            @NotNull Player player,
            @NotNull NocturnePlayer nocturnePlayer,
            @Nullable UUID votedPlayerId,
            @NotNull List<VoteEntry> allVotes
    ) {}

    /** Appelé lorsque le joueur porteur de cette capacité est éliminé. */
    default void onEliminated(
            @NotNull Player player,
            @NotNull NocturnePlayer nocturnePlayer,
            @NotNull EliminationCause cause
    ) {}

    /** Appelé sur tous les joueurs vivants lorsqu'un autre joueur est éliminé. */
    default void onOtherEliminated(
            @NotNull Player self,
            @NotNull Player eliminated,
            @NotNull EliminationCause cause
    ) {}

    /**
     * Appelé lorsque le joueur porteur de cette capacité interagit avec un autre joueur.
     *
     * @param emptyHand {@code true} si la main principale du lanceur était vide
     */
    default void onPlayerInteract(
            @NotNull Player caster,
            @NotNull NocturnePlayer casterPlayer,
            @NotNull Player receiver,
            @NotNull NocturnePlayer receiverPlayer,
            boolean emptyHand
    ) {}

    /**
     * Appelé sur toutes les capacités de tous les joueurs vivants lorsqu'une
     * capacité active est utilisée par n'importe quel joueur.
     */
    default void onActiveAbilityUsed(
            @NotNull Player caster,
            @NotNull NocturnePlayer casterPlayer,
            @NotNull AbilityContext context,
            @NotNull AbilityResult result
    ) {}
}
