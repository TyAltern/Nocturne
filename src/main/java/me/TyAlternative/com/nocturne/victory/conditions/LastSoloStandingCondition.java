package me.TyAlternative.com.nocturne.victory.conditions;

import me.TyAlternative.com.nocturne.api.role.RoleTeam;
import me.TyAlternative.com.nocturne.core.NocturneGame;
import me.TyAlternative.com.nocturne.victory.VictoryCondition;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Victoire d'une Flamme solitaire : elle est la dernière survivante
 * (tous les Bâtons et le duo Silex/Acier éliminés).
 */
@SuppressWarnings("unused")
public final class LastSoloStandingCondition implements VictoryCondition {
    @Override
    public @Nullable RoleTeam check(@NotNull NocturneGame game) {
        int solitaires = game.getPlayerManager().getAliveCountByTeam(RoleTeam.SOLITAIRE);
        int batons     = game.getPlayerManager().getAliveCountByTeam(RoleTeam.BATONS);
        int silexAcier = game.getPlayerManager().getAliveCountByTeam(RoleTeam.SILEX_ACIER);

        if (solitaires == 1 && batons == 0 && silexAcier == 0) {
            return RoleTeam.SOLITAIRE;
        }
        return null;
    }

    @Override
    public @NotNull String getVictoryMessage(@NotNull RoleTeam winner) {
        return "§c§lVICTOIRE DE LA FLAMME ! §r§7La dernière Flamme remporte la partie.";
    }
}
