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
                "Brise du sud-ouest, douce mais déterminée. Chaque manche, votre bénédiction protège un allié des §cEmbrasements§f. Éliminez les §cFlammes§f et gagnez avec les autres§e Bâtons§f.",
                RoleType.BATON,
                RoleTeam.BATONS,
                Material.FEATHER
        );
        registerAbility(new ApelioteAbility());
    }
}
