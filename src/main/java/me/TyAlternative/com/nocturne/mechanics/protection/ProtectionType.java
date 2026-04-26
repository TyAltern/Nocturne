package me.TyAlternative.com.nocturne.mechanics.protection;


import org.jetbrains.annotations.NotNull;

/**
 * Source d'une protection, permettant de distinguer l'origine
 * et d'appliquer des règles différentes selon le type.
 */
@SuppressWarnings("unused")
public enum ProtectionType {

    APELIOTE("Apéliote"),
    CAECIAS("Caecias"),
    BOREE("Borée"),
    NOTOS("Notos"),
    EUROS("Euros"),
    SCIRON("Sciron"),
    ZEPHYR("Zéphyr"),
    OTHER("Autre");

    private final @NotNull String displayName;

    ProtectionType(@NotNull String displayName) {
        this.displayName = displayName;
    }

    public @NotNull String getDisplayName() {
        return displayName;
    }
}