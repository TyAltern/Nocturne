package me.TyAlternative.com.nocturne.role.impl.baton;

import me.TyAlternative.com.nocturne.ability.impl.info.OmbresResiduellesAbility;
import me.TyAlternative.com.nocturne.api.role.RoleTeam;
import me.TyAlternative.com.nocturne.api.role.RoleType;
import me.TyAlternative.com.nocturne.role.AbstractRole;
import me.TyAlternative.com.nocturne.role.RoleIds;
import org.bukkit.Material;

/**
 * Le Crépuscule — Bâton informateur post-vote.
 *
 * <p>Capacités :
 * <ul>
 *   <li>{@link OmbresResiduellesAbility} — après chaque vote, révèle la moitié
 *       des joueurs n'ayant pas voté contre l'éliminé.</li>
 * </ul>
 */
public final class Crepuscule extends AbstractRole {

    public static final String ID = RoleIds.CREPUSCULE;

    public Crepuscule() {
        super(
                ID,
                "§c§lLe Crépuscule",
                "Les ombres vous livrent leurs secrets. Éliminez les Flammes et gagnez avec les autres Bâtons.",
                RoleType.BATON,
                RoleTeam.BATONS,
                Material.RED_STAINED_GLASS
        );
        registerAbility(new OmbresResiduellesAbility());
    }
}
