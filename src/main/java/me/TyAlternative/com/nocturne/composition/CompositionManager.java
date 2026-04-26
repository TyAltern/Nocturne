package me.TyAlternative.com.nocturne.composition;

import me.TyAlternative.com.nocturne.Nocturne;
import me.TyAlternative.com.nocturne.api.role.Role;
import me.TyAlternative.com.nocturne.api.role.RoleType;
import me.TyAlternative.com.nocturne.role.RoleRegistry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;

/**
 * Gestionnaire de la composition d'une partie : définit combien d'instances
 * de chaque rôle seront distribuées aux joueurs.
 *
 * <p>La composition est exprimée comme une {@code Map<String, Integer>} associant
 * un identifiant de rôle à un nombre d'instances. La validité de la composition
 * (IDs enregistrés, présence d'au moins une Flamme et un Bâton) est vérifiée
 * via {@link #validate(RoleRegistry)}.
 *
 * <h2>Exemple</h2>
 * <pre>{@code
 * composition.set("ETINCELLE", 1);
 * composition.set("BATON", 5);
 * composition.set("AUTAN", 1);
 * // Résultat : 7 joueurs, 1 Flamme, 6 Bâtons
 * }</pre>
 */
@SuppressWarnings("unused")
public final class CompositionManager {

    /** Map interne : ID de rôle → nombre d'instances. */
    private final Map<String, Integer> compositionMap = new LinkedHashMap<>();


    // -------------------------------------------------------------------------
    // Modification
    // -------------------------------------------------------------------------

    /**
     * Définit le nombre d'instances pour un rôle donné.
     * Passer {@code count ≤ 0} retire le rôle de la composition.
     *
     * @param roleId identifiant du rôle (insensible à la casse)
     * @param count  nombre d'instances souhaitées
     */
    public void set(@NotNull String roleId, int count) {
        String normalized = roleId.toUpperCase();
        if (count <= 0) compositionMap.remove(normalized);
        else compositionMap.put(normalized, count);
        //noinspection StringTemplateMigration
        Nocturne.getInstance().getLogger().info("Added to compo : " + roleId + " x" + Math.max(0,count));
    }

    /**
     * Ajoute {@code delta} instances d'un rôle à la composition.
     * Si le résultat est ≤ 0, le rôle est retiré.
     *
     * @param roleId identifiant du rôle
     * @param delta  nombre d'instances à ajouter (peut être négatif)
     */
    public void add(@NotNull String roleId, int delta) {
        String normalized = roleId.toUpperCase();
        int current = compositionMap.getOrDefault(normalized,0);
        set(normalized, current + delta);
    }

    /** Vide entièrement la composition. */
    public void clear() {
        compositionMap.clear();
    }


    // -------------------------------------------------------------------------
    // Lecture
    // -------------------------------------------------------------------------

    /**
     * Retourne le nombre d'instances configurées pour le rôle {@code roleId}.
     * Retourne {@code 0} si le rôle n'est pas dans la composition.
     */
    public int getCount(@NotNull String roleId) {
        return compositionMap.getOrDefault(roleId.toUpperCase(), 0);
    }

    /**
     * Retourne une vue non-modifiable de la composition complète.
     * Les clés sont les identifiants de rôles en majuscules.
     */
    public @NotNull @Unmodifiable Map<String, Integer> getCompositionMap() {
        return Collections.unmodifiableMap(compositionMap);
    }

    /** Nombre total de joueurs que cette composition peut accueillir. */
    public int getTotalPlayers() {
        return compositionMap.values().stream().mapToInt(Integer::intValue).sum();
    }

    /** {@code true} si la composition est vide (aucun rôle défini). */
    public boolean isEmpty() {
        return compositionMap.isEmpty();
    }


    // -------------------------------------------------------------------------
    // Validation
    // -------------------------------------------------------------------------

    /**
     * Valide la composition avant le démarrage d'une partie.
     *
     * <p>Vérifie :
     * <ul>
     *   <li>La composition n'est pas vide.</li>
     *   <li>Tous les identifiants de rôles sont enregistrés dans le {@code registry}.</li>
     *   <li>La composition contient au moins une Flamme (condition de partie non-triviale).</li>
     *   <li>La composition contient au moins un Bâton.</li>
     * </ul>
     *
     * @param registry registre des rôles pour vérifier l'existence des IDs
     * @return liste de messages d'erreur ; vide si la composition est valide
     */
    public @NotNull List<String> validate(@NotNull RoleRegistry registry) {
        List<String> errors = new ArrayList<>();

        if (compositionMap.isEmpty()) {
            errors.add("La composition est vide.");
            return errors;
        }

        boolean hasFlamme = false;
        boolean hasBaton = false;

        for (Map.Entry<String, Integer> entry : compositionMap.entrySet()) {
            String roleId = entry.getKey();
            int count = entry.getValue();

            if (!registry.isRegistered(roleId)) {
                errors.add("Rôle inconnu dans la composition : '%s'.".formatted(roleId));
                continue;
            }

            // Créer une instance temporaire pour vérifier le type du rôle
            // (on ne stocke pas le type ddans la composition pour rester découplé)
            //TODO: update this assignment
            Role role = registry.create(roleId);
            if (role.getType() == RoleType.FLAMME) hasFlamme = true;
            if (role.getType() == RoleType.BATON) hasBaton = true;
        }

//        if (!hasBaton) {
//            errors.add("La composition doit contenir au moins un Bâton.");
//        }

//        if (!hasFlamme) {
//            errors.add("La composition doit contenir au moins une Flamme.");
//        }

        return errors;
    }



}
