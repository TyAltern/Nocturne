package me.TyAlternative.com.nocturne.ability.impl.info;

import me.TyAlternative.com.nocturne.Nocturne;
import me.TyAlternative.com.nocturne.ability.AbilityIds;
import me.TyAlternative.com.nocturne.ability.AbstractAbility;

import me.TyAlternative.com.nocturne.api.ability.*;

import me.TyAlternative.com.nocturne.api.phase.PhaseType;
import me.TyAlternative.com.nocturne.core.phase.PhaseContext;
import me.TyAlternative.com.nocturne.mechanics.particle.DelayedParticleScheduler;
import me.TyAlternative.com.nocturne.player.NocturnePlayer;

import net.kyori.adventure.text.Component;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;


/**
 * Réverbération Lumineuse — capacité passive de la Lueur.
 *
 * <p>Symétrique aux Échos d'Interaction, mais axée sur les <em>cibles</em> plutôt que
 * sur les utilisateurs de capacité. Chaque fois qu'un joueur est ciblé par la
 * capacité active d'un autre joueur, la Lueur voit des particules dorées
 * ({@link Particle#END_ROD}) apparaître autour de ce joueur après un délai
 * aléatoire de 10 à 60 secondes.
 *
 * <p>Un joueur leurre aléatoire reçoit également des particules au même moment.
 * Géré par {@link DelayedParticleScheduler}.
 *
 * <p>Un {@link AbilityContext} avec une cible explicite ({@code context.hasTarget()})
 * est nécessaire pour enregistrer un signal.
 */

public final class ReverberationLumineuseAbility extends AbstractAbility {

    /** ID public pour référence depuis d'autres classes (ex: AbilityIds). */
    public static final String ABILITY_ID = AbilityIds.REVERBERATION_LUMINEUSE; // réutilise la constante existante

    private static final int      MIN_DELAY = 2;  // secondes
    private static final int      MAX_DELAY = 3;  // secondes
    private static final Particle PARTICLE  = Particle.END_ROD;   // doré/blanc discret


    private DelayedParticleScheduler scheduler;
    private final List<UUID> reverberationPlayersId;

    private final Random random = game().getRandom();


    public ReverberationLumineuseAbility() {
        super(
                AbilityIds.REVERBERATION_LUMINEUSE,
                "Réverbération Lumineuse",
                "Vous percevez des particules dorées autour des joueurs ayant été ciblés "
                        + "par une capacité active (délai 2–3s). Un leurre est toujours inclus.",
                AbilityCategory.CAPACITY,
                AbilityUseType.PASSIVE,
                AbilityTrigger.AUTOMATIC
        );
        setAllowedPhases(PhaseType.GAMEPLAY);

        this.reverberationPlayersId = new ArrayList<>();
    }


    @Override
    public boolean canExecute(@NotNull Player player, @NotNull NocturnePlayer nocturnePlayer, @NotNull AbilityContext context) {
        return false;
    }

    @Override
    protected @NotNull AbilityResult executeLogic(@NotNull Player player, @NotNull NocturnePlayer nocturnePlayer, @NotNull AbilityContext context) {
        return AbilityResult.silentSuccess();
    }

    @Override
    public boolean supportsDrunk() {
        return true;
    }

    // -------------------------------------------------------------------------
    // Initialisation
    // -------------------------------------------------------------------------

    @Override
    public void onAssigned(@NotNull Player player, @NotNull NocturnePlayer nocturnePlayer) {
        super.onAssigned(player, nocturnePlayer);

        // Créer le scheduler une fois pour la durée de vie de cette ability
        scheduler = new DelayedParticleScheduler(Nocturne.getInstance());
    }



    // -------------------------------------------------------------------------
    // Hook : une capacité active vient d'être utilisée
    // -------------------------------------------------------------------------
    @Override
    public void onGameplayPhaseStart(@NotNull Player player, @NotNull NocturnePlayer nocturnePlayer, @NotNull PhaseContext phaseContext) {
        super.onGameplayPhaseStart(player, nocturnePlayer, phaseContext);
        if (scheduler == null) return;

        reverberationPlayersId.clear();

        int phaseDuration = phaseContext.getRemainingSeconds();
        int minPhaseDuration = phaseDuration / 6;
        int maxPhaseDuration = phaseDuration / 2;

        scheduleDecoy(nocturnePlayer, minPhaseDuration, maxPhaseDuration);

    }

    @Override
    public void onActiveAbilityUsed(
            @NotNull Player caster,
            @NotNull NocturnePlayer nocturneCaster,
            @NotNull AbilityContext context,
            @NotNull AbilityResult result
    ) {
        if (!result.isSuccess()) return;
        if (!context.hasTarget())  return; // seules les capacités avec cible explicite comptent

        NocturnePlayer self = getOwner();
        if (self == null || scheduler == null) return;

        if (isDrunk()) {
            manageDrunkAbility(self);
        }
        Player target   = context.getTarget();
        if (target == null) return;
        NocturnePlayer nocturneTarget = game().getPlayerManager().get(target);
        if (nocturneTarget == null) return;

        // Ne pas s'observer soi-même
        if (target.getUniqueId().equals(self.getPlayerId())) return;

        // Vérifie que le caster n'es pas déja affiché
        if (reverberationPlayersId.contains(target.getUniqueId())) return;

        // Ajoute le caster à la pool.
        reverberationPlayersId.add(target.getUniqueId());

        scheduler.schedule(
                self,
                nocturneTarget,
                PARTICLE,
                MIN_DELAY,
                MAX_DELAY
        );
    }

    private void manageDrunkAbility(@NotNull NocturnePlayer self) {
        switch (random.nextInt(0,3)) {
            case 1 -> scheduleDecoy(self, MIN_DELAY, MAX_DELAY);

            case 2 -> {
                scheduleDecoy(self, MIN_DELAY, MAX_DELAY);
                scheduleDecoy(self, MIN_DELAY, MAX_DELAY);
            }
        }
    }

    private void scheduleDecoy(@NotNull NocturnePlayer self, int min_delay, int max_delay) {
        NocturnePlayer decoy = findDecoy();

        if (decoy == null) return;
        if (reverberationPlayersId.contains(decoy.getPlayerId())) return;

        reverberationPlayersId.add(decoy.getPlayerId());

        scheduler.schedule(
                self,
                decoy,
                PARTICLE,
                min_delay,
                max_delay
        );
    }

    private @Nullable NocturnePlayer findDecoy() {
        List<NocturnePlayer> players = new ArrayList<>(game().getPlayerManager().getAlive().stream()
                .filter(np -> !reverberationPlayersId.contains(np.getPlayerId()))
                .toList());
        players.remove(getOwner());
        if (players.isEmpty()) return null;

        return players.get(random.nextInt(players.size()));
    }


    @Override
    public @Nullable Component getCannotExecuteMessage(@NotNull Player p, @NotNull NocturnePlayer np) {
        return null;
    }

}
