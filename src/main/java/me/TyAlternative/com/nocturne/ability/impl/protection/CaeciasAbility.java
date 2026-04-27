package me.TyAlternative.com.nocturne.ability.impl.protection;
import me.TyAlternative.com.nocturne.ability.AbilityIds;
import me.TyAlternative.com.nocturne.ability.AbstractAbility;
import me.TyAlternative.com.nocturne.ability.DrunkSupport;
import me.TyAlternative.com.nocturne.api.ability.*;
import me.TyAlternative.com.nocturne.api.phase.PhaseType;
import me.TyAlternative.com.nocturne.core.phase.PhaseContext;
import me.TyAlternative.com.nocturne.mechanics.protection.ProtectionManager;
import me.TyAlternative.com.nocturne.mechanics.protection.ProtectionType;
import me.TyAlternative.com.nocturne.player.NocturnePlayer;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@SuppressWarnings("DataFlowIssue")
public final class CaeciasAbility extends AbstractAbility {

    private final double radius;

    public CaeciasAbility() {
        super(
                AbilityIds.CAECIAS,
                "Bénédiction de Caecias",
                "Vous êtes protégé par défaut contre tout Embrasement. Cependant, si vous vous retrouvez seul en fin de phase (aucun joueur à moins de §710§r blocs), vous perdez cette protection.",
                Material.AIR,
                AbilityCategory.CAPACITY,
                AbilityUseType.PASSIVE,
                AbilityTrigger.AUTOMATIC
        );
        setAllowedPhases(PhaseType.GAMEPLAY);
        radius = game().getSettings().getCaeciasRadiusProtection();
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
    public DrunkSupport supportsDrunk() {
        return DrunkSupport.DEFAULT_LOGIC;
    }

    // -------------------------------------------------------------------------
    // Hook : fin de phase de Gameplay — vol d'embrasement
    // -------------------------------------------------------------------------


    @Override
    public void onGameplayPhaseStart(@NotNull Player player, @NotNull NocturnePlayer nocturnePlayer, @NotNull PhaseContext phaseContext) {
        if (isDrunk()) return;

        if (game().getCurrentRound() == null) return;
        ProtectionManager protectionManager  = game().getCurrentRound().getProtectionManager();

        protectionManager.protect(nocturnePlayer.getPlayerId(), ProtectionType.CAECIAS);
    }

    @Override
    public void onGameplayPhaseEnd(@NotNull Player player, @NotNull NocturnePlayer nocturnePlayer, @NotNull PhaseContext phaseContext) {
        if (isDrunk()) return;

        if (game().getCurrentRound() == null) return;
        ProtectionManager protectionManager  = game().getCurrentRound().getProtectionManager();

        List<NocturnePlayer> aroundPlayers = game().getPlayerManager().getAliveInRadius(nocturnePlayer, radius);
        if (aroundPlayers.isEmpty()) {
            protectionManager.removeProtectionByType(nocturnePlayer.getPlayerId(), ProtectionType.CAECIAS);
        }

    }

    // -------------------------------------------------------------------------
    // Utilitaires
    // -------------------------------------------------------------------------


    @Override
    public @NotNull String getDescription() {
        return "Vous êtes protégé par défaut contre tout §cEmbrasement§f. Cependant, si vous vous retrouvez seul en fin de phase (personne à moins de §7" + (int) radius + "§r blocs), vous perdez cette protection.";
    }

    @Override
    public @Nullable Component getCannotExecuteMessage(@NotNull Player p, @NotNull NocturnePlayer np) {
        return null;
    }
}
