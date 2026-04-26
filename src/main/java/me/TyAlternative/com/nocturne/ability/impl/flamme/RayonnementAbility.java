package me.TyAlternative.com.nocturne.ability.impl.flamme;

import me.TyAlternative.com.nocturne.ability.AbilityIds;
import me.TyAlternative.com.nocturne.ability.AbstractAbility;
import me.TyAlternative.com.nocturne.ability.DrunkSupport;
import me.TyAlternative.com.nocturne.api.ability.*;
import me.TyAlternative.com.nocturne.api.phase.PhaseType;
import me.TyAlternative.com.nocturne.core.phase.PhaseContext;
import me.TyAlternative.com.nocturne.mechanics.embrasement.EmbrasementCause;
import me.TyAlternative.com.nocturne.player.NocturnePlayer;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Rayonnement — capacité passive à ticks de la Torche.
 *
 * <p>Chaque seconde (20 ticks), incrémente le compteur de présence de chaque joueur
 * se trouvant dans le rayon configurable. En fin de phase de Gameplay, le joueur
 * ayant le compteur le plus élevé (au-dessus du seuil minimum) est Embrasé.
 *
 * <p>Le Rayonnement peut être suspendu pour une manche via
 * {@link EtouffementFlammeAbility}. Si suspendu, aucun embrasement n'est déclenché.
 *
 * <p>En mode drunk : le tick s'exécute, mais les compteurs sont ignorés en fin de phase.
 */
@SuppressWarnings("DataFlowIssue")
public final class RayonnementAbility extends AbstractAbility {

    /** Rayon de détection en blocs. */
    private final double radius;

    /** Nombre minimum de secondes passées à proximité avant embrasement. */
    private final int minExposureSeconds;

    /** Affiche le pourcentage d'embrasement au-dessus de chaque joueur. */
    private final boolean showProgress;

    /** Compteurs de présence par joueur pour la manche en cours. UUID → secondes. */
    private final Map<UUID, Integer> exposureCounters = new HashMap<>();
    private int totalExposure = 5;

    /** État interne : rayonnement actif ou étouffé. Propre à chaque instance (= chaque joueur). */
    private boolean rayonnementActif = true;

    public RayonnementAbility() {
        super(
                AbilityIds.RAYONNEMENT,
                "Rayonnement",
                "Le joueur ayant passé le plus de temps dans un rayon de §7" + 10.0
                        + "§f blocs autour de vous sera Embrasé en fin de phase.",
                Material.TORCH,
                AbilityCategory.CAPACITY,
                AbilityUseType.PASSIVE,
                AbilityTrigger.TICKS
        );
        setTickInterval(20); // Toutes les secondes
        setAllowedPhases(PhaseType.GAMEPLAY);

        this.radius = game().getSettings().getRayonnementRadius();
        this.minExposureSeconds = game().getSettings().getRayonnementMinExposureSeconds();
        this.showProgress = game().getSettings().shouldRayonnementShowValue();

    }

    @Override
    public DrunkSupport supportsDrunk() {
        return DrunkSupport.DEFAULT_LOGIC;
    }

    @Override
    public boolean canExecute(@NotNull Player player, @NotNull NocturnePlayer nocturnePlayer, @NotNull AbilityContext context) {
        // Actif seulement si le rayonnement n'a pas été étouffé
        return isRayonnementActif();
    }

    @Override
    protected @NotNull AbilityResult executeLogic(@NotNull Player player, @NotNull NocturnePlayer nocturnePlayer, @NotNull AbilityContext context) {
        List<NocturnePlayer> aroundPlayers = game().getPlayerManager().getAliveInRadius(nocturnePlayer, radius);
        List<NocturnePlayer> notAroundPlayers = game().getPlayerManager().getAlive();
        notAroundPlayers.removeAll(aroundPlayers);

        for (NocturnePlayer nocturneTarget : aroundPlayers) {

            exposureCounters.merge(nocturneTarget.getPlayerId(), 1, Integer::sum);
            totalExposure++;

            if (showProgress && exposureCounters.containsKey(nocturneTarget.getPlayerId())) {
                int targetExposure = exposureCounters.get(nocturneTarget.getPlayerId());
                game().getAnonymityManager().addCustomPrefixSuffixToNametag(nocturnePlayer, nocturneTarget, null, " §7(" + Math.floorDiv(targetExposure*100, totalExposure) + "%)" );
            }
        }
        for (NocturnePlayer notAroundPlayer : notAroundPlayers) {
            if (showProgress && exposureCounters.containsKey(notAroundPlayer.getPlayerId())) {
                int targetExposure = exposureCounters.get(notAroundPlayer.getPlayerId());
                game().getAnonymityManager().addCustomPrefixSuffixToNametag(nocturnePlayer, notAroundPlayer, null, " §7(" + Math.floorDiv(targetExposure*100, totalExposure) + "%)" );
            }
        }

        // Retour silencieux : cette capacité ne consomme pas de charge
        return AbilityResult.silentSuccess();
    }



    // -------------------------------------------------------------------------
    // Fin de phase — déclenchement de l'embrasement
    // -------------------------------------------------------------------------

    @Override
    public void onGameplayPhaseStart(@NotNull Player player, @NotNull NocturnePlayer nocturnePlayer, @NotNull PhaseContext phaseContext) {
        // Réinitialiser les compteurs et activer le rayonnement
        exposureCounters.clear();
        totalExposure = 5;
        rayonnementActif = true;
    }



    @Override
    public void onGameplayPhaseEnd(@NotNull Player player, @NotNull NocturnePlayer nocturnePlayer, @NotNull PhaseContext phaseContext) {
        // TODO: add feedback to player (even to drunk)
        if (isDrunk()) return;

        if (!isRayonnementActif()) return;

        UUID mostExposedId = null;
        int maxExposure = minExposureSeconds;

        for (Map.Entry<UUID, Integer> entry : exposureCounters.entrySet()) {
            if (entry.getValue() > maxExposure) {
                mostExposedId = entry.getKey();
                maxExposure = entry.getValue();
            }
        }

        if (mostExposedId == null) return; // Personne n'est resté assez longtemps

        NocturnePlayer target = game().getPlayerManager().get(mostExposedId);
        if (target == null || !target.isAlive()) return;

        game().getCurrentRound().getEmbrasementManager().embrase(mostExposedId, EmbrasementCause.TORCHE, nocturnePlayer.getPlayerId());

    }

    // -------------------------------------------------------------------------
    // Flag "rayonnement actif" — partagé avec EtouffementFlammeAbility
    // -------------------------------------------------------------------------

    /**
     * Éteint le Rayonnement pour la manche en cours.
     * Appelé par {@link EtouffementFlammeAbility}.
     */
    public void extinguish() { this.rayonnementActif = false; }

    /** Retourne si le Rayonnement est actuellement actif pour cette instance. */
    public boolean isRayonnementActif() { return rayonnementActif; }

    @Override
    public @NotNull String getDescription() {
        return "Le joueur ayant passé le plus de temps dans un rayon de §7" + radius
                + "§r blocs autour de vous sera Embrasé en fin de phase.";
    }

    @Override
    public @Nullable Component getCannotExecuteMessage(@NotNull Player player, @NotNull NocturnePlayer nocturnePlayer) {
        return null; // Capacité passive, pas de message d'erreur
    }
}
