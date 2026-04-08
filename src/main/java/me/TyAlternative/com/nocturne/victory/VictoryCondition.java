package me.TyAlternative.com.nocturne.victory;

import me.TyAlternative.com.nocturne.api.role.RoleTeam;
import me.TyAlternative.com.nocturne.core.NocturneGame;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Condition de victoire vérifiable en fin de phase.
 *
 * <p>Chaque implémentation représente un scénario de victoire possible.
 * Le {@link VictoryManager} les évalue dans l'ordre d'enregistrement
 * après chaque élimination.
 */
@SuppressWarnings("unused")
public interface VictoryCondition {
    /**
     * Vérifie si cette condition est remplie dans l'état actuel du jeu.
     *
     * @param game état courant du jeu
     * @return équipe gagnante, ou {@code null} si la condition n'est pas remplie
     */
    @Nullable RoleTeam check(@NotNull NocturneGame game);

    /**
     * Message de victoire diffusé à tous les joueurs.
     *
     * @param winner équipe gagnante
     */
    @NotNull String getVictoryMessage(@NotNull RoleTeam winner);
}
