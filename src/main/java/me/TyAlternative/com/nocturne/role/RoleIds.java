package me.TyAlternative.com.nocturne.role;

import java.util.Set;

public final class RoleIds {
    public static final String BATON           = "BATON";
    public static final String BRAISE          = "BRAISE";
    public static final String CALCINE         = "CALCINE";
    public static final String CENDRE          = "CENDRE";
    public static final String FUMEE           = "FUMEE";
    public static final String SCORIE          = "SCORIE";
    public static final String TISON           = "TISON";

    public static final String LUEUR           = "LUEUR";
    public static final String AUBE            = "AUBE";
    public static final String AURORE          = "AURORE";
    public static final String JOUR            = "JOUR";
    public static final String ZENITH          = "ZENITH";
    public static final String CREPUSCULE      = "CREPUSCULE";
    public static final String VESPER          = "VESPER";
    public static final String NUIT            = "NUIT";

    public static final String GALERNE         = "GALERNE";
    public static final String LOMBARDE        = "LOMBARDE";
    public static final String FOEHN           = "FOEHN";
    public static final String BISE            = "BISE";
    public static final String MISTRAL         = "MISTRAL";
    public static final String EISSAURE        = "EISSAURE";
    public static final String SIROCCO         = "SIROCCO";
    public static final String AUTAN           = "AUTAN";

    public static final String ETINCELLE = "ETINCELLE";
    public static final String FLAMBEAU = "FLAMBEAU";
    public static final String SILEX = "SILEX";
    public static final String TORCHE = "TORCHE";

    public static final String NUEE = "NUEE";

    private static final Set<String> IDS = Set.of(
            BATON, BRAISE, CALCINE, CENDRE, FUMEE, SCORIE, TISON,
            LUEUR, AUBE, AURORE, JOUR, ZENITH, CREPUSCULE, VESPER, NUIT,
            GALERNE, LOMBARDE, FOEHN, BISE, MISTRAL, EISSAURE, SIROCCO, AUTAN,

            ETINCELLE, FLAMBEAU, SILEX, TORCHE,

            NUEE
    );

    public static boolean isId(String value) {
        return value != null && IDS.contains(value);
    }

}
