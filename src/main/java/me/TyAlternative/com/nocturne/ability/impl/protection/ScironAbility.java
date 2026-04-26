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
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@SuppressWarnings("DataFlowIssue")
public final class ScironAbility extends AbstractAbility {

    private UUID lastTargetId = null;
    private UUID lastMarkedByTargetId = null;

    public ScironAbility() {
        super(AbilityIds.SCIRON,
                "Bénédiction de Sciron",
                "",
                Material.AIR,
                AbilityCategory.CAPACITY,
                AbilityUseType.ACTIVE,
                AbilityTrigger.RIGHT_CLICK_PLAYER);

        setUsageLimit(UsageLimit.perRound(game().getSettings().getScironMaxUse()));

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
        lastTargetId = context.getTarget().getUniqueId();

        game().getAbilityManager().setCooldown(nocturnePlayer.getPlayerId(), getId(), game().getSettings().getScironCooldown());
        return AbilityResult.success(
                Component.text("§6[Bénédiction de Sciron]§7 Vous avez marqué un joueur. Le prochain joueur marqué par ce joueur sera protégé.")
        );
    }

    @Override
    public @NotNull Component getCannotExecuteMessage(
            @NotNull Player player,
            @NotNull NocturnePlayer nocturnePlayer
    ) {
        return Component.text("§cVous avez déjà utilisé la Bénédiction de Lips cette manche !");
    }

    @Override
    public DrunkSupport supportsDrunk() {
        return DrunkSupport.DEFAULT_LOGIC;
    }

    @Override
    public void onGameplayPhaseStart(@NotNull Player player, @NotNull NocturnePlayer nocturnePlayer, @NotNull PhaseContext phaseContext) {
        lastTargetId = null;
        lastMarkedByTargetId = null;
        game().getAbilityManager().setCooldown(nocturnePlayer.getPlayerId(), getId(), game().getSettings().getScironStartingCooldown());
    }

    @Override
    public void onGameplayPhaseEnd(@NotNull Player player, @NotNull NocturnePlayer nocturnePlayer, @NotNull PhaseContext phaseContext) {
        if (isDrunk()) return;
        if (lastMarkedByTargetId == null) return;
        game().getCurrentRound().getProtectionManager().protect(lastMarkedByTargetId, ProtectionType.SCIRON);
    }

    @Override
    public void onActiveAbilityUsed(@NotNull Player caster, @NotNull NocturnePlayer nocturneCaster, @NotNull AbilityContext context, @NotNull AbilityResult result) {
        if (isDrunk()) return;
        if (!result.isSuccess()) return;
        if (nocturneCaster.getPlayerId() != lastTargetId) return;
        if (!context.hasTarget())  return; // seules les capacités avec cible explicite comptent
        lastMarkedByTargetId = context.getTarget().getUniqueId();
        getOwner().getPlayer().sendMessage("§e[Boite d'Allumettes]§7 Caster: " + Bukkit.getPlayer(lastTargetId).getName() + " ----> Target: " + Bukkit.getPlayer(lastMarkedByTargetId).getName());
        lastTargetId = null;
    }
}
