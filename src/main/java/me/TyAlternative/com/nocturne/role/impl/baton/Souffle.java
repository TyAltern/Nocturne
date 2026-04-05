package me.TyAlternative.com.nocturne.role.impl.baton;

import me.TyAlternative.com.nocturne.api.role.RoleTeam;
import me.TyAlternative.com.nocturne.api.role.RoleType;
import me.TyAlternative.com.nocturne.role.AbstractRole;
import org.bukkit.Material;

/**
 * Le Souffle — Bâton protecteur de base.
 *
 * <p>Capacités :
 * <ul>
 *   <li><b>Brise</b> (Actif, 1/manche) — clic droit sur un joueur pour le protéger
 *       contre l'Embrasement des Flammes jusqu'à la fin de la phase.</li>
 * </ul>
 */
public final class Souffle extends AbstractRole {

    public static final String ID = "SOUFFLE";

    public Souffle() {
        super(
                ID,
                "§b§lLe Souffle",
                "Vous êtes l'allié le plus fort pour contrer les Flammes. Protégez vos alliés et gagnez ensemble.",
                RoleType.BATON,
                RoleTeam.BATONS,
                Material.FEATHER
        );
        // TODO : registerAbility(new BriseAbility());
    }
}
