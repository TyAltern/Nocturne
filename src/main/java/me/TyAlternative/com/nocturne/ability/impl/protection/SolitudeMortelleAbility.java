package me.TyAlternative.com.nocturne.ability.impl.protection;

import me.TyAlternative.com.nocturne.ability.AbilityIds;
import me.TyAlternative.com.nocturne.ability.AbstractAbility;
import me.TyAlternative.com.nocturne.api.ability.*;
import me.TyAlternative.com.nocturne.api.phase.PhaseType;
import me.TyAlternative.com.nocturne.core.phase.PhaseContext;
import me.TyAlternative.com.nocturne.mechanics.protection.ProtectionManager;
import me.TyAlternative.com.nocturne.mechanics.protection.ProtectionType;
import me.TyAlternative.com.nocturne.player.NocturnePlayer;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@SuppressWarnings("DataFlowIssue")
public final class SolitudeMortelleAbility extends AbstractAbility {

    private final double radius = 10.0;

    public SolitudeMortelleAbility() {
        super(
                AbilityIds.SOLITUDE_MORTELLE,
                "Solitude Mortelle",
                "Vous êtes protégé par défaut contre tous types §cd'Embrasement§r, cependant, si vous vous retrouvez seul à la fin d'un round (personne à moins de 10.0 blocs) vous perdez cette résistance.",
                AbilityCategory.CAPACITY,
                AbilityUseType.PASSIVE,
                AbilityTrigger.AUTOMATIC
        );
        setAllowedPhases(PhaseType.GAMEPLAY);
    }

    @Override
    public boolean canExecute(@NotNull Player player, @NotNull NocturnePlayer nocturnePlayer, @NotNull AbilityContext context) {
        return false;
    }

    @Override
    protected @NotNull AbilityResult executeLogic(@NotNull Player player, @NotNull NocturnePlayer nocturnePlayer, @NotNull AbilityContext context) {
        return AbilityResult.silentSuccess();
    }

    @Override
    public boolean supportsDrunk() {
        return true;
    }

    // -------------------------------------------------------------------------
    // Hook : fin de phase de Gameplay — vol d'embrasement
    // -------------------------------------------------------------------------


    @Override
    public void onGameplayPhaseStart(@NotNull Player player, @NotNull NocturnePlayer nocturnePlayer, @NotNull PhaseContext phaseContext) {

        if (game().getCurrentRound() == null) return;
        ProtectionManager protectionManager  = game().getCurrentRound().getProtectionManager();

        if (isDrunk()) return;
        protectionManager.protect(nocturnePlayer.getPlayerId(), ProtectionType.SOLITUDE_MORTELLE);
    }

    @Override
    public void onGameplayPhaseEnd(@NotNull Player player, @NotNull NocturnePlayer nocturnePlayer, @NotNull PhaseContext phaseContext) {

        if (game().getCurrentRound() == null) return;
        ProtectionManager protectionManager  = game().getCurrentRound().getProtectionManager();

        if (isDrunk()) return;
        List<NocturnePlayer> aroundPlayers = game().getPlayerManager().getAliveInRadius(nocturnePlayer, radius);
        if (aroundPlayers.isEmpty()) {
            protectionManager.removeProtectionByType(nocturnePlayer.getPlayerId(), ProtectionType.SOLITUDE_MORTELLE);
        }

    }

    // -------------------------------------------------------------------------
    // Utilitaires
    // -------------------------------------------------------------------------


    @Override
    public @NotNull String getDescription() {
        return "Vous êtes protégé par défaut contre tous types §cd'Embrasement§r, cependant, si vous vous retrouvez seul à la fin d'un round (personne à moins de §6" + (int) radius + "§r blocs) vous perdez cette résistance.";
    }

    @Override
    public @Nullable Component getCannotExecuteMessage(@NotNull Player p, @NotNull NocturnePlayer np) {
        return null;
    }
}
