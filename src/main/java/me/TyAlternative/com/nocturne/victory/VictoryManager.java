package me.TyAlternative.com.nocturne.victory;

import me.TyAlternative.com.nocturne.api.role.RoleTeam;
import me.TyAlternative.com.nocturne.core.NocturneGame;
import me.TyAlternative.com.nocturne.victory.conditions.AllFlammesEliminatedCondition;
import me.TyAlternative.com.nocturne.victory.conditions.FlintAndSteelVictoryCondition;
import me.TyAlternative.com.nocturne.victory.conditions.LastSoloStandingCondition;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Évalue les conditions de victoire après chaque élimination.
 *
 * <p>Les conditions sont vérifiées dans l'ordre d'enregistrement.
 * La première condition remplie déclenche la fin de partie.
 * Le résultat est propagé via {@link NocturneGame#handleVictory(RoleTeam, String)}.
 */

public final class VictoryManager {

    private final List<VictoryCondition> conditions = new ArrayList<>();
    private final NocturneGame game;


    /**
     * @param game façade centrale du jeu, utilisée pour accéder à l'état
     *             des joueurs et déclencher la fin de partie
     */
    public VictoryManager(@NotNull NocturneGame game) {
        this.game = game;

        // Ordre d'évaluation intentionnel :
        // 1. Toutes les Flammes éliminées → Bâtons gagnent
        // 2. Dernière Flamme seule → elle gagne
        // 3. Silex/Acier dominant → ils gagnent
        conditions.add(new AllFlammesEliminatedCondition());
        conditions.add(new LastSoloStandingCondition());
        conditions.add(new FlintAndSteelVictoryCondition());
    }

    /**
     * Vérifie toutes les conditions de victoire dans l'ordre.
     *
     * @return {@code true} si une condition est remplie et la partie doit s'arrêter
     */
    public boolean checkVictory() {
        for (VictoryCondition condition : conditions) {
            RoleTeam winner = condition.check(game);
            if (winner != null) {
                game.handleVictory(winner, condition.getVictoryMessage(winner));
                return true;
            }
        }
        return false;
    }

    /**
     * Ajoute une condition de victoire personnalisée.
     * Utilisé pour étendre le système sans modifier cette classe.
     *
     * @param condition condition à ajouter en fin de liste
     */
    public void addCondition(@NotNull VictoryCondition condition) {
        conditions.add(condition);
    }

}
