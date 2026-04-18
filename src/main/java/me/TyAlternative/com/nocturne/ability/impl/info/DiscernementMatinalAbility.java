package me.TyAlternative.com.nocturne.ability.impl.info;

import me.TyAlternative.com.nocturne.ability.AbilityIds;
import me.TyAlternative.com.nocturne.ability.AbstractAbility;

import me.TyAlternative.com.nocturne.api.ability.*;
import me.TyAlternative.com.nocturne.api.phase.PhaseType;
import me.TyAlternative.com.nocturne.api.role.RoleType;
import me.TyAlternative.com.nocturne.mechanics.vote.VoteEntry;
import me.TyAlternative.com.nocturne.player.NocturnePlayer;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

/**
 * Discernement Matinal — capacité passive de l'Aube.
 *
 * <p>À la fin de chaque vote, l'Aube reçoit une information binaire sur les votants
 * ayant voté <em>contre</em> le joueur éliminé :
 * <ul>
 *   <li>Si <strong>tous</strong> ces votants apparaissent comme des Bâtons (selon leur
 *       {@link RoleType}), l'Aube en est informée.</li>
 *   <li>Sinon, l'Aube sait qu'<strong>au moins une Flamme</strong> a voté contre le voté,
 *       sans connaître son identité.</li>
 * </ul>
 *
 * <p>Note : "apparaître comme Bâton" se base sur le {@code RoleType} affiché,
 * ce qui signifie qu'un Silex/Acier (type {@code FLAMME}) sera détecté comme
 * non-Bâton s'il a voté contre le joueur éliminé.
 */
@SuppressWarnings({"DataFlowIssue","unused"})
public final class DiscernementMatinalAbility extends AbstractAbility {
    /** ID public pour référence depuis d'autres classes (ex: AbilityIds). */
    public static final String ABILITY_ID = AbilityIds.DISCERNEMENT_MATINAL; // réutilise la constante existante

    public DiscernementMatinalAbility() {
        super(
                AbilityIds.DISCERNEMENT_MATINAL,
                "Discernement Matinal",
                "À la fin de chaque vote, vous saurez si au moins une Flamme a voté "
                        + "contre le joueur éliminé.",
                AbilityCategory.CAPACITY,
                AbilityUseType.PASSIVE,
                AbilityTrigger.AUTOMATIC
        );
        // S'active à la fin du vote, mais le hook afterVoteCalculation est déclenché
        // dans tous les contextes de phase → pas de restriction de phase ici
        setAllowedPhases(PhaseType.VOTE);
    }

    @Override
    public boolean canExecute(@NotNull Player player, @NotNull NocturnePlayer nocturnePlayer, @NotNull AbilityContext context) {
        return false; // Capacité passive, pas de déclenchement manuel
    }

    @Override
    protected @NotNull AbilityResult executeLogic(@NotNull Player player, @NotNull NocturnePlayer nocturnePlayer, @NotNull AbilityContext context) {
        return AbilityResult.silentSuccess();
    }

    @Override
    public boolean supportsDrunk() {
        return true;
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
        super.afterVoteCalculation(player, nocturnePlayer, votedPlayerId, allVotes);

        if (votedPlayerId == null) {
            player.sendMessage(Component.text(
                    "§7[Discernement Matinal] §8Aucun joueur n'a été éliminé au vote."
            ));
            return;
        }

        // Collecter les votants qui ont voté contre le joueur éliminé
        List<VoteEntry> votesAgainstEliminated = allVotes.stream()
                .filter(vote -> vote.hasTarget() && votedPlayerId.equals(vote.getTargetId()))
                .toList();

        if (votesAgainstEliminated.isEmpty()) {
            // Ne dois pas arriver (un joueur ne peut être éliminé sans votes),
            // mais on gère le cas pour la robustesse.
            player.sendMessage(Component.text(
                    "§7[Discernement Matinal] §8Impossible de déterminer les votants."
            ));
            return;
        }

        boolean allAreBatons;

        if (isDrunk()) {
            allAreBatons = game().getRandom().nextInt(0,3) == 0;
        } else {
            allAreBatons = votesAgainstEliminated.stream().allMatch(vote -> {
                NocturnePlayer nocturneVoter = game().getPlayerManager().get(vote.getVoterId());
                if (nocturneVoter == null || !nocturneVoter.hasRole()) return true;
                return nocturneVoter.getRole().getType() == RoleType.BATON;
            });
        }

        if (allAreBatons) {
            player.sendMessage(Component.text(
                    "§7[Discernement Matinal] §aTous les votants contre §e"
                            + getPlayerName(votedPlayerId)
                            + "§a apparaissent comme des Bâtons."
            ));
        } else {
            player.sendMessage(Component.text(
                    "§7[Discernement Matinal] §cAu moins une Flamme a voté contre §e"
                            + getPlayerName(votedPlayerId)
                            + "§c ! Leur identité et nombre vous est inconnue."
            ));
        }




    }

    // -------------------------------------------------------------------------
    // Utilitaire
    // -------------------------------------------------------------------------

    private @NotNull String getPlayerName(@NotNull UUID playerId) {
        NocturnePlayer np = game().getPlayerManager().get(playerId);
        Player p = np != null ? np.getPlayer() : null;
        return p != null ? p.getName() : "Inconnu";
    }

    @Override
    public @Nullable Component getCannotExecuteMessage(@NotNull Player p, @NotNull NocturnePlayer np) {
        return null;
    }
}
