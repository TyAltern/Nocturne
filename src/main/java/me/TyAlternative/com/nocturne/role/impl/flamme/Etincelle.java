package me.TyAlternative.com.nocturne.role.impl.flamme;

import me.TyAlternative.com.nocturne.api.role.RoleTeam;
import me.TyAlternative.com.nocturne.api.role.RoleType;
import me.TyAlternative.com.nocturne.role.AbstractRole;
import org.bukkit.Material;

/**
 * L'Étincelle — Flamme solitaire et traître principal de la partie.
 *
 * <p>Capacités :
 * <ul>
 *   <li><b>Embrasement</b> (Actif) — clic droit sur un joueur pour le marquer,
 *       il sera éliminé en fin de phase de Gameplay.</li>
 *   <li><b>Poudre de Cheminée</b> (Actif) — échange sa position avec un joueur
 *       aléatoire toutes les X secondes.</li>
 * </ul>
 */
public final class Etincelle extends AbstractRole {

    public static final String ID = "ETINCELLE";

    public Etincelle() {
        super(
                ID,
                "§c§lL'Étincelle",
                "Vous êtes le traître de la partie. Embrasez tous les Bâtons et gagnez seul.",
                RoleType.FLAMME,
                RoleTeam.SOLITAIRE,
                Material.BLAZE_POWDER
        );

        // TODO : registerAbility(new EmbrasementAbility());
        // TODO : registerAbility(new PoudreChemineeAbility());
    }
}
