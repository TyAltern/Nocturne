package me.TyAlternative.com.nocturne.ability.impl.protection;
import me.TyAlternative.com.nocturne.ability.AbilityIds;
import me.TyAlternative.com.nocturne.ability.AbstractAbility;
import me.TyAlternative.com.nocturne.ability.DrunkSupport;
import me.TyAlternative.com.nocturne.ability.UsageLimit;
import me.TyAlternative.com.nocturne.api.ability.*;
import me.TyAlternative.com.nocturne.core.phase.PhaseContext;
import me.TyAlternative.com.nocturne.mechanics.protection.ProtectionManager;
import me.TyAlternative.com.nocturne.mechanics.protection.ProtectionType;
import me.TyAlternative.com.nocturne.player.NocturnePlayer;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


/**
 * Clic droit à main vide sur autant de joueurs que souhaité pour les marquer.
 * En fin de phase, tous les joueurs marqués sont protégés — sauf si l'un d'eux
 * a utilisé une capacité active pendant la phase, auquel cas tous perdent
 * leur protection.
 *
 * <p>Les protections sont appliquées en {@link #onGameplayPhaseEnd} plutôt qu'au
 * moment du clic, car la condition d'annulation dépend du comportement en phase.
 *
 * <p>En mode drunk : les marquages sont enregistrés et les protections appliquées,
 * mais sans vérifier la condition d'annulation.
 */
@SuppressWarnings("DataFlowIssue")
public final class NotosAbility extends AbstractAbility {

    /** Joueurs marqués par la Eissaure pour la manche en cours. */
    private final List<UUID> markedPlayers = new ArrayList<>();

    private final List<UUID> activePlayers = new ArrayList<>();

    /**
     * {@code false} si la condition d'annulation a été déclenchée : un joueur
     * marqué a utilisé une capacité active.
     */
    private boolean protectionValid = true;

    public NotosAbility() {
        super(
                AbilityIds.NOTOS,
                "Bénédiction de Notos",
                "Vous pouvez protéger autant de §eBâtons§f que vous le souhaitez, mais dès que l'un d'entre eux utilise une capacité, tous redeviennent fragiles.",
                Material.AIR,
                AbilityCategory.CAPACITY,
                AbilityUseType.ACTIVE,
                AbilityTrigger.RIGHT_CLICK_PLAYER
        );
        setUsageLimit(UsageLimit.unlimited());
    }


    // -------------------------------------------------------------------------
    // Exécution
    // -------------------------------------------------------------------------


    @Override
    public DrunkSupport supportsDrunk() {
        return DrunkSupport.DEFAULT_LOGIC;
    }

    @Override
    protected @NotNull AbilityResult executeLogic(@NotNull Player player, @NotNull NocturnePlayer nocturnePlayer, @NotNull AbilityContext context) {
        UUID targetId = context.getTarget().getUniqueId();
//        if (markedPlayers.contains(targetId)) {
//            return AbilityResult.failure(
//                    Component.text("§cCe joueur est déjà frappé par votre §6Notos§c.")
//            );
//        }
        if (activePlayers.contains(targetId) && game().getSettings().shouldNotosRemoveIfBefore()) {
            protectionValid = false;
        }

        int cooldown = game().getSettings().getNotosCooldown();
        game().getAbilityManager().setCooldown(player.getUniqueId(), getId(), cooldown);

        markedPlayers.add(targetId);
        String addition = (game().getSettings().shouldNotosShowProtectedCount()) ?
                " §7(" + markedPlayers.size() + ")"
                    :
                "";
        return AbilityResult.success(
                Component.text("\n§6[Bénédiction de Notos]§f Vous avez marqué un " + (markedPlayers.size() > 1 ? "autre " : "") + "§eBâton§f." + addition)
        );

    }

    @Override
    public boolean canExecute(@NotNull Player player, @NotNull NocturnePlayer nocturnePlayer, @NotNull AbilityContext context) {
        if (!context.hasTarget() || !context.isEmptyHand()) return false;
        NocturnePlayer targetData = game().getPlayerManager().get(context.getTarget());
        return targetData != null && targetData.isAlive();
    }

    // -------------------------------------------------------------------------
    // Hooks de phase
    // -------------------------------------------------------------------------


    @Override
    public void onGameplayPhaseStart(@NotNull Player player, @NotNull NocturnePlayer nocturnePlayer, @NotNull PhaseContext phaseContext) {
        game().getAbilityManager().setCooldown(nocturnePlayer.getPlayerId(), getId(), game().getSettings().getNotosStartingCooldown());
        markedPlayers.clear();
        activePlayers.clear();
        protectionValid = true;

    }

    @Override
    public void onGameplayPhaseEnd(@NotNull Player player, @NotNull NocturnePlayer nocturnePlayer, @NotNull PhaseContext phaseContext) {

        if (isDrunk() || !protectionValid) {
            // Annulation : un marqué a utilisé une capacité ou le joueur est drunk
            markedPlayers.clear();
            activePlayers.clear();
            return;
        }


        ProtectionManager protectionManager = game().getCurrentRound().getProtectionManager();
        for (UUID targetId : markedPlayers) {
            protectionManager.protect(targetId, ProtectionType.NOTOS);
        }
        markedPlayers.clear();
    }

    // -------------------------------------------------------------------------
    // Surveillance des capacités actives
    // -------------------------------------------------------------------------

    /**
     * Si un joueur marqué utilise une capacité active, invalide les protections.
     */
    @Override
    public void onActiveAbilityUsed(@NotNull Player caster, @NotNull NocturnePlayer nocturneCaster, @NotNull AbilityContext context, @NotNull AbilityResult result) {
        if (!result.isSuccess()) return;
        if (markedPlayers.contains(caster.getUniqueId())) protectionValid = false;
        if (!activePlayers.contains(caster.getUniqueId())) activePlayers.add(caster.getUniqueId());
    }

    @Override
    public @NotNull Component getCannotExecuteMessage(
            @NotNull Player player,
            @NotNull NocturnePlayer nocturnePlayer
    ) {
        return Component.text("§cVous ne pouvez pas utiliser la §6[Bénédiction de Notos]§c maintenant.");
    }
}
