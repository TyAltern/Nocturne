package me.TyAlternative.com.nocturne.ui;

import me.TyAlternative.com.nocturne.api.ability.Ability;
import me.TyAlternative.com.nocturne.api.ability.AbilityCategory;
import me.TyAlternative.com.nocturne.api.ability.AbilityTrigger;
import me.TyAlternative.com.nocturne.api.role.Role;
import me.TyAlternative.com.nocturne.config.GameSettings;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

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
@SuppressWarnings("unused")
public final class MessageManager {

    private static final Component SEPARATOR = Component.text(
            "§8§m          §r §8/ §eNocturne §8/ §m          §r"
    );

    private static final Component EMPTY = Component.empty();

    private final GameSettings settings;

    /**
     * @param settings configuration contenant le préfixe des messages
     */
    public MessageManager(@NotNull GameSettings settings) {
        this.settings = settings;
    }


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
        List<Ability> capacities = new ArrayList<>();
        List<Ability> effects    = new ArrayList<>();
        List<Ability> curses     = new ArrayList<>();

        for (Ability ability : role.getAbilities()) {
            if (ability.isHidden()) continue;
            switch (ability.getCategory()) {
                case CAPACITY -> capacities.add(ability);
                case EFFECT   -> effects.add(ability);
                case CURSE    -> curses.add(ability);
            }
        }

        player.sendMessage(EMPTY);
        player.sendMessage(SEPARATOR);
        player.sendMessage(EMPTY);
        player.sendMessage(Component.text("§r§8§l» §r§7Vous êtes " + role.getDisplayName()));
        player.sendMessage(Component.text("§r§8§l» §r§7Objectif : §r" + role.getDescription()));

        if (!capacities.isEmpty()) {
            player.sendMessage(EMPTY);
            player.sendMessage(Component.text(
                    "§r§8§l» §r§7Vous disposez de "
                            + AbilityCategory.CAPACITY.getColor().asHexString().replace("#", "§x")
                            + "§l" + capacities.size() + " Capacité" + (capacities.size() > 1 ? "s" : "") + "§r§7 :"
            ));
            for (Ability ability : capacities) {
                player.sendMessage(buildAbilityLine(ability, AbilityCategory.CAPACITY));
            }
        }

        if (!effects.isEmpty()) {
            player.sendMessage(EMPTY);
            player.sendMessage(Component.text(
                    "§r§8§l» §r§7Vous bénéficiez de "
                            + AbilityCategory.EFFECT.getColor().asHexString().replace("#", "§x")
                            + "§l" + effects.size() + " Effet" + (effects.size() > 1 ? "s" : "") + "§r§7 :"
            ));
            for (Ability ability : effects) {
                player.sendMessage(buildAbilityLine(ability, AbilityCategory.EFFECT));
            }
        }

        if (!curses.isEmpty()) {
            player.sendMessage(EMPTY);
            player.sendMessage(Component.text(
                    "§r§8§l» §r§7Vous portez "
                            + AbilityCategory.CURSE.getColor().asHexString().replace("#", "§x")
                            + "§l" + curses.size() + " Malédiction" + (curses.size() > 1 ? "s" : "") + "§r§7 :"
            ));
            for (Ability ability : curses) {
                player.sendMessage(buildAbilityLine(ability, AbilityCategory.CURSE));
            }
        }

        player.sendMessage(EMPTY);
        player.sendMessage(SEPARATOR);
        player.sendMessage(EMPTY);
    }

    // -------------------------------------------------------------------------
    // Broadcast
    // -------------------------------------------------------------------------

    /**
     * Préfixe un message avec le préfixe configuré et le retourne.
     *
     * @param message message brut (codes couleur Minecraft acceptés)
     * @return composant prêt à être diffusé
     */
    public @NotNull Component buildBroadcast(@NotNull String message) {
        return Component.text(settings.getPrefix() + "§r" + message);
    }

    /**
     * Remplace les placeholders {@code {player}} et {@code {role}} dans un template.
     *
     * @param template  chaîne contenant des placeholders
     * @param playerName nom du joueur
     * @param roleName   nom du rôle
     */
    public @NotNull String format(
            @NotNull String template,
            @NotNull String playerName,
            @NotNull String roleName
    ) {
        return template
                .replace("{player}", playerName)
                .replace("{role}", roleName);
    }

    // -------------------------------------------------------------------------
    // Construction des lignes de capacité
    // -------------------------------------------------------------------------

    private @NotNull Component buildAbilityLine(@NotNull Ability ability, @NotNull AbilityCategory category) {
        Component line = Component.text("  §n" + ability.getDisplayName()).color(category.getColor())
                .append(Component.text(
                        "§r§f : " + ability.getDescription()
                                + " §8(" + ability.getUseType().getDisplayName() + ")"
                ));

        // Ajouter le bouton de touche pour les capacités actives à swap
        if (ability.getTrigger() == AbilityTrigger.SWAP_HAND) {
            line = line.append(Component.text(" "))
                       .append(buildKeyButton("key.swapOffHand", false));
        } else if (ability.getTrigger() == AbilityTrigger.DOUBLE_SWAP_HAND) {
            line = line.append(Component.text(" "))
                    .append(buildKeyButton("key.swapOffHand", true));
        }

        return line;
    }

    /**
     * Construit un bouton de touche interactif avec la keybind Adventure.
     *
     * @param keybind    identifiant de la touche (ex: {@code "key.swapOffhand"})
     * @param doublePress {@code true} pour afficher "2x [touche]"
     */
    @SuppressWarnings("SameParameterValue")
    private @NotNull Component buildKeyButton(@NotNull String keybind, boolean doublePress) {
        Component key = Component.keybind(keybind)
                .style(Style.style(TextColor.color(255, 85, 255)));

        if (doublePress) {
            return Component.text("[2x ", Style.style(TextColor.color(255, 85, 255)))
                    .append(key)
                    .append(Component.text("]", Style.style(TextColor.color(255, 85, 255))));
        }
        return Component.text("[", Style.style(TextColor.color(255, 85, 255)))
                .append(key)
                .append(Component.text("]", Style.style(TextColor.color(255, 85, 255))));

    }

}
