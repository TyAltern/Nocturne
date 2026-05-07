package me.TyAlternative.com.nocturne.api.event;

import me.TyAlternative.com.nocturne.api.ability.Ability;
import me.TyAlternative.com.nocturne.api.ability.AbilityContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Événement déclenché lorsqu'un joueur tente d'utiliser une capacité active,
 * après les vérifications de l'{@link me.TyAlternative.com.nocturne.ability.AbilityManager}
 * mais avant l'appel à {@link Ability#execute}.
 *
 * <p>Les capacités peuvent :
 * <ul>
 *   <li>Annuler le cast (la capacité n'est pas exécutée, la charge n'est pas consommée).</li>
 *   <li>Modifier le contexte via {@link #setContext(AbilityContext)} — changer la cible,
 *       forcer la main vide, etc.</li>
 *   <li>Forcer le mode drunk pour ce cast uniquement via {@link #setForceDrunk(boolean)}.</li>
 * </ul>
 *
 * <h2>Exemple — rediriger la cible d'une capacité</h2>
 * <pre>{@code
 * @Override
 * public void onAbilityCast(AbilityCastEvent event, Player self, NocturnePlayer np) {
 *     // Si quelqu'un cible le joueur protégé, rediriger vers une autre cible
 *     if (!event.getContext().hasTarget()) return;
 *     if (!event.getContext().getTarget().getUniqueId().equals(protectedId)) return;
 *     Player newTarget = findAlternativeTarget();
 *     if (newTarget == null) {
 *         event.cancel("§cCette cible est protégée !");
 *         return;
 *     }
 *     event.setContext(AbilityContext.withEmptyHand(newTarget));
 * }
 * }</pre>
 */
public final class AbilityCastEvent extends GameEvent<AbilityContext> {

    private final Ability ability;
    private @NotNull AbilityContext context;
    private boolean forceDrunk = false;

    /**
     * @param ability capacité qui va être exécutée
     * @param context contexte d'exécution initial (cible, main vide)
     */
    public AbilityCastEvent(
            @NotNull Ability ability,
            @NotNull AbilityContext context
    ) {
        this.ability = ability;
        this.context = context;
    }

    // -------------------------------------------------------------------------
    // Valeur principale
    // -------------------------------------------------------------------------

    /** Contexte courant du cast (peut avoir été modifié). */
    @Override
    public @Nullable AbilityContext getValue() {
        return context;
    }

    // -------------------------------------------------------------------------
    // Capacité
    // -------------------------------------------------------------------------

    /**
     * Capacité qui va être exécutée.
     * Non modifiable : une interception ne peut pas changer quelle capacité est lancée,
     * seulement comment elle l'est.
     */
    public @NotNull Ability getAbility() {
        return ability;
    }

    // -------------------------------------------------------------------------
    // Contexte
    // -------------------------------------------------------------------------

    /** Contexte courant du cast. */
    public @NotNull AbilityContext getContext() {
        return context;
    }

    /**
     * Remplace le contexte d'exécution.
     * Permet de changer la cible, l'état de la main, etc.
     *
     * @param context nouveau contexte, jamais {@code null}
     */
    public void setContext(@NotNull AbilityContext context) {
        this.context = context;
    }

    // -------------------------------------------------------------------------
    // Mode drunk forcé
    // -------------------------------------------------------------------------

    /**
     * {@code true} si ce cast doit être forcé en mode drunk,
     * indépendamment de l'état réel de la capacité.
     */
    public boolean isForceDrunk() {
        return forceDrunk;
    }

    /**
     * Force ou non le mode drunk pour ce cast uniquement.
     * N'affecte pas l'état drunk permanent de la capacité.
     *
     * @param forceDrunk {@code true} pour forcer le drunk sur ce cast
     */
    public void setForceDrunk(boolean forceDrunk) {
        this.forceDrunk = forceDrunk;
    }


}
