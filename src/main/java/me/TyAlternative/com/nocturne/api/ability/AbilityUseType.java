package me.TyAlternative.com.nocturne.api.ability;

import org.jetbrains.annotations.NotNull;

/**
 * Décrit le mode d'utilisation d'une capacité, influençant l'affichage
 * dans l'action bar et la présentation du rôle.
 */
public enum AbilityUseType {

    /** Le joueur doit effectuer une action explicite pour déclencher la capacité. */
    ACTIVE("Actif"),

    /** La capacité s'applique en permanence sans intervention du joueur. */
    PASSIVE("Passif"),

    /** La capacité peut être activée ou désactivée par le joueur. */
    TOGGLE("Toggle"),

    /** La capacité n'est utilisable que pendant la phase de vote. */
    VOTE("Vote");

    private final String displayName;

    AbilityUseType(@NotNull String displayName) {
        this.displayName = displayName;
    }

    /** Nom affiché dans l'interface joueur. */
    public @NotNull String getDisplayName() {
        return displayName;
    }
}
