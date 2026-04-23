package me.TyAlternative.com.nocturne.config;

import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Agrégat de tous les paramètres de configuration de la partie.
 *
 * <p>Chargé par le {@link ConfigManager} depuis {@code config.yml}.
 * Exposé en lecture seule via des getters : les phases et capacités
 * interrogent cet objet plutôt que de lire le fichier YAML directement.
 *
 * <p>Organisé en sections correspondant aux blocs du fichier de configuration.
 */
@SuppressWarnings({"unused", "BooleanMethodIsAlwaysInverted"})
public final class GameSettings {
    // -------------------------------------------------------------------------
    // Général
    // -------------------------------------------------------------------------

    private boolean debug = false;
    private String worldName = "world";

    // -------------------------------------------------------------------------
    // Phases
    // -------------------------------------------------------------------------

    private int maxGameplayDurationSeconds = 360;
    private int minGameplayDurationSeconds = 360;
    private int maxVoteDurationSeconds     = 120;
    private int minVoteDurationSeconds     = 120;

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
    private boolean redeemSpectralArrowIfMiss = false;

    // -------------------------------------------------------------------------
    // Capacités
    // -------------------------------------------------------------------------

    // STARTING COOLDOWNS

    private int eclaircissementStartingCooldown = 10;
    private int embrasementStartingCooldown = 10;
    private int etouffementStartingCooldown = 10;
    private int briseStartingCooldown = 10;
    private int aquilonStartingCooldown = 10;
    private int austerStartingCooldown = 10;



    // INFORMATION


    // PROTECTION

        // ALIZE
        private double alizeRadius = 45.0;
        private boolean alizeProtectOnlyUnprotected = true;

        // AUSTER
        private int austerCooldown = 0;
        private boolean austerShowProtectedCount = false;
        private boolean austerRemoveIfBefore = false;


    // FLAMMES
        // POUDRE DE CHEMIEE
        private int poudreChemineeCooldownSeconds  = 5;

        // RAYONNEMENT
        private double rayonnementRadius = 10.0;
        private int rayonnementMinExposureSeconds = 5;
        private boolean rayonnementShowValue = true;


    // MISC

        // INCANDESCENCE
        private List<String> incandescenceAbilityCandidates = new ArrayList<>();


    // CURSE
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

    // -------------------------------------------------------------------------
    // Sons
    // -------------------------------------------------------------------------

    /** Map clé logique → nom Minecraft (ex: "phase_change" → "entity.player.levelup"). */
    private java.util.Map<String, String> soundNames = new java.util.HashMap<>();

    // =========================================================================
    // Getters
    // =========================================================================

    public boolean isDebug()                                          { return debug; }
    public @NotNull String getWorldName()                             { return worldName; }
    public int getMaxGameplayDurationSeconds()                        { return maxGameplayDurationSeconds; }
    public int getMinGameplayDurationSeconds()                        { return minGameplayDurationSeconds; }
    public int getMaxVoteDurationSeconds()                            { return maxVoteDurationSeconds; }
    public int getMinVoteDurationSeconds()                            { return minVoteDurationSeconds; }
    public boolean shouldHideSkins()                                  { return hideSkins; }
    public boolean shouldHideNametags()                               { return hideNametags; }
    public @NotNull String getDefaultSkin()                           { return defaultSkin; }
    public @NotNull List<Location> getVoteTableLocations()            { return voteTableLocations; }
    public boolean shouldRevealRoleOnDeath()                          { return revealRoleOnDeath; }
    public boolean shouldTeleportSpectatorsOnDeath()                  { return teleportSpectatorsOnDeath; }
    public @Nullable Location getSpectatorLocation()                  { return spectatorLocation; }
    public int getDefaultSpectralArrows()                             { return defaultSpectralArrows; }
    public boolean shouldRedeemSpectralArrowIfMiss()                  { return redeemSpectralArrowIfMiss; }
    public boolean isDoubleSwapEnabled()                              { return doubleSwapEnabled; }
    public int getDoubleSwapMaxDelayMs()                              { return doubleSwapMaxDelayMs; }
// STARTING COOLDOWN
    public int getEclaircissementStartingCooldown()                   { return eclaircissementStartingCooldown; }
    public int getEmbrasementStartingCooldown()                       { return embrasementStartingCooldown; }
    public int getEtouffementStartingCooldown()                       { return etouffementStartingCooldown; }
    public int getBriseStartingCooldown()                             { return briseStartingCooldown; }
    public int getAquilonStartingCooldown()                           { return aquilonStartingCooldown; }
    public int getAusterStartingCooldown()                            { return austerStartingCooldown; }
// ABILITIES
    public double getAlizeRadius()                                    { return alizeRadius; }
    public boolean shouldAlizeProtectOnlyUnprotected()                { return alizeProtectOnlyUnprotected; }
    public int getAusterCooldown()                                    { return austerCooldown; }
    public boolean shouldAusterShowProtectedCount()                   { return austerShowProtectedCount; }
    public boolean shouldAusterRemoveIfBefore()                       { return austerRemoveIfBefore; }

    public @NotNull List<String> getIncandescenceAbilityCandidates()  { return incandescenceAbilityCandidates; }
    public int getPoudreChemineeCooldownSeconds()                     { return poudreChemineeCooldownSeconds; }
    public double getRayonnementRadius()                              { return rayonnementRadius; }
    public boolean shouldRayonnementShowValue()                       { return rayonnementShowValue; }
    public int getRayonnementMinExposureSeconds()                     { return rayonnementMinExposureSeconds; }
    public @NotNull List<String> getAmnesiaRoleCandidates()           { return amnesiaRoleCandidates; }
// UI
    public boolean isBossBarEnabled()                                 { return bossBarEnabled; }
    public @NotNull String getBossBarColorGameplay()                  { return bossBarColorGameplay; }
    public @NotNull String getBossBarColorVote()                      { return bossBarColorVote; }
    public boolean isActionBarEnabled()                               { return actionBarEnabled; }
    public boolean areSoundsEnabled()                                 { return soundsEnabled; }
    public @NotNull String getPrefix()                                { return prefix; }
    public @Nullable String getSoundName(@NotNull String key)         { return soundNames.get(key); }
    // =========================================================================
    // Setters (package private, utilisés uniquement par ConfigManager)
    // =========================================================================

    void setDebug(boolean debug)                                      { this.debug = debug; }
    void setWorldName(String worldName)                               { this.worldName = worldName; }
    void setMaxGameplayDurationSeconds(int seconds)                   { this.maxGameplayDurationSeconds = seconds; }
    void setMinGameplayDurationSeconds(int seconds)                   { this.minGameplayDurationSeconds = seconds; }
    void setMaxVoteDurationSeconds(int seconds)                       { this.maxVoteDurationSeconds = seconds; }
    void setMinVoteDurationSeconds(int seconds)                       { this.minVoteDurationSeconds = seconds; }
    void setHideSkins(boolean hideSkins)                              { this.hideSkins = hideSkins; }
    void setHideNametags(boolean hideNametags)                        { this.hideNametags = hideNametags; }
    void setDefaultSkin(String skin)                                  { this.defaultSkin = skin; }
    void setVoteTableLocations(List<Location> locations)              { this.voteTableLocations = locations; }
    void setRevealRoleOnDeath(boolean reveal)                         { this.revealRoleOnDeath = reveal; }
    void setTeleportSpectatorsOnDeath(boolean teleport)               { this.teleportSpectatorsOnDeath = teleport; }
    void setSpectatorLocation(@Nullable Location location)            { this.spectatorLocation = location; }
    void setDefaultSpectralArrows(int count)                          { this.defaultSpectralArrows = count; }
    void setRedeemSpectralArrowIfMiss(boolean redeem)                 { this.redeemSpectralArrowIfMiss = redeem; }
    void setDoubleSwapEnabled(boolean enabled)                        { this.doubleSwapEnabled = enabled; }
    void setDoubleSwapMaxDelayMs(int delayMs)                         { this.doubleSwapMaxDelayMs = delayMs; }
// STARTING COOLDOWNS
    void setEclaircissementStartingCooldown(int cooldown)             { this.eclaircissementStartingCooldown = cooldown; }
    void setEmbrasementStartingCooldown(int cooldown)                 { this.embrasementStartingCooldown = cooldown; }
    void setEtouffementStartingCooldown(int cooldown)                 { this.etouffementStartingCooldown = cooldown; }
    void setBriseStartingCooldown(int cooldown)                       { this.briseStartingCooldown = cooldown; }
    void setAquilonStartingCooldown(int cooldown)                     { this.aquilonStartingCooldown = cooldown; }
    void setAusterStartingCooldown(int cooldown)                      { this.austerStartingCooldown = cooldown; }
// ABILITIES
    void setAlizeRadius(double radius)                                { this.alizeRadius = radius; }
    void setAlizeProtectOnlyUnprotected(boolean protect)              { this.alizeProtectOnlyUnprotected = protect; }
    void setAusterCooldown(int cooldown)                              { this.austerCooldown = cooldown; }
    void setAusterShowProtectedCount(boolean show)                    { this.austerShowProtectedCount = show; }
    void setAusterRemoveIfBefore(boolean remove)                      { this.austerRemoveIfBefore = remove; }
    void setIncandescenceAbilityCandidates(List<String> candidates)   { this.incandescenceAbilityCandidates = candidates; }
    void setRayonnementRadius(double radius)                          { this.rayonnementRadius = radius; }
    void setRayonnementMinExposureSeconds(int seconds)                { this.rayonnementMinExposureSeconds = seconds; }
    void setRayonnementShowValue(boolean showValue)                   { this.rayonnementShowValue = showValue; }
    void setPoudreChemineeCooldownSeconds(int seconds)                { this.poudreChemineeCooldownSeconds = seconds; }
    void setAmnesiaRoleCandidates(List<String> candidates)            { this.amnesiaRoleCandidates = candidates; }
// UI
    void setBossBarEnabled(boolean enabled)                           { this.bossBarEnabled = enabled; }
    void setBossBarColorGameplay(String color)                        { this.bossBarColorGameplay = color; }
    void setBossBarColorVote(String color)                            { this.bossBarColorVote = color; }
    void setActionBarEnabled(boolean enabled)                         { this.actionBarEnabled = enabled; }
    void setSoundsEnabled(boolean enabled)                            { this.soundsEnabled = enabled; }
    void setPrefix(String prefix)                                     { this.prefix = prefix; }
    void setSoundNames(Map<String, String> names)                     { this.soundNames = names; }

}
