package me.TyAlternative.com.nocturne.api.ability;


import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Résultat immuable retourné par l'exécution d'une capacité.
 *
 * <p>Encapsule trois informations :
 * <ol>
 *   <li>{@link #isSuccess()} — la capacité a-t-elle produit son effet ?</li>
 *   <li>{@link #getFeedbackMessage()} — message optionnel à envoyer au lanceur.</li>
 *   <li>{@link #countsAsUse()} — cette exécution doit-elle être comptabilisée
 *       dans les limites d'utilisation ? Permet à une capacité passive de s'exécuter
 *       en tâche de fond sans consommer de "charge".</li>
 * </ol>
 *
 * <p>Utiliser les fabriques statiques :
 * <pre>{@code
 * AbilityResult.success();
 * AbilityResult.success(Component.text("Vous avez embrasé X"));
 * AbilityResult.failure(Component.text("Ce joueur est protégé !"));
 * AbilityResult.silentSuccess(); // succès passif, pas de comptage
 * }</pre>
 */
public class AbilityResult {

    private final boolean success;
    private final @Nullable Component feedbackMessage;
    private final boolean countAsUse;

    public AbilityResult(boolean success, @Nullable Component feedbackMessage, boolean countAsUse) {
        this.success = success;
        this.feedbackMessage = feedbackMessage;
        this.countAsUse = countAsUse;
    }

    // -------------------------------------------------------------------------
    // Statics Fabrics
    // -------------------------------------------------------------------------

    /** Succès sans message, comptabilisé comme une utilisation. */
    public static @NotNull AbilityResult success() {
        return new AbilityResult(true, null, true);
    }

    /** Succès avec message de feedback, comptabilisé comme une utilisation. */
    public static @NotNull AbilityResult success(@NotNull Component message) {
        return new AbilityResult(true, message, true);
    }

    /**
     * Succès silencieux : la capacité s'est exécutée, mais cette exécution
     * n'est pas comptabilisée dans les limites d'utilisation.
     * Utilisé typiquement par les capacités passives à ticks.
     */
    public static @NotNull AbilityResult silentSuccess() {
        return new AbilityResult(true, null, false);
    }

    /** Échec avec message explicatif envoyé au lanceur. */
    public static @NotNull AbilityResult failure(@NotNull Component reason) {
        return new AbilityResult(false, reason, false);
    }

    /**
     * Échec silencieux : aucun message, aucun comptage.
     * Utilisé lorsque la condition d'échec est normale et ne nécessite pas de retour visuel.
     */
    public static @NotNull AbilityResult silentFailure() {
        return new AbilityResult(false, null, false);
    }


    // -------------------------------------------------------------------------
    // Getters
    // -------------------------------------------------------------------------


    /** {@code true} si la capacité a produit son effet attendu. */
    public boolean isSuccess() {
        return success;
    }

    /**
     * Message de feedback à afficher au lanceur, ou {@code null} si aucun.
     *
     * @see #hasFeedback()
     */
    public @Nullable Component getFeedbackMessage() {
        return feedbackMessage;
    }

    /** {@code true} si un message de feedback est disponible. */
    public boolean hasFeedback() {
        return feedbackMessage != null;
    }

    /**
     * {@code true} si cette exécution doit être comptabilisée dans les limites d'utilisation.
     * Toujours {@code false} en cas d'échec.
     */
    public boolean countsAsUse() {
        return countAsUse && success;
    }
}
