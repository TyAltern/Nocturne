package me.TyAlternative.com.nocturne.ability.impl.info;

import me.TyAlternative.com.nocturne.ability.AbilityIds;
import me.TyAlternative.com.nocturne.ability.AbstractAbility;

import me.TyAlternative.com.nocturne.ability.DrunkSupport;
import me.TyAlternative.com.nocturne.api.ability.*;
import me.TyAlternative.com.nocturne.api.phase.PhaseType;
import me.TyAlternative.com.nocturne.api.role.RoleType;
import me.TyAlternative.com.nocturne.mechanics.vote.VoteEntry;
import me.TyAlternative.com.nocturne.player.NocturnePlayer;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;


@SuppressWarnings({"DataFlowIssue","unused"})
public final class ScintillementAbility extends AbstractAbility {

    public ScintillementAbility() {
        super(
                AbilityIds.SCINTILLEMENT,
                "Scintillement",
                "À la fin de chaque vote, vous saurez si au moins une §cFlamme§f a voté "
                        + "contre l'éliminé.",
                Material.AIR,
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
    public DrunkSupport supportsDrunk() {
        return DrunkSupport.DEFAULT_LOGIC;
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
                    "\n§6[Scintillement] §fPersonne n'a été éliminé aux précédents votes. Cela vous suffit comme bonne nouvelle."
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
                    "\n§6[Scintillement] §fUne force obscure vous cache les votants, méfiez-vous..."
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
                    "\n§6[Scintillement] §fTous les votants contre §e"
                            + getPlayerName(votedPlayerId)
                            + "§f apparaissent comme des §eBâtons§f."
            ));
        } else {
            player.sendMessage(Component.text(
                    "\n§6[Scintillement] §fAu moins une §cFlamme§f a voté contre §e"
                            + getPlayerName(votedPlayerId)
                            + "§f ! Leur identité et nombre vous est inconnue."
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
