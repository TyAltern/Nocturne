package me.TyAlternative.com.nocturne.mechanics.particle;

import me.TyAlternative.com.nocturne.Nocturne;
import me.TyAlternative.com.nocturne.player.NocturnePlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@SuppressWarnings({"unused", "UnnecessaryLocalVariable"})
public final class TickingParticleTimer {


    private final Map<UUID, Map<UUID, ParticleData>> particlesDatas;
    private BukkitTask task;
    private int currentTick = 0;

    private final Nocturne plugin;


    public TickingParticleTimer() {
        this.plugin = Nocturne.getInstance();
        this.particlesDatas = new HashMap<>();
    }

    // -------------------------------------------------------------------------
    // Cycle de vie
    // -------------------------------------------------------------------------

    /**
     * Démarre le ticker.
     * Si un ticker est déjà actif, il est arrêté avant de redémarrer.
     */
    public void start() {
        stop(); // Garantit qu'un seul ticker tourne à la fois
        currentTick = 0;
        task = plugin.getServer()
                .getScheduler()
                .runTaskTimer(plugin, this::tick, 0L, 2L);
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
        particlesDatas.clear();
    }

    /** {@code true} si le ticker est actif. */
    public boolean isRunning() {
        return task != null && !task.isCancelled();
    }

    private void tick() {
        currentTick+=1;

        for (Map.Entry<UUID, Map<UUID, ParticleData>> entrySet : particlesDatas.entrySet()) {
            UUID viewerId = entrySet.getKey();
            for (Map.Entry<UUID, ParticleData> uuidParticleDataEntry : entrySet.getValue().entrySet()) {
                UUID targetId = uuidParticleDataEntry.getKey();
                ParticleData particleData = uuidParticleDataEntry.getValue();

                int tickInterval = particleData.getSpawnTickInterval()/2;
                if (tickInterval <= 0) tickInterval = 1;

                if (currentTick % tickInterval != 0) continue;
                Player viewer = Bukkit.getPlayer(viewerId);
                Player target = Bukkit.getPlayer(targetId);
                if (viewer == null || target == null) continue;
                if (!viewer.isOnline() || !target.isOnline()) continue;

                spawnParticlesFor(viewer, target, particleData);


            }
        }


    }

    public void addTickingParticles(
            @NotNull NocturnePlayer nocturneViewer,
            @NotNull NocturnePlayer nocturneTarget,
            @NotNull ParticleData particleData
    ) {
        if (nocturneViewer.getPlayer() == null || nocturneTarget.getPlayer() == null) return;

        Map<UUID, ParticleData> datas = particlesDatas.computeIfAbsent(nocturneViewer.getPlayerId(), k -> {
            Map<UUID, ParticleData> uuidParticleData = new HashMap<>();
            return uuidParticleData;
        });

        datas.put(nocturneTarget.getPlayerId(), particleData);

    }

    public void removeTickingParticles(
            @NotNull NocturnePlayer nocturneViewer,
            @NotNull NocturnePlayer nocturneTarget
    ) {
        if (nocturneViewer.getPlayer() == null || nocturneTarget.getPlayer() == null) return;

        Map<UUID, ParticleData> datas = particlesDatas.get(nocturneViewer.getPlayerId());
        if (datas == null || datas.isEmpty()) return;

        datas.remove(nocturneTarget.getPlayerId());
    }


    /**
     * Envoie des particules à la position donnée, visibles uniquement par {@code observer}.
     */
    private void spawnParticlesFor(
            @NotNull Player observer,
            @NotNull Player target,
            @NotNull ParticleData particleData
    ) {
        observer.spawnParticle(
                particleData.getParticle(),
                target.getLocation().add(0, particleData.getHeight_offset(), 0),
                particleData.getParticle_count(),
                particleData.getSpread(), particleData.getSpread(), particleData.getSpread(),
                0.0 // vitesse nulle (donc particules statiques)
        );
    }

}
