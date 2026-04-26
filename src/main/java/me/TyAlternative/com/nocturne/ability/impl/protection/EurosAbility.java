package me.TyAlternative.com.nocturne.ability.impl.protection;

import me.TyAlternative.com.nocturne.ability.AbilityIds;
import me.TyAlternative.com.nocturne.ability.AbstractAbility;
import me.TyAlternative.com.nocturne.ability.DrunkSupport;
import me.TyAlternative.com.nocturne.api.ability.*;
import me.TyAlternative.com.nocturne.core.phase.PhaseContext;
import me.TyAlternative.com.nocturne.mechanics.protection.ProtectionType;
import me.TyAlternative.com.nocturne.player.NocturnePlayer;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

@SuppressWarnings("DataFlowIssue")
public final class EurosAbility extends AbstractAbility {

    private UUID lastCasterId = null;

    public EurosAbility() {
        super(AbilityIds.EUROS,
                "Bénédiction d'Euros",
                "",
                Material.AIR,
                AbilityCategory.CAPACITY,
                AbilityUseType.PASSIVE,
                AbilityTrigger.AUTOMATIC);
    }

    @Override
    public boolean canExecute(@NotNull Player player, @NotNull NocturnePlayer nocturnePlayer, @NotNull AbilityContext context) {
        return false;
    }

    @Override
    public @Nullable Component getCannotExecuteMessage(@NotNull Player player, @NotNull NocturnePlayer nocturnePlayer) {
        return null;
    }

    @Override
    protected @NotNull AbilityResult executeLogic(@NotNull Player player, @NotNull NocturnePlayer nocturnePlayer, @NotNull AbilityContext context) {
        return AbilityResult.silentSuccess();
    }

    @Override
    public DrunkSupport supportsDrunk() {
        return DrunkSupport.DEFAULT_LOGIC;
    }

    @Override
    public void onActiveAbilityUsed(@NotNull Player caster, @NotNull NocturnePlayer nocturneCaster, @NotNull AbilityContext context, @NotNull AbilityResult result) {
        if (!result.isSuccess()) return;
        if (!context.hasTarget())  return; // seules les capacités avec cible explicite comptent
        if (isDrunk()) return;
        if (context.getTarget() != getOwnerPlayer()) return;

        lastCasterId = nocturneCaster.getPlayerId(); // Mise à jour du dernier caster

    }

    @Override
    public void onGameplayPhaseStart(@NotNull Player player, @NotNull NocturnePlayer nocturnePlayer, @NotNull PhaseContext phaseContext) {
        lastCasterId = null;
    }

    @Override
    public void onGameplayPhaseEnd(@NotNull Player player, @NotNull NocturnePlayer nocturnePlayer, @NotNull PhaseContext phaseContext) {
        if (isDrunk()) return;

        if (lastCasterId == null) {
            if (game().getSettings().shouldEurosSelfProtectionIfNotMarked()) {
                game().getCurrentRound().getProtectionManager().protect(nocturnePlayer.getPlayerId(), ProtectionType.EUROS);
            }
            return;
        }
        game().getCurrentRound().getProtectionManager().protect(lastCasterId, ProtectionType.EUROS);

    }
}
