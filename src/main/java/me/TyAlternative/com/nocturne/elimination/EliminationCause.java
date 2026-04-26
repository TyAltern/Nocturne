package me.TyAlternative.com.nocturne.elimination;

import org.jetbrains.annotations.NotNull;

/**
 * Raison pour laquelle un joueur a été éliminé de la partie.
 *
 * <p>Utilisée dans les hooks {@code onEliminated} et {@code onOtherEliminated}
 * pour permettre aux rôles de réagir différemment selon la cause.
 */
public enum EliminationCause {

    // -- Embrasement ----------------------------------------------------------

    /** Éliminé par la capacité Embrasement de l'Étincelle. */
    EMBRASEMENT_ETINCELLE("Embrasement (Étincelle)", true, false, false),

    /** Éliminé par la capacité Rayonnement de la Torche. */
    EMBRASEMENT_TORCHE("Embrasement (Torche)", true, false, false),

    /** Éliminé par la capacité Murmuration du Frémissement. */
    EMBRASEMENT_MURMURATION("Embrasement (Murmuration)", true, false, false),

    /** Éliminé par un Embrasement dont la source n'est pas précisée. */
    EMBRASEMENT("Embrasement", true, false, false),

    // -- Disparition ----------------------------------------------------------

    /** Éliminé par Solitude Mortelle (trop isolé en fin de phase). */
    DISPARITION_SOLITUDE("Disparition (Solitude)", false, true, false),

    /** Éliminé par une disparition sans cause spécifique. */
    DISPARITION("Disparition", false, true, false),

    // -- Vote -----------------------------------------------------------------

    /** Éliminé par le vote des joueurs en phase de Vote. */
    VOTE("Vote", false, false, true),

    // -- Autres ---------------------------------------------------------------

    /** Éliminé suite à une déconnexion. */
    DISCONNECT("Déconnexion", false, false, false),

    /** Cause inconnue ou non catégorisée. */
    UNKNOWN("Inconnu", false, false, false);

    // -------------------------------------------------------------------------

    private final @NotNull String displayName;
    private final boolean isEmbrasement;
    private final boolean isDisparition;
    private final boolean isVote;

    EliminationCause(
            @NotNull String displayName,
            boolean isEmbrasement,
            boolean isDisparition,
            boolean isVote
    ) {
        this.displayName = displayName;
        this.isEmbrasement = isEmbrasement;
        this.isDisparition = isDisparition;
        this.isVote = isVote;
    }

    /** Nom affiché dans les messages d'élimination. */
    public @NotNull String getDisplayName() {
        return displayName;
    }

    /** {@code true} si l'élimination est due à un Embrasement, quelle qu'en soit la source. */
    public boolean isEmbrasement() {
        return isEmbrasement;
    }

    /** {@code true} si l'élimination est due à une Disparition. */
    public boolean isDisparition() {
        return isDisparition;
    }

    /** {@code true} si l'élimination résulte d'un vote. */
    public boolean isVote() {
        return isVote;
    }
}
