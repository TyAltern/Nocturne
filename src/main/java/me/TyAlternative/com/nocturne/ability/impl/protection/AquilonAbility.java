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
import org.jetbrains.annotations.Nullable;


/**
 * Aquilon — capacité active de la Bourrasque.
 *
 * <p>Une seule fois par partie, protège tous les joueurs vivants contre
 * l'Embrasement des Flammes jusqu'à la fin de la phase de Gameplay en cours.
 *
 * <p>En mode drunk : affiche le message de succès, mais n'applique aucune protection.
 */
public final class AquilonAbility extends AbstractAbility {

    public AquilonAbility() {
        super(
                AbilityIds.AQUILON,
                "Aquilon",
                "Une fois par partie, protégez tous les joueurs contre l'Embrasement des Flammes.",
                Material.AIR,
                AbilityCategory.CAPACITY,
                AbilityUseType.ACTIVE,
                AbilityTrigger.SWAP_HAND
        );
        setUsageLimit(UsageLimit.perGame(1));
    }

    @Override
    public boolean canExecute(
            @NotNull Player player,
            @NotNull NocturnePlayer nocturnePlayer,
            @NotNull AbilityContext context
    ) {
        return true;
    }

    @SuppressWarnings("DataFlowIssue")
    @Override
    protected @NotNull AbilityResult executeLogic(
            @NotNull Player player,
            @NotNull NocturnePlayer nocturnePlayer,
            @NotNull AbilityContext context
    ) {
        var protectionManager = game().getCurrentRound().getProtectionManager();

        for (NocturnePlayer np : game().getPlayerManager().getAlive()) {
            protectionManager.protect(np.getPlayerId(), ProtectionType.AQUILON);
        }

        return AbilityResult.success(
                Component.text("§9Votre §6Aquilon §9a recouvert toute la partie: tous les joueurs ont été protégé de §cl'Embrasement§9 !")
        );
    }

    @Override
    public DrunkSupport supportsDrunk() {
        return DrunkSupport.CUSTOM_LOGIC;
    }

    @Override
    protected @NotNull AbilityResult executeDrunkLogic(
            @NotNull Player player,
            @NotNull NocturnePlayer nocturnePlayer,
            @NotNull AbilityContext context
    ) {
        return AbilityResult.success(
                Component.text("§9Votre §6Aquilon §9a recouvert toute la partie: tous les joueurs ont été protégé de §Cl'Embrasement§9 !")
        );
    }

    @Override
    public void onGameplayPhaseStart(@NotNull Player player, @NotNull NocturnePlayer nocturnePlayer, @NotNull PhaseContext phaseContext) {
        game().getAbilityManager().setCooldown(nocturnePlayer.getPlayerId(), getId(), game().getSettings().getAquilonStartingCooldown());

    }

    @Override
    public @NotNull Component getCannotExecuteMessage(
            @NotNull Player player,
            @NotNull NocturnePlayer nocturnePlayer
    ) {
        return Component.text("§cVous avez déjà utilisé votre Aquilon cette partie !");
    }

}
