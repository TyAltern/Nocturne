package me.TyAlternative.com.nocturne.role.impl.flamme;

import me.TyAlternative.com.nocturne.ability.impl.flamme.EmbrasementAbility;
import me.TyAlternative.com.nocturne.ability.impl.flamme.SelfEmbrasementAbility;
import me.TyAlternative.com.nocturne.api.role.RoleTeam;
import me.TyAlternative.com.nocturne.api.role.RoleType;
import me.TyAlternative.com.nocturne.role.AbstractRole;
import me.TyAlternative.com.nocturne.role.RoleIds;
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

    public static final String ID = RoleIds.ETINCELLE;

    public Etincelle() {
        super(
                ID,
                "§c§lL'Étincelle",
                "Vous êtes le traître de la partie. Embrasez tous les Bâtons et gagnez seul.",
                RoleType.FLAMME,
                RoleTeam.SOLITAIRE,
                Material.BLAZE_POWDER
        );

        registerAbility(new EmbrasementAbility());
        registerAbility(new SelfEmbrasementAbility()); // TODO: A SUPPRIMER S'IL TE PLAIT NE FAIT PAS L'ERREUR DE LE LAISSER!!!!!!!!!!!!!!!
        // TODO : registerAbility(new PoudreChemineeAbility());
    }
}
