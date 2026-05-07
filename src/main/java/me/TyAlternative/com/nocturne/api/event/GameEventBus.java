package me.TyAlternative.com.nocturne.api.event;

import me.TyAlternative.com.nocturne.api.ability.Ability;
import me.TyAlternative.com.nocturne.player.NocturnePlayer;
import me.TyAlternative.com.nocturne.player.PlayerManager;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.logging.Logger;

/**
 * Bus central de dispatch des événements de jeu interceptables.
 *
 * <p>Lorsqu'un manager (EmbrasementManager, VoteManager, etc.) veut déclencher
 * une action interceptable, il appelle la méthode correspondante de ce bus.
 * Le bus propage l'événement à toutes les capacités de tous les joueurs vivants,
 * puis retourne l'événement (potentiellement modifié) au manager appelant.
 *
 * <h2>Flux d'un événement</h2>
 * <pre>
 * Manager appelle bus.fireEmbrasement(event)
 *   → pour chaque joueur vivant
 *     → pour chaque capacité du joueur
 *       → ability.onEmbrasementEvent(event, player, nocturnePlayer)
 * Manager lit event.isCancelled() et event.getTargetId()
 * </pre>
 *
 * <h2>Isolation des erreurs</h2>
 * Chaque appel à une capacité est isolé dans un try/catch.
 * Une erreur dans une capacité n'interrompt pas la propagation aux suivantes.
 *
 * <h2>Ordre de propagation</h2>
 * Les capacités reçoivent l'événement dans l'ordre :
 * joueurs vivants → capacités du joueur dans l'ordre d'enregistrement.
 * Cet ordre est intentionnellement non garanti entre joueurs différents.
 */
@SuppressWarnings("DataFlowIssue")
public final class GameEventBus {

    private final PlayerManager playerManager;
    private final Logger logger;

    /**
     * @param playerManager gestionnaire des joueurs vivants
     * @param logger        logger du plugin pour les erreurs
     */
    public GameEventBus(
            @NotNull PlayerManager playerManager,
            @NotNull Logger logger
    ) {
        this.playerManager = playerManager;
        this.logger        = logger;
    }

    // -------------------------------------------------------------------------
    // Dispatch — Embrasement
    // -------------------------------------------------------------------------

    /**
     * Propage un {@link EmbrasementEvent} à toutes les capacités de tous les joueurs vivants.
     *
     * @param event événement à propager
     * @return le même événement, potentiellement modifié
     */
    public @NotNull EmbrasementEvent fireEmbrasement(@NotNull EmbrasementEvent event) {
        dispatch(event, ((ability, player, nocturnePlayer) ->
                ability.onEmbrasementEvent(event, player, nocturnePlayer)));
        return event;
    }

    // -------------------------------------------------------------------------
    // Dispatch — Protection
    // -------------------------------------------------------------------------

    /**
     * Propage un {@link ProtectionEvent} à toutes les capacités de tous les joueurs vivants.
     *
     * @param event événement à propager
     * @return le même événement, potentiellement modifié
     */
    public @NotNull ProtectionEvent fireProtection(@NotNull ProtectionEvent event) {
        dispatch(event, (ability, player, np) ->
                ability.onProtectionEvent(event, player, np));
        return event;
    }

    // -------------------------------------------------------------------------
    // Dispatch — Élimination
    // -------------------------------------------------------------------------

    /**
     * Propage un {@link EliminationEvent} à toutes les capacités de tous les joueurs vivants.
     *
     * @param event événement à propager
     * @return le même événement, potentiellement modifié
     */
    public @NotNull EliminationEvent fireElimination(@NotNull EliminationEvent event) {
        dispatch(event, (ability, player, np) ->
                ability.onEliminationEvent(event, player, np));
        return event;
    }

    // -------------------------------------------------------------------------
    // Dispatch — Vote
    // -------------------------------------------------------------------------

    /**
     * Propage un {@link VoteCastEvent} à toutes les capacités de tous les joueurs vivants.
     *
     * @param event événement à propager
     * @return le même événement, potentiellement modifié
     */
    public @NotNull VoteCastEvent fireVoteCast(@NotNull VoteCastEvent event) {
        dispatch(event, (ability, player, np) ->
                ability.onVoteCastEvent(event, player, np));
        return event;
    }

    // -------------------------------------------------------------------------
    // Dispatch — AbilityCast
    // -------------------------------------------------------------------------

    /**
     * Propage un {@link AbilityCastEvent} à toutes les capacités de tous les joueurs vivants.
     *
     * @param event événement à propager
     * @return le même événement, potentiellement modifié
     */
    public @NotNull AbilityCastEvent fireAbilityCast(@NotNull AbilityCastEvent event) {
        dispatch(event, (ability, player, np) ->
                ability.onAbilityCastEvent(event, player, np));
        return event;
    }

    // -------------------------------------------------------------------------
    // Dispatch générique
    // -------------------------------------------------------------------------

    /**
     * Itère sur tous les joueurs vivants et propage l'action à chacune de leurs capacités.
     * Chaque appel est isolé dans un try/catch individuel.
     */
    private <E extends GameEvent<?>> void dispatch(@NotNull E event, @NotNull AbilityEventAction<E> action) {
        for (NocturnePlayer np : playerManager.getAlive()) {
            if (!np.hasRole()) continue;
            Player player = np.getPlayer();
            if (player == null) continue;

            // Snapshot pour éviter ConcurrentModificationException
            List<Ability> abilities = np.getRole().getAbilities();
            for (Ability ability : abilities) {
                try {
                    action.dispatch(ability, player, np);
                } catch (Exception e) {
                    logger.severe(
                            "[Nocturne] Erreur dans l'interception d'événement (ability=%s, player=%s) : %s"
                                    .formatted(ability.getId(), player.getName(), e.getMessage())
                    );
                }
            }

        }
    }



    // -------------------------------------------------------------------------
    // Interface fonctionnelle interne
    // -------------------------------------------------------------------------

    @FunctionalInterface
    private interface AbilityEventAction<E extends GameEvent<?>> {
        void dispatch(
                @NotNull Ability ability,
                @NotNull Player player,
                @NotNull NocturnePlayer nocturnePlayer
        ) throws Exception;
    }
}
