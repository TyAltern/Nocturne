package me.TyAlternative.com.nocturne.api.ability;

import me.TyAlternative.com.nocturne.listener.PlayerInteractionListener;

/**
 * Définit le type d'action joueur qui déclenche l'exécution d'une capacité.
 *
 * <p>Le listener d'interaction ({@link PlayerInteractionListener})
 * traduit les événements Bukkit en {@code AbilityTrigger} et les délègue aux capacités concernées.
 */
public enum AbilityTrigger {

    /** Clic droit sur un autre joueur avec la main principale. */
    RIGHT_CLICK_PLAYER,

    /** Clic gauche sur un autre joueur avec la main principale. */
    LEFT_CLICK_PLAYER,

    /**
     * Appui simple sur la touche d'échange de main (F par défaut).
     * Déclenché uniquement si aucun double-appui n'est détecté dans la fenêtre de temps configurée.
     */
    SWAP_HAND,

    /**
     * Double appui rapide sur la touche d'échange de main (F par défaut).
     * Déclenché à la place de {@link #SWAP_HAND} si deux appuis sont détectés
     * dans la fenêtre de temps configurée.
     */
    DOUBLE_SWAP_HAND,

    /**
     * Capacité passive déclenchée automatiquement par le jeu selon le contexte.
     * Aucune action joueur n'est requise.
     */
    AUTOMATIC,

    /**
     * Capacité déclenchée à intervalle régulier, défini en ticks.
     * Nécessite de surcharger {@link Ability#getTickInterval()}.
     */
    TICKS,

    /**
     * Capacité déclenchée manuellement via une commande ou une interface dédiée.
     * Non liée à un événement Bukkit standard.
     */
    MANUAL
}
