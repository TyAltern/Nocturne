package me.TyAlternative.com.nocturne.api.ability;


import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Objet immuable encapsulant le contexte dans lequel une capacité est déclenchée.
 *
 * <p>Passé en paramètre à {@link Ability#canExecute} et {@link Ability#execute},
 * il évite de multiplier les signatures de méthodes tout en restant extensible.
 *
 * <p>Utiliser les fabriques statiques plutôt que le constructeur directement :
 * <pre>{@code
 * AbilityContext.withTarget(targetPlayer);
 * AbilityContext.withEmptyHand(targetPlayer);
 * AbilityContext.noTarget();
 * }</pre>
 */
public class AbilityContext {

    private final @Nullable Player target;
    private final boolean emptyHand;

    public AbilityContext(@Nullable Player target, boolean emptyHand) {
        this.target = target;
        this.emptyHand = emptyHand;
    }

    // -------------------------------------------------------------------------
    // Static Fabrics
    // -------------------------------------------------------------------------

    /**
     * Contexte avec une cible, main non-vide.
     *
     * @param target joueur ciblé par la capacité
     */
    public static @NotNull AbilityContext withTarget(@NotNull Player target) {
        return new AbilityContext(target, false);
    }


    /**
     * Contexte avec une cible et la main principale vide.
     * Utilisé pour les capacités nécessitant que le joueur n'ait rien en main.
     *
     * @param target joueur ciblé par la capacité
     */
    public static @NotNull AbilityContext withEmptyHand(@NotNull Player target) {
        return new AbilityContext(target, true);
    }

    /**
     * Contexte sans cible. Utilisé pour les capacités passives ou à déclenchement
     * automatique ne ciblant pas de joueur spécifique.
     */
    public static @NotNull AbilityContext noTarget() {
        return new AbilityContext(null, false);
    }


    // -------------------------------------------------------------------------
    // Getters
    // -------------------------------------------------------------------------

    /**
     * Joueur ciblé par la capacité, ou {@code null} si aucune cible.
     *
     * @see #hasTarget()
     */
    public @Nullable Player getTarget() {
        return target;
    }

    public boolean hasTarget() {
        return target != null;
    }

    public boolean isEmptyHand() {
        return emptyHand;
    }
}
