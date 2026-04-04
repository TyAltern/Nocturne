package me.TyAlternative.com.nocturne.api.role;


import net.kyori.adventure.text.format.TextColor;
import org.jetbrains.annotations.NotNull;

/**
 * Catégorie d'un rôle, déterminant son camp et son comportement général dans la partie.
 *
 * <p>Les {@code FLAMME} sont les traîtres cherchant à éliminer les Bâtons.
 * Les {@code BATON} sont les innocents cherchant à identifier et éliminer les Flammes.
 * Les {@code NEUTRE} ont des objectifs propres, indépendants des deux camps.</p>
 */
public enum RoleType {
    FLAMME("Flamme", TextColor.color(208, 4, 17)),
    BATON("Bâton", TextColor.color(230, 201, 14)),
    NEUTRE("Neutre", TextColor.color(200, 200, 200));

    private final String displayName;
    private final TextColor color;

    RoleType(@NotNull String displayName, @NotNull TextColor color) {
        this.displayName = displayName;
        this.color = color;
    }

    /** Nom affiché dans les interfaces joueur. */
    public @NotNull String getDisplayName() {
        return displayName;
    }

    /** Couleur Adventure associée à ce type, utilisée dans les messages et UI. */
    public @NotNull TextColor getColor() {
        return color;
    }
}


