package me.TyAlternative.com.nocturne.ability.impl.flamme;

import me.TyAlternative.com.nocturne.ability.AbilityIds;
import me.TyAlternative.com.nocturne.ability.AbstractAbility;
import me.TyAlternative.com.nocturne.ability.DrunkSupport;
import me.TyAlternative.com.nocturne.ability.UsageLimit;
import me.TyAlternative.com.nocturne.api.ability.*;
import me.TyAlternative.com.nocturne.api.role.Role;
import me.TyAlternative.com.nocturne.core.phase.PhaseContext;
import me.TyAlternative.com.nocturne.player.NocturnePlayer;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Étouffement de Flamme — capacité active de la Torche.
 *
 * <p>Permet à la Torche d'éteindre son {@link RayonnementAbility} pour la manche
 * en cours. Aucun embrasement ne sera déclenché en fin de phase si cette capacité
 * est utilisée.
 *
 * <p>En mode drunk : l'étouffement est simulé (le message s'affiche) mais le
 * Rayonnement n'est pas réellement désactivé.
 */
public final class EtouffementFlammeAbility extends AbstractAbility {


    public EtouffementFlammeAbility() {
        super(
                AbilityIds.ETOUFFEMENT_FLAMME,
                "Étouffement de Flamme",
                "Abandonnez votre §6Rayonnement§f pour cette manche. "
                        + "Aucun joueur ne sera Embrasé en fin de phase.",
                Material.BUCKET,
                AbilityCategory.CAPACITY,
                AbilityUseType.ACTIVE,
                AbilityTrigger.SWAP_HAND
        );
        // TODO: change if torche toggle is enable in config
        setUsageLimit(UsageLimit.perRound(1));
    }
    @Override
    public boolean canExecute(@NotNull Player player, @NotNull NocturnePlayer nocturnePlayer, @NotNull AbilityContext context) {
        // TODO: change if torche toggle is enable in config
        // Utilisable uniquement si le rayonnement est encore actif cette manche
        RayonnementAbility rayonnementAbility = getRayonnement(nocturnePlayer);
        return rayonnementAbility != null && rayonnementAbility.isRayonnementActif();
    }

    @Override
    protected @NotNull AbilityResult executeLogic(@NotNull Player player, @NotNull NocturnePlayer nocturnePlayer, @NotNull AbilityContext context) {
        RayonnementAbility rayonnementAbility = getRayonnement(nocturnePlayer);
        if (rayonnementAbility != null) rayonnementAbility.extinguish();

        return AbilityResult.success(
                Component.text("§6Vous avez étouffé votre Flamme pour cette manche.")
        );
    }

    @Override
    public DrunkSupport supportsDrunk() {
        return DrunkSupport.DEFAULT_LOGIC;
    }

    @Override
    public void onGameplayPhaseStart(@NotNull Player player, @NotNull NocturnePlayer nocturnePlayer, @NotNull PhaseContext phaseContext) {
        game().getAbilityManager().setCooldown(nocturnePlayer.getPlayerId(), getId(), game().getSettings().getEtouffementStartingCooldown());
    }

    // -------------------------------------------------------------------------
    // Utilitaire
    // -------------------------------------------------------------------------

    /** Récupère l'instance de {@link RayonnementAbility} du joueur, ou {@code null}. */
    private @Nullable RayonnementAbility getRayonnement(@NotNull NocturnePlayer nocturnePlayer) {
        Role role = nocturnePlayer.getRole();
        if (role == null) return null;
        Ability ability = role.getAbility(AbilityIds.RAYONNEMENT);
        return ability instanceof RayonnementAbility rayonnementAbility ? rayonnementAbility : null;
    }

    @Override
    public @NotNull Component getCannotExecuteMessage(
            @NotNull Player player,
            @NotNull NocturnePlayer nocturnePlayer
    ) {
        return Component.text("§cVous avez déjà étouffé votre Flamme cette manche !");
    }
}
