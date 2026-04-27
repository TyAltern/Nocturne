package me.TyAlternative.com.nocturne.ability.impl.info;

import me.TyAlternative.com.nocturne.ability.AbilityIds;
import me.TyAlternative.com.nocturne.ability.AbstractAbility;
import me.TyAlternative.com.nocturne.ability.DrunkSupport;
import me.TyAlternative.com.nocturne.api.ability.*;
import me.TyAlternative.com.nocturne.core.phase.PhaseContext;
import me.TyAlternative.com.nocturne.core.round.RoundContext;
import me.TyAlternative.com.nocturne.player.NocturnePlayer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class IrradiationAbility extends AbstractAbility {

    private RoundContext roundContext;
    private int embrasementCount = 0;
    private int eliminateCount = 0;
    private int startingAliveCount = 0;
    private int endingAliveCount = 0;

    public IrradiationAbility() {
        super(
                AbilityIds.IRRADIATION,
                "Irradiation",
                "X - reçoit le nombre exact de joueurs embrasés cette manche (même s'ils ont été protégé)",
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
    public void onGameplayPhaseStart(@NotNull Player player, @NotNull NocturnePlayer nocturnePlayer, @NotNull PhaseContext phaseContext) {
        endingAliveCount = game().getPlayerManager().getAliveCount();
        eliminateCount = startingAliveCount - endingAliveCount;

        if (roundContext != null) {
            if (isDrunk()) embrasementCount = getDrunkEmbrasementCount();

            if (embrasementCount == 0) player.sendMessage("\n§6[Irradiation]§f Au total, lors de la manche précédente,§e personne§f n'a été §cEmbrasée §f par des §cFlammes§f.");

            else player.sendMessage("\n§6[Irradiation]§f Au total, lors de la manche précédente, §e" + embrasementCount + (embrasementCount>1? " personnes§f ont été §cEmbrasées": " personne§f a été §cEmbrasée") + "§f par des §cFlammes§f.");
        }

        startingAliveCount = game().getPlayerManager().getAliveCount();
        roundContext = phaseContext.getRoundContext();
    }

    @Override
    public void onVotePhaseStart(@NotNull Player player, @NotNull NocturnePlayer nocturnePlayer, @NotNull PhaseContext phaseContext) {
        embrasementCount = roundContext.getEmbrasementManager().count();
    }


    private int getDrunkEmbrasementCount() {
        int rng = game().getRandom().nextInt(0,100);
        if (rng < 10) return eliminateCount-1;
        else if (rng < 45) return eliminateCount;
        else if (rng < 80) return eliminateCount+1;
        else if (rng < 100) return eliminateCount+2;

        return eliminateCount;
    }
}
