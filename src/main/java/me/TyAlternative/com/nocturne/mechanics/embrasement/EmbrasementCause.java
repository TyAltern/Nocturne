package me.TyAlternative.com.nocturne.mechanics.embrasement;


import me.TyAlternative.com.nocturne.elimination.EliminationManager;
import org.jetbrains.annotations.NotNull;

/**
 * Source d'un Embrasement, permettant aux rôles et à l'
 * {@link EliminationManager}
 * de différencier l'origine de l'élimination.
 */
@SuppressWarnings("unused")
public enum EmbrasementCause {

    ETINCELLE("Étincelle"),
    TORCHE("Torche"),
    PRISE_DE_FEU("Prise de Feu"),
    OTHER("Autre");

    private final @NotNull String displayName;

    EmbrasementCause(@NotNull String displayName) {
        this.displayName = displayName;
    }

    public @NotNull String getDisplayName() {
        return displayName;
    }
}
