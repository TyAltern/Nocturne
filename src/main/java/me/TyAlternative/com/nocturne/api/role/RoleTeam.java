package me.TyAlternative.com.nocturne.api.role;

import org.jetbrains.annotations.NotNull;

/**
 * Équipe d'un rôle, déterminant avec qui le joueur partage sa condition de victoire.
 *
 * <ul>
 *   <li>{@link #SOLITAIRE} — Flamme solo, doit survivre en dernier.</li>
 *   <li>{@link #SILEX_ACIER} — Duo de Flammes, gagnent ensemble.</li>
 *   <li>{@link #BATONS} — Camp des innocents, gagnent ensemble en éliminant toutes les Flammes.</li>
 * </ul>
 */
public enum RoleTeam {

    SOLITAIRE(
            "Solitaire",
            "Survivez en étant la dernière Flamme.",
            true
    ),

    SILEX_ACIER(
            "Silex & Acier",
            "Éliminez tous les Bâtons avec votre partenaire.",
            false
    ),

    BATONS(
            "Bâtons",
            "Éliminez toutes les Flammes ensemble.",
            false
    );

    private final String displayName;
    private final String victoryObjective;
    private final boolean isSolo;

    RoleTeam(@NotNull String displayName, @NotNull String victoryObjective, boolean isSolo) {
        this.displayName = displayName;
        this.victoryObjective = victoryObjective;
        this.isSolo = isSolo;
    }

    /** Nom affiché dans les interfaces joueur. */
    public @NotNull String getDisplayName() {
        return displayName;
    }

    /** Description courte de l'objectif de victoire de cette équipe. */
    public @NotNull String getVictoryObjective() {
        return victoryObjective;
    }

    /** {@code true} si chaque membre de cette équipe joue pour lui-même. */
    public boolean isSolo() {
        return isSolo;
    }
}
