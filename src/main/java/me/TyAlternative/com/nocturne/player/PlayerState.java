package me.TyAlternative.com.nocturne.player;

/**
 * État d'un joueur dans le cycle de vie de la partie.
 *
 * <p>Transitions valides :
 * <pre>
 * LOBBY → PLAYING → DEAD → LOBBY
 *                 ↘ DISCONNECTED → LOBBY
 * </pre>
 * </p>
 */
public enum PlayerState {

    /** En attente dans le lobby, hors partie. */
    LOBBY,

    /** En jeu, vivant et actif. */
    PLAYING,

    /** Éliminé, en mode spectateur. */
    DEAD,

    /** Déconnecté pendant la partie. Traité comme une élimination pour les mécaniques. */
    DISCONNECTED;

    /** {@code true} si le joueur participe activement à la partie en cours. */
    public boolean isAlive() {
        return this == PLAYING;
    }

    /** {@code true} si le joueur a été ou est en jeu (vivant ou mort, pas lobby). */
    public boolean isInGame() {
        return this == PLAYING || this == DEAD || this == DISCONNECTED;
    }
}
