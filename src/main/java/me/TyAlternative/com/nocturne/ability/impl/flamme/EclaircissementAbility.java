package me.TyAlternative.com.nocturne.ability.impl.flamme;
import me.TyAlternative.com.nocturne.ability.AbilityIds;
import me.TyAlternative.com.nocturne.ability.AbstractAbility;
import me.TyAlternative.com.nocturne.ability.DrunkSupport;
import me.TyAlternative.com.nocturne.ability.UsageLimit;
import me.TyAlternative.com.nocturne.api.ability.*;
import me.TyAlternative.com.nocturne.core.phase.PhaseContext;
import me.TyAlternative.com.nocturne.mechanics.vote.VoteEntry;
import me.TyAlternative.com.nocturne.player.NocturnePlayer;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

/**
 * Éclaircissement — capacité active du Flambeau.
 *
 * <p>Une fois par manche, le Flambeau peut marquer un joueur. Si ce joueur est
 * éliminé lors du vote de la même manche, le Flambeau gagne +1 de poids de vote
 * pour toutes les manches suivantes (permanent).
 *
 * <p>La cible marquée est révélée au début de la phase de Vote avec un rappel
 * de la tâche et du poids de vote actuel.
 */
public final class EclaircissementAbility extends AbstractAbility {

    /** UUID du joueur marqué pour la manche en cours. {@code null} si aucun marquage. */
    private @Nullable UUID markedPlayerId = null;

    /** */
    private int goalSuccess = 0;

    public EclaircissementAbility() {
        super(
                AbilityIds.ECLAIRCISSEMENT,
                "Éclaircissement",
                "Marquez un joueur par manche. S'il est éliminé au vote suivant, "
                        + "vous gagnez un poids de vote permanent.",
                Material.LANTERN,
                AbilityCategory.CAPACITY,
                AbilityUseType.ACTIVE,
                AbilityTrigger.RIGHT_CLICK_PLAYER
        );
        setUsageLimit(UsageLimit.perRound(1));
    }

    // -------------------------------------------------------------------------
    // Exécution
    // -------------------------------------------------------------------------


    @SuppressWarnings("DataFlowIssue")
    @Override
    public boolean canExecute(@NotNull Player player, @NotNull NocturnePlayer nocturnePlayer, @NotNull AbilityContext context) {
        if (!context.hasTarget() || !context.isEmptyHand()) return false;
        NocturnePlayer targetData = game().getPlayerManager().get(context.getTarget());
        return targetData != null && targetData.isAlive();
    }

    @SuppressWarnings("DataFlowIssue")
    @Override
    protected @NotNull AbilityResult executeLogic(@NotNull Player player, @NotNull NocturnePlayer nocturnePlayer, @NotNull AbilityContext context) {
        markedPlayerId = context.getTarget().getUniqueId();
        return AbilityResult.success(
                Component.text("§eVous avez marqué un joueur.")
        );
    }

    @Override
    public DrunkSupport supportsDrunk() {
        return DrunkSupport.DEFAULT_LOGIC;
    }

    // -------------------------------------------------------------------------
    // Hooks de phase
    // -------------------------------------------------------------------------

    @Override
    public void onGameplayPhaseStart(@NotNull Player player, @NotNull NocturnePlayer nocturnePlayer, @NotNull PhaseContext phaseContext) {
        game().getAbilityManager().setCooldown(nocturnePlayer.getPlayerId(), getId(), game().getSettings().getEclaircissementStartingCooldown());
        markedPlayerId = null; // Réinitialiser la cible en début de manche
    }

    @Override
    public void onVotePhaseStart(@NotNull Player player, @NotNull NocturnePlayer nocturnePlayer, @NotNull PhaseContext phaseContext) {
        if (markedPlayerId == null) {
            // TODO: ajout d'une version du rôle qui ne possède son poids de vote que s'il n'as marqué personne lors du round (donc soit il stack soit il attack)
            player.sendMessage(Component.text(
                    "§7Vous n'avez marqué aucun joueur cette manche. Vous n'avez pas de cible à éliminer."
            ));
            return;
        }
        Player marked = Bukkit.getPlayer(markedPlayerId);
        String markedName = marked != null ? marked.getName() : "Inconnu";

        player.sendMessage(Component.text(
                "§7Votre cible est §d" + markedName + "§7. Assurez-vous qu'il soit éliminé !"
        ));
        player.sendMessage(Component.text(
                "§7Votre poids de vote actuel : §6" + (isDrunk() ? nocturnePlayer.getVoteWeight() + goalSuccess : nocturnePlayer.getVoteWeight())
        ));

    }


    @Override
    public void afterVoteCalculation(@NotNull Player player, @NotNull NocturnePlayer nocturnePlayer, @Nullable UUID votedPlayerId, @NotNull List<VoteEntry> allVotes) {

        if (votedPlayerId != null && votedPlayerId.equals(markedPlayerId)) {
            // Cible éliminée : gain de poids permanent
            int newWeight = (isDrunk() ? nocturnePlayer.getVoteWeight() + goalSuccess : nocturnePlayer.getVoteWeight()) + 1;
            goalSuccess++;
            if (!isDrunk()) nocturnePlayer.setVoteWeight(newWeight);
            player.sendMessage(Component.text(
                    "§aVotre cible a été éliminée ! Votre poids de vote passe à §6" + newWeight + "§a."
            ));
        } else {
            player.sendMessage(Component.text(
                    "§cVotre cible a survécu. Aucun bonus cette manche."
            ));
        }

    }



    @Override
    public @NotNull Component getCannotExecuteMessage(@NotNull Player player, @NotNull NocturnePlayer nocturnePlayer) {
        return Component.text("§cVous avez déjà marqué un joueur cette manche !");
    }
}
