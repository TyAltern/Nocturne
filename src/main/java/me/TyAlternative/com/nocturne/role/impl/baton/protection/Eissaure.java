package me.TyAlternative.com.nocturne.role.impl.baton.protection;

import me.TyAlternative.com.nocturne.ability.impl.protection.LipsAbility;
import me.TyAlternative.com.nocturne.api.role.RoleTeam;
import me.TyAlternative.com.nocturne.api.role.RoleType;
import me.TyAlternative.com.nocturne.role.AbstractRole;
import me.TyAlternative.com.nocturne.role.RoleIds;
import org.bukkit.Material;

/**
 * La Eissaure — Bâton protecteur conditionnel.
 *
 * <p>Capacités :
 * <ul>
 *   <li><b>Notos</b> (Actif) — protège autant de joueurs que souhaité via clic droit,
 *       mais si l'un des joueurs protégés utilise une capacité active, tous perdent
 *       leur protection.</li>
 * </ul>
 */
public final class Eissaure extends AbstractRole {

    public static final String ID = RoleIds.EISSAURE;

    public Eissaure() {
        super(
                ID,
                "§3§lL'Eissaure",
                "Vent glacé du sud-ouest qui éteint les braises avant qu'elles ne s'embrasent. Éliminez les §cFlammes§f et gagnez avec les autres §eBâtons§f.",
                RoleType.BATON,
                RoleTeam.BATONS,
                Material.WIND_CHARGE
        );
        registerAbility(new LipsAbility());
    }
}