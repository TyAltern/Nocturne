package me.TyAlternative.com.nocturne.mechanics.protection;

import me.TyAlternative.com.nocturne.core.round.RoundContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Gestionnaire des protections actives pour une manche de jeu.
 *
 * <p>Une protection empêche un joueur d'être éliminé par Embrasement.
 *
 * <p>Une instance par manche, créée dans le {@link RoundContext}.
 */
@SuppressWarnings("unused")
public final class ProtectionManager {

    /** Map UUID joueur -> type de protection active. */
    private final Map<UUID, ProtectionType> protections = new LinkedHashMap<>();

    // -------------------------------------------------------------------------
    // Protection
    // -------------------------------------------------------------------------

    /**
     * Tente de protéger le joueur identifié par {@code playerId}.
     *
     * <p>Si le joueur est déjà protégé, l'entrée est mise à jour
     *    avec la nouvelle protection.
     *
     * @param playerId UUID du joueur à protéger
     * @param type     source de la protection
     * @return {@code true} si la protection a été appliquée, {@code false} si le joueur l'était déjà
     */
    public boolean protect(@NotNull UUID playerId, @NotNull ProtectionType type) {
        boolean returnValue = !protections.containsKey(playerId);
        protections.put(playerId, type);
        return returnValue;
    }

    /**
     * Retire la protection du joueur identifié par {@code playerId}, quelle qu'en soit la source.
     *
     * @param playerId UUID du joueur
     */
    public void removeProtection(@NotNull UUID playerId) {
        protections.remove(playerId);
    }

    /**
     * Retire la protection du joueur uniquement si elle correspond au type donné.
     * Sans effet si le joueur n'est pas protégé ou si la source ne correspond pas.
     *
     * @param playerId UUID du joueur
     * @param type     type de protection à retirer
     */
    public void removeProtectionByType(@NotNull UUID playerId, @NotNull ProtectionType type) {
        protections.remove(playerId, type);
    }

    /**
     * Retire toutes les protections d'un type donné, pour tous les joueurs.
     * Utilisé par certaines mécaniques annulant une protection collective.
     *
     * @param type type de protection à retirer globalement
     */
    public void removeAllByType(@NotNull ProtectionType type) {
        protections.values().removeIf(t -> t == type);
    }

    // -------------------------------------------------------------------------
    // Lecture
    // -------------------------------------------------------------------------

    /** {@code true} si le joueur bénéficie d'une protection active. */
    public boolean isProtected(@NotNull UUID playerId) {
        return protections.containsKey(playerId);
    }

    /**
     * Retourne le type de protection active du joueur, ou {@code null}
     * s'il n'est pas protégé.
     */
    public @Nullable ProtectionType getProtectionType(@NotNull UUID playerId) {
        return protections.get(playerId);
    }

    /**
     * Retourne une vue non-modifiable de toutes les protections actives
     * (UUID joueur → type de protection).
     */
    public @NotNull @Unmodifiable Map<UUID, ProtectionType> getAll() {
        return Collections.unmodifiableMap(protections);
    }

    /** Nombre de joueurs actuellement protégés. */
    public int count() {
        return protections.size();
    }

    /** Vide toutes les protections. Appelé via le {@code RoundContext} en début de manche. */
    public void clear() {
        protections.clear();
    }
}
