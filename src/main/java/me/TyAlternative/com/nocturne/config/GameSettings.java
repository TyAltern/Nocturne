package me.TyAlternative.com.nocturne.config;

import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Agrégat de tous les paramètres de configuration de la partie.
 *
 * <p>Chargé par le {@link ConfigManager} depuis {@code config.yml}.
 * Exposé en lecture seule via des getters : les phases et capacités
 * interrogent cet objet plutôt que de lire le fichier YAML directement.
 *
 * <p>Organisé en sections correspondant aux blocs du fichier de configuration.
 */
@SuppressWarnings("unused")
public final class GameSettings {
    // -------------------------------------------------------------------------
    // Général
    // -------------------------------------------------------------------------

    private boolean debug = false;
    private String worldName = "world";

    // -------------------------------------------------------------------------
    // Phases
    // -------------------------------------------------------------------------

    private int gameplayDurationSeconds = 360;
    private int voteDurationSeconds     = 120;

    // -------------------------------------------------------------------------
    // Anonymat
    // -------------------------------------------------------------------------

    private boolean hideSkins    = true;
    private boolean hideNametags = true;
    private String defaultSkin   = "steve";

    // -------------------------------------------------------------------------
    // Vote
    // -------------------------------------------------------------------------

    private List<Location> voteTableLocations = new ArrayList<>();

    // -------------------------------------------------------------------------
    // Élimination
    // -------------------------------------------------------------------------

    private boolean revealRoleOnDeath      = false;
    private boolean teleportSpectatorsOnDeath = false;
    private @Nullable Location spectatorLocation = null;

    // -------------------------------------------------------------------------
    // Flèches spectrales
    // -------------------------------------------------------------------------

    private int defaultSpectralArrows = 1;

    // -------------------------------------------------------------------------
    // Capacités
    // -------------------------------------------------------------------------

    private int poudreChemineeCooldownSeconds  = 5;
    private List<String> amnesiaRoleCandidates = new ArrayList<>();

    // -------------------------------------------------------------------------
    // UI
    // -------------------------------------------------------------------------

    private boolean bossBarEnabled    = true;
    private String bossBarColorGameplay = "BLUE";
    private String bossBarColorVote   = "PINK";
    private boolean actionBarEnabled  = true;
    private boolean soundsEnabled     = true;
    private String prefix             = "§e[Nocturne]";

    // -------------------------------------------------------------------------
    // Double swap
    // -------------------------------------------------------------------------

    private boolean doubleSwapEnabled  = true;
    private int doubleSwapMaxDelayMs   = 200;

    // =========================================================================
    // Getters
    // =========================================================================

    public boolean isDebug()                       { return debug; }
    public @NotNull String getWorldName()          { return worldName; }

    public int getGameplayDurationSeconds()        { return gameplayDurationSeconds; }
    public int getVoteDurationSeconds()            { return voteDurationSeconds; }

    public boolean shouldHideSkins()               { return hideSkins; }
    public boolean shouldHideNametags()            { return hideNametags; }
    public @NotNull String getDefaultSkin()        { return defaultSkin; }

    public @NotNull List<Location> getVoteTableLocations() { return voteTableLocations; }

    public boolean shouldRevealRoleOnDeath()       { return revealRoleOnDeath; }
    public boolean shouldTeleportSpectatorsOnDeath() { return teleportSpectatorsOnDeath; }
    public @Nullable Location getSpectatorLocation() { return spectatorLocation; }

    public int getDefaultSpectralArrows()          { return defaultSpectralArrows; }

    public int getPoudreChemineeCooldownSeconds()  { return poudreChemineeCooldownSeconds; }
    public @NotNull List<String> getAmnesiaRoleCandidates() { return amnesiaRoleCandidates; }

    public boolean isBossBarEnabled()              { return bossBarEnabled; }
    public @NotNull String getBossBarColorGameplay() { return bossBarColorGameplay; }
    public @NotNull String getBossBarColorVote()   { return bossBarColorVote; }
    public boolean isActionBarEnabled()            { return actionBarEnabled; }
    public boolean areSoundsEnabled()              { return soundsEnabled; }
    public @NotNull String getPrefix()             { return prefix; }

    public boolean isDoubleSwapEnabled()           { return doubleSwapEnabled; }
    public int getDoubleSwapMaxDelayMs()           { return doubleSwapMaxDelayMs; }

    // =========================================================================
    // Setters (package-private, utilisés uniquement par ConfigManager)
    // =========================================================================

    void setDebug(boolean debug)                                     { this.debug = debug; }
    void setWorldName(String worldName)                              { this.worldName = worldName; }
    void setGameplayDurationSeconds(int seconds)                     { this.gameplayDurationSeconds = seconds; }
    void setVoteDurationSeconds(int seconds)                         { this.voteDurationSeconds = seconds; }
    void setHideSkins(boolean hideSkins)                             { this.hideSkins = hideSkins; }
    void setHideNametags(boolean hideNametags)                       { this.hideNametags = hideNametags; }
    void setDefaultSkin(String skin)                                 { this.defaultSkin = skin; }
    void setVoteTableLocations(List<Location> locations)             { this.voteTableLocations = locations; }
    void setRevealRoleOnDeath(boolean reveal)                        { this.revealRoleOnDeath = reveal; }
    void setTeleportSpectatorsOnDeath(boolean teleport)              { this.teleportSpectatorsOnDeath = teleport; }
    void setSpectatorLocation(@Nullable Location location)           { this.spectatorLocation = location; }
    void setDefaultSpectralArrows(int count)                         { this.defaultSpectralArrows = count; }
    void setPoudreChemineeCooldownSeconds(int seconds)               { this.poudreChemineeCooldownSeconds = seconds; }
    void setAmnesiaRoleCandidates(List<String> candidates)           { this.amnesiaRoleCandidates = candidates; }
    void setBossBarEnabled(boolean enabled)                          { this.bossBarEnabled = enabled; }
    void setBossBarColorGameplay(String color)                        { this.bossBarColorGameplay = color; }
    void setBossBarColorVote(String color)                           { this.bossBarColorVote = color; }
    void setActionBarEnabled(boolean enabled)                        { this.actionBarEnabled = enabled; }
    void setSoundsEnabled(boolean enabled)                           { this.soundsEnabled = enabled; }
    void setPrefix(String prefix)                                    { this.prefix = prefix; }
    void setDoubleSwapEnabled(boolean enabled)                       { this.doubleSwapEnabled = enabled; }
    void setDoubleSwapMaxDelayMs(int delayMs)                        { this.doubleSwapMaxDelayMs = delayMs; }

}
