package me.TyAlternative.com.nocturne.core;

import me.TyAlternative.com.nocturne.ability.AbilityManager;
import me.TyAlternative.com.nocturne.ability.TickingAbilityManager;
import me.TyAlternative.com.nocturne.api.phase.PhaseType;
import me.TyAlternative.com.nocturne.api.role.RoleTeam;
import me.TyAlternative.com.nocturne.composition.CompositionManager;
import me.TyAlternative.com.nocturne.config.GameSettings;
import me.TyAlternative.com.nocturne.core.phase.PhaseManager;
import me.TyAlternative.com.nocturne.core.round.RoundContext;
import me.TyAlternative.com.nocturne.elimination.EliminationManager;
import me.TyAlternative.com.nocturne.mechanics.anonymity.AnonymityManager;
import me.TyAlternative.com.nocturne.mechanics.disparition.DisparitionManager;
import me.TyAlternative.com.nocturne.mechanics.embrasement.EmbrasementManager;
import me.TyAlternative.com.nocturne.mechanics.glowing.GlowingManager;
import me.TyAlternative.com.nocturne.mechanics.protection.ProtectionManager;
import me.TyAlternative.com.nocturne.mechanics.sign.SignManager;
import me.TyAlternative.com.nocturne.mechanics.vote.VoteManager;
import me.TyAlternative.com.nocturne.player.NocturnePlayer;
import me.TyAlternative.com.nocturne.player.PlayerManager;
import me.TyAlternative.com.nocturne.player.PlayerState;
import me.TyAlternative.com.nocturne.role.RoleDistributor;
import me.TyAlternative.com.nocturne.role.RoleRegistry;
import me.TyAlternative.com.nocturne.ui.BossBarManager;
import me.TyAlternative.com.nocturne.ui.MessageManager;
import me.TyAlternative.com.nocturne.ui.SoundManager;
import me.TyAlternative.com.nocturne.victory.VictoryManager;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.LoggerFactory;

import java.util.List;
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
@SuppressWarnings({"unused", "BooleanMethodIsAlwaysInverted"})
public final class NocturneGame {
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(NocturneGame.class);

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
    private final SignManager        signManager;
    private final AnonymityManager   anonymityManager;
    private final EliminationManager eliminationManager;
    private final VictoryManager     victoryManager;
    private final PhaseManager       phaseManager;
    private final MessageManager     messageManager;
    private final BossBarManager     bossBarManager;
    private final SoundManager       soundManager;
    private final GlowingManager     glowingManager;
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
        this.playerManager      = new PlayerManager();
        this.compositionManager = new CompositionManager();
        this.roleRegistry       = registry;
        this.signManager        = new SignManager();
        this.glowingManager     = new GlowingManager();
        this.messageManager     = new MessageManager(settings);

        // Managers avec dépendances simples
        this.abilityManager      = new AbilityManager(playerManager);
        this.tickingAbilityManager = new TickingAbilityManager(playerManager, abilityManager, logger);
        this.voteManager         = new VoteManager(playerManager, messageManager);
        this.anonymityManager    = new AnonymityManager(playerManager, settings);
        this.eliminationManager  = new EliminationManager(playerManager, logger);
        this.roleDistributor     = new RoleDistributor(registry, playerManager, logger);

        // VictoryManager référence this -> initialisé après le reste
        this.victoryManager      = new VictoryManager(this);

        // Managers UI
        this.bossBarManager      = new BossBarManager(this);
        this.soundManager        = new SoundManager(this, logger);

        // PhaseManager avec callback de transition
        this.phaseManager        = new PhaseManager(this::handlePhaseTransition, logger);
    }

    // -------------------------------------------------------------------------
    // Démarrage de partie
    // -------------------------------------------------------------------------

    /**
     * Démarre une nouvelle partie.
     *
     * @return {@code true} si la partie a démarré, {@code false} si une validation a échoué
     */
    public boolean startGame() {
        if (gameRunning) {
            logger.warning("[Nocturne] Une partie est déjà en cours.");
            return false;
        }

        // Validation de la composition
        List<String> errors = compositionManager.validate(roleRegistry);
        if (!errors.isEmpty()) {
            errors.forEach(e -> logger.warning("[Nocturne] Composition invalide : " + e));
            return false;
        }


        // Validation des joueurs
        List<Player> players = List.copyOf(Bukkit.getOnlinePlayers()); // TODO: MODIFY WITH READY PLAYERS ONLY
        int expected = compositionManager.getTotalPlayers();
        if (players.isEmpty()) {
            logger.warning("[Nocturne] Aucun joueur en ligne.");
            return false;
        }
        if (players.size() != expected) {
            logger.warning("[Nocturne] Nombre de joueurs incorrect : %d en ligne, %d attendus."
                    .formatted(players.size(), expected));
            return false;
        }

        logger.info("[Nocturne] Démarrage avec %d joueurs...".formatted(players.size()));

        // Initialiser les joueurs
        for (Player player : players) {
            NocturnePlayer np = playerManager.getOrCreate(player);
            np.setState(PlayerState.PLAYING);
            np.setSpectralArrowsRemaining(settings.getDefaultSpectralArrows());
        }

        // Distribuer les rôles
        roleDistributor.distribute(players, compositionManager);

        gameRunning = true;
        roundNumber = 1;

        // Démarrer la première manche
        startNewRound(roundNumber);

        bossBarManager.start();

        broadcast("§aLa partie commence !");
        soundManager.playToAll("game_start");
        return true;
    }


    // -------------------------------------------------------------------------
    // Gestion des manches
    // -------------------------------------------------------------------------

    /**
     * Crée un nouveau {@link RoundContext} et démarre la phase de Gameplay.
     *
     * @param round numéro de la manche
     */
    private void startNewRound(int round) {
        // Chaque manche crée ses propres managers de mécaniques
        ProtectionManager protection = new ProtectionManager();
        EmbrasementManager embrasement = new EmbrasementManager();
        DisparitionManager disparition = new DisparitionManager();

        currentRound = new RoundContext(round, embrasement, protection, disparition);

        // Réinitialiser les données de manche des joueurs
        playerManager.resetAllForNewRound();

        phaseManager.startPhase(PhaseType.GAMEPLAY, currentRound);
    }

    /**
     * Passe à la manche suivante. Appelé par {@link #handlePhaseTransition}
     * après la phase de Vote.
     */
    private void nextRound() {
        roundNumber ++;
        logger.info("[Nocturne] Début de la manche " + roundNumber);
        startNewRound(roundNumber);
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
        if (!gameRunning || currentRound == null) return;

        switch (endedPhase) {
            case GAMEPLAY -> {
                if (victoryManager.checkVictory()) return;
                // Pas de victoire -> passer au vote
                phaseManager.startPhase(PhaseType.VOTE, currentRound);
            }
            case VOTE -> {
                if (victoryManager.checkVictory()) return;
                // Pas de victoire -> nouvelle manche
                nextRound();
            }
            case END -> stopGame("Fin de la phase END");
            default -> {}
        }
    }

    // -------------------------------------------------------------------------
    // Victoire
    // -------------------------------------------------------------------------

    /**
     * Annonce la victoire et arrête la partie.
     * Appelé par {@link VictoryManager#checkVictory()}.
     *
     * @param winner  équipe gagnante
     * @param message message de victoire à diffuser
     */
    public void handleVictory(@NotNull RoleTeam winner, @NotNull String message) {
        broadcast("§a§l=============================");
        broadcast(message);
        broadcast("§a§l=============================");
        stopGame("Victoire de " + winner.getDisplayName());
    }

    // -------------------------------------------------------------------------
    // Arrêt de partie
    // -------------------------------------------------------------------------

    /**
     * Arrête la partie et effectue le nettoyage complet.
     *
     * @param reason raison de l'arrêt (loggée uniquement)
     */
    public void stopGame(@NotNull String reason) {
        if (!gameRunning) return;

        logger.info("[Nocturne] Arrêt de la partie : " + reason);
        gameRunning = false;

        phaseManager.stop();
        cleanup();
    }

    /** Arrêt forcé, utilisé lors de la désactivation du plugin. */
    public void forceStop() {
        if (gameRunning) stopGame("Plugin désactivé");
    }

    // -------------------------------------------------------------------------
    // Nettoyage
    // -------------------------------------------------------------------------

    /**
     * Réinitialise tous les managers à leur état initial.
     * Appelé par {@link #stopGame(String)}.
     */
    private void cleanup() {
        tickingAbilityManager.stop();
        bossBarManager.stop();
        signManager.clearAll();
        anonymityManager.restoreAll();
        abilityManager.clearAll();
        voteManager.clearAll();

        // Remettre les joueurs en survie
        for (NocturnePlayer np : playerManager.getAll()) {
            Player player = np.getPlayer();
            if (player != null) {
                player.setGameMode(GameMode.SURVIVAL);
                player.getInventory().clear();
            }
        }

        playerManager.clearAll();
        currentRound = null;
        roundNumber = 0;
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
    public @NotNull SignManager        getSignManager()         { return signManager; }
    public @NotNull AnonymityManager   getAnonymityManager()    { return anonymityManager; }
    public @NotNull EliminationManager getEliminationManager()  { return eliminationManager; }
    public @NotNull VictoryManager     getVictoryManager()      { return victoryManager; }
    public @NotNull PhaseManager       getPhaseManager()        { return phaseManager; }
    public @NotNull MessageManager     getMessageManager()      { return messageManager; }
    public @NotNull BossBarManager     getBossBarManager()      { return bossBarManager; }
    public @NotNull SoundManager       getSoundManager()        { return soundManager; }
    public @NotNull GlowingManager     getGlowingManager()      { return glowingManager; }
    public @NotNull RoleDistributor    getRoleDistributor()     { return roleDistributor; }
    public @NotNull GameSettings       getSettings()            { return settings; }

    public @Nullable RoundContext getCurrentRound()              { return currentRound; }
    public boolean isGameRunning()                              { return gameRunning; }
    public int getRoundNumber()                                 { return roundNumber; }
    public @NotNull PhaseType getCurrentPhase()                 {return phaseManager.getCurrentType(); }
}
