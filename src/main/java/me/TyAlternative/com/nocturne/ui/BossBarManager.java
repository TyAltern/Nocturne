package me.TyAlternative.com.nocturne.ui;

import me.TyAlternative.com.nocturne.Nocturne;
import me.TyAlternative.com.nocturne.ability.AbilityIds;
import me.TyAlternative.com.nocturne.api.phase.PhaseType;
import me.TyAlternative.com.nocturne.core.NocturneGame;
import me.TyAlternative.com.nocturne.player.NocturnePlayer;
import me.TyAlternative.com.nocturne.util.TimeUtil;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Gère les BossBars individuelles affichant la phase et le timer aux joueurs.
 *
 * <p>Chaque joueur vivant possède sa propre {@link BossBar}. Le timer n'est
 * affiché qu'aux joueurs ayant la capacité {@code CLAIRVOYANCE} (Gameplay)
 * ou à tous (Vote).
 *
 * <p>Démarré par {@link NocturneGame#startGame()} via {@link #start()},
 * arrêté proprement via {@link #stop()}.
 */
@SuppressWarnings({"unused", "DataFlowIssue"})
public final class BossBarManager {
    private final NocturneGame game;

    private final Map<UUID, BossBar> bossBars = new HashMap<>();

    private BukkitTask updateTask;

    public BossBarManager(NocturneGame game) {
        this.game = game;
    }

    // -------------------------------------------------------------------------
    // Cycle de vie
    // -------------------------------------------------------------------------

    /** Démarre la mise à jour automatique des BossBars toutes les secondes. */
    public void start() {
        if (!game.getSettings().isBossBarEnabled()) return;

        updateTask = Bukkit.getScheduler().runTaskTimer(
                Nocturne.getInstance(),
                this::update,
                0L, 20L
        );
    }

    /** Arrête la mise à jour et retire toutes les BossBars. */
    public void stop() {
        if (updateTask != null && !updateTask.isCancelled()) {
            updateTask.cancel();
            updateTask = null;
        }
        removeAll();
    }

    // -------------------------------------------------------------------------
    // Mise à jour
    // -------------------------------------------------------------------------

    private void update() {
        if (!game.getSettings().isBossBarEnabled()) return;

        PhaseType phase = game.getCurrentPhase();

        if (!phase.isInGame()) {
            removeAll();
            return;
        }
        for (NocturnePlayer nocturnePlayer : game.getPlayerManager().getAll()) {
            Player player = nocturnePlayer.getPlayer();
            if (player == null) continue;
            updatePlayerBossBar(player, nocturnePlayer, phase);
        }
    }

    private void updatePlayerBossBar(@NotNull Player player, @NotNull NocturnePlayer nocturnePlayer, @NotNull PhaseType phase) {
        BossBar bar = bossBars.computeIfAbsent(player.getUniqueId(), id -> {
            BossBar created = Bukkit.createBossBar("", resolveColor(phase), BarStyle.SOLID);
            created.addPlayer(player);
            return created;
        });

        boolean showTimer = canSeeTimer(nocturnePlayer, phase);
        int     remaining = game.getPhaseManager().getRemainingSeconds();

        int dotNumber = (3-remaining%3);
        // Titre
        String title = showTimer ?
                phase.getDisplayName() + " §7-§f " + TimeUtil.formatSeconds(remaining):
                "§7Phase de " + phase.getDisplayName() + " §7en cours" + ".".repeat(dotNumber)+" ".repeat(3-dotNumber);

        // Progression
        float progress = showTimer ? calculateProgress(phase, remaining) : 1.0f;
        bar.setProgress(Math.max(0.0, Math.min(1.0, progress)));
        bar.setTitle(title);
        bar.setColor(resolveColor(phase));
    }

    // -------------------------------------------------------------------------
    // Gestion individuelle
    // -------------------------------------------------------------------------

    /**
     * Retire la BossBar d'un joueur (mort, déconnexion).
     *
     * @param playerId UUID du joueur
     */
    public void removePlayer(@NotNull UUID playerId) {
        BossBar bar = bossBars.remove(playerId);
        if (bar != null) bar.removeAll();
    }

    /** Retire toutes les BossBars. */
    public void removeAll() {
        bossBars.values().forEach(BossBar::removeAll);
        bossBars.clear();
    }


    // -------------------------------------------------------------------------
    // Utilitaires privés
    // -------------------------------------------------------------------------

    /**
     * Un joueur peut voir le timer en phase de Vote (tous) ou en phase de Gameplay
     * uniquement s'il possède la capacité Clairvoyance.
     */
    private boolean canSeeTimer(@NotNull NocturnePlayer nocturnePlayer, @NotNull PhaseType phase) {
        if (phase == PhaseType.VOTE) return true;
        if (phase != PhaseType.GAMEPLAY) return false;
        if (!nocturnePlayer.hasRole()) return true;
        if (nocturnePlayer.getPlayer().getGameMode() == GameMode.SPECTATOR) return true;
        return nocturnePlayer.getRole().hasAbility(
                AbilityIds.CLAIRVOYANCE
        );
    }

    private float calculateProgress(@NotNull PhaseType phase, int remainingSeconds) {
//        int total = switch (phase) {
//            case GAMEPLAY -> game.getSettings().getGameplayDurationSeconds();
//            case VOTE     -> game.getSettings().getVoteDurationSeconds();
//            default       -> remainingSeconds;
//        };
        int total = (int) game.getPhaseManager().getCurrentContext().getDurationMs();
        if (total <= 0) return 1000.0f;
        return (float) (remainingSeconds * 1000)/total;
    }

    private @NotNull BarColor resolveColor(@NotNull PhaseType phase) {
        String colorName = switch (phase) {
            case GAMEPLAY -> game.getSettings().getBossBarColorGameplay();
            case VOTE     -> game.getSettings().getBossBarColorVote();
            default       -> "WHITE";
        };
        try {
            return BarColor.valueOf(colorName.toUpperCase());
        } catch (IllegalArgumentException e) {
            return BarColor.WHITE;
        }
    }

}
