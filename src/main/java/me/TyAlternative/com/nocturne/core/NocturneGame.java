package me.TyAlternative.com.nocturne.core;

import me.TyAlternative.com.nocturne.ability.AbilityManager;
import me.TyAlternative.com.nocturne.ability.TickingAbilityManager;
import me.TyAlternative.com.nocturne.api.phase.PhaseType;
import me.TyAlternative.com.nocturne.composition.CompositionManager;
import me.TyAlternative.com.nocturne.config.GameSettings;
import me.TyAlternative.com.nocturne.core.phase.PhaseManager;
import me.TyAlternative.com.nocturne.core.round.RoundContext;
import me.TyAlternative.com.nocturne.elimination.EliminationCause;
import me.TyAlternative.com.nocturne.elimination.EliminationManager;
import me.TyAlternative.com.nocturne.mechanics.vote.VoteManager;
import me.TyAlternative.com.nocturne.player.PlayerManager;
import me.TyAlternative.com.nocturne.role.RoleDistributor;
import me.TyAlternative.com.nocturne.role.RoleRegistry;
import me.TyAlternative.com.nocturne.ui.MessageManager;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
    private final AbilityManager     abilityManager;
    private final TickingAbilityManager tickingAbilityManager;
    private final VoteManager        voteManager;


    private final EliminationManager eliminationManager;

    private final PhaseManager       phaseManager;
    private final MessageManager     messageManager;
    private final GameSettings       settings;
    private final Logger             logger;

    // -------------------------------------------------------------------------
    // État de manche (recréé à chaque nouvelle manche)
    // -------------------------------------------------------------------------

    /** Contexte de la manche courante, {@code null} si aucune partie en cours. */
    private @Nullable RoundContext currentRound;

    // -------------------------------------------------------------------------
    // État général
    // -------------------------------------------------------------------------

    private boolean gameRunning = false;
    private int     roundNumber = 0;


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
            @NotNull GameSettings settings,
            @NotNull RoleRegistry registry,
            @NotNull Logger logger
    ) {
        this.settings = settings;
        this.logger = logger;

        // Managers sans dépendances
        this.playerManager = new PlayerManager();
        this.compositionManager = new CompositionManager();
        this.roleRegistry = registry;
        this.messageManager = new MessageManager(settings);

        // Managers avec dépendances simples
        this.abilityManager  = new AbilityManager(playerManager);
        this.tickingAbilityManager = new TickingAbilityManager(playerManager, abilityManager, logger);
        this.voteManager     = new VoteManager(playerManager);

        this.eliminationManager = new EliminationManager(playerManager, logger);
        this.roleDistributor = new RoleDistributor(registry, playerManager, logger);



        // PhaseManager avec callback de transition
        this.phaseManager = new PhaseManager(this::handlePhaseTransition, logger);
    }







    // -------------------------------------------------------------------------
    // Callback de transition de phase
    // -------------------------------------------------------------------------

    /**
     * Callback appelé par le {@link PhaseManager} à la fin de chaque phase.
     * Contient la logique de transition : vérification de victoire et démarrage
     * de la phase suivante.
     *
     * @param endedPhase phase qui vient de se terminer
     */
    private void handlePhaseTransition(@NotNull PhaseType endedPhase) {
        // TODO: finish this function
    }









    // -------------------------------------------------------------------------
    // Broadcast
    // -------------------------------------------------------------------------

    /**
     * Diffuse un message à tous les joueurs en ligne avec le préfixe configuré.
     *
     * @param message message brut (codes couleur acceptés)
     */
    public void broadcast(@NotNull String message) {
        Bukkit.broadcast(messageManager.buildBroadcast(message));
    }

    /**
     * Diffuse un message uniquement aux joueurs vivants.
     *
     * @param message message brut
     */
    public void broadcastToAlive(@NotNull String message) {
        var component = messageManager.buildBroadcast(message);
        playerManager.getAlivePlayers().forEach(p -> p.sendMessage(component));
    }



    // -------------------------------------------------------------------------
    // Accesseurs (lecture seule)
    // -------------------------------------------------------------------------

    public @NotNull PlayerManager      getPlayerManager()       { return playerManager; }
    public @NotNull CompositionManager getCompositionManager()  { return compositionManager; }
    public @NotNull RoleRegistry       getRoleRegistry()        { return roleRegistry; }
    public @NotNull AbilityManager     getAbilityManager()      { return abilityManager; }
    public @NotNull TickingAbilityManager getTickingAbilityManager() { return tickingAbilityManager; }
    public @NotNull VoteManager        getVoteManager()         { return voteManager; }
    public @NotNull EliminationManager getEliminationManager()  { return eliminationManager; }
    public @NotNull PhaseManager       getPhaseManager()        { return phaseManager; }
    public @NotNull MessageManager     getMessageManager()      { return messageManager; }
    public @NotNull RoleDistributor    getRoleDistributor()     { return roleDistributor; }
    public @NotNull GameSettings       getSettings()            { return settings; }

    public @NotNull RoundContext getCurrentRound()              { return currentRound; }
    public boolean isGameRunning()                              { return gameRunning; }
    public int getRoundNumber()                                 { return roundNumber; }
    public @NotNull PhaseType getCurrentPhase()                 {return phaseManager.getCurrentType(); }
}
