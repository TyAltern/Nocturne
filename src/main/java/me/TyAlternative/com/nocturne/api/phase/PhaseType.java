package me.TyAlternative.com.nocturne.api.phase;

import org.jetbrains.annotations.NotNull;

/**
 * Enum de toutes les phases du cycle de vie d'une partie de Nocturne.
 *
 * <p>Cycle standard :
 * <pre>
 * LOBBY → GAMEPLAY → VOTE → GAMEPLAY → VOTE → ... → END
 * </pre>
 */
public enum PhaseType {

    /** Phase d'attente avant le début de la partie. Durée infinie. */
    LOBBY("Lobby", false),

    /** Phase de jeu anonyme : interactions, capacités actives. */
    GAMEPLAY("Gameplay", true),

    /** Phase de discussion et de vote pour éliminer un joueur. */
    VOTE("Vote", true),

    /** Phase de fin de partie : affichage des résultats. */
    END("Fin", false);

    private final @NotNull String displayName;
    private final boolean isInGame;

    PhaseType(@NotNull String displayName, boolean isInGame) {
        this.displayName = displayName;
        this.isInGame = isInGame;
    }

    /** Nom affiché dans les interfaces joueur. */
    public @NotNull String getDisplayName() {
        return displayName;
    }

    /**
     * {@code true} si cette phase fait partie d'une partie en cours.
     * Les capacités et mécaniques de jeu ne sont actives que pendant ces phases.
     */
    public boolean isInGame() {
        return isInGame;
    }
}
