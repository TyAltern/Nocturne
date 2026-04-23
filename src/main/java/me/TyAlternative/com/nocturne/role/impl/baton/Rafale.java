package me.TyAlternative.com.nocturne.role.impl.baton;

import me.TyAlternative.com.nocturne.ability.impl.protection.AusterAbility;
import me.TyAlternative.com.nocturne.api.role.RoleTeam;
import me.TyAlternative.com.nocturne.api.role.RoleType;
import me.TyAlternative.com.nocturne.role.AbstractRole;
import me.TyAlternative.com.nocturne.role.RoleIds;
import org.bukkit.Material;

/**
 * La Rafale — Bâton protecteur conditionnel.
 *
 * <p>Capacités :
 * <ul>
 *   <li><b>Auster</b> (Actif) — protège autant de joueurs que souhaité via clic droit,
 *       mais si l'un des joueurs protégés utilise une capacité active, tous perdent
 *       leur protection.</li>
 * </ul>
 */
public final class Rafale extends AbstractRole {

    public static final String ID = RoleIds.RAFALE;

    public Rafale() {
        super(
                ID,
                "§1§lLa Rafale",
                "Votre protection est puissante mais fragile. Choisissez bien vos protégés.",
                RoleType.BATON,
                RoleTeam.BATONS,
                Material.WIND_CHARGE
        );
        registerAbility(new AusterAbility());
    }
}