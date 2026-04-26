package me.TyAlternative.com.nocturne.ability;

import me.TyAlternative.com.nocturne.api.role.Role;

/**
 * Registre centralisé de tous les identifiants de capacités.
 *
 * <p>Utiliser ces constantes partout où un identifiant de capacité est requis
 * (ex : {@link Role#getAbility(String)},
 * {@link AbilityManager}, clés de stockage) afin d'éviter les magic strings
 * et les bugs silencieux dus aux fautes de frappe.
 *
 * <pre>{@code
 * // ✅ Correct
 * role.getAbility(AbilityIds.EMBRASEMENT);
 *
 * // ❌ À éviter
 * role.getAbility("embrasement"); // typo non détectée à la compilation
 * }</pre>
 */
public final class AbilityIds {

    private AbilityIds() {
        // Classe utilitaire, non instanciable
    }

    // -------------------------------------------------------------------------
    // Flammes
    // -------------------------------------------------------------------------

    public static final String EMBRASEMENT            = "EMBRASEMENT";
    public static final String SELF_EMBRASEMENT       = "SELF_EMBRASEMENT";
    public static final String POUDRE_CHEMINEE        = "POUDRE_CHEMINEE";
    public static final String RAYONNEMENT            = "RAYONNEMENT";
    public static final String ETOUFFEMENT_FLAMME     = "ETOUFFEMENT_FLAMME";
    public static final String ECLAIRCISSEMENT        = "ECLAIRCISSEMENT";

    // -------------------------------------------------------------------------
    // Information
    // -------------------------------------------------------------------------

    public static final String DISCERNEMENT_MATINAL   = "DISCERNEMENT_MATINAL";
    public static final String OMBRES_RESIDUELLES     = "OMBRES_RESIDUELLES";
    public static final String CLAIRVOYANCE           = "CLAIRVOYANCE";
    public static final String ECHOS_INTERACTION      = "ECHOS_INTERACTION";
    public static final String REVERBERATION_LUMINEUSE = "REVERBERATION_LUMINEUSE";

    // -------------------------------------------------------------------------
    // Protection
    // -------------------------------------------------------------------------

    public static final String APELIOTE               = "APELIOTE";
    public static final String CAECIAS                = "CAECIAS";
    public static final String MURMURATION            = "MURMURATION";
    public static final String BOREE                  = "BOREE";
    public static final String LIPS                   = "LIPS";
    public static final String SCIRON                 = "SCIRON";
    public static final String NOTOS                  = "NOTOS";
    public static final String EUROS                  = "EUROS";
    public static final String ZEPHYR                 = "ZEPHYR";

    // -------------------------------------------------------------------------
    // Divers
    // -------------------------------------------------------------------------

    public static final String CENDRE                 = "CENDRE";
    public static final String CICATRICES             = "CICATRICES";
    public static final String CORROSION              = "CORROSION";
    public static final String SOLITUDE_MORTELLE      = "SOLITUDE_MORTELLE";
    public static final String INCANDESCENCE          = "INCANDESCENCE";

    // -------------------------------------------------------------------------
    // Malédictions
    // -------------------------------------------------------------------------

    public static final String AMNESIE                = "AMNESIE";

    // -------------------------------------------------------------------------
    // Neutres
    // -------------------------------------------------------------------------

    public static final String MANIPULATION_VOTE      = "MANIPULATION_VOTE";
    public static final String MORT_FEINTE            = "MORT_FEINTE";
}
