package me.TyAlternative.com.nocturne.role.impl.flamme;
import me.TyAlternative.com.nocturne.ability.impl.flamme.EclaircissementAbility;
import me.TyAlternative.com.nocturne.api.role.RoleTeam;
import me.TyAlternative.com.nocturne.api.role.RoleType;
import me.TyAlternative.com.nocturne.role.AbstractRole;
import me.TyAlternative.com.nocturne.role.RoleIds;
import org.bukkit.Material;

/**
 * Le Flambeau — Flamme solitaire qui gagne en pouvoir de vote en anticipant les éliminations.
 *
 * <p>Capacités :
 * <ul>
 *   <li><b>Éclaircissement</b> (Actif, 1/manche) — marque un joueur en phase de Gameplay ;
 *       si ce joueur est éliminé au prochain vote, le Flambeau gagne un poids de vote.</li>
 * </ul>
 */
public final class Flambeau extends AbstractRole {

    public static final String ID = RoleIds.FLAMBEAU;

    public Flambeau() {
        super(
                ID,
                "§e§lLe Flambeau",
                "Votre sagesse est votre arme. Manipulez les votes pour éliminer les Bâtons et gagner seul.",
                RoleType.FLAMME,
                RoleTeam.SOLITAIRE,
                Material.SOUL_LANTERN
        );

         registerAbility(new EclaircissementAbility());
    }
}
