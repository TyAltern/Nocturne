package me.TyAlternative.com.nocturne.role;

import me.TyAlternative.com.nocturne.api.role.Role;
import me.TyAlternative.com.nocturne.api.role.RoleFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;
import java.util.logging.Logger;

/**
 * Registre centralisé des fabriques de rôles.
 *
 * <p>Contrairement à l'ancienne implémentation qui stockait des instances singleton,
 * ce registre stocke des {@link RoleFactory} : chaque appel à {@link #create(String)}
 * produit une instance fraîche et isolée, garantissant qu'aucun état n'est partagé
 * entre deux joueurs ayant le même rôle.
 *
 * <h2>Enregistrement d'un rôle</h2>
 * <pre>{@code
 * registry.register("ETINCELLE", Etincelle::new);
 * }</pre>
 *
 * <h2>Création d'une instance à la distribution</h2>
 * <pre>{@code
 * Role role = registry.create("ETINCELLE"); // nouvelle instance à chaque appel
 * }</pre>
 */
@SuppressWarnings("unused")
public final class RoleRegistry {

    private final Logger logger;

    /** Map principale : ID du rôle → fabrique. */
    private final Map<String, RoleFactory> factories = new LinkedHashMap<>();

    /**
     * @param logger logger du plugin, utilisé pour tracer les enregistrements
     */
    public RoleRegistry(@NotNull Logger logger) {
        this.logger = logger;
    }

    // -------------------------------------------------------------------------
    // Enregistrement
    // -------------------------------------------------------------------------

    /**
     * Enregistre une fabrique pour le rôle identifié par {@code roleId}.
     *
     * <p>L'identifiant est normalisé en majuscules avant stockage.
     *
     * @param roleId  identifiant unique du rôle (ex: {@code "ETINCELLE"})
     * @param factory fabrique produisant une nouvelle instance à chaque appel
     * @throws IllegalArgumentException si un rôle avec cet identifiant est déjà enregistré
     * @throws NullPointerException     si {@code roleId} ou {@code factory} est {@code null}
     */

    public void register(@NotNull String roleId, @NotNull RoleFactory factory) {
        String normalizedId = roleId.toUpperCase();

        if (factories.containsKey(normalizedId)) {
            throw new IllegalArgumentException(
                    "Un rôle avec l'identifiant '%s' est déjà enregistré.".formatted(normalizedId)
            );
        }

        factories.put(normalizedId, factory);
        logger.info("  → Rôle enregistré : " + normalizedId);
    }

    // -------------------------------------------------------------------------
    // Accès
    // -------------------------------------------------------------------------

    /**
     * Crée et retourne une nouvelle instance du rôle identifié par {@code roleId}.
     *
     * @param roleId identifiant du rôle (insensible à la casse)
     * @return instance fraîche du rôle, jamais {@code null}
     * @throws NoSuchElementException si aucun rôle n'est enregistré sous cet identifiant
     */
    public @NotNull Role create(@NotNull String roleId) {
        String normalizedId = roleId.toUpperCase();
        RoleFactory factory = factories.get(normalizedId);

        if (factory == null) {
            throw new NoSuchElementException(
                    "Aucun rôle enregistré avec l'identifiant '%s'.".formatted(normalizedId)
            );
        }

        return factory.create();
    }


    /**
     * {@code true} si un rôle est enregistré sous l'identifiant {@code roleId}.
     *
     * @param roleId identifiant à vérifier (insensible à la casse)
     */
    public boolean isRegistered(@NotNull String roleId) {
        return factories.containsKey(roleId.toUpperCase());
    }

    /**
     * Retourne l'ensemble des identifiants de rôles enregistrés.
     * L'ordre de retour correspond à l'ordre d'enregistrement.
     */
    public @NotNull @Unmodifiable Set<String> getRegisteredIds() {
        return Collections.unmodifiableSet(factories.keySet());
    }

    /**
     * Retourne une collection de toutes les fabriques enregistrées.
     * Utilisé par le GUI de composition pour afficher tous les rôles disponibles.
     * L'ordre correspond à l'ordre d'enregistrement.
     */
    public @NotNull @Unmodifiable Collection<RoleFactory> getAllFactories() {
        return Collections.unmodifiableCollection(factories.values());
    }


    /**
     * Retourne une instance de chaque rôle enregistré (une par factory).
     * Utilisé par le GUI de composition. Ne pas utiliser ces instances en jeu :
     * passer par {@link #create(String)} pour obtenir une instance dédiée à un joueur.
     */
    public @NotNull List<Role> createAll() {
        List<Role> result = new ArrayList<>();
        for (RoleFactory factory : factories.values()) {
            result.add(factory.create());
        }
        return result;
    }

    /** Nombre de rôles enregistrés. */
    public int count() {
        return factories.size();
    }
}
