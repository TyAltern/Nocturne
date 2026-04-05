package me.TyAlternative.com.nocturne.role;

import me.TyAlternative.com.nocturne.api.role.Role;
import me.TyAlternative.com.nocturne.api.role.RoleFactory;
import me.TyAlternative.com.nocturne.composition.CompositionManager;
import me.TyAlternative.com.nocturne.core.NocturneGame;
import me.TyAlternative.com.nocturne.player.PlayerManager;
import me.TyAlternative.com.nocturne.player.NocturnePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.logging.Logger;

/**
 * Responsable de la distribution aléatoire des rôles aux joueurs en début de partie.
 *
 * <p>La distribution est intentionnellement isolée dans cette classe pour respecter
 * le principe de responsabilité unique : ni le {@link NocturneGame}
 * ni le {@link RoleRegistry} ne contiennent cette logique.
 *
 * <h2>Processus de distribution</h2>
 * <ol>
 *   <li>Construire la liste complète des rôles à distribuer depuis la composition.</li>
 *   <li>Mélanger aléatoirement cette liste.</li>
 *   <li>Associer chaque joueur à un rôle via une nouvelle instance produite par la factory.</li>
 *   <li>Déclencher le hook {@link Role#onAssigned} pour chaque joueur.</li>
 * </ol>
 */
public final class RoleDistributor {

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(RoleDistributor.class);
    private final RoleRegistry registry;
    private final PlayerManager playerManager;
    private final Logger logger;

    /**
     * @param registry      registre des fabriques de rôles
     * @param playerManager gestionnaire des joueurs
     * @param logger        logger du plugin
     */
    public RoleDistributor(
            @NotNull RoleRegistry registry,
            @NotNull PlayerManager playerManager,
            @NotNull Logger logger
    ) {
        this.registry = registry;
        this.playerManager = playerManager;
        this.logger = logger;
    }

    // -------------------------------------------------------------------------
    // Distribution
    // -------------------------------------------------------------------------

    /**
     * Distribue les rôles à tous les joueurs fournis selon la composition définie.
     *
     * <p>Chaque rôle est instancié via son {@link RoleFactory},
     * garantissant l'isolation complète de l'état entre joueurs.
     *
     * @param players     liste des joueurs à qui distribuer un rôle
     * @param composition gestionnaire de la composition définissant combien de chaque rôle
     * @throws IllegalArgumentException si le nombre de joueurs ne correspond pas
     *                                  au nombre total de rôles dans la composition
     * @throws IllegalStateException    si un identifiant de rôle dans la composition
     *                                  n'est pas enregistré dans le registry
     */
    public void distribute(
            @NotNull List<Player> players,
            @NotNull CompositionManager composition
    ) {
        List<String> roleIds = buildRoleIdList(composition);

        if (players.size() != roleIds.size()) {
            throw new IllegalArgumentException(
                    "Le nombre de joueurs (%d) ne correspond pas au nombre de rôles dans la composition (%d)."
                            .formatted(players.size(), roleIds.size())
            );
        }

        // Validation préalable : tous les IDs doivent être enregistrés
        for (String roleId : roleIds) {
            if (!registry.isRegistered(roleId)) {
                throw new IllegalStateException(
                        "L'identifiant de rôle '%s' présent dans la composition n'est pas enregistré.". formatted(roleId)
                );
            }
        }

        // Mélange aléatoire pour la distribution équitable
        Collections.shuffle(roleIds);

        // Association joueur <-> rôle et déclenchement du hook onAssigned
        for (int i = 0; i < players.size(); i++) {
            Player player = players.get(i);
            String roleId = roleIds.get(i);

            // Crée une nouvelle instance via la factory
            Role role = registry.create(roleId);

            NocturnePlayer nocturnePlayer = playerManager.getOrCreate(player);
            nocturnePlayer.setRole(role);

            logger.info("  → %s = %s".formatted(player.getName(), role.getDisplayName()));

            // Notifier le rôle de son assignation (envoi du message de présentation, etc.)
            try {
                role.onAssigned(player, nocturnePlayer);
            } catch (Exception e) {
                logger.severe(
                        "Erreur lors du hook onAssigned pour %s (rôle %s) : %s"
                                .formatted(player.getName(), roleId, e.getMessage())
                );
            }
        }
    }

    // -------------------------------------------------------------------------
    // Construction de la liste de rôles à distribuer
    // -------------------------------------------------------------------------

    /**
     * Construit la liste ordonnée des identifiants de rôles à distribuer,
     * en répétant chaque identifiant autant de fois que défini dans la composition.
     *
     * <p>Exemple : {@code {ETINCELLE: 1, BATON: 3}} → {@code ["ETINCELLE", "BATON", "BATON", "BATON"]}
     *
     * @param composition composition définissant les quantités par rôle
     * @return liste plate des identifiants de rôles
     */
    private @NotNull List<String> buildRoleIdList(@NotNull CompositionManager composition) {
        List<String> result = new ArrayList<>();

        for (Map.Entry<String, Integer> entry : composition.getCompositionMap().entrySet()) {
            String roleId = entry.getKey();
            int count = entry.getValue();

            for (int i = 0; i < count; i++) {
                result.add(roleId);
            }
        }

        return result;
    }

}
