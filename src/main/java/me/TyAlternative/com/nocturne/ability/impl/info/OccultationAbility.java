package me.TyAlternative.com.nocturne.ability.impl.info;

import me.TyAlternative.com.nocturne.ability.AbilityIds;
import me.TyAlternative.com.nocturne.ability.AbstractAbility;
import me.TyAlternative.com.nocturne.ability.DrunkSupport;
import me.TyAlternative.com.nocturne.api.ability.*;
import me.TyAlternative.com.nocturne.mechanics.vote.VoteEntry;
import me.TyAlternative.com.nocturne.player.NocturnePlayer;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public final class OccultationAbility extends AbstractAbility {

    public OccultationAbility() {
        super(
                AbilityIds.OCCULTATION,
                "Occultation",
                "X - obtient le nombre de vote contre le joueur éliminé",
                Material.AIR,
                AbilityCategory.CAPACITY,
                AbilityUseType.PASSIVE,
                AbilityTrigger.AUTOMATIC
        );
    }

    @Override
    public DrunkSupport supportsDrunk() {
        return DrunkSupport.DEFAULT_LOGIC;
    }

    @Override
    protected @NotNull AbilityResult executeLogic(@NotNull Player player, @NotNull NocturnePlayer nocturnePlayer, @NotNull AbilityContext context) {
        return AbilityResult.silentSuccess();
    }

    @Override
    public boolean canExecute(@NotNull Player player, @NotNull NocturnePlayer nocturnePlayer, @NotNull AbilityContext context) {
        return false;
    }

    @Override
    public void afterVoteCalculation(@NotNull Player player, @NotNull NocturnePlayer nocturnePlayer, @Nullable UUID votedPlayerId, @NotNull List<VoteEntry> allVotes) {

        if (votedPlayerId == null) {
            player.sendMessage(Component.text(
                    "\n§6[Occultation] §fPersonne n'a été éliminé aux précédents votes. Cela vous suffit comme bonne nouvelle."
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
                    "\n§6[Occultation] §fUne force obscure vous cache les votants, méfiez-vous..."
            ));
            return;
        }
        int alive = game().getPlayerManager().getAlive().size();
        int numberOfVotes = votesAgainstEliminated.size();
        int min = 1;
        int max = Math.floorDiv(alive*3,4);

        if (isDrunk()) {
            int test = game().getRandom().nextInt(Math.ceilDiv(alive,2),alive + Math.ceilDiv(alive,2));
            numberOfVotes = Math.min(Math.max(test,min),max);
        }
        player.sendMessage(Component.text(
                "\n§6[Occultation] §fAu total, §e" + numberOfVotes + (numberOfVotes>1? "personnes§f ont voté":"personne§f a voté") + " contre l'éliminé."
        ));

    }
}
