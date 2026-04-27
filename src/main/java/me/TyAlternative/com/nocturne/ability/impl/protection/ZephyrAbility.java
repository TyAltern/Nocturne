package me.TyAlternative.com.nocturne.ability.impl.protection;

import me.TyAlternative.com.nocturne.ability.AbilityIds;
import me.TyAlternative.com.nocturne.ability.AbstractAbility;
import me.TyAlternative.com.nocturne.ability.DrunkSupport;
import me.TyAlternative.com.nocturne.api.ability.*;
import me.TyAlternative.com.nocturne.core.phase.PhaseContext;
import me.TyAlternative.com.nocturne.mechanics.protection.ProtectionManager;
import me.TyAlternative.com.nocturne.mechanics.protection.ProtectionType;
import me.TyAlternative.com.nocturne.player.NocturnePlayer;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * En fin de phase de Gameplay, protège le joueur vivant non-protégé le plus
 * proche dans un rayon de 45 blocs. Si personne n'est à portée, se protège lui-même.
 */
@SuppressWarnings("DataFlowIssue")
public final class ZephyrAbility extends AbstractAbility {
    private final double radius;
    private final boolean onlyUnprotected;

    public ZephyrAbility() {
        super(
                AbilityIds.ZEPHYR,
                "Bénédiction de Zéphyr",
                "En fin de phase, vous protégez automatiquement le §eBâton§f vivant non-protégé le plus proche dans" +
                        " un rayon de §745§r blocs. Si personne n'est à portée, vous vous protégez vous-même.",
                Material.AIR,
                AbilityCategory.CAPACITY,
                AbilityUseType.PASSIVE,
                AbilityTrigger.AUTOMATIC
        );
        radius = game().getSettings().getZephyrRadius();
        onlyUnprotected = game().getSettings().shouldZephyrProtectOnlyUnprotected();
    }


    @Override
    public @NotNull String getDescription() {
        return "En fin de phase, vous protégez automatiquement le §eBâton§f vivant " + (onlyUnprotected ? "§7non-protégé§f " : "") + "le plus proche dans un rayon de §7" + radius +"§f blocs. "
                + "Si personne n'est à portée, vous vous protégez vous-même.";
    }

    @Override
    public DrunkSupport supportsDrunk() {
        return DrunkSupport.DEFAULT_LOGIC;
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
    public @Nullable Component getCannotExecuteMessage(@NotNull Player player, @NotNull NocturnePlayer nocturnePlayer) {
        return null;
    }


    @Override
    public void onGameplayPhaseEnd(@NotNull Player player, @NotNull NocturnePlayer nocturnePlayer, @NotNull PhaseContext phaseContext) {
        if (isDrunk()) return;

        ProtectionManager protectionManager = game().getCurrentRound().getProtectionManager();

        NocturnePlayer closest = nocturnePlayer;
        double closestDistSq = radius * radius;

        for (NocturnePlayer other : game().getPlayerManager().getAlive()) {
            if (other.getPlayerId().equals(nocturnePlayer.getPlayerId())) continue;
            if (onlyUnprotected && protectionManager.isProtected(other.getPlayerId())) continue;
            Player otherPlayer = other.getPlayer();
            if (otherPlayer == null) continue;

            double distSq = otherPlayer.getLocation().distanceSquared(player.getLocation());
            if (distSq < closestDistSq) {
                closest = other;
                closestDistSq = distSq;
            }
        }
        protectionManager.protect(closest.getPlayerId(), ProtectionType.ZEPHYR);

    }
}
