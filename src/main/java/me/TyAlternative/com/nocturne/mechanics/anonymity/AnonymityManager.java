package me.TyAlternative.com.nocturne.mechanics.anonymity;

import me.TyAlternative.com.nocturne.config.GameSettings;
import me.TyAlternative.com.nocturne.player.PlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.NotNull;

/**
 * Gère l'anonymat des joueurs pendant les phases de Gameplay.
 *
 * <h2>Deux mécaniques</h2>
 * <ul>
 *   <li><b>Skins</b> : délégué à SkinsRestorer via {@code /skin set <skin> -all}
 *       et {@code /skin clear -all}.</li>
 *   <li><b>Nametags</b> : masqués via une équipe Scoreboard avec l'option
 *       {@link Team.OptionStatus#NEVER} sur la visibilité des nametags.</li>
 * </ul>
 *
 * <p>Appeler {@link #restoreAll()} en fin de phase de Gameplay pour rétablir
 * l'apparence réelle des joueurs avant le vote.
 */
@SuppressWarnings("unused")
public final class AnonymityManager {

    private static final String TEAM_NAME = "nocturne_hidden";

    private final PlayerManager playerManager;
    private final GameSettings settings;

    /**
     * @param playerManager gestionnaire des joueurs pour itérer sur les vivants
     * @param settings      configuration (skin par défaut)
     */
    public AnonymityManager(
            @NotNull PlayerManager playerManager,
            @NotNull GameSettings settings
    ) {
        this.playerManager = playerManager;
        this.settings = settings;
    }

    // -------------------------------------------------------------------------
    // Skins
    // -------------------------------------------------------------------------

    /**
     * Remplace le skin de tous les joueurs vivants par le skin par défaut configuré.
     * Nécessite SkinsRestorer.
     */
    public void hideAllSkins() {
        String skin = settings.getDefaultSkin();
        Bukkit.dispatchCommand(
                Bukkit.getConsoleSender(),
                "skin set " + skin + " -all"
        );
    }

    /**
     * Restaure le skin réel de tous les joueurs.
     */
    public void showAllSkins() {
        Bukkit.dispatchCommand(
                Bukkit.getConsoleSender(),
                "skin clear -all"
        );
    }

    // -------------------------------------------------------------------------
    // Nametags
    // -------------------------------------------------------------------------

    /**
     * Masque les nametags de tous les joueurs vivants via une équipe Scoreboard.
     */
    public void hideAllNametags() {
        Team team = getOrCreateTeam();
        team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);

        for (Player player : playerManager.getAlivePlayers()) {
            if (!team.hasEntry(player.getName())) team.addEntry(player.getName());
        }
    }

    /**
     * Restaure la visibilité des nametags en retirant les joueurs de l'équipe.
     */
    public void showAllNametags() {
        Team team = getOrCreateTeam();
        for (Player player : playerManager.getAlivePlayers()) {
            team.removeEntry(player.getName());
        }
    }

    // -------------------------------------------------------------------------
    // Restauration complète
    // -------------------------------------------------------------------------

    /**
     * Restaure skins et nametags pour tous les joueurs.
     * Appelé en fin de phase de Gameplay, avant le début du vote.
     */
    public void restoreAll() {
        showAllSkins();
        showAllNametags();
    }

    // -------------------------------------------------------------------------
    // Utilitaires privés
    // -------------------------------------------------------------------------

    private @NotNull Team getOrCreateTeam() {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        Team team = scoreboard.getTeam(TEAM_NAME);
        if (team == null) team = scoreboard.registerNewTeam(TEAM_NAME);
        return team;
    }
}
