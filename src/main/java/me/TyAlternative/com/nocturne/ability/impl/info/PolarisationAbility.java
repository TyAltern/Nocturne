package me.TyAlternative.com.nocturne.ability.impl.info;

import me.TyAlternative.com.nocturne.ability.AbilityIds;
import me.TyAlternative.com.nocturne.ability.AbstractAbility;
import me.TyAlternative.com.nocturne.ability.DrunkSupport;
import me.TyAlternative.com.nocturne.ability.UsageLimit;
import me.TyAlternative.com.nocturne.api.ability.*;
import me.TyAlternative.com.nocturne.api.role.RoleType;
import me.TyAlternative.com.nocturne.core.phase.PhaseContext;
import me.TyAlternative.com.nocturne.player.NocturnePlayer;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@SuppressWarnings("DataFlowIssue")
public final class PolarisationAbility extends AbstractAbility {

    List<UUID> markedPlayers = new ArrayList<>();

    public PolarisationAbility() {
        super(
                AbilityIds.POLARISATION,
                "Polarisation",
                "X - il marque des joueurs puis sait cb de flamme (0-2 decoys)",
                Material.AIR,
                AbilityCategory.CAPACITY,
                AbilityUseType.ACTIVE,
                AbilityTrigger.RIGHT_CLICK_PLAYER
        );
        setUsageLimit(UsageLimit.unlimited());
    }

    @Override
    public DrunkSupport supportsDrunk() {
        return DrunkSupport.DEFAULT_LOGIC;
    }

    @Override
    protected @NotNull AbilityResult executeLogic(@NotNull Player player, @NotNull NocturnePlayer nocturnePlayer, @NotNull AbilityContext context) {
        markedPlayers.add(context.getTarget().getUniqueId());

        game().getAbilityManager().setCooldown(nocturnePlayer.getPlayerId(), getId(), game().getSettings().getPolarisationCooldown());

        return AbilityResult.success(
                Component.text("\n§6[Polarisation]§f Vous avez marqué une personne. §7(" + markedPlayers.size()+")")
        );
    }

    @Override
    public boolean canExecute(@NotNull Player player, @NotNull NocturnePlayer nocturnePlayer, @NotNull AbilityContext context) {
        if (!context.hasTarget() || !context.isEmptyHand()) return false;
        NocturnePlayer targetData = game().getPlayerManager().get(context.getTarget());
        return targetData != null && targetData.isAlive();
    }

    @Override
    public void onGameplayPhaseStart(@NotNull Player player, @NotNull NocturnePlayer nocturnePlayer, @NotNull PhaseContext phaseContext) {
        game().getAbilityManager().setCooldown(nocturnePlayer.getPlayerId(), getId(), game().getSettings().getPolarisationStartingCooldown());

        markedPlayers.clear();

        int minDecoy = game().getSettings().getPolarisationMinDecoy();
        int maxDecoy = game().getSettings().getPolarisationMaxDecoy();

        int numberOfDecoy = game().getRandom().nextInt(minDecoy, maxDecoy+1);
        List<NocturnePlayer> alive = game().getPlayerManager().getAlive();
        if (alive.isEmpty() || numberOfDecoy <= 0) return;
        for (int i = 0; i < numberOfDecoy; i++) {
            markedPlayers.add(alive.get(game().getRandom().nextInt(0,alive.size())).getPlayerId());
        }
    }

    @Override
    public void onGameplayPhaseEnd(@NotNull Player player, @NotNull NocturnePlayer nocturnePlayer, @NotNull PhaseContext phaseContext) {
        int numberOfFlamme = 0;
        for (UUID markedId : markedPlayers) {
            NocturnePlayer markedPlayer = game().getPlayerManager().get(markedId);
            if (markedPlayer.getRole().getType() == RoleType.FLAMME) {
                numberOfFlamme++;
            }
        }
        int markedSize = markedPlayers.size();
        if (isDrunk()) {
            int midPoint = Math.ceilDiv(markedSize,4);
            numberOfFlamme = game().getRandom().nextInt(midPoint-1, midPoint+2);
            numberOfFlamme = Math.min(Math.max(numberOfFlamme, 0), markedSize);
        }
        player.sendMessage("\n§6[Polarisation]§f Parmi les §e" + markedSize + " personne" + (markedSize>1?"s":"") + "§f marquée" + (markedSize>1?"s":"") + ", il y a §c" + numberOfFlamme + " Flamme" + (numberOfFlamme>1?"s":"") + "§f.");
    }
}
