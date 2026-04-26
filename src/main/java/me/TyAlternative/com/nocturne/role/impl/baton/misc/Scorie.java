package me.TyAlternative.com.nocturne.role.impl.baton.misc;

import me.TyAlternative.com.nocturne.ability.impl.misc.CorrosionAbility;
import me.TyAlternative.com.nocturne.api.role.RoleTeam;
import me.TyAlternative.com.nocturne.api.role.RoleType;
import me.TyAlternative.com.nocturne.role.AbstractRole;
import me.TyAlternative.com.nocturne.role.RoleIds;
import org.bukkit.Material;

/**
 * La Scorie — Bâton au vote corrosif.
 *
 * <p>Capacités :
 * <ul>
 *   <li><b>Corrosion</b> (Actif, Vote) — bascule son poids de vote entre +2 et -2.
 *       Un vote négatif est toujours comptabilisé comme un vote émis.</li>
 * </ul>
 */
public final class Scorie extends AbstractRole {

    public static final String ID = RoleIds.SCORIE;

    public Scorie() {
        super(
                ID,
                "§8§lLa Scorie",
                "Votre vote vaut double, mais peut se retourner contre vous. Jouez finement.",
                RoleType.BATON,
                RoleTeam.BATONS,
                Material.COAL_BLOCK
        );
        registerAbility(new CorrosionAbility());
    }
}
