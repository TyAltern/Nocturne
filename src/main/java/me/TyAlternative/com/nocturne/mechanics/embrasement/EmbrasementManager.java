package me.TyAlternative.com.nocturne.mechanics.embrasement;

import me.TyAlternative.com.nocturne.core.round.RoundContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;

/**
 * Gestionnaire des embrasements actifs pour une manche de jeu.
 *
 * <p>Un embrasement représente la potentielle mort d'un joueur : il sera éliminé
 * en fin de phase de Gameplay, sauf s'il est protégé.
 *
 * <p>Une instance par manche : créée dans le {@link RoundContext}.
 */
@SuppressWarnings("unused")
public final class EmbrasementManager {

    /** Map UUID joueur → cause de l'embrasement. */
    private final Map<UUID, EmbrasementCause> embrased = new LinkedHashMap<>();
    private final List<UUID> lipsBannedFlamme = new ArrayList<>();

    // -------------------------------------------------------------------------
    // Embrasement
    // -------------------------------------------------------------------------

    /**
     * Tente d'embraser le joueur identifié par {@code playerId}.
     *
     * <p>Si le joueur est déjà embrasé, l'entrée est mise à jour
     *    avec la nouvelle cause.
     *
     * @param playerId UUID du joueur à embraser
     * @param cause    source de l'embrasement
     * @return {@code true} si l'embrasement a été appliquée, {@code false} si le joueur l'était déjà
     */
    public boolean embrase(@NotNull UUID playerId, @NotNull EmbrasementCause cause, @Nullable UUID casterID) {
        if (casterID != null && lipsBannedFlamme.contains(casterID)) return false;
        boolean returnValue = !embrased.containsKey(playerId);

        embrased.put(playerId, cause);
        return returnValue;
    }

    /**
     * Retire l'embrasement d'un joueur sans l'éliminer.
     * Utilisé par certaines mécaniques de protection conditionnelle.
     *
     * @param playerId UUID du joueur
     */
    public void removeEmbrasement(@NotNull UUID playerId) {
        embrased.remove(playerId);
    }


    // -------------------------------------------------------------------------
    // Lecture
    // -------------------------------------------------------------------------

    public boolean addNewLipsBanFlamme(@NotNull UUID playerId) {
        return lipsBannedFlamme.add(playerId);
    }
    public boolean removeLipsBanFlamme(@NotNull UUID playerId) {
        return lipsBannedFlamme.remove(playerId);
    }

    /** {@code true} si le joueur est actuellement embrasé. */
    public boolean isEmbrased(@NotNull UUID playerId) {
        return embrased.containsKey(playerId);
    }

    /**
     * Retourne la cause de l'embrasement du joueur, ou {@code null}
     * s'il n'est pas embrasé.
     */
    public @Nullable EmbrasementCause getCause(@NotNull UUID playerId) {
        return embrased.get(playerId);
    }

    /**
     * Retourne une vue non-modifiable de tous les embrasements actifs
     * (UUID → cause).
     */
    public @NotNull @Unmodifiable Map<UUID, EmbrasementCause> getAll() {
        return Collections.unmodifiableMap(embrased);
    }

    /** Nombre de joueurs actuellement embrasés. */
    public int count() {
        return embrased.size();
    }

    /** Vide tous les embrasements. Appelé en début de manche via le {@code RoundContext}. */
    public void clear() {
        embrased.clear();
        lipsBannedFlamme.clear();
    }
}
