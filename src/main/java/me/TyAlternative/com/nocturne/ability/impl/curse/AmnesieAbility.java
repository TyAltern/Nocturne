package me.TyAlternative.com.nocturne.ability.impl.curse;
import me.TyAlternative.com.nocturne.Nocturne;
import me.TyAlternative.com.nocturne.ability.AbilityIds;
import me.TyAlternative.com.nocturne.ability.AbstractAbility;
import me.TyAlternative.com.nocturne.api.ability.*;
import me.TyAlternative.com.nocturne.api.role.Role;
import me.TyAlternative.com.nocturne.api.role.RoleType;
import me.TyAlternative.com.nocturne.player.NocturnePlayer;
import me.TyAlternative.com.nocturne.role.AbstractRole;
import me.TyAlternative.com.nocturne.ui.MessageManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Amnésie — malédiction cachée du Silex.
 *
 * <p>À l'assignation, le Silex reçoit la présentation complète d'un rôle aléatoire
 * différent du sien — et le croit sincèrement. Il ne saura jamais qu'il est en réalité
 * un Silex. Les capacités du faux rôle sont présentées normalement.
 *
 * <h2>Comportement</h2>
 * <ul>
 *   <li>Dans {@link #onAssigned}, un rôle est tiré aléatoirement parmi les rôles
 *       enregistrés, en excluant les rôles {@code SILEX} et {@code ACIER}
 *       (pour ne pas révéler accidentellement l'équipe) et la Nuée.</li>
 *   <li>La présentation standard du {@link MessageManager}
 *       est interceptée : c'est le faux rôle qui est affiché, pas le vrai.</li>
 *   <li>La capacité est {@link #setHidden(boolean) cachée}, donc elle n'apparaît
 *       jamais dans aucun message de présentation.</li>
 * </ul>
 *
 * <h2>Ce que le Silex voit</h2>
 * Le Silex reçoit exactement le même message qu'un joueur ayant vraiment ce faux rôle,
 * y compris l'objectif, les capacités et les descriptions. Les capacités du faux rôle
 * ne sont pas enregistrées sur son vrai rôle et ne fonctionnent pas réellement —
 * seule la présentation est falsifiée.
 *
 * <h2>Ce que le Silex ne sait pas</h2>
 * <ul>
 *   <li>Son vrai rôle (Silex, Flamme, équipe Silex/Acier).</li>
 *   <li>Que sa mort révèlerait son vrai rôle si {@code reveal_role_on_death} est activé.</li>
 * </ul>
 */

@SuppressWarnings("DataFlowIssue")
public final class AmnesieAbility extends AbstractAbility {


    /**
     * IDs exclus du tirage : on ne peut pas donner au Silex l'apparence
     * d'un rôle qui trahirait son camp ou créerait une confusion mécanique.
     */
    private static final List<String> EXCLUDED_IDS = List.of("SILEX", "ACIER", "NUEE");

    public AmnesieAbility() {
        super(
                AbilityIds.AMNESIE,
                "Amnésie",
                "Vous ignorez votre vrai rôle. Un autre rôle vous a été présenté à la place.",
                Material.AIR,
                AbilityCategory.CURSE,
                AbilityUseType.PASSIVE,
                AbilityTrigger.AUTOMATIC
        );
        setHidden(true);
    }


    // -------------------------------------------------------------------------
    // Pas d'exécution directe — ability purement passive
    // -------------------------------------------------------------------------

    @Override
    public boolean canExecute(
            @NotNull Player player,
            @NotNull NocturnePlayer nocturnePlayer,
            @NotNull AbilityContext context
    ) {
        return false;
    }

    @Override
    protected @NotNull AbilityResult executeLogic(
            @NotNull Player player,
            @NotNull NocturnePlayer nocturnePlayer,
            @NotNull AbilityContext context
    ) {
        return AbilityResult.silentSuccess();
    }

    // -------------------------------------------------------------------------
    // Centre de la mécanique : présentation du faux rôle à l'assignation
    // -------------------------------------------------------------------------

    /**
     * Intercepte l'assignation pour afficher la présentation d'un faux rôle aléatoire.
     *
     * <p>Note : {@link AbstractRole#onAssigned} appelle
     * d'abord {@code game().getMessageManager().sendRolePresentation()} (vrai rôle),
     * puis délègue aux abilities. Cette méthode envoie ensuite la présentation du faux rôle,
     * qui <em>écrase visuellement</em> la présentation du vrai rôle puisqu'elle arrive
     * juste après dans le chat.
     *
     * <p>Le joueur voit donc deux messages : le vrai rôle (Silex, bref) et immédiatement
     * après la présentation complète et convaincante du faux rôle.
     * Pour éviter toute confusion, on envoie d'abord une ligne de séparation.
     */
    @Override
    public void onAssigned(@NotNull Player player, @NotNull NocturnePlayer nocturnePlayer) {
        Role fakeRole =  drawFakeRole();
        Role owner = nocturnePlayer.getRole();
//
        if (fakeRole == null || !(owner instanceof AbstractRole abstractRole)) return;
//
        Nocturne.getInstance().getServer().getScheduler().runTask(
                Nocturne.getInstance(),
                () -> {
                    for (Ability ability : fakeRole.getAbilities()) {
                        abstractRole.registerDrunkAbility(ability);
                        if (ability instanceof AbstractAbility abstractAbility) abstractAbility.injectOwner(nocturnePlayer);
                        ability.onAssigned(player, nocturnePlayer);
                        if (game().getPhaseManager().getCurrentContext() == null) continue;
                        ability.onGameplayPhaseStart(player, nocturnePlayer, game().getPhaseManager().getCurrentContext());
                        game().getActionBarManager().updatePlayerActionBar(player, nocturnePlayer,game().getCurrentPhase());
                    }
                    game().getMessageManager().sendRolePresentation(player, fakeRole);
                }
        );
    }

    // -------------------------------------------------------------------------
    // Tirage du faux rôle
    // -------------------------------------------------------------------------

    /**
     * Tire aléatoirement un rôle parmi les rôles enregistrés, en excluant
     * {@link #EXCLUDED_IDS} et les rôles de type {@link RoleType#NEUTRE}.
     *
     * <p>Préfère les Bâtons pour renforcer le camouflage : un Silex présenté
     * comme un Bâton est le scénario idéal pour le gameplay.
     *
     * @return instance fraîche du faux rôle, ou {@code null} si aucun candidat
     */
    private @Nullable Role drawFakeRole() {
        List<String> candidates = new ArrayList<>();

        List<String> possible = game().getSettings().getAmnesiaRoleCandidates();
        for (String roleId : possible) {
            if (EXCLUDED_IDS.contains(roleId)) continue;
            if (!game().getRoleRegistry().getRegisteredIds().contains(roleId)) continue;


//        }
//
//        for (String roleId : game().getRoleRegistry().getRegisteredIds()) {
//            if (EXCLUDED_IDS.contains(roleId)) continue;

            try {
                Role role = game().getRoleRegistry().create(roleId);
                if (!(role instanceof AbstractRole abstractRole) ) continue;
//                if (EXCLUDED_IDS.contains(roleId)) continue;
                if (!abstractRole.canBeDrunk()) continue;

//                if (abstractRole.getType() == RoleType.NEUTRE) continue;
                candidates.add(roleId);
            } catch (Exception ignored) {
                Nocturne.getInstance().getLogger().info("drawFakeRole error");
                // Rôle non instanciable — ignoré silencieusement
            }
        }

        if (candidates.isEmpty()) return null;

        String chosenId = candidates.get(game().getRandom().nextInt(candidates.size()));
        try {
            return game().getRoleRegistry().create(chosenId);
        } catch (Exception e) {
            Nocturne.getInstance().getLogger().info("create(chosenId) error");
            return null;
        }
    }

}
