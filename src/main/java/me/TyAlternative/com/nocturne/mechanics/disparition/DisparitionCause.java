package me.TyAlternative.com.nocturne.mechanics.disparition;

import me.TyAlternative.com.nocturne.elimination.EliminationCause;
import me.TyAlternative.com.nocturne.elimination.EliminationManager;
import org.jetbrains.annotations.NotNull;

/**
 * Cause d'une disparition, permettant à l'
 * {@link EliminationManager}
 * de déterminer la {@link EliminationCause} correspondante.
 */
@SuppressWarnings("unused")
public enum DisparitionCause {
    SOLITUDE("Solitude Mortelle"),
    OTHER("Autre");

    private final @NotNull String displayName;

    DisparitionCause(@NotNull String displayName) {
        this.displayName = displayName;
    }

    public @NotNull String getDisplayName() {
        return displayName;
    }
}
