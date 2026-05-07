package me.TyAlternative.com.nocturne.mechanics.embrasement;

import me.TyAlternative.com.nocturne.api.event.EmbrasementEvent;
import me.TyAlternative.com.nocturne.api.event.GameEventBus;
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

    private final GameEventBus eventBus;

    public EmbrasementManager(GameEventBus eventBus) {
        this.eventBus = eventBus;
    }

    // -------------------------------------------------------------------------
    // Embrasement
    // -------------------------------------------------------------------------

    /**
     * Tente d'embraser un joueur, en passant d'abord par le {@link GameEventBus}.
     *
     * <p>Ordre des opérations :
     * <ol>
     *   <li>Vérification de la bannière Lips (si le lanceur est banni, retour immédiat).</li>
     *   <li>Création et dispatch de l'{@link EmbrasementEvent}.</li>
     *   <li>Si annulé → retour {@code false}.</li>
     *   <li>Enregistrement de la cible finale (potentiellement redirigée).</li>
     * </ol>
     *
     * @param playerId UUID du joueur à embraser (cible initiale)
     * @param cause    source de l'embrasement
     * @param casterId UUID du lanceur, ou {@code null} si automatique
     * @return {@code true} si l'embrasement a été enregistré, {@code false} sinon
     */
    public boolean embrase(@NotNull UUID playerId, @NotNull EmbrasementCause cause, @Nullable UUID casterId) {
        if (casterId != null && lipsBannedFlamme.contains(casterId)) return false;

        EmbrasementEvent event = new EmbrasementEvent(playerId, casterId, cause);
        eventBus.fireEmbrasement(event);

        if (event.isCancelled()) return false;

        UUID finalTarget = event.getTargetId();
        EmbrasementCause finalCause = event.getCause();

        boolean isNew = !embrased.containsKey(finalTarget);
        embrased.put(finalTarget, finalCause);
        return isNew;
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
