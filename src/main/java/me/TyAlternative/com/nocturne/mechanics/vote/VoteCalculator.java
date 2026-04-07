package me.TyAlternative.com.nocturne.mechanics.vote;

import me.TyAlternative.com.nocturne.api.role.RoleType;
import me.TyAlternative.com.nocturne.player.NocturnePlayer;
import me.TyAlternative.com.nocturne.player.PlayerManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Calcule le résultat d'un vote à partir d'une liste de {@link VoteEntry}.
 *
 * <h2>Algorithme</h2>
 * <ol>
 *   <li>Additionner les poids de vote pour chaque joueur ciblé.</li>
 *   <li>Identifier le ou les joueur(s) avec le score le plus élevé.</li>
 *   <li>En cas d'égalité, éliminer en priorité un Bâton parmi les ex-equos.</li>
 *   <li>Si tous les ex-equos sont du même type, tirage aléatoire.</li>
 * </ol>
 *
 * <p>Cette logique préserve l'avantage des Flammes en cas d'égalité
 */

@SuppressWarnings("unused")
public class VoteCalculator {

    private static final Random RANDOM = new Random();

    /**
     * Calcule l'UUID du joueur à éliminer.
     *
     * @param votes         liste des votes collectés
     * @param playerManager gestionnaire des joueurs pour accéder aux types de rôle
     * @return UUID du joueur éliminé, ou {@code null} si aucun vote n'a été émis
     */
    @Nullable UUID calculateEliminated(
            @NotNull List<VoteEntry> votes,
            @NotNull PlayerManager playerManager
    ) {
        // Agréger les scores par joueur
        Map<UUID, Integer> scores =aggregateScores(votes);

        if (scores.isEmpty()) {
            return null;
        }

        // Trouver le score maximum
        int maxScore = scores.values().stream()
                .mapToInt(Integer::intValue)
                .max()
                .orElse(0);

        // Aucun vote positif émis
        if (maxScore <= 0) {
            return null;
        }

        // Collecter tous les joueurs à égalité avec le score maximum
        List<UUID> topCandidates = new ArrayList<>();
        for (Map.Entry<UUID, Integer> entry : scores.entrySet()) {
            if (entry.getValue() == maxScore) {
                topCandidates.add(entry.getKey());
            }
        }

        // Pas d'égalité : résultat direct
        if (topCandidates.size() == 1) {
            return topCandidates.getFirst();
        }

        // Égalité : priorité aux Bâtons parmi les ex-equos.
        return resolveTie(topCandidates, playerManager);
    }



    /**
     * Agrège les scores de vote par joueur ciblé.
     * Les abstentions (targetId null) sont ignorées.
     *
     * @param votes liste des votes
     * @return map UUID → score total
     */
    private @NotNull Map<UUID, Integer> aggregateScores(@NotNull List<VoteEntry> votes) {
        Map<UUID, Integer> scores = new HashMap<>();

        for (VoteEntry vote : votes) {
            if (!vote.hasTarget()) continue;
            scores.merge(vote.getTargetId(), vote.getWeight(), Integer::sum);
        }
        return scores;
    }

    /**
     * Résout une égalité entre plusieurs candidats.
     *
     * <p>Stratégie :
     * <ol>
     *   <li>Si des Flammes figurent parmi les ex-equos, tirage parmi les Bâtons uniquement.</li>
     *   <li>Sinon, tirage aléatoire parmi tous les ex-equos.</li>
     * </ol>
     *
     * @param candidates    joueurs à égalité de score
     * @param playerManager gestionnaire permettant d'accéder au type de rôle
     * @return UUID du joueur sélectionné
     */
    private @Nullable UUID resolveTie(List<UUID> candidates, @NotNull PlayerManager playerManager) {
        List<UUID> batons = new ArrayList<>();

        for (UUID candidateId : candidates) {
            NocturnePlayer nocturneCandidate = playerManager.get(candidateId);
            //noinspection DataFlowIssue
            if (nocturneCandidate != null
                    && nocturneCandidate.hasRole()
                    && nocturneCandidate.getRole().getType() == RoleType.BATON) batons.add(candidateId);
        }

        // Priorité aux Bâtons en cas d'égalité
        List<UUID> pool = batons.isEmpty() ? candidates : batons;
        return pool.get(RANDOM. nextInt(pool.size()));

    }


}
