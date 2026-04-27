package me.TyAlternative.com.nocturne.ability.impl.info;

import me.TyAlternative.com.nocturne.Nocturne;
import me.TyAlternative.com.nocturne.ability.AbilityIds;
import me.TyAlternative.com.nocturne.ability.AbstractAbility;
import me.TyAlternative.com.nocturne.ability.DrunkSupport;
import me.TyAlternative.com.nocturne.api.ability.*;
import me.TyAlternative.com.nocturne.api.phase.PhaseType;
import me.TyAlternative.com.nocturne.core.phase.PhaseContext;
import me.TyAlternative.com.nocturne.mechanics.particle.DelayedParticleScheduler;
import me.TyAlternative.com.nocturne.player.NocturnePlayer;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * <p>Chaque fois qu'un joueur utilise une capacité active sur quelqu'un,
 * l'Aurore voit des particules violettes ({@link Particle#WITCH}) apparaître autour
 * de ce joueur après un délai aléatoire de 2 à 3 secondes.
 *
 * <p>Un joueur leurre aléatoire reçoit également des particules au même moment
 * pour empêcher toute déduction certaine. Géré par {@link DelayedParticleScheduler}.
 *
 * <p>Seules les utilisations de succès génèrent un signal visuel.
 */
public final class DiffractionAbility extends AbstractAbility {

    private static final int      MIN_DELAY  = 2; // secondes
    private static final int      MAX_DELAY  = 3; // secondes
    private static final Particle PARTICLE   = Particle.WITCH; // Violet

    private DelayedParticleScheduler scheduler;
    private final List<UUID> diffractionPlayersId;

    private final Random random = game().getRandom();

    public DiffractionAbility() {
        super(
                AbilityIds.DIFFRACTION,
                "Diffraction",
                "Vous percevez des particules violettes autour des personnes ayant utilisé "
                        + "une capacité active après un délai. Un leurre est toujours inclus.",
                Material.AIR,
                AbilityCategory.CAPACITY,
                AbilityUseType.PASSIVE,
                AbilityTrigger.AUTOMATIC
        );
        setAllowedPhases(PhaseType.GAMEPLAY);

        this.diffractionPlayersId = new ArrayList<>();
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
    public DrunkSupport supportsDrunk() {
        return DrunkSupport.DEFAULT_LOGIC;
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

        diffractionPlayersId.clear();

        int phaseDuration = phaseContext.getRemainingSeconds();
        int minPhaseDuration = phaseDuration / 6;
        int maxPhaseDuration = phaseDuration / 2;

//        scheduleDecoy(nocturnePlayer, minPhaseDuration, maxPhaseDuration);

    }

    @Override
    public void onActiveAbilityUsed(@NotNull Player caster, @NotNull NocturnePlayer nocturneCaster, @NotNull AbilityContext context, @NotNull AbilityResult result) {
        if (!result.isSuccess()) return;

        NocturnePlayer self = getOwner();
        if (self == null || scheduler == null) return;


        if (isDrunk()) {
            manageDrunkAbility(self);
        }
        // Ne pas s'observer soi-même
//        if (caster.getUniqueId().equals(self.getPlayerId())) return;

        // Vérifie que le caster n'es pas déja affiché
        if (diffractionPlayersId.contains(nocturneCaster.getPlayerId())) return;

        // Ajoute le caster à la pool.
        diffractionPlayersId.add(nocturneCaster.getPlayerId());

        scheduler.schedule(
                self,
                nocturneCaster,
                PARTICLE,
                MIN_DELAY,
                MAX_DELAY
        );
    }

    private void manageDrunkAbility(@NotNull NocturnePlayer self) {
        int rng = random.nextInt(0,4);
        switch (rng) {
            case 1,2 -> scheduleDecoy(self, MIN_DELAY, MAX_DELAY);
            case 3 -> {
                scheduleDecoy(self, MIN_DELAY, MAX_DELAY);
                scheduleDecoy(self, MIN_DELAY, MAX_DELAY);
            }
        }
    }

    private void scheduleDecoy(@NotNull NocturnePlayer self, int min_delay, int max_delay) {
        NocturnePlayer decoy = findDecoy();

        if (decoy == null) return;
        if (diffractionPlayersId.contains(decoy.getPlayerId())) return;

        diffractionPlayersId.add(decoy.getPlayerId());

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
                .filter(np -> !diffractionPlayersId.contains(np.getPlayerId()))
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
