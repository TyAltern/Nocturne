package me.TyAlternative.com.nocturne.role;

import me.TyAlternative.com.nocturne.Nocturne;
import me.TyAlternative.com.nocturne.api.ability.Ability;
import me.TyAlternative.com.nocturne.api.ability.AbilityContext;
import me.TyAlternative.com.nocturne.api.ability.AbilityResult;
import me.TyAlternative.com.nocturne.api.role.Role;
import me.TyAlternative.com.nocturne.api.role.RoleTeam;
import me.TyAlternative.com.nocturne.api.role.RoleType;
import me.TyAlternative.com.nocturne.ability.AbstractAbility;
import me.TyAlternative.com.nocturne.core.NocturneGame;
import me.TyAlternative.com.nocturne.elimination.EliminationCause;
import me.TyAlternative.com.nocturne.mechanics.vote.VoteEntry;
import me.TyAlternative.com.nocturne.player.NocturnePlayer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.logging.Logger;

/**
 * Classe de base de tous les rôles du plugin.
 *
 * <p>Fournit l'implémentation commune de :
 * <ul>
 *   <li>Identité du rôle (id, nom, description, type, équipe, icône)</li>
 *   <li>Gestion des capacités via {@link #registerAbility}, {@link #registerHiddenAbility},
 *       {@link #registerDrunkAbility}</li>
 *   <li>Délégation des hooks d'événements à chaque capacité avec isolation des erreurs</li>
 *   <li>Envoi du message de présentation à l'assignation</li>
 * </ul>
 *
 * <h2>Créer un nouveau rôle</h2>
 * <pre>{@code
 * public class Etincelle extends AbstractRole {
 *
 *     public static final String ID = "ETINCELLE";
 *
 *     public Etincelle() {
 *         super(ID, "§c§lL'Étincelle", "Description...",
 *               RoleType.FLAMME, RoleTeam.SOLITAIRE, Material.BLAZE_POWDER);
 *
 *         registerAbility(new EmbrasementAbility());
 *         registerAbility(new PoudreChemineeAbility());
 *     }
 * }
 * }</pre>
 *
 * <h2>Isolation des erreurs dans les hooks</h2>
 * Chaque hook délègue aux capacités via {@link #dispatchToAbilities}, qui wrappe
 * chaque appel dans un try/catch individuel. Une erreur dans une capacité n'empêche
 * pas les autres capacités ni les autres joueurs de recevoir l'événement.
 */
@SuppressWarnings("unused")
public abstract class AbstractRole implements Role {

    // -------------------------------------------------------------------------
    // Identité
    // -------------------------------------------------------------------------

    private final String id;
    private final String displayName;
    private final String description;
    private final RoleType type;
    private RoleTeam team;
    private final Material guiIcon;

    /** Liste interne des capacités. Jamais exposée directement. */
    private final List<Ability> abilities = new ArrayList<>();

    // -------------------------------------------------------------------------
    // Constructeur
    // -------------------------------------------------------------------------

    /**
     * @param id          identifiant unique en majuscules (ex: {@code "ETINCELLE"})
     * @param displayName nom affiché aux joueurs, avec codes couleur
     * @param description objectif du rôle affiché à l'assignation
     * @param type        catégorie (Flamme, Bâton, Neutre)
     * @param team        équipe de victoire partagée
     * @param guiIcon     icône dans le GUI de composition
     */
    public AbstractRole(
            @NotNull String id,
            @NotNull String displayName,
            @NotNull String description,
            @NotNull RoleType type,
            @NotNull Material guiIcon,
            @NotNull RoleTeam team
    ) {
        this.id = id;
        this.displayName = displayName;
        this.description = description;
        this.type = type;
        this.guiIcon = guiIcon;
        this.team = team;
    }


    // -------------------------------------------------------------------------
    // Accès au jeu
    // -------------------------------------------------------------------------

    protected @NotNull NocturneGame game() {
        return Nocturne.getInstance().getGame();
    }

    private @NotNull Logger logger() {
        return Nocturne.getInstance().getLogger();
    }

    // -------------------------------------------------------------------------
    // Identité — implémentation de Role
    // -------------------------------------------------------------------------

    @Override
    public @NotNull String getId() {
        return id;
    }

    @Override
    public @NotNull String getDisplayName() {
        return displayName;
    }

    @Override
    public @NotNull String getDescription() {
        return description;
    }

    @Override
    public @NotNull RoleType getType() {
        return type;
    }

    @Override
    public @NotNull RoleTeam getTeam() {
        return team;
    }

    @Override
    public void setTeam(@NotNull RoleTeam team) {
        this.team = team;
    }

    @Override
    public @NotNull Material getGuiIcon() {
        return guiIcon;
    }


    // -------------------------------------------------------------------------
    // Capacités — implémentation de Role
    // -------------------------------------------------------------------------

    /**
     * Retourne une vue non-modifiable de toutes les capacités enregistrées sur ce rôle,
     * incluant les capacités cachées et drunk.
     */
    @Override
    public @Unmodifiable @NotNull List<Ability> getAbilities() {
        return Collections.unmodifiableList(abilities);
    }

    @Override
    public @Nullable Ability getAbility(@NotNull String abilityId) {
        for (Ability ability : abilities) {
            if (ability.getId().equals(abilityId)) return ability;
        }
        return null;
    }

    @Override
    public boolean hasAbility(@NotNull String abilityId) {
        return getAbility(abilityId) != null;
    }


    // -------------------------------------------------------------------------
    // Enregistrement des capacités (à appeler uniquement dans le constructeur)
    // -------------------------------------------------------------------------

    /**
     * Enregistre une capacité visible sur ce rôle.
     *
     * @param ability capacité à ajouter, jamais {@code null}
     * @return la capacité enregistrée (pour chaînage si nécessaire)
     * @throws IllegalArgumentException si {@code ability} est {@code null}
     */
    protected final @NotNull Ability registerAbility(@NotNull Ability ability) {
        abilities.add(ability);
        return ability;
    }
    /**
     * Enregistre une capacité invisible dans la présentation du rôle.
     * La capacité fonctionne normalement, mais n'apparaît pas dans le message d'assignation.
     *
     * @param ability capacité à masquer, jamais {@code null}
     * @return la capacité enregistrée
     */
    protected final @NotNull Ability registerHiddenAbility(@NotNull Ability ability) {
        if (ability instanceof AbstractAbility abstractAbility) abstractAbility.setHidden(true);

        abilities.add(ability);
        return ability;
    }
    /**
     * Enregistre une capacité en mode "drunk" sur ce rôle.
     * La capacité est visible, mais son comportement est erroné ou aléatoire.
     *
     * @param ability capacité à rendre "drunk", jamais {@code null}
     * @return la capacité enregistrée
     */
    protected final @NotNull Ability registerDrunkAbility(@NotNull Ability ability) {
        ability.setDrunk(true);
        abilities.add(ability);
        return ability;
    }

    // -------------------------------------------------------------------------
    // Hooks — implémentation de Role avec délégation isolée
    // -------------------------------------------------------------------------

    /**
     * Appelé à l'assignation du rôle.
     * Envoie le message de présentation via {@link me.TyAlternative.com.nocturne.ui.MessageManager},
     * puis délègue à chaque capacité.
     *
     * <p>Surcharger cette méthode pour ajouter un comportement spécifique,
     * en appelant {@code super.onAssigned()} pour conserver la présentation standard.
     */
    @Override
    public void onAssigned(@NotNull Player player, @NotNull NocturnePlayer nocturnePlayer) {
        // Présentation du rôle au joueur
        game().getMessageManager().sendRolePresentation(player, this);

        // Délégation aux capacités
        dispatchToAbilities(ability -> ability.onAssigned(player, nocturnePlayer));
    }

    @Override
    public void onGameplayPhaseStart(@NotNull Player player, @NotNull NocturnePlayer nocturnePlayer) {
        dispatchToAbilities(ability -> ability.onGameplayPhaseStart(player, nocturnePlayer));
    }

    @Override
    public void onGameplayPhaseEnd(@NotNull Player player, @NotNull NocturnePlayer nocturnePlayer) {
        dispatchToAbilities(ability -> ability.onGameplayPhaseEnd(player, nocturnePlayer));
    }

    @Override
    public void onVotePhaseStart(@NotNull Player player, @NotNull NocturnePlayer nocturnePlayer) {
        dispatchToAbilities(ability -> ability.onVotePhaseStart(player, nocturnePlayer));
    }

    @Override
    public void onVotePhaseEnd(@NotNull Player player, @NotNull NocturnePlayer nocturnePlayer) {
        dispatchToAbilities(ability -> ability.onVotePhaseEnd(player, nocturnePlayer));
    }

    @Override
    public void afterVoteCalculation(@NotNull Player player, @NotNull NocturnePlayer nocturnePlayer, @Nullable UUID votedPlayerId, @NotNull List<VoteEntry> allVotes) {
        dispatchToAbilities(ability -> ability.afterVoteCalculation(player, nocturnePlayer, votedPlayerId, allVotes));
    }

    @Override
    public void onPlayerInteract(@NotNull Player caster, @NotNull NocturnePlayer nocturneCaster, @NotNull Player receiver, @NotNull NocturnePlayer nocturneReceiver, boolean emptyHand) {
        dispatchToAbilities(ability -> ability.onPlayerInteract(caster, nocturneCaster, receiver, nocturneReceiver, emptyHand));
    }

    @Override
    public void onActiveAbilityUsed(@NotNull Player caster, @NotNull NocturnePlayer nocturneCaster, @NotNull AbilityContext context, @NotNull AbilityResult result) {
        dispatchToAbilities(ability -> ability.onActiveAbilityUsed(caster, nocturneCaster, context, result));
    }

    @Override
    public void onEliminated(@NotNull Player player, @NotNull NocturnePlayer nocturnePlayer, @NotNull EliminationCause cause) {
        dispatchToAbilities(ability -> ability.onEliminated(player, nocturnePlayer, cause));
    }

    @Override
    public void onOtherEliminated(@NotNull Player self, @NotNull Player eliminated, @NotNull NocturnePlayer nocturneEliminated, @NotNull EliminationCause cause) {
        dispatchToAbilities(ability -> ability.onOtherEliminated(self, eliminated, nocturneEliminated, cause));
    }
    // -------------------------------------------------------------------------
    // Dispatch isolé — cœur de la robustesse des hooks
    // -------------------------------------------------------------------------

    /**
     * Délègue une action à chaque capacité enregistrée de façon isolée.
     *
     * <p>Chaque capacité est appelée dans son propre try/catch. Si une capacité
     * lève une exception, elle est loggée et les capacités suivantes continuent
     * de recevoir l'événement normalement.
     *
     * @param action lambda à exécuter pour chaque capacité
     */
    private void dispatchToAbilities(@NotNull AbilityAction action) {
        for (Ability ability : abilities) {
            try {
                action.execute(ability);
            } catch (Exception e) {
                logger().severe(
                        "[Nocturne] Erreur dans la capacité '%s' du rôle '%s' : %s"
                                .formatted(ability.getId(), id, e.getMessage())
                );
                logger().severe("Stacktrace : " + e);
            }
        }
    }


    /**
     * Interface fonctionnelle interne pour le dispatch isolé des hooks.
     * Séparée de {@link Consumer} pour permettre les exceptions vérifiées.
     */
    @FunctionalInterface
    private interface AbilityAction {
        void execute(@NotNull Ability ability) throws Exception;
    }


    // -------------------------------------------------------------------------
    // Object
    // -------------------------------------------------------------------------


    @Override
    public String toString() {
        return "Role[%s, %s, %s".formatted(id, type, team);
    }
}
