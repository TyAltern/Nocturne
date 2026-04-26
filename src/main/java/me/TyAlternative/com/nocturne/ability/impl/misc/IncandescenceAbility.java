package me.TyAlternative.com.nocturne.ability.impl.misc;

import me.TyAlternative.com.nocturne.Nocturne;
import me.TyAlternative.com.nocturne.ability.AbilityIds;
import me.TyAlternative.com.nocturne.ability.AbstractAbility;
import me.TyAlternative.com.nocturne.api.ability.*;
import me.TyAlternative.com.nocturne.api.phase.PhaseType;
import me.TyAlternative.com.nocturne.api.role.Role;
import me.TyAlternative.com.nocturne.core.phase.PhaseContext;
import me.TyAlternative.com.nocturne.mechanics.ability.RandomAbilityPool;
import me.TyAlternative.com.nocturne.player.NocturnePlayer;

import me.TyAlternative.com.nocturne.role.AbstractRole;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;



/**
 * Incandescence — capacité passive du Tison.
 *
 * <p>À chaque début de phase de Gameplay, le Tison reçoit une capacité aléatoire
 * différente de la précédente, choisie dans le {@link RandomAbilityPool}. Cette capacité
 * est active pour toute la manche et se comporte comme une capacité normale.
 *
 * <h2>Fonctionnement</h2>
 * Incandescence agit comme une <em>capacité-conteneur</em> : elle délègue tous les
 * hooks d'événements à la capacité courante ({@link #currentAbility}). L'exécution
 * directe (clic, swap) est aussi déléguée si la capacité courante est active.
 *
 * <h2>Limites</h2>
 * <ul>
 *   <li>La capacité tirée est instanciée fraîche à chaque manche (état isolé).</li>
 *   <li>Le {@code owner} est injecté dans la sous-capacité dès le tirage.</li>
 *   <li>Les capacités à trigger {@link AbilityTrigger#TICKS} ne sont pas gérées
 *       ici (le TickingAbilityManager écoute l'ability d'Incandescence, pas la sous-capacité).</li>
 * </ul>
 */

public final class IncandescenceAbility extends AbstractAbility {

    private static final RandomAbilityPool POOL = new RandomAbilityPool();

    /** Capacité active pour la manche en cours. Null avant la première manche. */
    private @Nullable AbstractAbility currentAbility = null;

    /** ID de la capacité de la manche précédente (pour garantir la variété). */
    private @Nullable String lastAbilityId = null;

    public IncandescenceAbility() {
        super(
                AbilityIds.INCANDESCENCE,
                "Incandescence",
                "Chaque manche, vous obtenez une capacité aléatoire différente "
                        + "(Bâton ou Flamme). Elle change à chaque début de phase.",
                Material.AIR,
                AbilityCategory.CAPACITY,
                AbilityUseType.PASSIVE,
                AbilityTrigger.AUTOMATIC
        );
        setAllowedPhases(PhaseType.GAMEPLAY, PhaseType.VOTE);
    }

    // -------------------------------------------------------------------------
    // Tirage d'une nouvelle capacité en début de manche
    // -------------------------------------------------------------------------

    @Override
    public void onGameplayPhaseStart(@NotNull Player player, @NotNull NocturnePlayer nocturnePlayer, @NotNull PhaseContext phaseContext) {
        drawNewAbility(player, nocturnePlayer);
        if (currentAbility == null) return;

        Nocturne.getInstance().getServer().getScheduler().runTask(
                Nocturne.getInstance(),
                () -> dispatchSafely(() -> currentAbility.onGameplayPhaseStart(player, nocturnePlayer, phaseContext))
        );
    }


    /**
     * Tire une nouvelle capacité aléatoire, l'injecte au joueur et l'informe.
     */
    private void drawNewAbility(@NotNull Player player, @NotNull NocturnePlayer nocturnePlayer) {
        Role role = nocturnePlayer.getRole();
        if (role == null) return;

        // Retirer l'ancienne ability APRÈS le dispatch courant via le scheduler
        if (lastAbilityId != null) {
            String idToRemove = lastAbilityId;
            Nocturne.getInstance().getServer().getScheduler().runTask(
                    Nocturne.getInstance(),
                    () -> {
                        role.removeAbility(idToRemove);
                        game().getActionBarManager().updatePlayerActionBar(player, nocturnePlayer, game().getCurrentPhase());
                    }
            );
        }

        currentAbility = POOL.draw(lastAbilityId);

        if (currentAbility == null) {

            player.sendMessage(Component.text(
                    "§7[Incandescence] §fIl semblerait que§c les Flammes§f soient contre vous et vous empêche d'utiliser votre rôle."
            ));
            return;
        }
        lastAbilityId = currentAbility.getId();
        ((AbstractRole) role).registerAbility(currentAbility);

        // Injecter le propriétaire dans la sous-capacité (même mécanique qu'AbstractRole)
        currentAbility.injectOwner(nocturnePlayer);

        // Notifier le joueur
        // TODO: MODIFY ABILITY MESSAGE
        player.sendMessage(Component.text(
                "§7[Incandescence] §fCette manche : §e" + currentAbility.getDisplayName()
        ));
        player.sendMessage(game().getMessageManager().buildAbilityLine(currentAbility));

        dispatchSafely(() -> currentAbility.onAssigned(player, nocturnePlayer));
        // Propager onAssigned à la sous-capacité (initialisation du scheduler, etc.)
    }

    // -------------------------------------------------------------------------
    // Délégation des hooks à la capacité courante
    // -------------------------------------------------------------------------

//    @Override
//    public void onGameplayPhaseEnd(@NotNull Player player, @NotNull NocturnePlayer nocturnePlayer, @NotNull PhaseContext phaseContext) {
//        if (currentAbility == null) return;
//        dispatchSafely(() -> currentAbility.onGameplayPhaseEnd(player, nocturnePlayer, phaseContext));
//    }
//
//    @Override
//    public void onVotePhaseStart(@NotNull Player player, @NotNull NocturnePlayer nocturnePlayer, @NotNull PhaseContext phaseContext) {
//        if (currentAbility == null) return;
//        dispatchSafely(() -> currentAbility.onVotePhaseStart(player, nocturnePlayer, phaseContext));
//    }
//
//    @Override
//    public void onVotePhaseEnd(@NotNull Player player, @NotNull NocturnePlayer nocturnePlayer, @NotNull PhaseContext phaseContext) {
//        if (currentAbility == null) return;
//        dispatchSafely(() -> currentAbility.onVotePhaseEnd(player, nocturnePlayer, phaseContext));
//    }
//
//    @Override
//    public void afterVoteCalculation(@NotNull Player player, @NotNull NocturnePlayer nocturnePlayer, @Nullable UUID votedPlayerId, @NotNull List<VoteEntry> allVotes) {
//        if (currentAbility == null) return;
//        dispatchSafely(() -> currentAbility.afterVoteCalculation(player, nocturnePlayer, votedPlayerId, allVotes));
//    }
//
//    @Override
//    public void onEliminated(@NotNull Player player, @NotNull NocturnePlayer nocturnePlayer, @NotNull EliminationCause cause) {
//        if (currentAbility == null) return;
//        dispatchSafely(() -> currentAbility.onEliminated(player, nocturnePlayer, cause));
//    }
//
//    @Override
//    public void onOtherEliminated(@NotNull Player self, @NotNull Player eliminated, @NotNull NocturnePlayer nocturneEliminated, @NotNull EliminationCause cause) {
//        if (currentAbility == null) return;
//        dispatchSafely(() -> currentAbility.onOtherEliminated(self, eliminated, nocturneEliminated, cause));
//    }
//
//    @Override
//    public void onPlayerInteract(@NotNull Player caster, @NotNull NocturnePlayer nocturneCaster, @NotNull Player receiver, @NotNull NocturnePlayer nocturneReceiver, boolean emptyHand) {
//        if (currentAbility == null) return;
//        dispatchSafely(() -> currentAbility.onPlayerInteract(caster, nocturneCaster, receiver, nocturneReceiver, emptyHand));
//    }
//
//    @Override
//    public void onActiveAbilityUsed(@NotNull Player caster, @NotNull NocturnePlayer nocturneCaster, @NotNull AbilityContext context, @NotNull AbilityResult result) {
//        if (currentAbility == null) return;
//        dispatchSafely(() -> currentAbility.onActiveAbilityUsed(caster, nocturneCaster, context, result));
//    }
//
//
//    // -------------------------------------------------------------------------
//    // Exécution directe — déléguer à la sous-capacité si elle est active
//    // -------------------------------------------------------------------------
//
//
    @Override
    public boolean canExecute(@NotNull Player p, @NotNull NocturnePlayer np, @NotNull AbilityContext ctx) {
//        if (currentAbility == null) return false;
//        return currentAbility.canExecute(p, np, ctx);
        return false;
    }


    @Override
    protected @NotNull AbilityResult executeLogic(@NotNull Player p, @NotNull NocturnePlayer np, @NotNull AbilityContext ctx) {
//        if (currentAbility == null) return AbilityResult.silentFailure();
//        return currentAbility.execute(p, np, ctx);
        return AbilityResult.silentSuccess();
    }

    // -------------------------------------------------------------------------
    // Accesseur
    // -------------------------------------------------------------------------

    /**
     * Retourne la capacité active pour la manche en cours, ou {@code null}
     * si aucune manche n'a encore commencé.
     */
    public @Nullable AbstractAbility getCurrentAbility() {
        return currentAbility;
    }


    // -------------------------------------------------------------------------
    // Utilitaire
    // -------------------------------------------------------------------------

    /** Exécute un hook de manière isolée (ne lève pas d'exception). */
    private void dispatchSafely(@NotNull Runnable action) {
        try {
            action.run();
        } catch (Exception e) {
            // Loggé silencieusement — l'Incandescence ne doit pas planter le Tison
        }
    }

    @Override
    public @Nullable Component getCannotExecuteMessage(@NotNull Player player, @NotNull NocturnePlayer nocturnePlayer) {
        if (currentAbility == null) return Component.text("§cAucune capacité active cette manche.");
        return currentAbility.getCannotExecuteMessage(player, nocturnePlayer);

    }
}
