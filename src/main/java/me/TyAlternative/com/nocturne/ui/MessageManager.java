package me.TyAlternative.com.nocturne.ui;

import me.TyAlternative.com.nocturne.api.role.Role;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Centralise la construction et l'envoi de tous les messages du plugin.
 *
 * <p>Aucune chaîne de caractères "display" ne doit se trouver dans les phases,
 * capacités ou managers — tout passe par ici.
 *
 * <h2>Responsabilités</h2>
 * <ul>
 *   <li>Message de présentation du rôle à l'assignation.</li>
 *   <li>Messages de broadcast (début/fin de phase, éliminations).</li>
 *   <li>Placeholder {@code {player}}, {@code {role}}, {@code {count}} dans les templates.</li>
 * </ul>
 */
public final class MessageManager {

    private static final Component SEPARATOR = Component.text(
            "§8§m          §r §8/ §eNocturne §8/ §m          §r"
    );

    private static final Component EMPTY = Component.empty();

    // -------------------------------------------------------------------------
    // Présentation du rôle
    // -------------------------------------------------------------------------

    /**
     * Envoie le message de présentation complet du rôle au joueur.
     *
     * <p>Structure :
     * <pre>
     * ─────────── / Nocturne / ───────────
     *
     * - Vous êtes [NomDuRôle]
     * - Objectif : [Description]
     *
     * - Vous disposez de X Capacité(s) :
     *   [NomCapacité] : [Description] (Type) [touche]
     *
     * - Vous portez X Malédiction(s) :
     *   [NomMalé] : [Description]
     * </pre>
     *
     * @param player joueur destinataire
     * @param role   rôle assigné à ce joueur
     */
    public void sendRolePresentation(@NotNull Player player, @NotNull Role role) {

    }
}
