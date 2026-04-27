package me.TyAlternative.com.nocturne.ability.impl.protection;

import me.TyAlternative.com.nocturne.ability.AbilityIds;
import me.TyAlternative.com.nocturne.ability.AbstractAbility;
import me.TyAlternative.com.nocturne.ability.DrunkSupport;
import me.TyAlternative.com.nocturne.ability.UsageLimit;
import me.TyAlternative.com.nocturne.api.ability.*;
import me.TyAlternative.com.nocturne.core.phase.PhaseContext;
import me.TyAlternative.com.nocturne.player.NocturnePlayer;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("DataFlowIssue")
public final class LipsAbility extends AbstractAbility {

    public LipsAbility() {
        super(AbilityIds.LIPS,
                "Bénédiction de Lips",
                "Vous pouvez marquer une personne pour lui interdire d'utiliser son §cEmbrasement§f cette manche (s'il en a un). Tous ceux qu'il tenterait§c d'embraser§f seront épargnés.",
                Material.AIR,
                AbilityCategory.CAPACITY,
                AbilityUseType.ACTIVE,
                AbilityTrigger.RIGHT_CLICK_PLAYER);

        setUsageLimit(UsageLimit.perRound(1));
    }

    @Override
    public boolean canExecute(@NotNull Player player, @NotNull NocturnePlayer nocturnePlayer, @NotNull AbilityContext context) {
        if (!context.hasTarget() || !context.isEmptyHand()) return false;
        assert context.getTarget() != null;
        NocturnePlayer targetData = game().getPlayerManager().get(context.getTarget());
        return targetData != null && targetData.isAlive();
    }

    @Override
    protected @NotNull AbilityResult executeLogic(@NotNull Player player, @NotNull NocturnePlayer nocturnePlayer, @NotNull AbilityContext context) {
        if (!isDrunk()) game().getCurrentRound().getEmbrasementManager().addNewLipsBanFlamme(context.getTarget().getUniqueId());
        return AbilityResult.success(
                Component.text("\n§6[Bénédiction de Lips]§f Vos actions vont permettre de peut être sauver des bâtons.")
        );

    }

    @Override
    public void onGameplayPhaseStart(@NotNull Player player, @NotNull NocturnePlayer nocturnePlayer, @NotNull PhaseContext phaseContext) {
        game().getAbilityManager().setCooldown(nocturnePlayer.getPlayerId(), getId(), game().getSettings().getLipsStartingCooldown());
    }

    @Override
    public DrunkSupport supportsDrunk() {
        return DrunkSupport.DEFAULT_LOGIC;
    }

    @Override
    public @NotNull Component getCannotExecuteMessage(
            @NotNull Player player,
            @NotNull NocturnePlayer nocturnePlayer
    ) {
        return Component.text("§cVous avez déjà utilisé la §6Bénédiction de Lips§f cette manche !");
    }
}
