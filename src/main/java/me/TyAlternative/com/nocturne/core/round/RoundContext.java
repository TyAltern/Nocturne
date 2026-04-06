package me.TyAlternative.com.nocturne.core.round;

import me.TyAlternative.com.nocturne.mechanics.disparition.DisparitionManager;
import me.TyAlternative.com.nocturne.mechanics.embrasement.EmbrasementManager;
import me.TyAlternative.com.nocturne.mechanics.protection.ProtectionManager;
import me.TyAlternative.com.nocturne.core.NocturneGame;
import me.TyAlternative.com.nocturne.core.phase.PhaseContext;
import org.jetbrains.annotations.NotNull;

/**
 * Encapsule l'état mutable d'une manche de jeu.
 *
 * <p>Une nouvelle instance est créée à chaque début de manche par le
 * {@link NocturneGame}, et passée au {@link PhaseContext}.
 * Cela garantit qu'aucune donnée de manche (embrasements, protections, disparitions)
 * ne persiste accidentellement d'une manche à l'autre.
 *
 * <p>Les managers de mécaniques sont des sous-composants du {@code RoundContext}
 * plutôt que des singletons du {@code NocturneGame}, pour renforcer l'isolation
 * et faciliter les tests.
 */
@SuppressWarnings("unused")
public final class RoundContext {
    private final int roundNumber;
    private final EmbrasementManager embrasementManager;
    private final ProtectionManager protectionManager;
    private final DisparitionManager disparitionManager;

    /**
     * @param roundNumber         numéro de la manche (commence à 1)
     * @param embrasementManager  gestionnaire des embrasements pour cette manche
     * @param protectionManager   gestionnaire des protections pour cette manche
     * @param disparitionManager  gestionnaire des disparitions pour cette manche
     */
    public RoundContext(
            int roundNumber,
            @NotNull EmbrasementManager embrasementManager,
            @NotNull ProtectionManager protectionManager,
            @NotNull DisparitionManager disparitionManager
    ) {
        this.roundNumber = roundNumber;
        this.embrasementManager = embrasementManager;
        this.protectionManager = protectionManager;
        this.disparitionManager = disparitionManager;
    }

    /** Numéro de la manche courante, commence à {@code 1}. */
    public int getRoundNumber() {
        return roundNumber;
    }

    /** Gestionnaire des embrasements actifs pour cette manche. */
    public @NotNull EmbrasementManager getEmbrasementManager() {
        return embrasementManager;
    }

    /** Gestionnaire des protections actives pour cette manche. */
    public @NotNull ProtectionManager getProtectionManager() {
        return protectionManager;
    }

    /** Gestionnaire des disparitions actives pour cette manche. */
    public @NotNull DisparitionManager getDisparitionManager() {
        return disparitionManager;
    }
}
