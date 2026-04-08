package me.TyAlternative.com.nocturne.victory.conditions;

import me.TyAlternative.com.nocturne.api.role.RoleTeam;
import me.TyAlternative.com.nocturne.core.NocturneGame;
import me.TyAlternative.com.nocturne.victory.VictoryCondition;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Victoire du duo Silex/Acier : il ne reste plus qu'eux (et au plus un Bâton),
 * sans Flamme solitaire encore en vie.
 *
 * <p>Condition : 0 Solitaire, au plus 1 Bâton, au moins 1 Silex/Acier vivant.
 */
@SuppressWarnings("unused")
public final class FlintAndSteelVictoryCondition implements VictoryCondition {
    @Override
    public @Nullable RoleTeam check(@NotNull NocturneGame game) {
        int solitaires = game.getPlayerManager().getAliveCountByTeam(RoleTeam.SOLITAIRE);
        int batons     = game.getPlayerManager().getAliveCountByTeam(RoleTeam.BATONS);
        int silexAcier = game.getPlayerManager().getAliveCountByTeam(RoleTeam.SILEX_ACIER);

        if (solitaires == 0 && batons <= 0 && silexAcier >= 1) {
            return RoleTeam.SILEX_ACIER;
        }
        return null;
    }

    @Override
    public @NotNull String getVictoryMessage(@NotNull RoleTeam winner) {
        return "§7§lVICTOIRE DU SILEX ET DE L'ACIER ! §r§7Ces êtres incendiaires remportent la partie.";
    }
}
