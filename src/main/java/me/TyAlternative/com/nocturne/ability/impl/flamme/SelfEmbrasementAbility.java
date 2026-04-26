package me.TyAlternative.com.nocturne.ability.impl.flamme;

import me.TyAlternative.com.nocturne.ability.AbilityIds;
import me.TyAlternative.com.nocturne.ability.AbstractAbility;
import me.TyAlternative.com.nocturne.ability.DrunkSupport;
import me.TyAlternative.com.nocturne.ability.UsageLimit;
import me.TyAlternative.com.nocturne.api.ability.*;
import me.TyAlternative.com.nocturne.mechanics.embrasement.EmbrasementCause;
import me.TyAlternative.com.nocturne.player.NocturnePlayer;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Embrasement — capacité active de l'Étincelle.
 *
 * <p>Clic droit à main vide sur un joueur vivant : ce joueur est Embrasé et sera
 * éliminé en fin de phase de Gameplay, sauf s'il est protégé.
 * Utilisable une seule fois par manche.
 */
@SuppressWarnings("DataFlowIssue")
public final class SelfEmbrasementAbility extends AbstractAbility {

    public SelfEmbrasementAbility() {
        super(
                AbilityIds.SELF_EMBRASEMENT,
                "Embrasement",
                "Une fois par phase de Gameplay, vous pouvez cibler un joueur. "
                        + "Ce dernier sera éliminé à la fin de la phase.",
                Material.BLAZE_POWDER,
                AbilityCategory.CAPACITY,
                AbilityUseType.ACTIVE,
                AbilityTrigger.DOUBLE_SWAP_HAND
        );
        setUsageLimit(UsageLimit.perRound(1));
    }

    @Override
    public boolean canExecute(@NotNull Player player, @NotNull NocturnePlayer nocturnePlayer, @NotNull AbilityContext context) {
        return true;
    }

    @Override
    protected @NotNull AbilityResult executeLogic(@NotNull Player player, @NotNull NocturnePlayer nocturnePlayer, @NotNull AbilityContext context) {


        game().getCurrentRound().getEmbrasementManager()
                .embrase(player.getUniqueId(), EmbrasementCause.ETINCELLE, nocturnePlayer.getPlayerId());


        return AbilityResult.success(Component.text("§cVous vous êtes embrasé (pour le debug hein)."));

    }

    @Override
    protected @NotNull AbilityResult executeDrunkLogic(@NotNull Player player, @NotNull NocturnePlayer nocturnePlayer, @NotNull AbilityContext context) {

        return AbilityResult.success(Component.text("§cVous vous êtes embrasé (pour le debug hein (sinon peut être arrêter de boire...)). "));
    }

    @Override
    public DrunkSupport supportsDrunk() {
        return DrunkSupport.CUSTOM_LOGIC;
    }

    @Override
    public @Nullable Component getCannotExecuteMessage(
            @NotNull Player player,
            @NotNull NocturnePlayer nocturnePlayer
    ) {
        return Component.text("§cVous avez déjà utilisé votre Embrasement cette manche !");
    }
}
