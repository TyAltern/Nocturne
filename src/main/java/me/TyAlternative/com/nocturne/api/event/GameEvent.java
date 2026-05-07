package me.TyAlternative.com.nocturne.api.event;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Événement de jeu interceptable et modifiable par les capacités.
 *
 * <p>Chaque événement peut être :
 * <ul>
 *   <li><b>Annulé</b> — l'action ne se produit pas (ex: embrasement bloqué).</li>
 *   <li><b>Redirigé</b> — la cible est remplacée (ex: vote dévié).</li>
 *   <li><b>Modifié</b> — des données internes sont changées (ex: poids de vote).</li>
 * </ul>
 *
 * <p>Le champ {@link #cancelReason} est optionnel : s'il est fourni, un message
 * est envoyé à l'instigateur de l'action annulée.
 *
 * <p>Les capacités reçoivent le même objet dans l'ordre de leur enregistrement.
 * Une fois {@link #setCancelled(boolean)} appelé, les capacités suivantes voient
 * {@link #isCancelled()} à {@code true} mais peuvent encore le lever.
 *
 * @param <T> type de la valeur principale transportée par l'événement
 */
public abstract class GameEvent<T> {

    private boolean cancelled = false;
    private @Nullable String cancelReason = null;

    // -------------------------------------------------------------------------
    // Valeur principale
    // -------------------------------------------------------------------------

    /**
     * Retourne la valeur courante de l'événement (peut avoir été modifiée).
     */
    public abstract @Nullable T getValue();

    // -------------------------------------------------------------------------
    // Annulation
    // -------------------------------------------------------------------------

    /**
     * Annule l'événement sans message explicatif.
     */
    public final void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    /**
     * Annule l'événement avec un message explicatif (envoyé à l'instigateur).
     *
     * @param reason message à afficher, codes couleur Minecraft acceptés
     */
    public final void cancelWithReason(@NotNull String reason) {
        this.cancelled = true;
        this.cancelReason = reason;
    }

    /** {@code true} si l'événement a été annulé par au moins une capacité. */
    public final boolean isCancelled() {
        return cancelled;
    }

    /**
     * Message d'annulation à envoyer à l'instigateur, ou {@code null} si aucun.
     */
    public final @Nullable String getCancelReason() {
        return cancelReason;
    }

}


