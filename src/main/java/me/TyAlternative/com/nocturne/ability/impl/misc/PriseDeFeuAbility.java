package me.TyAlternative.com.nocturne.ability.impl.misc;

import me.TyAlternative.com.nocturne.ability.AbilityIds;
import me.TyAlternative.com.nocturne.ability.AbstractAbility;
import me.TyAlternative.com.nocturne.ability.impl.protection.SolitudeMortelleAbility;
import me.TyAlternative.com.nocturne.api.ability.*;
import me.TyAlternative.com.nocturne.api.phase.PhaseType;
import me.TyAlternative.com.nocturne.core.phase.PhaseContext;
import me.TyAlternative.com.nocturne.mechanics.embrasement.EmbrasementCause;
import me.TyAlternative.com.nocturne.mechanics.embrasement.EmbrasementManager;
import me.TyAlternative.com.nocturne.mechanics.protection.ProtectionManager;
import me.TyAlternative.com.nocturne.player.NocturnePlayer;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Prise de Feu — malédiction passive du Gaz.
 *
 * <p>En fin de phase de Gameplay, si un ou plusieurs joueurs embrasés se trouvent
 * à moins de 10.0 blocs du Gaz, celui-ci vole leurs embrasements :
 * <ul>
 *   <li>Les joueurs embrasés à portée sont <strong>protégés</strong> (leur embrasement
 *       est retiré).</li>
 *   <li>Le Gaz <strong>s'embrase</strong> à leur place, même s'il était protégé
 *       par {@link SolitudeMortelleAbility}.</li>
 * </ul>
 *
 * <p>Si plusieurs joueurs embrasés sont à portée, le Gaz en absorbe autant
 * qu'il y en a, mais ne s'embrase qu'une seule fois (il ne peut mourir qu'une fois).
 *
 * <h2>Interaction avec SolitudeMortelle</h2>
 * La Prise de Feu est traitée <em>après</em> SolitudeMortelle dans {@code onGameplayPhaseEnd}.
 * Elle bypasse explicitement la protection de SolitudeMortelle en retirant le joueur
 * de la liste des protégés avant de l'embraser.
 */
@SuppressWarnings("DataFlowIssue")
public final class PriseDeFeuAbility extends AbstractAbility {

    /** Rayon de détection des joueurs embrasés (en blocs). */
    private final double radius = 10.0;

    public PriseDeFeuAbility() {

        super(
                AbilityIds.PRISE_DE_FEU,
                "Prise de Feu",
                "Si un joueur Embrasé se trouve à moins de " + 10.0
                        + " blocs de vous en fin de phase, vous volez son Embrasement "
                        + "(il est protégé, vous vous embrasez à sa place, même si protégé).",
                Material.AIR,
                AbilityCategory.CURSE,
                AbilityUseType.PASSIVE,
                AbilityTrigger.AUTOMATIC
        );
        setAllowedPhases(PhaseType.GAMEPLAY);
    }

    @Override
    public boolean canExecute(@NotNull Player player, @NotNull NocturnePlayer nocturnePlayer, @NotNull AbilityContext context) {
        return false;
    }

    @Override
    protected @NotNull AbilityResult executeLogic(@NotNull Player player, @NotNull NocturnePlayer nocturnePlayer, @NotNull AbilityContext context) {
        return AbilityResult.silentSuccess();
    }

    // -------------------------------------------------------------------------
    // Hook : fin de phase de Gameplay — vol d'embrasement
    // -------------------------------------------------------------------------

    @Override
    public void onGameplayPhaseEnd(@NotNull Player player, @NotNull NocturnePlayer nocturnePlayer, @NotNull PhaseContext phaseContext) {
        if (game().getCurrentRound() == null) return;
        EmbrasementManager embrasementManager = game().getCurrentRound().getEmbrasementManager();
        ProtectionManager protectionManager  = game().getCurrentRound().getProtectionManager();

        // Collecter les joueurs embrasés dans le rayon
        List<UUID> stolenIds = collectNearbyEmbrased(player, embrasementManager);
        if (stolenIds.isEmpty()) return;

        // Vol de chaque embrasement à portée
        for (UUID stolenId : stolenIds) {
            // Retirer l'embrasement de la victime originale
            embrasementManager.removeEmbrasement(stolenId);
        }

        // Le Gaz s'embrase à la place — bypass de toute protection
        // On force l'embrasement en retirant aussi les autres protections actives.
        protectionManager.removeProtection(nocturnePlayer.getPlayerId());

        // Embraser directement (le ProtectionManager est maintenant vide pour ce joueur)
        boolean result = embrasementManager.embrase(nocturnePlayer.getPlayerId(), EmbrasementCause.PRISE_DE_FEU);

        if (result) {
            player.sendMessage(Component.text(
                    "§7[Prise de Feu] §cVous avez absorbé l'Embrasement d'un joueur proche. Votre sacrifice est honorable..."
            ));
        }
    }

    // -------------------------------------------------------------------------
    // Utilitaires
    // -------------------------------------------------------------------------

    /**
     * Retourne la liste des UUID de joueurs vivants embrasés se trouvant
     * à moins de 10.0 blocs du Gaz.
     */
    private @NotNull List<UUID> collectNearbyEmbrased(
            @NotNull Player player,
            @NotNull EmbrasementManager embrasementManager
    ) {
        List<UUID> result = new ArrayList<>();
        double radiusSquared = radius*radius;

        for (Map.Entry<UUID, EmbrasementCause> entry : embrasementManager.getAll().entrySet()) {
            UUID targetId = entry.getKey();
            NocturnePlayer nocturneTarget = game().getPlayerManager().get(targetId);
            if (nocturneTarget == null || !nocturneTarget.isAlive()) continue;

            Player targetPlayer = nocturneTarget.getPlayer();
            if (targetPlayer == null) continue;
            if (!targetPlayer.getWorld().equals(player.getWorld())) continue;

            double distanceSquared = player.getLocation().distanceSquared(targetPlayer.getLocation());
            if (distanceSquared <= radiusSquared) result.add(targetId);

        }
        return result;
    }

    @Override
    public @NotNull String getDescription() {
        return "Si un joueur Embrasé se trouve à moins de §6" + radius
                + "§r blocs de vous en fin de phase, vous volez son Embrasement "
                + "(il est protégé, vous vous embrasez à sa place, même si protégé).";
    }

    @Override
    public @Nullable Component getCannotExecuteMessage(@NotNull Player p, @NotNull NocturnePlayer np) {
        return null;
    }
}
