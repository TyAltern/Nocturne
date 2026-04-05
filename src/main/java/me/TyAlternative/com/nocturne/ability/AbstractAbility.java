package me.TyAlternative.com.nocturne.ability;

import me.TyAlternative.com.nocturne.Nocturne;
import me.TyAlternative.com.nocturne.api.ability.*;
import me.TyAlternative.com.nocturne.api.phase.PhaseType;
import me.TyAlternative.com.nocturne.core.NocturneGame;
import me.TyAlternative.com.nocturne.player.NocturnePlayer;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Classe de base de toutes les capacités du plugin.
 *
 * <p>Fournit l'implémentation commune de :
 * <ul>
 *   <li>Identité (id, nom, description, catégorie, type, trigger)</li>
 *   <li>Phases autorisées pour l'exécution</li>
 *   <li>Limites d'utilisation via {@link UsageLimit}</li>
 *   <li>Mode "drunk" (comportement erroné)</li>
 *   <li>Visibilité dans la présentation du rôle</li>
 *   <li>Accès au {@link NocturneGame} via {@link Nocturne#getGame()}</li>
 * </ul>
 *
 * <h2>Créer une nouvelle capacité</h2>
 * <pre>{@code
 * public class EmbrasementAbility extends AbstractAbility {
 *
 *     public EmbrasementAbility() {
 *         super(
 *             AbilityIds.EMBRASEMENT,
 *             "Embrasement",
 *             "Description...",
 *             AbilityCategory.CAPACITY,
 *             AbilityUseType.ACTIVE,
 *             AbilityTrigger.RIGHT_CLICK_PLAYER
 *         );
 *         setUsageLimit(UsageLimit.perRound(1));
 *     }
 *
 *     @Override
 *     public boolean canExecute(Player player, NocturnePlayer np, AbilityContext ctx) {
 *         return ctx.hasTarget() && ctx.isEmptyHand();
 *     }
 *
 *     @Override
 *     protected AbilityResult executeLogic(Player player, NocturnePlayer np, AbilityContext ctx) {
 *         // logique principale
 *     }
 * }
 * }</pre>
 *
 * <h2>Mode drunk</h2>
 * Pour activer le mode drunk, surcharger {@link #supportsDrunk()} pour retourner {@code true}
 * puis surcharger {@link #executeDrunkLogic} pour définir le comportement erroné.
 */
public abstract class AbstractAbility implements Ability {

    // -------------------------------------------------------------------------
    // Identité (final, définie au constructeur)
    // -------------------------------------------------------------------------

    private final String id;
    private final String displayName;
    private final String description;
    private final AbilityCategory category;
    private final AbilityUseType useType;
    private final AbilityTrigger trigger;

    // -------------------------------------------------------------------------
    // Configuration (modifiable par les sous-classes via setters protégés)
    // -------------------------------------------------------------------------

    /** Phases pendant lesquelles cette capacité peut être déclenchée. */
    private final List<PhaseType> allowedPhases = new ArrayList<>();

    /** Limite d'utilisation. Par défaut illimitée. */
    private UsageLimit usageLimit = UsageLimit.unlimited();

    /** Intervalle en ticks pour les capacités TICKS. -1 = non applicable. */
    private int tickInterval = -1;

    /** Cette capacité est-elle masquée dans la présentation du rôle ? */
    private boolean hidden = false;

    /** Mode drunk actif sur cette capacité. */
    private boolean drunk = false;

    // -------------------------------------------------------------------------
    // Constructeur
    // -------------------------------------------------------------------------

    /**
     * @param id          identifiant unique, utiliser une constante de {@link AbilityIds}
     * @param displayName nom affiché aux joueurs
     * @param description description affichée à l'assignation du rôle
     * @param category    catégorie visuelle
     * @param useType     mode d'utilisation
     * @param trigger     déclencheur de la capacité
     */
    public AbstractAbility(
            @NotNull String id,
            @NotNull String displayName,
            @NotNull String description,
            @NotNull AbilityCategory category,
            @NotNull AbilityUseType useType,
            @NotNull AbilityTrigger trigger
    ) {
        this.id = id;
        this.displayName = displayName;
        this.description = description;
        this.category = category;
        this.useType = useType;
        this.trigger = trigger;

        // Phase autorisée par défaut : GAMEPLAY
        this.allowedPhases.add(PhaseType.GAMEPLAY);
    }

    // -------------------------------------------------------------------------
    // Accès au jeu
    // -------------------------------------------------------------------------

    /**
     * Accès au gestionnaire de jeu central.
     * Disponible uniquement pendant une partie active.
     */
    protected @NotNull NocturneGame game() {
        return Nocturne.getInstance().getGame();
    }

    // -------------------------------------------------------------------------
    // Identité — implémentation de Ability
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
    public final @NotNull AbilityCategory getCategory() {
        return category;
    }

    @Override
    public final @NotNull AbilityUseType getUseType() {
        return useType;
    }

    @Override
    public final @NotNull AbilityTrigger getTrigger() {
        return trigger;
    }


    // -------------------------------------------------------------------------
    // Configuration — implémentation de Ability
    // -------------------------------------------------------------------------

    @Override
    public int getTickInterval() {
        return tickInterval;
    }

    @Override
    public boolean isHidden() {
        return hidden;
    }

    @SuppressWarnings("RedundantMethodOverride")
    @Override
    public boolean supportsDrunk() {
        return false;
    }

    @Override
    public boolean isDrunk() {
        return supportsDrunk() && drunk;
    }

    @Override
    public void setDrunk(boolean drunk) {
        this.drunk = drunk;
    }


    // -------------------------------------------------------------------------
    // Phases autorisées
    // -------------------------------------------------------------------------

    /**
     * {@code true} si cette capacité peut être déclenchée pendant la phase {@code type}.
     */
    public boolean isPhaseAllowed(@NotNull PhaseType type) {
        return allowedPhases.contains(type);
    }

    /** Retourne une vue non-modifiable des phases autorisées. */
    public @NotNull List<PhaseType> getAllowedPhases() {
        return Collections.unmodifiableList(allowedPhases);
    }

    // -------------------------------------------------------------------------
    // Limites d'utilisation
    // -------------------------------------------------------------------------

    /** Retourne la limite d'utilisation configurée pour cette capacité. */
    public @NotNull UsageLimit getUsageLimit() {
        return usageLimit;
    }

    // -------------------------------------------------------------------------
    // Logique d'exécution — point d'entrée publique
    // -------------------------------------------------------------------------


    /**
     * Point d'entrée de l'exécution, appelé par l'
     * {@link AbilityManager}.
     *
     * <p>Redirige vers {@link #executeDrunkLogic} si le mode drunk est actif,
     * sinon vers {@link #executeLogic}.
     */
    @Override
    public @NotNull AbilityResult execute(
            @NotNull Player player,
            @NotNull NocturnePlayer nocturnePlayer,
            @NotNull AbilityContext context) {
        if (isDrunk()) {
            return executeDrunkLogic(player, nocturnePlayer, context);
        }
        return executeLogic(player, nocturnePlayer, context);
    }

    /**
     * Logique principale de la capacité.
     * À implémenter obligatoirement dans chaque sous-classe.
     */
    protected abstract @NotNull AbilityResult executeLogic(
            @NotNull Player player,
            @NotNull NocturnePlayer nocturnePlayer,
            @NotNull AbilityContext context
    );


    /**
     * Logique exécutée lorsque la capacité est en mode "drunk".
     * Par défaut retourne un {@link AbilityResult#silentSuccess()} sans effet.
     *
     * <p>Surcharger cette méthode conjointement avec {@link #supportsDrunk()}
     * pour définir un comportement erroné ou aléatoire.
     */
    protected @NotNull AbilityResult executeDrunkLogic(
            @NotNull org.bukkit.entity.Player player,
            @NotNull NocturnePlayer nocturnePlayer,
            @NotNull AbilityContext context
    ) {
        return AbilityResult.silentSuccess();
    }


    // -------------------------------------------------------------------------
    // Message d'impossibilité — implémentation par défaut
    // -------------------------------------------------------------------------

    @Override
    public @Nullable Component getCannotExecuteMessage(
            @NotNull Player player,
            @NotNull NocturnePlayer nocturnePlayer
    ) {
        return Component.text("§cVous ne pouvez pas utiliser cette capacité pour le moment.");
    }

    // -------------------------------------------------------------------------
    // Setters protégés — à appeler uniquement dans le constructeur des sous-classes
    // -------------------------------------------------------------------------

    /**
     * Définit la limite d'utilisation de cette capacité.
     *
     * @param limit limite à appliquer, jamais {@code null}
     */
    protected final void setUsageLimit(@NotNull UsageLimit limit) {
        this.usageLimit = limit;
    }

    /**
     * Remplace entièrement les phases autorisées par la liste fournie.
     *
     * @param phases phases pendant lesquelles la capacité peut être déclenchée
     */
    protected final void setAllowedPhases(@NotNull PhaseType... phases) {
        this.allowedPhases.clear();
        Collections.addAll(this.allowedPhases, phases);
    }

    /**
     * Définit l'intervalle en ticks pour les capacités avec {@link AbilityTrigger#TICKS}.
     *
     * @param ticks intervalle en ticks, doit être &gt; 0
     * @throws IllegalArgumentException si la capacité n'a pas le trigger {@link AbilityTrigger#TICKS}
     * @throws IllegalArgumentException si {@code ticks} ≤ 0
     */
    protected final void setTickInterval(int ticks) {
        if (trigger != AbilityTrigger.TICKS) {
            throw new IllegalArgumentException(
                    "setTickInterval() appelé sur une capacité dont le trigger n'est pas TICKS (id=%s)".formatted(id)
            );
        }
        if (ticks <= 0) {
            throw new IllegalArgumentException("L'intervalle de ticks doit être > 0, reçu : " + ticks);
        }
        this.tickInterval = ticks;
    }

    /**
     * Marque cette capacité comme cachée dans la présentation du rôle.
     * À appeler pour les capacités internes ou les malédictions invisibles.
     */
    protected final void setHidden(boolean hidden) {
        this.hidden = hidden;
    }


    // -------------------------------------------------------------------------
    // Object
    // -------------------------------------------------------------------------

    @Override
    public String toString() {
        return "Ability[%s, %s, %s]".formatted(id, useType, trigger);
    }
}
