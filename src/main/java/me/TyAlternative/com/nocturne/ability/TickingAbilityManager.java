package me.TyAlternative.com.nocturne.ability;

import me.TyAlternative.com.nocturne.Nocturne;
import me.TyAlternative.com.nocturne.api.ability.Ability;
import me.TyAlternative.com.nocturne.api.ability.AbilityContext;
import me.TyAlternative.com.nocturne.api.ability.AbilityResult;
import me.TyAlternative.com.nocturne.api.ability.AbilityTrigger;
import me.TyAlternative.com.nocturne.api.phase.PhaseType;
import me.TyAlternative.com.nocturne.player.NocturnePlayer;
import me.TyAlternative.com.nocturne.player.PlayerManager;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Logger;

/**
 * Gère l'exécution périodique des capacités dont le déclencheur est {@link AbilityTrigger#TICKS}.
 *
 * <p>Un seul ticker global tourne toutes les secondes (20 ticks) et
 * distribue les exécutions selon l'intervalle de chaque capacité.
 * Ce design évite de créer une tâche Bukkit par capacité.
 *
 * <p>Démarré au début de chaque phase de Gameplay via {@link #start(PhaseType)},
 * arrêté proprement à la fin via {@link #stop()}.
 *
 * <h2>Cycle d'exécution par tick</h2>
 * Pour chaque joueur vivant → pour chaque capacité TICKS → si
 * {@code currentTick % interval == 0} → déléguer à {@link AbilityManager#tryExecute}.
 */
@SuppressWarnings("unused")
public final class TickingAbilityManager {

    private final PlayerManager playerManager;
    private final AbilityManager abilityManager;
    private final Logger logger;

    private BukkitTask task;
    private int currentTick = 0;
    private PhaseType activePhase;

    /**
     * @param playerManager  gestionnaire des joueurs vivants
     * @param abilityManager gestionnaire des capacités pour les vérifications et enregistrements
     * @param logger         logger du plugin pour les erreurs
     */
    public TickingAbilityManager(
            @NotNull PlayerManager playerManager,
            @NotNull AbilityManager abilityManager,
            @NotNull Logger logger) {
        this.playerManager = playerManager;
        this.abilityManager = abilityManager;
        this.logger = logger;
    }


    // -------------------------------------------------------------------------
    // Cycle de vie
    // -------------------------------------------------------------------------

    /**
     * Démarre le ticker pour la phase donnée.
     * Si un ticker est déjà actif, il est arrêté avant de redémarrer.
     *
     * @param phase phase de jeu courante, passée au {@link AbilityManager#tryExecute}
     */
    public void start(@NotNull PhaseType phase) {
        stop(); // Garantit qu'un seul ticker tourne à la fois
        currentTick = 0;
        activePhase = phase;

        task = Nocturne.getInstance()
                .getServer()
                .getScheduler()
                .runTaskTimer(Nocturne.getInstance(), this::tick, 0L, 1L);
    }

    /**
     * Arrête le ticker proprement.
     * Sans effet si le ticker n'est pas actif.
     */
    public void stop() {
        if (task != null && !task.isCancelled()) {
            task.cancel();
            task = null;
        }
        currentTick = 0;
    }

    /** {@code true} si le ticker est actif. */
    public boolean isRunning() {
        return task != null && !task.isCancelled();
    }

    // -------------------------------------------------------------------------
    // Exécution par tick
    // -------------------------------------------------------------------------

    /**
     * Appelé à chaque tick du serveur (toutes les 1/20 de seconde).
     * Itère sur tous les joueurs vivants et exécute les capacités TICKS éligibles.
     */
    private void tick() {
        currentTick++;

        for (NocturnePlayer nocturnePlayer : playerManager.getAlive()) {
            if (!nocturnePlayer.hasRole()) continue;

            var player = nocturnePlayer.getPlayer();
            if (player == null) continue;

            //noinspection DataFlowIssue
            for (Ability ability : nocturnePlayer.getRole().getAbilities()) {
                if (ability.getTrigger() != AbilityTrigger.TICKS) continue;

                int interval = ability.getTickInterval();
                if (interval <= 0) continue;
                if (currentTick % interval != 0) continue;

                try {
                    AbilityResult result = abilityManager.tryExecute(
                            player,
                            nocturnePlayer,
                            ability,
                            AbilityContext.noTarget(),
                            activePhase
                    );

                    // Les capacités passives à ticks n'envoient généralement pas de message si c'est le cas, il sera envoyé directement via le rôle.
                    if (result.hasFeedback()) {
                        //noinspection DataFlowIssue
                        player.sendMessage(result.getFeedbackMessage());
                    }
                } catch (Exception e) {
                    logger.severe(
                            "[Nocturne] Erreur dans la capacité TICKS '%s' pour %s : %s"
                                    .formatted(ability.getId(), player.getName(), e.getMessage())
                    );
                }
            }
        }
    }
}
