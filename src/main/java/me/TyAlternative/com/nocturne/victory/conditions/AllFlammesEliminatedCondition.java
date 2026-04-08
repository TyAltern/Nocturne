package me.TyAlternative.com.nocturne.victory.conditions;

import me.TyAlternative.com.nocturne.api.role.RoleTeam;
import me.TyAlternative.com.nocturne.core.NocturneGame;
import me.TyAlternative.com.nocturne.victory.VictoryCondition;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Victoire des Bâtons : toutes les Flammes solitaires et le duo Silex/Acier
 * ont été éliminés.
 */
@SuppressWarnings("unused")
public final class AllFlammesEliminatedCondition implements VictoryCondition {
    @Override
    public @Nullable RoleTeam check(@NotNull NocturneGame game) {
        int solitaires = game.getPlayerManager().getAliveCountByTeam(RoleTeam.SOLITAIRE);
        int silexAcier = game.getPlayerManager().getAliveCountByTeam(RoleTeam.SILEX_ACIER);

        if (solitaires == 0 && silexAcier == 0) {
            return RoleTeam.BATONS;
        }

        return null;
    }

    @Override
    public @NotNull String getVictoryMessage(@NotNull RoleTeam winner) {
        return "§e§lVICTOIRE DES BÂTONS ! §r§7Toutes les Flammes ont été éliminées.";
    }
}
