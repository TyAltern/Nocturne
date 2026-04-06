package me.TyAlternative.com.nocturne.mechanics.protection;


import org.jetbrains.annotations.NotNull;

/**
 * Source d'une protection, permettant de distinguer l'origine
 * et d'appliquer des règles différentes selon le type.
 */
@SuppressWarnings("unused")
public enum ProtectionType {

    BRISE("Brise"),
    ALIZE("Alizé"),
    AQUILON("Aquilon"),
    AUSTER("Auster"),
    SOLITUDE_MORTELLE("Solitude Mortelle"),
    PRISE_DE_FEU("Prise de Feu"),
    OTHER("Autre");

    private final @NotNull String displayName;

    ProtectionType(@NotNull String displayName) {
        this.displayName = displayName;
    }

    public @NotNull String getDisplayName() {
        return displayName;
    }
}