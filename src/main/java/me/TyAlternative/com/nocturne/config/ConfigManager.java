package me.TyAlternative.com.nocturne.config;

import me.TyAlternative.com.nocturne.Nocturne;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Charge le fichier {@code config.yml} et alimente le {@link GameSettings}.
 *
 * <p>Responsabilité unique : lire le YAML et écrire dans le POJO de settings.
 * Aucune logique métier ici.
 */
@SuppressWarnings({"unused"})
public final class ConfigManager {

    private final Nocturne plugin;
    private final GameSettings settings;

    /**
     * @param plugin instance du plugin, pour accéder au fichier de configuration
     */
    public ConfigManager(@NotNull Nocturne plugin) {
        this.plugin = plugin;
        this.settings = new GameSettings();
        plugin.saveDefaultConfig();
        load();
    }

    // -------------------------------------------------------------------------
    // Chargement
    // -------------------------------------------------------------------------

    /**
     * Charge (ou recharge) tous les paramètres depuis {@code config.yml}.
     * Peut être appelé à tout moment via {@code /nocturne reload}.
     */
    public void load() {
        FileConfiguration cfg = plugin.getConfig();


        // Général
        settings.setDebug(cfg.getBoolean("general.debug", false));
        settings.setWorldName(cfg.getString("general.world", "world"));

        // Phases
        settings.setMaxGameplayDurationSeconds(cfg.getInt("phases.gameplay.max_duration", 360));
        settings.setMinGameplayDurationSeconds(cfg.getInt("phases.gameplay.min_duration", 360));
        settings.setMaxVoteDurationSeconds(cfg.getInt("phases.vote.max_duration", 120));
        settings.setMinVoteDurationSeconds(cfg.getInt("phases.vote.min_duration", 120));

        // Anonymat
        settings.setHideSkins(cfg.getBoolean("mechanics.anonymity.hide_skins", true));
        settings.setHideNametags(cfg.getBoolean("mechanics.anonymity.hide_nametags", true));
        settings.setDefaultSkin(cfg.getString("mechanics.anonymity.default_skin", "steve"));

        // Flèches spectrales
        settings.setDefaultSpectralArrows(cfg.getInt("mechanics.spectral_arrows.default_count", 1));
        settings.setRedeemSpectralArrowIfMiss(cfg.getBoolean("mechanics.spectral_arrows.redeem_if_miss", true));

        // Tables de vote
        settings.setVoteTableLocations(parseVoteTables(cfg));

        // Élimination
        settings.setRevealRoleOnDeath(cfg.getBoolean("mechanics.elimination.reveal_role_on_death", false));
        settings.setTeleportSpectatorsOnDeath(cfg.getBoolean("mechanics.elimination.teleport_spectators", false));
        settings.setSpectatorLocation(parseSpectatorLocation(cfg));

        // Starting Cooldown
        settings.setEclaircissementStartingCooldown(cfg.getInt("starting_cooldowns.eclaircissement", 10));
        settings.setEmbrasementStartingCooldown(cfg.getInt("starting_cooldowns.embrasement", 10));
        settings.setEtouffementStartingCooldown(cfg.getInt("starting_cooldowns.etouffement", 10));
        settings.setBriseStartingCooldown(cfg.getInt("starting_cooldowns.brise", 10));
        settings.setAquilonStartingCooldown(cfg.getInt("starting_cooldowns.aquilon", 10));
        settings.setAusterStartingCooldown(cfg.getInt("starting_cooldowns.auster", 10));


        // Capacités
        settings.setAlizeRadius(cfg.getDouble("abilities.capacity.alize.radius", 45.0));
        settings.setAlizeProtectOnlyUnprotected(cfg.getBoolean("abilities.capacity.alize.only_not_protected", true));
        settings.setAusterCooldown(cfg.getInt("abilities.capacity.auster.cooldown", 0));
        settings.setAusterShowProtectedCount(cfg.getBoolean("abilities.capacity.auster.show_protected_count", false));
        settings.setAusterRemoveIfBefore(cfg.getBoolean("abilities.capacity.auster.cancel_if_before", false));
        settings.setIncandescenceAbilityCandidates(cfg.getStringList("abilities.capacity.incandescence.targets"));


        settings.setRayonnementRadius(cfg.getDouble("abilities.capacity.rayonnement.radius", 10.0));
        settings.setRayonnementMinExposureSeconds(cfg.getInt("abilities.capacity.rayonnement.min_exposure_seconds", 5));
        settings.setRayonnementShowValue(cfg.getBoolean("abilities.capacity.rayonnement.show_value", true));
        settings.setPoudreChemineeCooldownSeconds(cfg.getInt("cooldowns.poudre_cheminee", 5));
        settings.setAmnesiaRoleCandidates(cfg.getStringList("abilities.curse.amnesia.targets"));

        // Double swap
        settings.setDoubleSwapEnabled(cfg.getBoolean("abilities.double_swap.enabled", true));
        settings.setDoubleSwapMaxDelayMs(cfg.getInt("abilities.double_swap.max_delay_ms", 200));

        // Sons
        settings.setSoundNames(parseSounds(cfg));

        // UI
        settings.setBossBarEnabled(cfg.getBoolean("ui.bossbar.enabled", true));
        settings.setBossBarColorGameplay(cfg.getString("ui.bossbar.color_gameplay", "BLUE"));
        settings.setBossBarColorVote(cfg.getString("ui.bossbar.color_vote", "PINK"));
        settings.setActionBarEnabled(cfg.getBoolean("ui.actionbar.enabled", true));
        settings.setSoundsEnabled(cfg.getBoolean("ui.sounds.enabled", true));
        settings.setPrefix(cfg.getString("messages.prefix", "§e[Nocturne]"));

        if (settings.isDebug()) {
            plugin.getLogger().info("[Nocturne] Configuration chargée.");
        }
    }

    /**
     * Recharge la configuration depuis le disque.
     */
    public void reload() {
        plugin.reloadConfig();
        load();
        plugin.getLogger().info("[Nocturne] Configuration rechargée.");
    }

    // -------------------------------------------------------------------------
    // Accesseur
    // -------------------------------------------------------------------------

    /** Retourne le POJO de settings chargé. */
    public @NotNull GameSettings getSettings() {
        return settings;
    }

    // -------------------------------------------------------------------------
    // Parsers privés
    // -------------------------------------------------------------------------

    private @NotNull List<Location> parseVoteTables(@NotNull FileConfiguration cfg) {
        List<Location> tables = new ArrayList<>();
        var world = plugin.getServer().getWorld(settings.getWorldName());

        for (String raw : cfg.getStringList("vote_tables")) {
            try {
                String[] parts = raw.replace(" ","").split(",");
                if (parts.length >= 3) {
                    double x = Double.parseDouble(parts[0]);
                    double y = Double.parseDouble(parts[1]);
                    double z = Double.parseDouble(parts[2]);
                    tables.add(new Location(world, x, y, z));
                }
            } catch (NumberFormatException e) {
                plugin.getLogger().warning(
                        "[Nocturne] Position de table de vote invalide ignorée : " + raw
                );
            }
        }

        return tables;
    }

    private @Nullable Location parseSpectatorLocation(@NotNull FileConfiguration cfg) {
        if (!cfg.getBoolean("mechanics.elimination.teleport_spectator", false)) return null;

        var world = plugin.getServer().getWorld(settings.getWorldName());
        double x = cfg.getDouble("mechanics.elimination.spectator_location.x", 0);
        double y = cfg.getDouble("mechanics.elimination.spectator_location.y", 100);
        double z = cfg.getDouble("mechanics.elimination.spectator_location.z", 0);
        return new Location(world, x, y, z);

    }

    private @NotNull Map<String, String> parseSounds(@NotNull FileConfiguration cfg) {
        Map<String, String> sounds = new HashMap<>();
        var soundsSection = cfg.getConfigurationSection("ui.sounds");
        if (soundsSection != null) {
            for (String key : soundsSection.getKeys(false)) {
                if (!key.equals("enabled")) {
                    sounds.put(key, soundsSection.getString(key, ""));
                }
            }
        }
        return sounds;
    }


}
