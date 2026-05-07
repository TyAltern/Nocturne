package me.TyAlternative.com.nocturne.mechanics.vote;

import me.TyAlternative.com.nocturne.Nocturne;
import me.TyAlternative.com.nocturne.api.event.GameEventBus;
import me.TyAlternative.com.nocturne.api.event.VoteCastEvent;
import me.TyAlternative.com.nocturne.core.phase.impl.VotePhase;
import me.TyAlternative.com.nocturne.mechanics.particle.ParticleData;
import me.TyAlternative.com.nocturne.player.NocturnePlayer;
import me.TyAlternative.com.nocturne.player.PlayerManager;
import me.TyAlternative.com.nocturne.ui.MessageManager;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Gestionnaire des votes pour la phase de Vote en cours.
 *
 * <p>Centralise l'enregistrement et le retrait des votes.
 * Le calcul du résultat est délégué au {@link VoteCalculator}.
 *
 * <p>Une seule instance partagée sur toute la partie (contrairement aux managers
 * de mécaniques de manche), car les votes sont nettoyés explicitement en fin de
 * chaque phase de Vote via {@link #clearAll()}.
 */
@SuppressWarnings({"unused", "DataFlowIssue", "StringTemplateMigration"})
public final class VoteManager {
    private final PlayerManager playerManager;
    private final VoteCalculator calculator;
    private final MessageManager messageManager;
    private final GameEventBus eventBus;

    /**
     * @param playerManager gestionnaire des joueurs, pour accéder aux données de vote
     */
    public VoteManager(@NotNull PlayerManager playerManager, @NotNull GameEventBus eventBus, @NotNull MessageManager messageManager) {
        this.playerManager = playerManager;
        this.eventBus       = eventBus;
        this.messageManager = messageManager;
        this.calculator = new VoteCalculator();
    }

    // -------------------------------------------------------------------------
    // Enregistrement des votes
    // -------------------------------------------------------------------------

    /**
     * Tente d'enregistrer le vote du joueur {@code voterId} vers {@code targetId},
     * en passant d'abord par le {@link GameEventBus}.
     *
     * <p>Ordre des opérations :
     * <ol>
     *   <li>Vérifications préalables (joueur peut voter, cible valide).</li>
     *   <li>Création et dispatch du {@link VoteCastEvent}.</li>
     *   <li>Si annulé → message éventuel au votant, retour.</li>
     *   <li>Enregistrement avec la cible et le poids finals.</li>
     * </ol>
     *
     * @param voterId  UUID du votant
     * @param targetId UUID de la cible initiale
     */
    public void castVote( @NotNull UUID voterId, @NotNull UUID targetId) {
        NocturnePlayer nocturneVoter = playerManager.get(voterId);
        if (nocturneVoter == null || !nocturneVoter.canVote()) return;

        Player voterPlayer = nocturneVoter.getPlayer();
        if (voterPlayer == null) return;

        // Toggle : re-cliquer sur sa cible annule le vote
        if (targetId.equals(nocturneVoter.getVotedPlayerId())) {
            removeVote(voterId, true);
            return;
        }
        if (nocturneVoter.getVotedPlayerId() != null) {
            removeVote(voterId, false);
        }
        NocturnePlayer nocturneVoted = playerManager.get(targetId);
        if (nocturneVoted == null) return;
        Player votedPlayer = nocturneVoted.getPlayer();
        if (votedPlayer == null) return;

        VoteCastEvent event = new VoteCastEvent(nocturneVoter, targetId, nocturneVoter.getVoteWeight());
        eventBus.fireVoteCast(event);

        if (event.isCancelled()) {
            String reason = event.getCancelReason();
            if (reason != null) {
                voterPlayer.sendMessage(messageManager.buildBroadcast(reason));
            }
            return;
        }
        UUID finalTargetId = event.getTargetId();
        int finalWeight    = event.getWeight();

        // La cible peut avoir été redirigée — recalculer nocturneVoted
        NocturnePlayer finalVoted = playerManager.get(finalTargetId);
        if (finalVoted == null) return;
        Player finalVotedPlayer = finalVoted.getPlayer();
        if (finalVotedPlayer == null) return;


//        Non votable invisible
        if (!finalVoted.canBeVoted() && finalVoted.isHiddenInVote()) {
            return;
        }
//        Non votable visible mais notifier
        else if (!finalVoted.canBeVoted() && !finalVoted.isVoteImmunityHidden()) {
            nocturneVoter.getPlayer().sendMessage(messageManager.buildBroadcast("§7Vous ne pouvez pas voter contre §e" + finalVotedPlayer.getName() + " §7!"));
        }
//        Non votable visible, mais non notifier +
//        Votable et visible
        else {
            nocturneVoter.getPlayer().sendMessage(messageManager.buildBroadcast("§7Vous avez §dvoté §7contre §e" + finalVotedPlayer.getName() + " §7!"));
            Nocturne.getInstance().getGame().getAnonymityManager().addCustomPrefixSuffixToNametag(nocturneVoter, finalVoted, "§6>> ", " <<");
            Nocturne.getInstance().getGame().getTickingParticleTimer().addTickingParticles(
                    nocturneVoter,
                    finalVoted,
                    new ParticleData.Builder()
                            .particle(Particle.WAX_ON)
                            .particle_count(10)
                            .height_offset(0.75)
                            .spread(0.4)
                            .spawnTickInterval(10)
                            .build()
            );

            nocturneVoter.voteFor(finalTargetId);
            nocturneVoter.setCurrentRoundVoteWeight(finalWeight);
        }

    }

    /**
     * Retire le vote du joueur identifié par {@code voterId}.
     * Sans effet si le joueur n'a pas voté.
     *
     * @param voterId UUID du joueur dont le vote est retiré
     */
    public void removeVote(@NotNull UUID voterId, boolean sendMessage) {
        NocturnePlayer nocturneVoter = playerManager.get(voterId);
        if (nocturneVoter == null) return;

        if (nocturneVoter.hasVoted()) {
            NocturnePlayer nocturneVoted = playerManager.get(nocturneVoter.getVotedPlayerId());
            if (nocturneVoted != null) {
                Player votedPlayer  = nocturneVoted.getPlayer();
                Player voterPlayer  = nocturneVoter.getPlayer();
                if (votedPlayer != null && voterPlayer != null) {
                    if (sendMessage)
                        voterPlayer.sendMessage(messageManager.buildBroadcast(
                                "§7Vous avez retiré votre §dvote §7contre §e"
                                        + votedPlayer.getName() + " §7!"
                        ));
                    Nocturne.getInstance().getGame().getAnonymityManager()
                            .resetCustomNametag(nocturneVoter, nocturneVoted);
                    Nocturne.getInstance().getGame().getTickingParticleTimer()
                            .removeTickingParticles(nocturneVoter, nocturneVoted);
                }
            }
        }

        nocturneVoter.clearVote();
        nocturneVoter.resetCurrentRoundVoteWeight();
    }


    // -------------------------------------------------------------------------
    // Calcul du résultat
    // -------------------------------------------------------------------------

    /**
     * Collecte tous les votes des joueurs vivants et calcule l'UUID du joueur éliminé.
     *
     * @return UUID du joueur à éliminer, ou {@code null} si aucun vote exploitable
     */
    public @Nullable UUID calculateResult() {
        List<VoteEntry> votes = collectVotes();
       return calculator.calculateEliminated(votes, playerManager);
    }

    /**
     * Collecte les votes de tous les joueurs vivants sous forme de {@link VoteEntry}.
     * Inclut les abstentions (vote sans cible) pour les mécaniques les utilisant.
     *
     * @return liste complète des votes de la phase
     */
    public @NotNull List<VoteEntry> collectVotes() {
        List<VoteEntry> votes = new ArrayList<>();

        for (NocturnePlayer nocturneVoter : playerManager.getAlive()) {
            if (!nocturneVoter.canVote()) continue;
            votes.add(new VoteEntry(
                    nocturneVoter.getPlayerId(),
                    nocturneVoter.getVotedPlayerId(),
                    nocturneVoter.getVoteWeight()
            ));
        }

        return votes;
    }

    // -------------------------------------------------------------------------
    // Nettoyage
    // -------------------------------------------------------------------------

    /**
     * Retire tous les votes enregistrés.
     * Appelé en fin de phase de Vote par {@link VotePhase}.
     */
    public void clearAll() {
        for (NocturnePlayer nocturnePlayer : playerManager.getAlive()) {
            nocturnePlayer.clearVote();
        }
    }

}
