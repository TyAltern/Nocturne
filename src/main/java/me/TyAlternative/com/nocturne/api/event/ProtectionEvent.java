package me.TyAlternative.com.nocturne.api.event;

import me.TyAlternative.com.nocturne.mechanics.protection.ProtectionType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * Événement déclenché lorsqu'un joueur est sur le point d'être protégé.
 *
 * <p>Les capacités peuvent :
 * <ul>
 *   <li>Annuler la protection (le joueur ne sera pas protégé).</li>
 *   <li>Rediriger la protection vers un autre joueur via {@link #redirectTarget(UUID)}.</li>
 *   <li>Modifier le type de protection via {@link #setProtectionType(ProtectionType)}.</li>
 * </ul>
 *
 * <h2>Exemple — voler une protection</h2>
 * <pre>{@code
 * @Override
 * public void onProtection(ProtectionEvent event, Player self, NocturnePlayer np) {
 *     if (!event.getTargetId().equals(somePlayerId)) return;
 *     // Voler la protection : elle s'applique maintenant à nous
 *     event.redirectTarget(np.getPlayerId());
 * }
 * }</pre>
 */
public final class ProtectionEvent extends GameEvent<UUID> {



    private @NotNull UUID targetId;
    private final @Nullable UUID casterId;
    private @NotNull ProtectionType protectionType;

    /**
     * @param targetId       UUID du joueur qui va être protégé
     * @param casterId       UUID du joueur à l'origine de la protection, ou {@code null}
     * @param protectionType type de protection appliquée
     */
    public ProtectionEvent(
            @NotNull UUID targetId,
            @Nullable UUID casterId,
            @NotNull ProtectionType protectionType
    ) {
        this.targetId       = targetId;
        this.casterId       = casterId;
        this.protectionType = protectionType;
    }

    // -------------------------------------------------------------------------
    // Valeur principale
    // -------------------------------------------------------------------------

    /** UUID courant du joueur qui sera protégé. */
    @Override
    public @NotNull UUID getValue() {
        return targetId;
    }

    // -------------------------------------------------------------------------
    // Cible
    // -------------------------------------------------------------------------

    /** UUID courant du joueur qui sera protégé. */
    public @NotNull UUID getTargetId() {
        return targetId;
    }

    /**
     * Redirige la protection vers un autre joueur.
     *
     * @param newTarget UUID du nouveau bénéficiaire, jamais {@code null}
     */
    public void redirectTarget(@NotNull UUID newTarget) {
        this.targetId = newTarget;
    }

    // -------------------------------------------------------------------------
    // Lanceur
    // -------------------------------------------------------------------------

    /**
     * UUID du joueur à l'origine de la protection, ou {@code null} si automatique.
     */
    public @Nullable UUID getCasterId() {
        return casterId;
    }

    // -------------------------------------------------------------------------
    // Type
    // -------------------------------------------------------------------------

    /** Type de protection courant. */
    public @NotNull ProtectionType getProtectionType() {
        return protectionType;
    }

    /**
     * Modifie le type de protection.
     *
     * @param type nouveau type, jamais {@code null}
     */
    public void setProtectionType(@NotNull ProtectionType type) {
        this.protectionType = type;
    }

}
