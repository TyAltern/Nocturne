package me.TyAlternative.com.nocturne.ability;

import org.jetbrains.annotations.NotNull;

/**
 * Configuration immuable d'une limite d'utilisation pour une capacité.
 *
 * <p>Deux portées sont disponibles :
 * <ul>
 *   <li>{@link #perRound(int)} — le compteur se remet à zéro à chaque nouvelle manche.</li>
 *   <li>{@link #perGame(int)} — le compteur ne se remet jamais à zéro pendant la partie.</li>
 *   <li>{@link #unlimited()} — aucune limite d'utilisation.</li>
 * </ul>
 *
 * <p>Exemple d'usage dans une capacité :
 * <pre>{@code
 * // Dans le constructeur d'AbstractAbility :
 * setUsageLimit(UsageLimit.perRound(1)); // une utilisation par manche
 * }</pre>
 */
public class UsageLimit {

    /** Type de portée de la limite. */
    public enum Scope {
        /** Remise à zéro à chaque nouvelle manche. */
        PER_ROUND,
        /** Jamais remise à zéro pendant la partie. */
        PER_GAME
    }

    private static final UsageLimit UNLIMITED = new UsageLimit(null, -1);

    private final Scope scope;
    private final int maxUses;

    public UsageLimit(Scope scope, int maxUses) {
        this.scope = scope;
        this.maxUses = maxUses;
    }


    // -------------------------------------------------------------------------
    // Fabriques
    // -------------------------------------------------------------------------

    /**
     * Limite de {@code maxUses} utilisations par manche.
     *
     * @param maxUses nombre maximum d'utilisations par manche, doit être > 0
     * @throws IllegalArgumentException si {@code maxUses} ≤ 0
     */
    public static @NotNull UsageLimit perRound(int maxUses) {
        if (maxUses <= 0) {
            throw new IllegalArgumentException("maxUses doit être >x 0, récu : " + maxUses);
        }
        return new UsageLimit(Scope.PER_ROUND, maxUses);
    }

    /**
     * Limite de {@code maxUses} utilisations pour toute la partie.
     *
     * @param maxUses nombre maximum d'utilisations pour la partie, doit être &gt; 0
     * @throws IllegalArgumentException si {@code maxUses} ≤ 0
     */
    public static @NotNull UsageLimit perGame(int maxUses) {
        if (maxUses <= 0) {
            throw new IllegalArgumentException("maxUses doit être >x 0, récu : " + maxUses);
        }
        return new UsageLimit(Scope.PER_GAME, maxUses);
    }

    /** Aucune limite d'utilisation. Instance partagée (singleton). */
    public static @NotNull UsageLimit unlimited() {
        return UNLIMITED;
    }


    // -------------------------------------------------------------------------
    // Accesseurs
    // -------------------------------------------------------------------------

    /** Portée de cette limite, ou {@code null} si illimitée. */
    public Scope getScope() {
        return scope;
    }

    /**
     * Nombre maximum d'utilisations autorisées.
     * Retourne {@code -1} si la limite est illimitée.
     */
    public int getMaxUses() {
        return maxUses;
    }

    /** {@code true} si cette limite représente une utilisation illimitée. */
    public boolean isUnlimited() {
        return scope == null || maxUses < 0;
    }

    @Override
    public String toString() {
        if (isUnlimited()) return "Illimité";
        return maxUses + "/" + (scope == Scope.PER_ROUND ? "manche" : "partie");
    }
}
