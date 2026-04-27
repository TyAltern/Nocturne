package me.TyAlternative.com.nocturne.ability.impl.info;

import me.TyAlternative.com.nocturne.ability.AbilityIds;
import me.TyAlternative.com.nocturne.ability.AbstractAbility;
import me.TyAlternative.com.nocturne.ability.DrunkSupport;
import me.TyAlternative.com.nocturne.api.ability.*;
import me.TyAlternative.com.nocturne.api.role.RoleType;
import me.TyAlternative.com.nocturne.elimination.EliminationCause;
import me.TyAlternative.com.nocturne.player.NocturnePlayer;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("DataFlowIssue")
public final class TransmissionAbility extends AbstractAbility {

    public TransmissionAbility() {
        super(
                AbilityIds.TRANSMISSION,
                "Transmission",
                "X - connaît le camp des morts",
                Material.AIR,
                AbilityCategory.CAPACITY,
                AbilityUseType.PASSIVE,
                AbilityTrigger.AUTOMATIC
        );
    }

    @Override
    public DrunkSupport supportsDrunk() {
        return DrunkSupport.DEFAULT_LOGIC;
    }

    @Override
    protected @NotNull AbilityResult executeLogic(@NotNull Player player, @NotNull NocturnePlayer nocturnePlayer, @NotNull AbilityContext context) {
        return AbilityResult.silentSuccess();
    }

    @Override
    public boolean canExecute(@NotNull Player player, @NotNull NocturnePlayer nocturnePlayer, @NotNull AbilityContext context) {
        return false;
    }

    @Override
    public void onOtherEliminated(@NotNull Player self, @NotNull Player eliminated, @NotNull NocturnePlayer nocturneEliminated, @NotNull EliminationCause cause) {
        super.onOtherEliminated(self, eliminated, nocturneEliminated, cause);

        if (!nocturneEliminated.hasRole()) return;
        RoleType type = nocturneEliminated.getRole().getType();
        if (isDrunk()) {
            int proba = game().getRandom().nextInt(0,100);
            if (proba < 10) type = RoleType.NEUTRE;
            else if (proba < 70) type = RoleType.BATON;
            else if (proba < 100) type = RoleType.FLAMME;
        }

        Component typeComp = switch (type) {
            case FLAMME -> Component.text(RoleType.FLAMME.getDisplayName(), RoleType.FLAMME.getColor());
            case NEUTRE -> Component.text(RoleType.NEUTRE.getDisplayName(), RoleType.NEUTRE.getColor());
            default -> Component.text(RoleType.BATON.getDisplayName(), RoleType.BATON.getColor()); // & BATON
        };
        self.sendMessage(Component.text("\n§6[Transmission]§f Vous avez la conviction profonde qu'il s'agissait d'un" + (type == RoleType.FLAMME? "e ":" ")).append(typeComp).append(Component.text("§f.")));

    }
}
