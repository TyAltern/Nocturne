package me.TyAlternative.com.nocturne.role.impl.baton.protection;

import me.TyAlternative.com.nocturne.ability.impl.protection.ApelioteAbility;
import me.TyAlternative.com.nocturne.api.role.RoleTeam;
import me.TyAlternative.com.nocturne.api.role.RoleType;
import me.TyAlternative.com.nocturne.role.AbstractRole;
import me.TyAlternative.com.nocturne.role.RoleIds;
import org.bukkit.Material;

/**
 * L'Autan — Bâton protecteur de base.
 *
 * <p>Capacités :
 * <ul>
 *   <li><b>Brise</b> (Actif, 1/manche) — clic droit sur un joueur pour le protéger
 *       contre l'Embrasement des Flammes jusqu'à la fin de la phase.</li>
 * </ul>
 */
public final class Autan extends AbstractRole {

    public static final String ID = RoleIds.AUTAN;

    public Autan() {
        super(
                ID,
                "§b§lL'Autan",
                "Vous êtes l'allié le plus fort pour contrer les Flammes. Protégez vos alliés et gagnez ensemble.",
                RoleType.BATON,
                RoleTeam.BATONS,
                Material.FEATHER
        );
        registerAbility(new ApelioteAbility());
    }
}
