package me.TyAlternative.com.nocturne.api.ability;


import net.kyori.adventure.text.format.TextColor;
import org.jetbrains.annotations.NotNull;

/**
 * Catégorie visuelle d'une capacité, utilisée pour structurer le message de présentation du rôle.
 *
 * <ul>
 *   <li>{@link #CAPACITY} — capacité positive, levier d'action principal du rôle.</li>
 *   <li>{@link #EFFECT} — effet neutre ou contextuel, ni avantage ni désavantage clair.</li>
 *   <li>{@link #CURSE} — contrainte négative imposée au joueur.</li>
 * </ul>
 */
public enum AbilityCategory {

    CAPACITY("Capacité", TextColor.color(255, 170, 0)),
    EFFECT("Effet",     TextColor.color(255, 255, 85)),
    CURSE("Malédiction", TextColor.color(170, 0, 170));

    private final String displayName;
    private final TextColor color;

    AbilityCategory(@NotNull String displayName, @NotNull TextColor color) {
        this.displayName = displayName;
        this.color = color;
    }

    /** Nom affiché dans la présentation du rôle. */
    public @NotNull String getDisplayName() {
        return displayName;
    }

    /** Couleur Adventure associée à cette catégorie. */
    public @NotNull TextColor getColor() {
        return color;
    }
}
