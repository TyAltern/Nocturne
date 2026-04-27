package me.TyAlternative.com.nocturne.config;

import org.bukkit.Color;
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
    private int apelioteStartingCooldown = 10;
    private int boreeStartingCooldown = 10;
    private int lipsStartingCooldown = 10;
    private int notosStartingCooldown = 10;
    private int scironStartingCooldown = 10;
    private int polarisationStartingCooldown = 10;



    // INFORMATION


    // PROTECTION

        // ZEPHYR
        private double zephyrRadius = 45.0;
        private boolean zephyrProtectOnlyUnprotected = true;

        // NOTOS
        private int notosCooldown = 0;
        private boolean notosShowProtectedCount = false;
        private boolean notosRemoveIfBefore = false;

        // EUROS
        private boolean eurosSelfProtectionIfNotMarked = true;

        // CAECIAS
        private double caeciasRadiusProtection = 10.0;

        // SCIRON
        private int scironCooldown = 0;
        private int scironMaxUse = -1;

    // PROTECTION

        // POLARISATION
        private int polarisationCooldown = 3;
        private int polarisationMinDecoy = 0;
        private int polarisationMaxDecoy = 2;

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

        // CORROSION
        private int corrosionNegativeWeight = -2;
        private int corrosionPositiveWeight = 2;
        private Color corrosionNegativeColor = Color.fromBGR(85, 255, 85);
        private Color corrosionPositiveColor = Color.fromBGR(255, 85, 85);


    // CURSE
    private double murmurationRadius = 10;

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
    public int getApelioteStartingCooldown()                          { return apelioteStartingCooldown; }
    public int getBoreeStartingCooldown()                             { return boreeStartingCooldown; }
    public int getLipsStartingCooldown()                              { return lipsStartingCooldown; }
    public int getNotosStartingCooldown()                             { return notosStartingCooldown; }
    public int getScironStartingCooldown()                            { return scironStartingCooldown; }
    public int getPolarisationStartingCooldown()                      { return polarisationStartingCooldown; }
// ABILITIES
    public double getZephyrRadius()                                   { return zephyrRadius; }
    public boolean shouldZephyrProtectOnlyUnprotected()               { return zephyrProtectOnlyUnprotected; }
    public int getNotosCooldown()                                     { return notosCooldown; }
    public boolean shouldNotosShowProtectedCount()                    { return notosShowProtectedCount; }
    public boolean shouldNotosRemoveIfBefore()                        { return notosRemoveIfBefore; }
    public boolean shouldEurosSelfProtectionIfNotMarked()             { return eurosSelfProtectionIfNotMarked; }
    public double getCaeciasRadiusProtection()                        { return caeciasRadiusProtection; }
    public double getMurmurationRadius()                              { return murmurationRadius; }
    public int getScironMaxUse()                                      { return scironMaxUse; }
    public int getScironCooldown()                                    { return scironCooldown; }

    public int getPolarisationCooldown()                              { return polarisationCooldown; }
    public int getPolarisationMinDecoy()                              { return polarisationMinDecoy; }
    public int getPolarisationMaxDecoy()                              { return polarisationMaxDecoy; }

    public @NotNull List<String> getIncandescenceAbilityCandidates()  { return incandescenceAbilityCandidates; }
    public int getCorrosionNegativeWeight()                           { return corrosionNegativeWeight; }
    public int getCorrosionPositiveWeight()                           { return corrosionPositiveWeight; }
    public Color getCorrosionNegativeColor()                          { return corrosionNegativeColor; }
    public Color getCorrosionPositiveColor()                          { return corrosionPositiveColor; }

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
    void setApelioteStartingCooldown(int cooldown)                    { this.apelioteStartingCooldown = cooldown; }
    void setBoreeStartingCooldown(int cooldown)                       { this.boreeStartingCooldown = cooldown; }
    void setLipsStartingCooldown(int cooldown)                        { this.lipsStartingCooldown = cooldown; }
    void setNotosStartingCooldown(int cooldown)                       { this.notosStartingCooldown = cooldown; }
    void setScironStartingCooldown(int cooldown)                      { this.scironStartingCooldown = cooldown; }
    void setPolarisationStartingCooldown(int cooldown)                { this.polarisationStartingCooldown = cooldown; }
// ABILITIES
    void setZephyrRadius(double radius)                               { this.zephyrRadius = radius; }
    void setZephyrProtectOnlyUnprotected(boolean protect)             { this.zephyrProtectOnlyUnprotected = protect; }
    void setNotosCooldown(int cooldown)                               { this.notosCooldown = cooldown; }
    void setNotosShowProtectedCount(boolean show)                     { this.notosShowProtectedCount = show; }
    void setNotosRemoveIfBefore(boolean remove)                       { this.notosRemoveIfBefore = remove; }
    void setEurosSelfProtectionIfNotMarked(boolean protect)           { this.eurosSelfProtectionIfNotMarked = protect; }
    void setCaeciasRadiusProtection(double radius)                    { this.caeciasRadiusProtection = radius; }
    void setMurmurationRadius(double radius)                          { this.murmurationRadius = radius; }
    void setScironMaxUse(int maxUse)                                  { this.scironMaxUse = maxUse; }
    void setScironCooldown(int cooldown)                              { this.scironCooldown = cooldown; }
    void setPolarisationCooldown(int cooldown)                        { this.polarisationCooldown = cooldown; }
    void setPolarisationMinDecoy(int minDecoy)                        { this.polarisationMinDecoy = minDecoy; }
    void setPolarisationMaxDecoy(int maxDecoy)                        { this.polarisationMaxDecoy = maxDecoy; }
    void setIncandescenceAbilityCandidates(List<String> candidates)   { this.incandescenceAbilityCandidates = candidates; }
    void setCorrosionNegativeWeight(int weight)                       { this.corrosionNegativeWeight = weight; }
    void setCorrosionPositiveWeight(int weight)                       { this.corrosionPositiveWeight = weight; }
    void setCorrosionNegativeColor(Color color)                       { this.corrosionNegativeColor = color; }
    void setCorrosionPositiveColor(Color color)                       { this.corrosionPositiveColor = color; }
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
