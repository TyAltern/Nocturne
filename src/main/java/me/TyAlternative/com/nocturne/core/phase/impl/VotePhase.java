package me.TyAlternative.com.nocturne.core.phase.impl;

import me.TyAlternative.com.nocturne.Nocturne;
import me.TyAlternative.com.nocturne.api.phase.GamePhase;
import me.TyAlternative.com.nocturne.api.phase.PhaseType;
import me.TyAlternative.com.nocturne.core.NocturneGame;
import me.TyAlternative.com.nocturne.core.phase.PhaseContext;
import me.TyAlternative.com.nocturne.mechanics.vote.VoteEntry;
import me.TyAlternative.com.nocturne.player.NocturnePlayer;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Phase de Vote : les joueurs discutent et votent pour éliminer un joueur.
 *
 * <h2>Début de phase</h2>
 * <ul>
 *   <li>Téléportation aux tables de vote.</li>
 *   <li>Portée d'interaction élargie à 100 blocs (pour voter à distance).</li>
 *   <li>Hooks {@code onVotePhaseStart} déclenchés.</li>
 * </ul>
 *
 * <h2>Fin de phase</h2>
 * <ul>
 *   <li>Hooks {@code onVotePhaseEnd} déclenchés.</li>
 *   <li>Résultat du vote calculé.</li>
 *   <li>Hooks {@code afterVoteCalculation} déclenchés avec le résultat.</li>
 *   <li>Joueur éliminé traité.</li>
 *   <li>Portée d'interaction restaurée.</li>
 *   <li>Votes nettoyés.</li>
 * </ul>
 */
@SuppressWarnings({"unused", "DataFlowIssue", "SameParameterValue"})
public final class VotePhase implements GamePhase {

    /** Portée d'interaction élargie pendant le vote (en blocs). */
    private static final double VOTE_INTERACTION_RANGE = 100.0;

    /** Portées par défaut (Paper 1.21.1). */
    private static final double DEFAULT_BLOCK_RANGE  = 4.5;
    private static final double DEFAULT_ENTITY_RANGE = 3.0;

    @Override
    public void onStart(@NotNull PhaseContext context) {
        NocturneGame game = Nocturne.getInstance().getGame();

        // Téléportation aux tables
        teleportToVoteTables(game);

        // Notifier mes rôles et élargir la portée d'interaction
        for (NocturnePlayer nocturnePlayer : game.getPlayerManager().getAlive()) {
            Player player = nocturnePlayer.getPlayer();
            if (player == null || !nocturnePlayer.hasRole()) continue;

            setInteractionRange(player, VOTE_INTERACTION_RANGE);

            safeDispatch(() ->
                    nocturnePlayer.getRole().onVotePhaseStart(player, nocturnePlayer),
                    player.getName(), "onVotePhaseStart"
            );
        }

        game.broadcast("§dPhase de §lVote §r§dcommencée !");
    }

    @Override
    public void onEnd(@NotNull PhaseContext context) {
        NocturneGame game = Nocturne.getInstance().getGame();

        // Notifier onVotePhaseEnd
        for (NocturnePlayer nocturnePlayer : game.getPlayerManager().getAlive()) {
            Player player = nocturnePlayer.getPlayer();
            if (player == null || !nocturnePlayer.hasRole()) continue;

            safeDispatch(() ->
                            nocturnePlayer.getRole().onVotePhaseEnd(player, nocturnePlayer),
                    player.getName(), "onVotePhaseEnd"
            );
        }

        for (NocturnePlayer nocturnePlayer : game.getPlayerManager().getAlive()) {
            game.getAnonymityManager().resetCustomNametagForAll(nocturnePlayer);
        }

        // Calculer le résultat du vote
        UUID votedPlayerId = game.getVoteManager().calculateResult();
        List<VoteEntry> allVotes = game.getVoteManager().collectVotes();

        // Notifier afterVoteCalculation avec le résultat et restaurer la portée d'interaction
        for (NocturnePlayer nocturnePlayer : game.getPlayerManager().getAlive()) {
            Player player = nocturnePlayer.getPlayer();
            if (player == null || !nocturnePlayer.hasRole()) continue;

            setInteractionRange(player, DEFAULT_BLOCK_RANGE, DEFAULT_ENTITY_RANGE);

            game.getGlowingManager().removeGlowForAllTargets(nocturnePlayer);

            safeDispatch(() ->
                            nocturnePlayer.getRole().afterVoteCalculation(
                                    player, nocturnePlayer, votedPlayerId, allVotes
                            ),
                    player.getName(), "afterVoteCalculation"
            );
        }


            // Élimination du joueur voté
        game.getEliminationManager().eliminateVotedPlayer(votedPlayerId);
        
        // Nettoyage des votes
        game.getVoteManager().clearAll();
    }

    @Override
    public @NotNull PhaseType getType() {
        return PhaseType.VOTE;
    }

    @Override
    public long getDurationMs(@NotNull PhaseContext context) {
        int min = Nocturne.getInstance().getGame().getSettings().getMinVoteDurationSeconds();
        int max = Nocturne.getInstance().getGame().getSettings().getMaxVoteDurationSeconds();
        int durationSec = Nocturne.getInstance().getGame().getRandom().nextInt(min, max+1);
        return durationSec * 1000L;
    }


    // -------------------------------------------------------------------------
    // Utilitaires privés
    // -------------------------------------------------------------------------

    /**
     * Téléporte les joueurs vivants aux tables de vote configurées.
     * Si moins de tables que de joueurs, les joueurs sans table restent sur place.
     */
    private void teleportToVoteTables(@NotNull NocturneGame game) {
        List<Location> tables = game.getSettings().getVoteTableLocations();
        Collections.shuffle(tables);
        if (tables.isEmpty()) return;

        List<Player> players = game.getPlayerManager().getAlivePlayers();
        for (int i = 0; i < players.size(); i++) {
            Location dest = tables.get(Math.min(i, tables.size() - 1));
            players.get(i).teleport(dest);
        }
    }
    private void setInteractionRange(@NotNull Player player, double range) {
        setInteractionRange(player, range, range);
    }

    private void setInteractionRange(@NotNull Player player, double rangeBlock, double rangeEntity) {
        try {
            var blockAttr = player.getAttribute(Attribute.BLOCK_INTERACTION_RANGE);
            var entityAttr = player.getAttribute(Attribute.ENTITY_INTERACTION_RANGE);
            if (blockAttr != null) blockAttr.setBaseValue(rangeBlock);
            if (entityAttr != null) entityAttr.setBaseValue(rangeEntity);
        } catch (Exception e) {
            Nocturne.getInstance().getLogger().warning(
                    "Impossible de modifier la portée d'interaction de " + player.getName()
            );
        }
    }

    private void safeDispatch(@NotNull ThrowingRunnable action, String playerName, String hookName) {
        try {
            action.run();
        } catch (Exception e) {
            Nocturne.getInstance().getLogger().severe(
                    "[Nocturne] Erreur dans %s pour %s : %s".formatted(hookName, playerName, e.getMessage())
            );
        }
    }

    @FunctionalInterface
    private interface ThrowingRunnable {
        void run() throws Exception;
    }
}
