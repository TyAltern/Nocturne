package me.TyAlternative.com.nocturne.mechanics.ability;

import me.TyAlternative.com.nocturne.ability.AbstractAbility;
import me.TyAlternative.com.nocturne.ability.impl.info.*;
//import me.TyAlternative.com.nocturne.ability.impl.flamme.*;
//import me.TyAlternative.com.nocturne.ability.impl.misc.*;
//import me.TyAlternative.com.nocturne.ability.impl.protection.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

/**
 * Réservoir de capacités aléatoires pour l'Incandescence du Tison.
 *
 * <p>Maintient une liste de {@link Supplier} de capacités. À chaque appel à
 * {@link #draw(String)}, une capacité différente de la précédente est instanciée.
 * Chaque manche, le Tison reçoit une nouvelle instance fraîche garantissant
 * un état isolé.
 *
 * <h2>Capacités disponibles</h2>
 * Le pool inclut des capacités de Bâtons (protection, info, misc) et de Flammes.
 * Les capacités de l'Incandescence ne peuvent pas être drunk (le Tison lui-même
 * peut l'être, mais les capacités tirées fonctionnent normalement).
 *
 * <h2>Garantie de variété</h2>
 * {@link #draw(String)} exclut l'ID de la dernière capacité tirée. Si le pool
 * ne contient qu'une seule entrée, la même est retournée.
 */

public final class RandomAbilityPool {


    private final Random random = new Random();

    /** Liste des fabriques de capacités disponibles. */
    private final List<Supplier<AbstractAbility>> suppliers;

    public RandomAbilityPool() {
        suppliers = new ArrayList<>();
        registerAll();
    }

    // -------------------------------------------------------------------------
    // API publique
    // -------------------------------------------------------------------------

    /**
     * Tire une capacité aléatoire différente de {@code excludedId}.
     *
     * @param excludedId ID de la dernière capacité (exclu du tirage), ou {@code null}
     * @return nouvelle instance de capacité prête à l'emploi
     */
    public @NotNull AbstractAbility draw(@Nullable String excludedId) {
        if (suppliers.size() == 1) {
            return suppliers.getFirst().get();
        }

        // Filtrer l'ID exclu
        List<Supplier<AbstractAbility>> candidates = new ArrayList<>();
        for (Supplier<AbstractAbility> supplier : suppliers) {
            AbstractAbility sample = supplier.get();
            if (!sample.getId().equals(excludedId)) candidates.add(supplier);
        }

        if (candidates.isEmpty()) {
            candidates = suppliers; // fallback : tous disponibles
        }
        return candidates.get(random.nextInt(candidates.size())).get();
    }

    /** Nombre de capacités disponibles dans le pool. */
    public int size() {
        return suppliers.size();
    }


    // -------------------------------------------------------------------------
    // Enregistrement des capacités disponibles
    // -------------------------------------------------------------------------

    private void registerAll() {
        // Flammes
//        suppliers.add(EmbrasementAbility::new);
//        suppliers.add(PoudreChemineeAbility::new);
//        suppliers.add(RayonnementAbility::new);

        // Protection
//        suppliers.add(BriseAbility::new);
//        suppliers.add(AlizeAbility::new);
//        suppliers.add(AquilonAbility::new);
//        suppliers.add(AusterAbility::new);

        // Info
        suppliers.add(DiscernementMatinalAbility::new);
        suppliers.add(EchosInteractionAbility::new);
        suppliers.add(OmbresResiduellesAbility::new);
        suppliers.add(ReverberationLumineuseAbility::new);

        // Misc
//        suppliers.add(CicatricesAbility::new);
//        suppliers.add(CorrosionAbility::new);
//        suppliers.add(SolitudeMortelleAbility::new);
    }

}
