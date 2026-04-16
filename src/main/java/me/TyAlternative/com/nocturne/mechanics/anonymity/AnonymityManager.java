package me.TyAlternative.com.nocturne.mechanics.anonymity;

import me.TyAlternative.com.nocturne.Nocturne;
import me.TyAlternative.com.nocturne.config.GameSettings;
import me.TyAlternative.com.nocturne.player.NocturnePlayer;
import me.TyAlternative.com.nocturne.player.PlayerManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.Transformation;
import org.jetbrains.annotations.NotNull;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

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
@SuppressWarnings({"unused", "DataFlowIssue"})
public final class AnonymityManager {

    private static final String TEAM_NAME = "nocturne_hidden";

    private final PlayerManager playerManager;
    private final GameSettings settings;

    // Maps pour stocker les configurations
    private final Map<UUID, Map<UUID, NameTag>> nameTagConfigs;

    // Map pour stocker les TextDisplay entities : ViewerUUID -> TargetUUID -> TextDisplay
    private final Map<UUID, Map<UUID, TextDisplay>> displayEntities;

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
        this.nameTagConfigs = new ConcurrentHashMap<>();
        this.displayEntities = new ConcurrentHashMap<>();
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
     * Remplace le skin d'un joueur vivant par le skin par défaut configuré.
     * Nécessite SkinsRestorer.
     */
    public void hideSkin(@NotNull Player player) {
        String skin = settings.getDefaultSkin();
        Bukkit.dispatchCommand(
                Bukkit.getConsoleSender(),
                "skin set " + skin + " " + player.getName()
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

    /**
     * Restaure le skin réel d'un joueur.
     */
    public void showSkin(@NotNull Player player) {
        Bukkit.dispatchCommand(
                Bukkit.getConsoleSender(),
                "skin clear " + player.getName()
        );
    }

    // -------------------------------------------------------------------------
    // Nametags
    // -------------------------------------------------------------------------

    /**
     * Masque les nametags classique de minecraft de tous les joueurs vivants via une équipe Scoreboard (à ne pas utiliser en jeu).
     */
    public void hideAllNametagsEnd() {
        Team team = getOrCreateTeam();
        team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);

        for (Player player : playerManager.getAlivePlayers()) {
            if (!team.hasEntry(player.getName())) team.addEntry(player.getName());
        }
    }

    /**
     * Masque le nametag classique de minecraft du joueur ciblé (à ne pas utiliser en jeu).
     *
     * @param nocturnePlayer donnée nocturne du joueur à cacher
     */
    public void hidePlayerNametagEnd(@NotNull NocturnePlayer nocturnePlayer) {
        Team team = getOrCreateTeam();
        team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);

        Player player = nocturnePlayer.getPlayer();
        if (player == null) return;
        String playerName = player.getName();
        if (!team.hasEntry(playerName)) team.addEntry(playerName);
    }

    /**
     * Restaure la visibilité des nametags classique de minecraft en retirant les joueurs de l'équipe (à ne pas utiliser en jeu).
     */
    public void showAllNametagsEnd() {
        Team team = getOrCreateTeam();
        for (Player player : playerManager.getAlivePlayers()) {
            team.removeEntry(player.getName());
        }
    }

    /**
     * Restaure la visibilité du nametag classique de minecraft du joueur ciblé (à ne pas utiliser en jeu).
     *
     * @param nocturnePlayer donnée nocturne du joueur à révéler
     */
    public void showPlayerNametagEnd(@NotNull NocturnePlayer nocturnePlayer) {
        Team team = getOrCreateTeam();

        Player player = nocturnePlayer.getPlayer();
        if (player == null) return;
        String playerName = player.getName();
        team.removeEntry(playerName);
    }

    /**
     * Cache le nametag d'un joueur aux yeux d'un autre joueur.
     *
     * @param nocturneViewer donnée nocturne du joueur qui observer
     * @param nocturneTarget donnée nocturne du joueur à cacher
     */
    public void hideCustomNametag(@NotNull NocturnePlayer nocturneViewer, @NotNull NocturnePlayer nocturneTarget) {
        if (nocturneViewer.getPlayer() == null || nocturneTarget.getPlayer() == null) return;

        if (nocturneViewer == nocturneTarget) return;

        NameTag nametag = new NameTag("", true);

        // Stocker la configuration de masquage
        nameTagConfigs.computeIfAbsent(nocturneViewer.getPlayerId(), key -> new ConcurrentHashMap<>())
                .put(nocturneTarget.getPlayerId(), nametag);

        modifyDisplayEntityText(nocturneViewer, nocturneTarget, nametag);

    }

    /**
     * Remplace le nom d'un joueur aux yeux d'un autre joueur par {@code customName}.
     *
     * @param nocturneViewer donnée nocturne du joueur qui observer
     * @param nocturneTarget donnée nocturne du joueur à reset
     * @param customName     String qui sera affiché en tant que nouveau pseudo du joueur
     */
    public void setCustomNametag(@NotNull NocturnePlayer nocturneViewer, @NotNull NocturnePlayer nocturneTarget, String customName) {
        if (nocturneViewer.getPlayer() == null || nocturneTarget.getPlayer() == null) return;

        if (nocturneViewer == nocturneTarget) return;

        // Supprimer l'ancienne entity s'il en existe une
//        removeDisplayEntity(nocturneViewer, nocturneTarget);

        NameTag nametag = new NameTag(customName, false);

        // Stocker la configuration
        nameTagConfigs.computeIfAbsent(nocturneViewer.getPlayerId(), k -> new ConcurrentHashMap<>())
                .put(nocturneTarget.getPlayerId(), nametag);

        // Modifier le TextDisplay entity du joueur target
        modifyDisplayEntityText(nocturneViewer, nocturneTarget, nametag);
    }

    /**
     * Affiche le nom d'un joueur aux yeux d'un autre joueur.
     *
     * @param nocturneViewer donnée nocturne du joueur qui observer
     * @param nocturneTarget donnée nocturne du joueur à reset
     */
    public void resetCustomNametag(@NotNull NocturnePlayer nocturneViewer, @NotNull NocturnePlayer nocturneTarget) {
        if (nocturneViewer.getPlayer() == null || nocturneTarget.getPlayer() == null) return;

        String targetName = nocturneTarget.getPlayer().getName();
        setCustomNametag(nocturneViewer, nocturneTarget, targetName);

    }

    /**
     * Affiche le nom d'un joueur aux yeux d'un autre joueur, en y ajoutant un {@code prefix}.
     *
     * @param nocturneViewer donnée nocturne du joueur qui observer
     * @param nocturneTarget donnée nocturne du joueur dont le nametag est modifié
     * @param prefix         string qui sera placé avant le nom joueur
     */
    public void setCustomNametagWithPrefix(@NotNull NocturnePlayer nocturneViewer, @NotNull NocturnePlayer nocturneTarget, String prefix) {

        if (nocturneViewer.getPlayer() == null || nocturneTarget.getPlayer() == null) return;

        String targetName = prefix + nocturneTarget.getPlayer().getName();
        setCustomNametag(nocturneViewer, nocturneTarget, targetName);
    }

    /**
     * Affiche le nom d'un joueur aux yeux de tous les joueurs, en vie ou non.
     *
     * @param nocturneTarget donnée nocturne du joueur à reset
     */
    public void resetCustomNametagForAll(@NotNull NocturnePlayer nocturneTarget) {
        for (NocturnePlayer nocturneViewer : playerManager.getAll()) {
            resetCustomNametag(nocturneViewer, nocturneTarget);
        }

    }

    /**
     * Cache le nametag d'un joueur aux yeux de tous les joueurs en vie.
     *
     * @param nocturneTarget donnée nocturne du joueur à cacher
     */
    public void hideCustomNametagForAll(@NotNull NocturnePlayer nocturneTarget) {
        for (NocturnePlayer nocturneViewer : playerManager.getAlive()) {
            hideCustomNametag(nocturneViewer, nocturneTarget);
        }
    }

    // -------------------------------------------------------------------------
    // Anonymat personnel
    // -------------------------------------------------------------------------

    /**
     * Retire un joueur spécifique de l'équipe de masquage, révélant son nametag et son skin.
     * Utilisé lors d'une révélation par flèche spectrale.
     *
     * @param player joueur à révéler
     */
    public void restoreIdentity(@NotNull Player player) {
        NocturnePlayer nocturneTarget = playerManager.get(player);

        resetCustomNametagForAll(nocturneTarget);
        showSkin(player);
    }

    public void hideIdentity(@NotNull Player player) {
        hideIdentity(player, true, true);
    }

    public void hideIdentity(@NotNull Player player, boolean hideSkin, boolean hideNametag) {
        NocturnePlayer nocturneTarget = playerManager.get(player);

        if (hideNametag) hideCustomNametagForAll(nocturneTarget);
        if (hideSkin) hideSkin(player);
    }

    // -------------------------------------------------------------------------
    // Anonymat totale
    // -------------------------------------------------------------------------

    /**
     * Restaure skins et nametags pour tous les joueurs.
     * Appelé en fin de phase de Gameplay, avant le début du vote.
     */
    public void restoreAll() {
        showAllSkins();
        for (NocturnePlayer nocturnePlayer : playerManager.getAlive()) {
            // TODO: hide if some player are invisible
            resetCustomNametagForAll(nocturnePlayer);
        }
    }

    /**
     * Cache skins et nametags pour tous les joueurs.
     * Appelé en début de phase de Gameplay, avant le début du vote.
     */
    public void hideAll(boolean hideSkin, boolean hideNametag) {
        if (hideSkin) hideAllSkins();
        if (hideNametag) {
            for (NocturnePlayer nocturnePlayer : playerManager.getAlive()) {
                hideCustomNametagForAll(nocturnePlayer);
            }
        }
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

    // -------------------------------------------------------------------------
    // Utilitaires privés (NAMETAGS)
    // -------------------------------------------------------------------------

    /**
     * Crée une TextDisplay entity qui "ride" sur le joueur target
     */
    private void createDisplayEntity(@NotNull NocturnePlayer nocturneViewer, @NotNull NocturnePlayer nocturneTarget, NameTag nametag) {

        String displayText = nametag.customName;
        boolean seeThrough = nametag.seeThrough;



        Player viewer = nocturneViewer.getPlayer();
        Player target = nocturneTarget.getPlayer();

        try {
            // Position initiale au-dessus de la tête du joueur
            Location spawnLoc = target.getLocation().add(0, target.getHealth() + 0.3, 0);

            // Créer la TextDisplay entity
            TextDisplay textDisplay = target.getWorld().spawn(spawnLoc, TextDisplay.class, entity -> {
                // Configuration de l'entity AVANT le spawn

                // 1. Texte à afficher (support couleurs)
                Component textComponent = LegacyComponentSerializer.legacySection().deserialize(displayText);
                entity.text(textComponent);

                // 2. Configuration d'affichage optimisée pour le riding
                entity.setBillboard(Display.Billboard.CENTER); // Centré et face au joueur
                entity.setSeeThrough(seeThrough);                    // Transparence à travers les blocs
                entity.setDefaultBackground(false);             // Pas de fond par défaut
                entity.setShadowed(false);                      // Ombre pour meilleure lisibilité

                // 3. Échelle optimisée pour le riding (légèrement plus petite)
                Transformation transformation = new Transformation(
                        new Vector3f(0, 0.3f, 0),      // Translation légèrement vers le haut
                        new AxisAngle4f(0, 0, 0, 1),      // Pas de rotation gauche
                        new Vector3f(1.0f, 1.0f, 1.0f),         // Échelle réduite (80%)
                        new AxisAngle4f(0, 0, 0, 1)       // Pas de rotation droite
                );
                entity.setTransformation(transformation);

                // 4. Propriétés d'entité optimisées pour riding
                entity.setGravity(false);          // Pas de gravité (important pour riding)
                entity.setInvulnerable(true);      // Indestructible
                entity.setSilent(true);            // Silencieux
                entity.setPersistent(false);       // Ne persiste pas après redémarrage
                entity.setVisibleByDefault(false); // INVISIBLE PAR DÉFAUT

                // 5. Métadonnées pour identification
                entity.customName(Component.text("riding_nametag_" + target.getName() + "_for_" + viewer.getName()));
                entity.setCustomNameVisible(false);
            });

            // Rendre visible SEULEMENT pour le viewer spécifique
            viewer.showEntity(Nocturne.getInstance(), textDisplay);

            // Faire rider l'entity sur le joueur
            target.addPassenger(textDisplay);

            // Stocker la référence
            displayEntities.computeIfAbsent(viewer.getUniqueId(), k -> new ConcurrentHashMap<>())
                    .put(nocturneTarget.getPlayerId(), textDisplay);

        } catch (Exception e) {
            Nocturne.getInstance().getLogger().severe(
                    "[Nocturne] Erreur dans la création du textDisplay de %s vu par %s : %s"
                            .formatted(target.getName(), viewer.getName(), e.getMessage())
            );
        }
    }

    /**
     * Supprime une TextDisplay entity qui ride sur un joueur
     */
    private void removeDisplayEntity(@NotNull NocturnePlayer nocturneViewer, @NotNull NocturnePlayer nocturneTarget) {
        Map<UUID, TextDisplay> viewerEntities = displayEntities.get(nocturneViewer.getPlayerId());
        if (viewerEntities == null) return;

        TextDisplay entity = viewerEntities.remove(nocturneTarget.getPlayerId());
        if (entity == null || !entity.isValid()) return;

        Player viewer = nocturneViewer.getPlayer();
        Player target = nocturneTarget.getPlayer();

        try {
            // Faire descendre l'entity du joueur AVANT de la supprimer
            if (target.isOnline() && target.getPassengers().contains(entity)) target.removePassenger(entity);

            // Cacher l'entity du viewer
            if (viewer.isOnline()) viewer.hideEntity(Nocturne.getInstance(), entity);

            // Puis la supprimer complètement
            entity.remove();

        } catch (Exception e) {
            Nocturne.getInstance().getLogger().severe(
                    "[Nocturne] Erreur dans l'élimination du textDisplay de %s vu par %s : %s"
                            .formatted(target.getName(), viewer.getName(), e.getMessage())
            );
        }

    }

    private void modifyDisplayEntityText(@NotNull NocturnePlayer nocturneViewer, @NotNull NocturnePlayer nocturneTarget, NameTag nametag) {
        Player viewer = nocturneViewer.getPlayer();
        Player target = nocturneTarget.getPlayer();
        Map<UUID, TextDisplay> viewerEntities = displayEntities.get(nocturneViewer.getPlayerId());
        if (viewerEntities == null) {
            createDisplayEntity(nocturneViewer, nocturneTarget, nametag);
            return;
        }

        TextDisplay entity = viewerEntities.get(nocturneTarget.getPlayerId());
        if (entity == null || !entity.isValid()) {
            createDisplayEntity(nocturneViewer, nocturneTarget, nametag);
            return;
        }


        String displayText = nametag.customName;
        boolean hidden = nametag.hidden;
        boolean seeThrough = nametag.seeThrough;

        try {
            if (hidden) {
                viewer.hideEntity(Nocturne.getInstance(), entity);
                return;
            }
            viewer.showEntity(Nocturne.getInstance(), entity);

            entity.setSeeThrough(seeThrough);

            Component textComponent = LegacyComponentSerializer.legacySection().deserialize(displayText);
            entity.text(textComponent);
        } catch (Exception e) {
            Nocturne.getInstance().getLogger().severe(
                    "[Nocturne] Erreur dans la modification du textDisplay de %s vu par %s : %s"
                            .formatted(target.getName(), viewer.getName(), e.getMessage())
            );
        }
    }

    /**
     * Nettoyage complet d'un joueur avec gestion du riding
     */
    public void cleanupPlayer(@NotNull NocturnePlayer nocturnePlayer) {
        UUID playerId = nocturnePlayer.getPlayerId();
        Player player = nocturnePlayer.getPlayer();

        if (player == null) return;

        // Nettoyer en tant que viewer (supprimer toutes ses TextDisplay entities)
        Map<UUID, TextDisplay> viewerEntities = displayEntities.remove(playerId);
        if (viewerEntities != null) {
            for (Map.Entry<UUID, TextDisplay> entry : viewerEntities.entrySet()) {
                TextDisplay entity = entry.getValue();

                if (entity != null && entity.isValid()) {
                    Player target = Bukkit.getPlayer(entry.getKey());
                    if (target != null && target.isOnline() && target.getPassengers().contains(entity)) {
                        target.removePassenger(entity);
                    }
                    entity.remove();
                }
            }
        }

        // Nettoyer les configurations
        nameTagConfigs.remove(playerId);

        // Nettoyer en tant que target (faire descendre toutes les entities qui ridaient sur lui)
        if (player.isOnline()) {
            List<TextDisplay> ridingEntities = new ArrayList<>();
            for (Entity passenger : player.getPassengers()) {
                if (passenger instanceof TextDisplay textDisplay) {
                    // Vérifier si c'est une de nos entities nametag
                    textDisplay.getName();
                    if (textDisplay.getName().contains("riding_nametag_" + player.getName())) {
                        ridingEntities.add(textDisplay);
                    }
                }
            }

            // Faire descendre et supprimer toutes nos entities
            for (TextDisplay entity : ridingEntities) {
                player.removePassenger(entity);
                entity.remove();
            }
        }

        // Nettoyer les références dans les autres maps
        for (Map<UUID, TextDisplay> otherViewerEntities : displayEntities.values()) {
            TextDisplay entityToRemove = otherViewerEntities.remove(playerId);
            if (entityToRemove != null && entityToRemove.isValid()) {
                entityToRemove.remove();
            }
        }

        // Nettoyer les configurations où ce joueur est target
        for (Map<UUID, NameTag> viewerConfigs : nameTagConfigs.values()) {
            viewerConfigs.remove(playerId);
        }




    }


    /**
     * Configuration d'un nametag
     */

    private static class NameTag {
        String customName;
        boolean hidden;
        boolean seeThrough;

        NameTag(String customName, boolean hidden) {
            this.customName = customName;
            this.hidden = hidden;
            this.seeThrough = true;
        }
        NameTag(String customName, boolean hidden, boolean seeThrough) {
            this.customName = customName;
            this.hidden = hidden;
            this.seeThrough = seeThrough;
        }
    }
}
