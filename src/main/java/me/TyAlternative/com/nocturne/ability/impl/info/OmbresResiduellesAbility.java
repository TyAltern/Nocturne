package me.TyAlternative.com.nocturne.ability.impl.info;

import me.TyAlternative.com.nocturne.ability.AbilityIds;
import me.TyAlternative.com.nocturne.ability.AbstractAbility;

import me.TyAlternative.com.nocturne.api.ability.*;
import me.TyAlternative.com.nocturne.api.phase.PhaseType;

import me.TyAlternative.com.nocturne.mechanics.vote.VoteEntry;
import me.TyAlternative.com.nocturne.player.NocturnePlayer;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Ombres Résiduelles — capacité passive du Crépuscule.
 *
 * <p>À la fin de chaque vote, le Crépuscule apprend la moitié des noms
 * (arrondi inférieur) des joueurs n'ayant <em>pas</em> voté contre le
 * joueur éliminé. Les noms révélés sont choisis aléatoirement parmi
 * ces non-votants.
 *
 * <h2>Exemple</h2>
 * Si 6 joueurs n'ont pas voté contre l'éliminé, le Crépuscule en connaît 3 (6/2).
 *
 * <p>Un joueur qui s'abstient (vote sans cible) est considéré comme n'ayant
 * pas voté contre le joueur éliminé.
 */
public final class OmbresResiduellesAbility extends AbstractAbility {
    /** ID public pour référence depuis d'autres classes (ex: AbilityIds). */
    public static final String ABILITY_ID = AbilityIds.OMBRES_RESIDUELLES; // réutilise la constante existante

    private final Random random = game().getRandom();

    public OmbresResiduellesAbility() {
        super(
                AbilityIds.OMBRES_RESIDUELLES,
                "Ombres Résiduelles",
                "À la fin de chaque vote, vous connaissez la moitié des joueurs "
                        + "n'ayant pas voté contre l'éliminé.",
                AbilityCategory.CAPACITY,
                AbilityUseType.PASSIVE,
                AbilityTrigger.AUTOMATIC
        );
        setAllowedPhases(PhaseType.VOTE);
    }

    @Override
    public boolean canExecute(@NotNull Player player, @NotNull NocturnePlayer nocturnePlayer, @NotNull AbilityContext context) {
        return false;
    }

    @Override
    protected @NotNull AbilityResult executeLogic(@NotNull Player player, @NotNull NocturnePlayer nocturnePlayer, @NotNull AbilityContext context) {
        return AbilityResult.silentSuccess();
    }


    // -------------------------------------------------------------------------
    // Hook principal : résultat du vote
    // -------------------------------------------------------------------------


    @Override
    public void afterVoteCalculation(
            @NotNull Player player,
            @NotNull NocturnePlayer nocturnePlayer,
            @Nullable UUID votedPlayerId,
            @NotNull List<VoteEntry> allVotes
    ) {
        if (votedPlayerId == null) {
            player.sendMessage(Component.text(
                    "§7[Ombres Résiduelles] §8Aucun joueur éliminé — aucune information."
            ));
            return;
        }

        // Construire la liste des UUID ayant voté CONTRE l'éliminé
        Set<UUID> votedAgainst = new HashSet<>();
        for (VoteEntry vote : allVotes) {
            if (vote.hasTarget() && votedPlayerId.equals(vote.getTargetId())) {
                votedAgainst.add(vote.getVoterId());
            }
        }

        // Joueurs n'ayant PAS voté contre l'éliminé (excluant l'éliminé lui-même et le Crépuscule)
        List<String> nonVoters = new ArrayList<>();
        for (VoteEntry vote : allVotes) {
            UUID voterId = vote.getVoterId();
            if (voterId.equals(nocturnePlayer.getPlayerId()))  continue; // s'exclure soi-même
            if (voterId.equals(votedPlayerId))                 continue; // exclure l'éliminé
            if (!votedAgainst.contains(voterId)) {
                String name = resolvePlayerName(voterId);
                if (name != null) nonVoters.add(name);
            }
        }

        // Nombre à révéler : moitié arrondie inférieure
        int revealCount = nonVoters.size() / 2;

        if (revealCount == 0) {
            player.sendMessage(Component.text(
                    "§7[Ombres Résiduelles] §8Pas assez de non-votants pour révéler des noms."
            ));
            return;
        }

        // Sélection aléatoire
        Collections.shuffle(nonVoters, random);
        List<String> revealed = nonVoters.subList(0, revealCount);

        player.sendMessage(Component.text(
                "§7[Ombres Résiduelles] §7Ces joueurs n'ont §eNOT§7 voté contre §e"
                        + resolvePlayerName(votedPlayerId) + "§7 : §f"
                        + String.join("§7, §f", revealed)
        ));

    }


    // -------------------------------------------------------------------------
    // Utilitaire
    // -------------------------------------------------------------------------

    private @Nullable String resolvePlayerName(@NotNull UUID playerId) {
        NocturnePlayer np = game().getPlayerManager().get(playerId);
        Player p = np != null ? np.getPlayer() : null;
        return p != null ? p.getName() : null;
    }

    @Override
    public @Nullable Component getCannotExecuteMessage(@NotNull Player p, @NotNull NocturnePlayer np) {
        return null;
    }
}
