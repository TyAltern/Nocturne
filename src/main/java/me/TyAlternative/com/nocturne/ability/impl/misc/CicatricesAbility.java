package me.TyAlternative.com.nocturne.ability.impl.misc;

import me.TyAlternative.com.nocturne.ability.AbilityIds;
import me.TyAlternative.com.nocturne.ability.AbstractAbility;

import me.TyAlternative.com.nocturne.ability.DrunkSupport;
import me.TyAlternative.com.nocturne.api.ability.*;
import me.TyAlternative.com.nocturne.elimination.EliminationCause;
import me.TyAlternative.com.nocturne.elimination.EliminationManager;
import me.TyAlternative.com.nocturne.player.NocturnePlayer;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Cicatrices — capacité passive du Calciné.
 *
 * <p>Si le Calciné est Embrasé par une Flamme, l'élimination est repoussée d'une
 * manche : il sera éliminé au début de la manche suivante lors du traitement des
 * Embrasements en attente. Le Calciné n'est pas informé de son Embrasement.
 *
 * <p>La logique de report est intégrée dans
 * {@link EliminationManager#processEmbrasements} :
 * lorsqu'un joueur avec Cicatrices est embrasé, son embrasement est enregistré
 * dans {@code pendingEliminationCause} et traité lors de la manche suivante.
 *
 * <p>En mode drunk : l'embrasement n'est pas retardé.
 */
public final class CicatricesAbility extends AbstractAbility {

    /**
     * Cause d'élimination en attente pour la prochaine manche.
     * {@code null} si aucun embrasement n'est en attente.
     */
    private @Nullable EliminationCause pendingEliminationCause = null;

    public CicatricesAbility() {
        super(
                AbilityIds.CICATRICES,
                "Cicatrices",
                "Votre Embrasement par les Flammes est retardé d'une manche. "
                        + "Vous n'êtes pas informé de votre condamnation.",
                Material.AIR,
                AbilityCategory.CAPACITY,
                AbilityUseType.PASSIVE,
                AbilityTrigger.AUTOMATIC
        );
    }

    @Override
    public boolean canExecute(@NotNull Player player,@NotNull NocturnePlayer nocturnePlayer,@NotNull AbilityContext context) {
        return false;
    }

    @Override
    protected @NotNull AbilityResult executeLogic(@NotNull Player player,@NotNull NocturnePlayer nocturnePlayer,@NotNull AbilityContext context) {
        return AbilityResult.silentSuccess();
    }

    @Override
    public DrunkSupport supportsDrunk() {
        return DrunkSupport.DEFAULT_LOGIC;
    }

    // -------------------------------------------------------------------------
    // API utilisée par EliminationManager
    // -------------------------------------------------------------------------

    /**
     * Tente d'enregistrer un embrasement en attente.
     *
     * @param cause cause de l'embrasement à retarder
     * @return {@code true} si l'embrasement a été retardé (capacité non-drunk, pas déjà en attente)
     */
    public boolean registerPendingEmbrasement(@NotNull EliminationCause cause) {
        if (isDrunk()) return false;
        if (pendingEliminationCause != null) return false; // Déjà un embrasement en attente
        pendingEliminationCause = cause;
        return true;
    }

    /**
     * {@code true} si un embrasement est en attente pour la prochaine manche.
     */
    public boolean hasPendingEmbrasement() {
        return pendingEliminationCause != null;
    }

    /**
     * Retourne la cause d'élimination en attente et la consomme.
     */
    public @Nullable EliminationCause consumePendingEmbrasement() {
        EliminationCause cause = pendingEliminationCause;
        pendingEliminationCause = null;
        return cause;
    }




    @Override
    public @Nullable Component getCannotExecuteMessage(@NotNull Player player,@NotNull NocturnePlayer nocturnePlayer) {
        return null;
    }
}
