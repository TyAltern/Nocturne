package me.TyAlternative.com.nocturne.role;

import java.util.Set;

public final class RoleIds {
    public static final String AUBE = "AUBE";
    public static final String AURORE = "AURORE";
    public static final String BATON = "BATON";
    public static final String BOURRASQUE = "BOURRASQUE";
    public static final String BRAISE = "BRAISE";
    public static final String CALCINE = "CALCINE";
    public static final String CENDRE = "CENDRE";
    public static final String CREPUSCULE = "CREPUSCULE";
    public static final String FREMISSEMENT = "FREMISSEMENT";
    public static final String FUMEE = "FUMEE";
    public static final String GAZ = "GAZ";
    public static final String LUEUR = "LUEUR";
    public static final String RAFALE = "RAFALE";
    public static final String SCORIE = "SCORIE";
    public static final String SOUFFLE = "SOUFFLE";
    public static final String TISON = "TISON";


    public static final String ETINCELLE = "ETINCELLE";
    public static final String FLAMBEAU = "FLAMBEAU";
    public static final String SILEX = "SILEX";
    public static final String TORCHE = "TORCHE";

    public static final String NUEE = "NUEE";

    private static final Set<String> IDS = Set.of(
            AUBE, AURORE, BATON, BOURRASQUE, BRAISE, CALCINE, CENDRE,
            CREPUSCULE, FREMISSEMENT, FUMEE, GAZ, LUEUR, RAFALE, SCORIE,
            SOUFFLE, TISON, ETINCELLE, FLAMBEAU, SILEX, TORCHE, NUEE
    );

    public static boolean isId(String value) {
        return value != null && IDS.contains(value);
    }

}
