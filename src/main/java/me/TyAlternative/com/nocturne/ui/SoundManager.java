package me.TyAlternative.com.nocturne.ui;

import me.TyAlternative.com.nocturne.core.NocturneGame;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Logger;

/**
 * Centralise la lecture des sons du plugin.
 *
 * <p>Les sons sont référencés par leur clé YAML et leur valeur Minecraft
 * est lue dans la configuration. Échec silencieux si clé absente ou invalide.
 */
@SuppressWarnings("unused")
public class SoundManager {

    private final NocturneGame game;
    private final Logger logger;

    public SoundManager(@NotNull NocturneGame game, @NotNull Logger logger) {
        this.game = game;
        this.logger = logger;
    }

    /** Joue un son à tous les joueurs vivants. */
    public void playToAll(@NotNull String soundKey) {
        if (!game.getSettings().areSoundsEnabled()) return;
        Sound sound = resolveSound(soundKey);
        if (sound == null) return;
        for (Player player : game.getPlayerManager().getAlivePlayers()) {
            player.playSound(player.getLocation(), sound, 1.0f, 1.0f);
        }
    }
    /** Joue un son à un joueur spécifique. */
    public void play(@NotNull Player player, @NotNull String soundKey) {
        if (!game.getSettings().areSoundsEnabled()) return;
        Sound sound = resolveSound(soundKey);
        if (sound == null) return;
        player.playSound(player.getLocation(), sound, 1.0f, 1.0f);

    }





    private Sound resolveSound(@NotNull String soundKey) {
        String soundName = game.getSettings().getSoundName(soundKey);
        if (soundName == null || soundName.isBlank()) return null;
        try {
            NamespacedKey key = NamespacedKey.minecraft(soundName.toLowerCase());
            return Registry.SOUNDS.get(key);
        } catch (Exception e) {
            logger.warning("[Nocturne] Clé de son invalide : " + soundName);
            return null;
        }
    }
}
