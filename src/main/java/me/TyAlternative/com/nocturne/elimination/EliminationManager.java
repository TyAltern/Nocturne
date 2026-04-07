package me.TyAlternative.com.nocturne.elimination;


import me.TyAlternative.com.nocturne.core.round.RoundContext;
import me.TyAlternative.com.nocturne.mechanics.disparition.DisparitionCause;
import me.TyAlternative.com.nocturne.mechanics.embrasement.EmbrasementCause;
import me.TyAlternative.com.nocturne.player.NocturnePlayer;
import me.TyAlternative.com.nocturne.player.PlayerManager;
import me.TyAlternative.com.nocturne.player.PlayerState;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * Gère le traitement des éliminations sous toutes leurs formes.
 *
 * <p>Trois méthodes de traitement correspondent aux trois sources d'élimination :
 * <ul>
 *   <li>{@link #processEmbrasements(RoundContext)} — fin de phase Gameplay, embrasements</li>
 *   <li>{@link #processDisparitions(RoundContext)} — fin de phase Gameplay, disparitions</li>
 *   <li>{@link #eliminateVotedPlayer(UUID)} — fin de phase Vote</li>
 * </ul>
 *
 * <p>Tous convergent vers {@link #eliminate(UUID, EliminationCause)},
 * point unique de traitement qui :
 * <ol>
 *   <li>Change l'état du joueur en {@link PlayerState#DEAD}.</li>
 *   <li>Passe le joueur en mode spectateur.</li>
 *   <li>Déclenche les hooks {@code onEliminated} et {@code onOtherEliminated}.</li>
 *   <li>Diffuse le message d'élimination.</li>
 * </ol>
 */
@SuppressWarnings("unused")
public final class EliminationManager {

    private final PlayerManager playerManager;
    private final Logger logger;

    /**
     * @param playerManager gestionnaire des joueurs
     * @param logger        logger du plugin pour les erreurs
     */
    public EliminationManager(
            @NotNull PlayerManager playerManager,
            @NotNull Logger logger) {
        this.playerManager = playerManager;
        this.logger = logger;
    }

    // -------------------------------------------------------------------------
    // Traitement de fin de phase Gameplay
    // -------------------------------------------------------------------------

    /**
     * Traite tous les embrasements enregistrés dans le {@link RoundContext}.
     *
     * <p>L'ordre de traitement : les disparitions avant les embrasements, afin
     * que les mécaniques qui dépendent du nombre de joueurs vivants (ex: Solitude
     * Mortelle) soient résolues avant les Embrasements.
     *
     * @param roundContext contexte de la manche contenant les embrasements
     */
    public void processEmbrasements(@NotNull RoundContext roundContext) {
        Map<UUID, EmbrasementCause> embrased = roundContext.getEmbrasementManager().getAll();

        // Snapshot pout éviter les modifications concurrentes pendant l'itération
        List<Map.Entry<UUID, EmbrasementCause>> snapshot = new ArrayList<>(embrased.entrySet());

        for (Map.Entry<UUID, EmbrasementCause> entry : snapshot) {
            UUID playerId = entry.getKey();
            EliminationCause cause = toEliminationCause(entry.getValue());

            if (roundContext.getProtectionManager().isProtected(playerId)) continue;

            eliminate(playerId, cause);
        }
    }

    /**
     * Traite toutes les disparitions enregistrées dans le {@link RoundContext}.
     *
     * @param roundContext contexte de la manche contenant les disparitions
     */
    public void processDisparitions(@NotNull RoundContext roundContext) {
        Map<UUID, DisparitionCause> disappeared = roundContext.getDisparitionManager().getAll();

        List<Map.Entry<UUID, DisparitionCause>> snapshot = new ArrayList<>(disappeared.entrySet());

        for (Map.Entry<UUID, DisparitionCause> entry: snapshot) {
            UUID playerId = entry.getKey();
            EliminationCause cause = toEliminationCause(entry.getValue());
            eliminate(playerId, cause);
        }
    }

    // -------------------------------------------------------------------------
    // Traitement de fin de phase Vote
    // -------------------------------------------------------------------------

    /**
     * Élimine le joueur désigné par le vote.
     *
     * @param votedPlayerId UUID du joueur éliminé, ou {@code null} si aucun vote n'a abouti
     */
    public void eliminateVotedPlayer(@Nullable UUID votedPlayerId) {
        if (votedPlayerId == null) return;
        eliminate(votedPlayerId, EliminationCause.VOTE);
    }

    // -------------------------------------------------------------------------
    // Point unique d'élimination
    // -------------------------------------------------------------------------

    /**
     * Élimine le joueur identifié par {@code playerId} pour la cause donnée.
     *
     * <p>Sans effet si le joueur est introuvable ou déjà éliminé.
     * Chaque hook est isolé dans un try/catch pour garantir que la chaîne
     * complète s'exécute même si une capacité lève une exception.
     *
     * @param playerId UUID du joueur à éliminer
     * @param cause    raison de l'élimination
     */
    public void eliminate(@NotNull UUID playerId, @NotNull EliminationCause cause) {
        NocturnePlayer nocturnePlayer = playerManager.get(playerId);
        if (nocturnePlayer == null || !nocturnePlayer.isAlive()) return;

        Player player = nocturnePlayer.getPlayer();
        if (player == null) return;

        // Changement d'état
        nocturnePlayer.setState(PlayerState.DEAD);
        player.setGameMode(GameMode.SPECTATOR);

        // Hook onEliminated sur le rôle du joueur éliminé
        if (nocturnePlayer.hasRole()) {
            try {
                //noinspection DataFlowIssue
                nocturnePlayer.getRole().onEliminated(player, nocturnePlayer, cause);
            } catch (Exception e) {
                logger.severe(
                        "[Nocturne] Erreur dans onEliminated pour %s : %s"
                                .formatted(player.getName(), e.getMessage())
                );
            }
        }

        // Hook onOtherEliminated sur tous les joueurs vivants
        for (NocturnePlayer other : playerManager.getAlive()) {
            if (!other.hasRole()) continue;
            Player otherPlayer = other.getPlayer();
            if (otherPlayer == null) continue;

            try {
                //noinspection DataFlowIssue
                other.getRole().onOtherEliminated(otherPlayer,player , nocturnePlayer, cause);
            } catch (Exception e) {
                logger.severe(
                        "[Nocturne] Erreur dans onOtherEliminated pour %s : %s"
                                .formatted(otherPlayer.getName(), e.getMessage())
                );
            }
        }

        logger.info("[Nocturne] %s éliminé (%s)".formatted(player.getName(), cause.getDisplayName()));
    }


    // -------------------------------------------------------------------------
    // Conversion des causes
    // -------------------------------------------------------------------------

    private @NotNull EliminationCause toEliminationCause(@NotNull EmbrasementCause cause) {
        return switch (cause) {
            case ETINCELLE    -> EliminationCause.EMBRASEMENT_ETINCELLE;
            case TORCHE       -> EliminationCause.EMBRASEMENT_TORCHE;
            case PRISE_DE_FEU -> EliminationCause.EMBRASEMENT_PRISE_DE_FEU;
            case OTHER        -> EliminationCause.EMBRASEMENT;
        };
    }

    private @NotNull EliminationCause toEliminationCause(@NotNull DisparitionCause cause) {
        return switch (cause) {
            case SOLITUDE    -> EliminationCause.DISPARITION_SOLITUDE;
            case OTHER        -> EliminationCause.DISPARITION;
        };
    }
}
