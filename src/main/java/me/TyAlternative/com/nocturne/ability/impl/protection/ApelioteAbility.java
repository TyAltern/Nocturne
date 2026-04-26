package me.TyAlternative.com.nocturne.ability.impl.protection;

import me.TyAlternative.com.nocturne.ability.AbilityIds;
import me.TyAlternative.com.nocturne.ability.AbstractAbility;
import me.TyAlternative.com.nocturne.ability.DrunkSupport;
import me.TyAlternative.com.nocturne.ability.UsageLimit;
import me.TyAlternative.com.nocturne.api.ability.*;
import me.TyAlternative.com.nocturne.core.phase.PhaseContext;
import me.TyAlternative.com.nocturne.mechanics.protection.ProtectionType;
import me.TyAlternative.com.nocturne.player.NocturnePlayer;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Clic droit à main vide sur un joueur vivant : ce joueur est protégé contre
 * l'Embrasement des Flammes pour la phase de Gameplay en cours.
 * Utilisable une seule fois par manche.
 *
 * <p>En mode drunk : affiche le message, mais n'applique aucune protection réelle.
 */
public final class ApelioteAbility extends AbstractAbility {

    public ApelioteAbility() {
        super(
                AbilityIds.APELIOTE,
                "Bénédiction d'Apéliote",
                "Protégez un joueur contre l'Embrasement des Flammes jusqu'à la fin de la phase.",
                Material.AIR,
                AbilityCategory.CAPACITY,
                AbilityUseType.ACTIVE,
                AbilityTrigger.RIGHT_CLICK_PLAYER
        );
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
    public DrunkSupport supportsDrunk() {
        return DrunkSupport.CUSTOM_LOGIC;
    }

    @SuppressWarnings("DataFlowIssue")
    @Override
    protected @NotNull AbilityResult executeLogic(@NotNull Player player, @NotNull NocturnePlayer nocturnePlayer, @NotNull AbilityContext context) {
        game().getCurrentRound().getProtectionManager().protect(context.getTarget().getUniqueId(), ProtectionType.APELIOTE);
        return AbilityResult.success(
                Component.text("§bVotre §6Apéliote§b a protégé un joueur. Celui-ci ne craindra plus les §cFlammes§b pour un tour.")
        );
    }

    @Override
    protected @NotNull AbilityResult executeDrunkLogic(@NotNull Player player, @NotNull NocturnePlayer nocturnePlayer, @NotNull AbilityContext context) {
        return AbilityResult.success(
                Component.text("§bVotre §6Apéliote§b a protégé un joueur. Celui-ci ne craindra plus les §cFlammes§b pour un tour.")
        );
    }

    @Override
    public void onGameplayPhaseStart(@NotNull Player player, @NotNull NocturnePlayer nocturnePlayer, @NotNull PhaseContext phaseContext) {
        game().getAbilityManager().setCooldown(nocturnePlayer.getPlayerId(), getId(), game().getSettings().getApelioteStartingCooldown());

    }

    @Override
    public @NotNull Component getCannotExecuteMessage(
            @NotNull Player player,
            @NotNull NocturnePlayer nocturnePlayer
    ) {
        return Component.text("§cVous avez déjà utilisé votre Apéliote cette manche !");
    }
}
