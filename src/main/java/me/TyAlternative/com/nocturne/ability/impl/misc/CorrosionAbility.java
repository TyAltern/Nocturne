package me.TyAlternative.com.nocturne.ability.impl.misc;


import me.TyAlternative.com.nocturne.ability.AbilityIds;
import me.TyAlternative.com.nocturne.ability.AbstractAbility;
import me.TyAlternative.com.nocturne.ability.DrunkSupport;
import me.TyAlternative.com.nocturne.api.ability.*;
import me.TyAlternative.com.nocturne.api.phase.PhaseType;
import me.TyAlternative.com.nocturne.player.NocturnePlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Corrosion — capacité active de la Scorie, utilisable pendant la phase de Vote.
 *
 * <p>Bascule le poids de vote de la Scorie entre §c+2§f et §a-2§f.
 * Un vote de poids -2 est toujours comptabilisé comme un vote émis pour les
 * mécaniques qui vérifient si un joueur a voté.
 *
 * <p>État initial à l'assignation : poids = +2.
 *
 * <p>En mode drunk : aucune modification du poids de vote n'est effectué.
 */
public final class CorrosionAbility extends AbstractAbility {

    private final int weightNegative;
    private final int weightPositive;

    private boolean positiveMode = true;

    private int drunkWeight;

    public CorrosionAbility() {
        super(
                AbilityIds.CORROSION,
                "Corrosion",
                "Basculez votre poids de vote entre §c+2§f et §a-2§f. "
                        + "Un vote négatif est toujours compté comme un vote émis.",
                Material.AIR,
                AbilityCategory.CAPACITY,
                AbilityUseType.ACTIVE,
                AbilityTrigger.SWAP_HAND
        );
        weightNegative = game().getSettings().getCorrosionNegativeWeight();
        weightPositive = game().getSettings().getCorrosionPositiveWeight();


        setAllowedPhases(PhaseType.VOTE);
    }

    @Override
    public void onAssigned(@NotNull Player player, @NotNull NocturnePlayer nocturnePlayer) {
        if (!isDrunk()) nocturnePlayer.setVoteWeight(weightPositive);
        nocturnePlayer.setVoteGlowColor(TextColor.color(255, 85, 85)); // rouge
        drunkWeight = weightPositive;

    }

    @Override
    public boolean canExecute(
            @NotNull Player player,
            @NotNull NocturnePlayer nocturnePlayer,
            @NotNull AbilityContext context
    ) {
        return true;
    }

    @Override
    protected @NotNull AbilityResult executeLogic(@NotNull Player player, @NotNull NocturnePlayer nocturnePlayer, @NotNull AbilityContext context) {
        positiveMode = !positiveMode;
        int previousVoteWeight = nocturnePlayer.getVoteWeight();

        if (positiveMode) {
            nocturnePlayer.setVoteWeight(previousVoteWeight + (weightPositive-weightNegative));
            nocturnePlayer.setVoteGlowColor(TextColor.color(game().getSettings().getCorrosionPositiveColor().asRGB()));
            return AbilityResult.success(Component.text("§c[Corrosion] Modification du poids de vote : §e" + nocturnePlayer.getVoteWeight()));
        } else {
            nocturnePlayer.setVoteWeight(previousVoteWeight - (weightPositive-weightNegative));
            nocturnePlayer.setVoteGlowColor(TextColor.color(game().getSettings().getCorrosionNegativeColor().asRGB()));
            return AbilityResult.success(Component.text("§c[Corrosion] Modification du poids de vote : §e" + nocturnePlayer.getVoteWeight()));
        }
    }

    @Override
    protected @NotNull AbilityResult executeDrunkLogic(@NotNull Player player, @NotNull NocturnePlayer nocturnePlayer, @NotNull AbilityContext context) {
        positiveMode = !positiveMode;

        if (positiveMode) {
            drunkWeight = drunkWeight + (weightPositive-weightNegative);
            nocturnePlayer.setVoteGlowColor(TextColor.color(game().getSettings().getCorrosionPositiveColor().asRGB()));
            return AbilityResult.success(Component.text("§c[Corrosion] Modification du poids de vote : §e" + (drunkWeight - 1 + nocturnePlayer.getVoteWeight())));
        } else {
            drunkWeight = drunkWeight - (weightPositive-weightNegative);
            nocturnePlayer.setVoteGlowColor(TextColor.color(game().getSettings().getCorrosionNegativeColor().asRGB()));
            return AbilityResult.success(Component.text("§c[Corrosion] Modification du poids de vote : §e" + (drunkWeight -1 + nocturnePlayer.getVoteWeight())));
        }
    }

    @Override
    public DrunkSupport supportsDrunk() {
        return DrunkSupport.CUSTOM_LOGIC;
    }

    @Override
    public @NotNull String getDescription() {
        return "Basculez votre poids de vote entre §c+"+weightPositive+"§f et §a"+weightNegative+"§f. "
                        + "Un vote négatif est toujours compté comme un vote émis.";
    }
}
