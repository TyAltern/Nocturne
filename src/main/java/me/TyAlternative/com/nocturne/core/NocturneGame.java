package me.TyAlternative.com.nocturne.core;

import me.TyAlternative.com.nocturne.ability.AbilityManager;
import me.TyAlternative.com.nocturne.composition.CompositionManager;
import me.TyAlternative.com.nocturne.core.phase.PhaseManager;
import me.TyAlternative.com.nocturne.player.PlayerManager;
import me.TyAlternative.com.nocturne.role.RoleDistributor;
import me.TyAlternative.com.nocturne.role.RoleRegistry;
import me.TyAlternative.com.nocturne.ui.MessageManager;
import org.bukkit.entity.Player;
import org.checkerframework.checker.units.qual.C;
import org.checkerframework.checker.units.qual.N;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Logger;

/**
 * Façade centrale du jeu Nocturne.
 *
 * <p>Compose tous les sous-managers et orchestre le cycle de vie d'une partie :
 * démarrage, transitions de phases, victoire, arrêt et nettoyage.
 *
 * <p>Les managers ne se connaissent pas entre eux directement : ils passent
 * toujours par {@code NocturneGame} pour accéder à leurs pairs. Cela évite
 * les dépendances circulaires et facilite les tests.
 *
 * <h2>Cycle de vie d'une partie</h2>
 * <pre>
 * startGame()
 *   → validation (composition, joueurs)
 *   → distribution des rôles
 *   → PhaseManager.startPhase(GAMEPLAY, round=1)
 *
 * [fin de phase GAMEPLAY]
 *   → checkVictory()
 *   → si victoire → handleVictory() → stopGame()
 *   → sinon → startPhase(VOTE)
 *
 * [fin de phase VOTE]
 *   → checkVictory()
 *   → si victoire → handleVictory() → stopGame()
 *   → sinon → nextRound() → startPhase(GAMEPLAY)
 *
 * stopGame()
 *   → cleanup()
 * </pre>
 */
@SuppressWarnings("unused")
public final class NocturneGame {

    // -------------------------------------------------------------------------
    // Managers persistants (durée de vie = instance NocturneGame)
    // -------------------------------------------------------------------------

    private final PlayerManager      playerManager;
    private final CompositionManager compositionManager;
    private final RoleRegistry       roleRegistry;
    private final RoleDistributor    roleDistributor;
    private final MessageManager     messageManager;
    private final Logger             logger;



    // -------------------------------------------------------------------------
    // Construction
    // -------------------------------------------------------------------------

    /**
     * Construit le jeu et tous ses managers dans l'ordre des dépendances.
     *
     * @param settings  paramètres de configuration chargés
     * @param registry  registre des fabriques de rôles
     * @param logger    logger du plugin
     */
    public NocturneGame(
            @NotNull RoleRegistry registry,
            @NotNull Logger logger
    ) {
        this.logger = logger;

        // Managers sans dépendances
        this.playerManager = new PlayerManager();
        this.compositionManager = new CompositionManager();
        this.roleRegistry = registry;
        this.messageManager = new MessageManager();

        // Managers avec dépendances simples
        this.roleDistributor = new RoleDistributor(registry, playerManager, logger);
    }


    // -------------------------------------------------------------------------
    // Accesseurs (lecture seule)
    // -------------------------------------------------------------------------

    public @NotNull PlayerManager      getPlayerManager()      { return playerManager; }
    public @NotNull CompositionManager getCompositionManager() { return compositionManager; }
    public @NotNull RoleRegistry       getRoleRegistry()       { return roleRegistry; }
    public @NotNull MessageManager     getMessageManager()     { return messageManager; }
    public @NotNull RoleDistributor    getRoleDistributor()    { return roleDistributor; }

}
