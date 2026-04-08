package me.TyAlternative.com.nocturne.listener;

import me.TyAlternative.com.nocturne.Nocturne;
import me.TyAlternative.com.nocturne.api.ability.Ability;
import me.TyAlternative.com.nocturne.api.ability.AbilityContext;
import me.TyAlternative.com.nocturne.api.ability.AbilityResult;
import me.TyAlternative.com.nocturne.api.ability.AbilityTrigger;
import me.TyAlternative.com.nocturne.api.phase.PhaseType;
import me.TyAlternative.com.nocturne.core.NocturneGame;
import me.TyAlternative.com.nocturne.player.NocturnePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * Listener principal des interactions joueur.
 *
 * <p>Responsabilités :
 * <ul>
 *   <li>Clic gauche sur joueur → trigger {@link AbilityTrigger#LEFT_CLICK_PLAYER} ou vote.</li>
 *   <li>Clic droit sur joueur → trigger {@link AbilityTrigger#RIGHT_CLICK_PLAYER} ou vote.</li>
 *   <li>Swap de main → détection simple/double via {@link DoubleSwapDetector},
 *       trigger {@link AbilityTrigger#SWAP_HAND} ou {@link AbilityTrigger#DOUBLE_SWAP_HAND}.</li>
 * </ul>
 */
@SuppressWarnings({"unused", "DataFlowIssue"})
public final class PlayerInteractionListener implements Listener {

    private final NocturneGame game;
    private final DoubleSwapDetector doubleSwapDetector;


    /**
     * @param game               façade du jeu
     * @param doubleSwapDetector détecteur de double-swap
     */
    public PlayerInteractionListener(
            @NotNull NocturneGame game,
            @NotNull DoubleSwapDetector doubleSwapDetector
    ) {
        this.game = game;
        this.doubleSwapDetector = doubleSwapDetector;
    }

    // -------------------------------------------------------------------------
    // Clic gauche sur joueur
    // -------------------------------------------------------------------------

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player attacker)) return;
        if (!(event.getDamager() instanceof Player target)) return;

        if (!game.isGameRunning()) return;
        event.setCancelled(true);

        NocturnePlayer nocturneAttacker = game.getPlayerManager().get(attacker);
        NocturnePlayer nocturneTarget = game.getPlayerManager().get(target);
        if (nocturneAttacker == null || !nocturneAttacker.isAlive()) return;
        if (nocturneTarget   == null || !nocturneTarget.isAlive())   return;

        PhaseType phase = game.getCurrentPhase();
        boolean emptyHand = attacker.getInventory().getItemInMainHand().isEmpty();

        if (phase == PhaseType.VOTE) {
            handleVoteClick(nocturneAttacker, target.getUniqueId());
            return;
        }

        if (phase.isInGame()) {
            notifyPlayerInteract(attacker, nocturneAttacker, target, nocturneTarget, emptyHand);
            dispatchAbilityTrigger(attacker, nocturneAttacker, target, AbilityTrigger.LEFT_CLICK_PLAYER, emptyHand);
        }
    }

    // -------------------------------------------------------------------------
    // Clic droit sur joueur
    // -------------------------------------------------------------------------

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onInteractAtEntity(PlayerInteractAtEntityEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;
        if (!(event.getRightClicked() instanceof Player target)) return;

        Player attacker = event.getPlayer();
        if (!game.isGameRunning()) return;

        NocturnePlayer nocturneAttacker = game.getPlayerManager().get(attacker);
        NocturnePlayer nocturneTarget = game.getPlayerManager().get(target);
        if (nocturneAttacker == null || !nocturneAttacker.isAlive()) return;
        if (nocturneTarget   == null || !nocturneTarget.isAlive())   return;

        PhaseType phase = game.getCurrentPhase();
        boolean emptyHand = attacker.getInventory().getItemInMainHand().isEmpty();

        if (phase == PhaseType.VOTE) {
            handleVoteClick(nocturneAttacker, target.getUniqueId());
            return;
        }

        if (phase.isInGame()) {
            notifyPlayerInteract(attacker, nocturneAttacker, target, nocturneTarget, emptyHand);
            dispatchAbilityTrigger(attacker, nocturneAttacker, target, AbilityTrigger.RIGHT_CLICK_PLAYER, emptyHand);
        }
    }

    // -------------------------------------------------------------------------
    // Swap de main
    // -------------------------------------------------------------------------

    @EventHandler(priority = EventPriority.HIGH)
    public void onSwapHand(PlayerSwapHandItemsEvent event) {
        if (!game.isGameRunning()) return;

        Player player = event.getPlayer();
        NocturnePlayer nocturnePlayer = game.getPlayerManager().get(player);
        if (nocturnePlayer == null || !nocturnePlayer.isAlive()) return;
        if (!game.getCurrentPhase().isInGame()) return;

        event.setCancelled(true);

        boolean emptyHand = player.getInventory().getItemInMainHand().isEmpty();
        boolean isDouble = game.getSettings().isDoubleSwapEnabled() && doubleSwapDetector.detect(player.getUniqueId());

        if (isDouble) {
            dispatchAbilityTrigger(player, nocturnePlayer, null, AbilityTrigger.DOUBLE_SWAP_HAND, emptyHand);
        } else {
            // Simple swap : exécuter après le délai de détection
            long delayTicks = Math.max(1L, (game.getSettings().getDoubleSwapMaxDelayMs() / 50L) + 1L);

            UUID playerId = player.getUniqueId();
            Nocturne.getInstance().getServer().getScheduler().runTaskLater(
                    Nocturne.getInstance(),
                    () -> {
                        if (! doubleSwapDetector.hasPendingSwap(playerId)) return;
                        // Re-résoudre le joueur (il peut s'être déconnecté)
                        Player playerLater = Nocturne.getInstance().getServer().getPlayer(playerId);
                        if (playerLater == null) return;
                        NocturnePlayer npLater = game.getPlayerManager().get(playerId);
                        if (npLater == null || !npLater.isAlive()) return;
                        // Lire emptyHand au moment de l'exécution, pas de la capture
                        boolean emptyHandLater = playerLater.getInventory().getItemInMainHand().isEmpty();
                        dispatchAbilityTrigger(playerLater, npLater, null, AbilityTrigger.SWAP_HAND, emptyHandLater);
                    },
                    delayTicks
            );

        }
    }

    // -------------------------------------------------------------------------
    // Vote
    // -------------------------------------------------------------------------

    private void handleVoteClick(@NotNull NocturnePlayer voter, @NotNull UUID targetId) {
        game.getVoteManager().castVote(voter.getPlayerId(), targetId);
    }

    // -------------------------------------------------------------------------
    // Dispatch des capacités
    // -------------------------------------------------------------------------

    /**
     * Notifie toutes les capacités des joueurs vivants d'une interaction joueur.
     */
    private void notifyPlayerInteract(
            @NotNull Player caster,
            @NotNull NocturnePlayer nocturneCaster,
            @NotNull Player receiver,
            @NotNull NocturnePlayer nocturneReceiver,
            boolean emptyHand
    ) {
        for (NocturnePlayer np : game.getPlayerManager().getAlive()) {
            if (!np.hasRole()) continue;
            Player player = np.getPlayer();
            if (player == null) continue;
            try {
                np.getRole().onPlayerInteract(caster, nocturneCaster, receiver, nocturneReceiver, emptyHand);
            } catch (Exception e) {
                Nocturne.getInstance().getLogger().severe(
                        "[Nocturne] Erreur onPlayerInteract pour %s : %s"
                                .formatted(player.getName(), e.getMessage())
                );
            }
        }

    }

    /**
     * Dispatche un trigger de capacité vers toutes les capacités du joueur correspondant.
     *
     * @param caster  joueur déclencheur
     * @param np      données Nocturne du déclencheur
     * @param target  cible éventuelle (null pour SWAP_HAND, DOUBLE_SWAP_HAND)
     * @param trigger type de déclencheur
     * @param empty   main vide au moment du déclenchement
     */
    private void dispatchAbilityTrigger(
            @NotNull Player caster,
            @NotNull NocturnePlayer np,
            @Nullable Player target,
            @NotNull AbilityTrigger trigger,
            boolean empty
    ) {
        if (!np.hasRole()) return;

        PhaseType phase = game.getCurrentPhase();

        for (Ability ability : np.getRole().getAbilities()) {
            if (ability.getTrigger() != trigger) continue;

            AbilityContext context = buildContext(target, empty);

            AbilityResult result = game.getAbilityManager().tryExecute(
                    caster, np, ability, context, phase
            );

            // Feedback au joueur
            if (result.hasFeedback()) {
                caster.sendMessage(result.getFeedbackMessage()); // TODO: Passer par Message Manager
            }

            // Notifier tous les joueurs vivants si la capacité a réussi
            if (result.isSuccess()) {
                for (NocturnePlayer nocturneOther : game.getPlayerManager().getAlive()) {
                    if (!nocturneOther.hasRole()) continue;
                    Player otherPlayer = nocturneOther.getPlayer();
                    if (otherPlayer == null) continue;
                    try {
                        nocturneOther.getRole().onActiveAbilityUsed(caster, np, context, result);
                    } catch (Exception e) {
                        Nocturne.getInstance().getLogger().severe(
                                "[Nocturne] Erreur onActiveAbilityUsed pour %s : %s"
                                        .formatted(otherPlayer.getName(), e.getMessage())
                        );
                    }
                }
            }
        }

    }

    private @NotNull AbilityContext buildContext(@Nullable Player target, boolean emptyHand) {
        if (target == null) return AbilityContext.noTarget();
        return emptyHand ?
                AbilityContext.withEmptyHand(target) :
                AbilityContext.withTarget(target);
    }

}
