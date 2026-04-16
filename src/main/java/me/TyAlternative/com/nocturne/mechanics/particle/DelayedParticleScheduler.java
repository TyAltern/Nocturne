package me.TyAlternative.com.nocturne.mechanics.particle;

import me.TyAlternative.com.nocturne.Nocturne;
import me.TyAlternative.com.nocturne.player.NocturnePlayer;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Planificateur de particules retardées avec leurre intégré.
 *
 * <p>Utilisé par les capacités visuelles d'information ({@code EchosInteractionAbility},
 * {@code ReverbérationLumineuse}) pour afficher des particules autour de joueurs
 * ciblés après un délai aléatoire, accompagnées d'un leurre sur un joueur innocent.
 *
 * <h2>Comportement</h2>
 * <ul>
 *   <li>Les particules apparaissent entre {@code minDelaySeconds} et {@code maxDelaySeconds}
 *       secondes après l'événement déclencheur.</li>
 *   <li>Un joueur leurre aléatoire (different de la vraie cible) reçoit également des
 *       particules au même moment, pour empêcher une déduction certaine.</li>
 *   <li>Les particules sont envoyées uniquement au joueur observateur (pas broadcast),
 *       afin de ne pas révéler l'information aux autres joueurs.</li>
 *   <li>Si l'observateur ou la cible se déconnecte avant le délai, la tâche est annulée
 *       silencieusement.</li>
 * </ul>
 *
 * <h2>Utilisation</h2>
 * <pre>{@code
 * DelayedParticleScheduler scheduler = new DelayedParticleScheduler(plugin, playerManager);
 * scheduler.schedule(
 *     observerId,      // UUID de l'observateur (qui voit les particules)
 *     targetId,        // UUID du joueur autour duquel afficher les vraies particules
 *     Particle.WITCH,  // type de particule
 *     10, 60           // délai min/max en secondes
 * );
 * }</pre>
 */

@SuppressWarnings({"unused"})
public final class DelayedParticleScheduler {

    /** Nombre de particules affichées par apparition. */
    private static final int PARTICLE_COUNT   = 15;

    /** Rayon d'éparpillement des particules autour du joueur. */
    private static final double SPREAD        = 0.6;

    /** Hauteur au-dessus du sol pour centrer les particules sur le corps. */
    private static final double HEIGHT_OFFSET = 1.0;

    private static final Random RANDOM = new Random();

    private final Nocturne plugin;

    /**
     * @param plugin        instance du plugin, pour planifier les tâches Bukkit
     */
    public DelayedParticleScheduler(@NotNull Nocturne plugin) {
        this.plugin = plugin;
    }


    // -------------------------------------------------------------------------
    // API publique
    // -------------------------------------------------------------------------

    /**
     * Planifie l'affichage de particules autour de {@code targetId} pour l'observateur
     * {@code observerId}, après un délai aléatoire entre {@code minDelaySeconds} et
     * {@code maxDelaySeconds} secondes.
     *
     * @param nocturneViewer donnée nocturne du joueur qui verra les particules
     * @param nocturneTarget   donnée nocturne du joueur autour duquel afficher les vraies particules
     * @param particle         type de particule à utiliser
     * @param minDelaySeconds  délai minimum avant affichage (en secondes)
     * @param maxDelaySeconds  délai maximum avant affichage (en secondes)
     */
    public void schedule(
            @NotNull NocturnePlayer nocturneViewer,
            @NotNull NocturnePlayer nocturneTarget,
            @NotNull Particle particle,
            int minDelaySeconds,
            int maxDelaySeconds
    ) {
        int delayTicks = secondsToRandomTicks(minDelaySeconds, maxDelaySeconds);
        Bukkit.getScheduler().runTaskLater(plugin, () -> plugin.getGame().getTickingParticleTimer().addTickingParticles(
                nocturneViewer,
                nocturneTarget,
                new ParticleData.Builder()
                        .particle(particle)
                        .particle_count(PARTICLE_COUNT)
                        .height_offset(HEIGHT_OFFSET)
                        .spread(SPREAD)
                        .spawnTickInterval(20)
                        .build()
        ), delayTicks);

    }


    // -------------------------------------------------------------------------
    // Logique interne
    // -------------------------------------------------------------------------

    /**
     * Convertit un délai en secondes aléatoire (entre min et max) en ticks Bukkit (×20).
     */
    private int secondsToRandomTicks(int minSeconds, int maxSeconds) {
        int range = Math.max(1, maxSeconds - minSeconds);
        int seconds = minSeconds + RANDOM.nextInt(range+1);
        return seconds * 20;
    }
}
