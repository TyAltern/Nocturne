package me.TyAlternative.com.nocturne.ability;

import me.TyAlternative.com.nocturne.api.ability.Ability;
import me.TyAlternative.com.nocturne.api.ability.AbilityContext;
import me.TyAlternative.com.nocturne.api.ability.AbilityResult;
import me.TyAlternative.com.nocturne.api.phase.PhaseType;
import me.TyAlternative.com.nocturne.player.NocturnePlayer;
import me.TyAlternative.com.nocturne.player.PlayerManager;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Gestionnaire unifié des usages et des cooldowns de toutes les capacités.
 *
 * <p>Fusionne les responsabilités de l'ancien {@code AbilityUsageManager} et du
 * {@code CooldownManager} en un seul composant cohérent. Toute logique de
 * "peut-on exécuter cette capacité ?" passe par ici, à l'exception de la
 * logique propre à la capacité elle-même ({@link Ability#canExecute}).
 *
 * <h2>Vérifications effectuées avant exécution</h2>
 * <ol>
 *   <li>Le joueur est vivant.</li>
 *   <li>La phase courante est autorisée pour cette capacité.</li>
 *   <li>La capacité n'est pas en cooldown.</li>
 *   <li>La limite d'utilisation n'est pas atteinte.</li>
 *   <li>{@link Ability#canExecute} retourne {@code true}.</li>
 * </ol>
 *
 * <h2>Clé interne</h2>
 * Les usages et cooldowns sont indexés par {@code (UUID joueur, String abilityId)}.
 * Cela fonctionne naturellement puisque chaque joueur possède sa propre instance de rôle.
 */
@SuppressWarnings({"unused", "FieldCanBeLocal"})
public final class AbilityManager {

    // -------------------------------------------------------------------------
    // Données internes
    // -------------------------------------------------------------------------

    /** Clé composite (playerId + abilityId) -> données d'usage. */
    private final Map<String, UsageData> usageData = new HashMap<>();

    /** Clé composite (playerId + abilityId) -> timestamp d'expiration du cooldown en ms. */
    private final Map<String, Long> cooldowns = new HashMap<>();

    private final PlayerManager playerManager;

    /**
     * @param playerManager gestionnaire des joueurs, pour vérifier l'état vivant
     */
    public AbilityManager(PlayerManager playerManager) {
        this.playerManager = playerManager;
    }

    // -------------------------------------------------------------------------
    // Point d'entrée principal
    // -------------------------------------------------------------------------

    /**
     * Tente d'exécuter une capacité pour un joueur donné.
     *
     * <p>Effectue toutes les vérifications préalables dans l'ordre décrit dans la Javadoc
     * de classe. Si toutes passent, délègue à {@link Ability#execute} et enregistre
     * l'utilisation si {@link AbilityResult#countsAsUse()} est vrai.
     *
     * @param player         joueur qui tente d'utiliser la capacité
     * @param nocturnePlayer données Nocturne du joueur
     * @param ability        capacité à exécuter
     * @param context        contexte d'exécution
     * @param currentPhase   phase de jeu courante
     * @return résultat de l'exécution, ou un résultat d'échec avec message si une
     *         vérification préalable échoue
     */
    public @NotNull AbilityResult tryExecute(
            @NotNull Player player,
            @NotNull NocturnePlayer nocturnePlayer,
            @NotNull Ability ability,
            @NotNull AbilityContext context,
            @NotNull PhaseType currentPhase
    ) {
        // 1. Joueur vivant
        if (!nocturnePlayer.isAlive()) return AbilityResult.silentFailure();

        // 2. Phase autorisée
        if (ability instanceof AbstractAbility abstractAbility
                && !abstractAbility.isPhaseAllowed(currentPhase)) {
            return AbilityResult.silentFailure();
        }

        // 3. Cooldown actif
        if (hasCooldown(player.getUniqueId(), ability.getId())) {
            int remaining = getRemainingCooldownSeconds(player.getUniqueId(), ability.getId());
            return AbilityResult.failure(
                    Component.text(
                            "§cCette capacité est en recharge encore §e" + remaining + "s§c."
                    )
            );
        }

        // 4. Limite d'utilisation atteinte
        if (!hasUsesRemaining(player.getUniqueId(), ability)) {
            if (ability instanceof AbstractAbility abstractAbility) {
                Component msg = abstractAbility.getCannotExecuteMessage(player, nocturnePlayer);
                return msg != null ? AbilityResult.failure(msg) : AbilityResult.silentFailure();
            }

            return AbilityResult.silentFailure();
        }

        // 5. Logique propre à la capacité
        if (!ability.canExecute(player, nocturnePlayer, context)) {
            Component msg = ability.getCannotExecuteMessage(player, nocturnePlayer);
            return msg != null ? AbilityResult.failure(msg) : AbilityResult.silentFailure();
        }

        // Exécution
        AbilityResult result = ability.execute(player, nocturnePlayer, context);

        // Enregistrement de l'usage si nécessaire
        if (result.countsAsUse()) {
            recordUsage(player.getUniqueId(), ability);
        }

        return result;
    }

    // -------------------------------------------------------------------------
    // Cooldowns
    // -------------------------------------------------------------------------

    /**
     * Définit un cooldown pour une capacité.
     *
     * @param playerId        UUID du joueur
     * @param abilityId       identifiant de la capacité
     * @param durationSeconds durée du cooldown en secondes
     */
    public void setCooldown(@NotNull UUID playerId, @NotNull String abilityId, int durationSeconds) {
        String key = makeKey(playerId, abilityId);
        long expiresAt = System.currentTimeMillis() + (durationSeconds * 1000L);
        cooldowns.put(key, expiresAt);
    }

    /** {@code true} si la capacité est actuellement en cooldown pour ce joueur. */
    public boolean hasCooldown(@NotNull UUID playerId, @NotNull String abilityId) {
        String key = makeKey(playerId, abilityId);
        Long expiresAt = cooldowns.get(key);
        if (expiresAt == null) return false;
        if (System.currentTimeMillis() >= expiresAt) {
            cooldowns.remove(key);
            return false;
        }
        return true;
    }

    /**
     * Retourne le temps de recharge restant en secondes arrondis à la seconde supérieure.
     * Retourne {@code 0} si aucun cooldown n'est actif.
     */
    public int getRemainingCooldownSeconds(@NotNull UUID playerId, @NotNull String abilityId) {
        String key = makeKey(playerId, abilityId);
        Long expiresAt = cooldowns.get(key);
        if (expiresAt == null) return 0;
        long remaining = expiresAt - System.currentTimeMillis();
        return (int) Math.max(0, Math.ceil(remaining / 1000.0));
    }

    /** Retire le cooldown d'une capacité pour un joueur. */
    public void removeCooldown(@NotNull UUID playerId, @NotNull String abilityId) {
        cooldowns.remove(makeKey(playerId, abilityId));
    }

    // -------------------------------------------------------------------------
    // Limites d'utilisation
    // -------------------------------------------------------------------------

    /**
     * {@code true} si le joueur peut encore utiliser la capacité au regard de sa limite.
     * Retourne toujours {@code true} pour les capacités à utilisation illimitée.
     */
    public boolean hasUsesRemaining(@NotNull UUID playerId, @NotNull Ability ability) {
        if (!(ability instanceof AbstractAbility abstractAbility)) return true;
        UsageLimit limit = abstractAbility.getUsageLimit();
        if (limit.isUnlimited()) return true;

        UsageData data = getOrCreateUsageData(playerId, ability.getId());
        int used = limit.getScope() == UsageLimit.Scope.PER_ROUND ? data.usesThisRound : data.usesThisGame;

        return used < limit.getMaxUses();
    }

    /**
     * Retourne le nombre d'utilisations restantes pour la capacité, ou {@code -1}
     * si la capacité est illimitée.
     */
    public int getRemainingUsed(@NotNull UUID playerId, @NotNull Ability ability) {
        if (!(ability instanceof AbstractAbility abstractAbility)) return -1;
        UsageLimit limit = abstractAbility.getUsageLimit();
        if (limit.isUnlimited()) return -1;

        UsageData data = getOrCreateUsageData(playerId, ability.getId());
        int used = limit.getScope() == UsageLimit.Scope.PER_ROUND ? data.usesThisRound : data.usesThisGame;

        return Math.max(0, limit.getMaxUses() - used);
    }

    /**
     * Enregistre manuellement une utilisation, indépendamment de l'exécution.
     * Utilisé par certaines mécaniques spéciales.
     */
    public void recordUsage(@NotNull UUID playerId, @NotNull Ability ability) {
        UsageData data = getOrCreateUsageData(playerId, ability.getId());
        data.usesThisGame++;
        data.usesThisRound++;
    }

    // -------------------------------------------------------------------------
    // Réinitialisations
    // -------------------------------------------------------------------------

    /**
     * Remet à zéro les compteurs de manche pour tous les joueurs vivants.
     * Appelé au début de chaque nouvelle phase de Gameplay.
     */
    public void resetRoundUsagesForAll() {
        for (UsageData data : usageData.values()) {
            data.usesThisRound = 0;
        }
    }

    /**
     * Vide entièrement toutes les données d'usage et de cooldown.
     * Appelé en fin de partie.
     */
    public void clearAll() {
        usageData.clear();
        cooldowns.clear();
    }

    // -------------------------------------------------------------------------
    // Utilitaires privés
    // -------------------------------------------------------------------------

    private @NotNull String makeKey(@NotNull UUID playerId, @NotNull String abilityId) {
        return playerId + ":" + abilityId;
    }

    private @NotNull UsageData getOrCreateUsageData(@NotNull UUID playerId, @NotNull String abilityId) {
        return usageData.computeIfAbsent(makeKey(playerId, abilityId), k -> new UsageData());
    }

    /**
     * Structure interne de comptage des utilisations d'une capacité pour un joueur.
     */
    private static final class UsageData {
        int usesThisRound = 0;
        int usesThisGame  = 0;
    }
}
