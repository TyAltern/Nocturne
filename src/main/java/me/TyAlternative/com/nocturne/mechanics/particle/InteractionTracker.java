package me.TyAlternative.com.nocturne.mechanics.particle;

import me.TyAlternative.com.nocturne.ability.impl.info.DiffractionAbility;
import me.TyAlternative.com.nocturne.ability.impl.info.RefractionAbility;
import me.TyAlternative.com.nocturne.core.round.RoundContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Registre des interactions entre joueurs pour la phase de Gameplay en cours.
 *
 * <p>Enregistre deux types d'événements distincts :</p>
 * <ul>
 *   <li><b>Casters</b> — joueurs ayant <em>utilisé</em> une capacité active sur quelqu'un
 *       (utilisé par {@link DiffractionAbility}).</li>
 *   <li><b>Targets</b> — joueurs ayant <em>été ciblés</em> par une capacité
 *       (utilisé par {@link RefractionAbility}).</li>
 * </ul>
 *
 * <p>Une instance est créée par manche dans le
 * {@link RoundContext} et réinitialisée automatiquement
 * à chaque nouvelle manche.</p>
 *
 * <p>Un même joueur peut apparaître plusieurs fois dans les listes si plusieurs
 * capacités le concernent dans la même manche, mais les capacités info filtrent
 * ces doublons au niveau de la planification des particules.</p>
 */
@SuppressWarnings({"unused"})
public final  class InteractionTracker {

    /** Joueurs ayant utilisé une capacité active (casters). */
    private final List<UUID> casters = new ArrayList<>();

    /** Joueurs ayant été ciblés par une capacité (targets). */
    private final List<UUID> targets = new ArrayList<>();

    // -------------------------------------------------------------------------
    // Enregistrement
    // -------------------------------------------------------------------------

    /**
     * Enregistre {@code casterId} comme ayant utilisé une capacité active.
     * Ignoré si l'UUID est déjà présent (un seul enregistrement par joueur).
     *
     * @param casterId UUID du joueur ayant utilisé la capacité
     */
    public void recordCaster(@NotNull UUID casterId) {
        casters.add(casterId);
    }

    /**
     * Enregistre {@code targetId} comme ayant été ciblé par une capacité.
     * Ignoré si l'UUID est déjà présent.
     *
     * @param targetId UUID du joueur ciblé
     */
    public void recordTarget(@NotNull UUID targetId) {
        targets.add(targetId);
    }

    // -------------------------------------------------------------------------
    // Lecture
    // -------------------------------------------------------------------------

    /**
     * Retourne une vue non-modifiable des joueurs ayant utilisé une capacité active
     * lors de cette manche.
     */
    public @NotNull @Unmodifiable List<UUID> getCasters() {
        return Collections.unmodifiableList(casters);
    }

    /**
     * Retourne une vue non-modifiable des joueurs ayant été ciblés par une capacité
     * lors de cette manche.
     */
    public @NotNull @Unmodifiable List<UUID> getTargets() {
        return Collections.unmodifiableList(targets);
    }

    /** {@code true} si au moins un caster a été enregistré. */
    public boolean hasCasters() {
        return !casters.isEmpty();
    }

    /** {@code true} si au moins une cible a été enregistrée. */
    public boolean hasTargets() {
        return !targets.isEmpty();
    }

    // -------------------------------------------------------------------------
    // Nettoyage
    // -------------------------------------------------------------------------

    /** Vide tous les enregistrements. Appelé en début de manche via le {@code RoundContext}. */
    public void clear() {
        casters.clear();
        targets.clear();
    }

}
