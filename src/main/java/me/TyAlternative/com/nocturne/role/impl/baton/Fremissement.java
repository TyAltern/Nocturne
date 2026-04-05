package me.TyAlternative.com.nocturne.role.impl.baton;

import me.TyAlternative.com.nocturne.api.role.RoleTeam;
import me.TyAlternative.com.nocturne.api.role.RoleType;
import me.TyAlternative.com.nocturne.role.AbstractRole;
import org.bukkit.Material;
/**
 * Le Frémissement — Bâton protecteur de proximité.
 *
 * <p>Capacités :
 * <ul>
 *   <li><b>Alizé</b> (Passif) — en fin de phase de Gameplay, protège le joueur
 *       non protégé le plus proche dans un rayon défini. Si personne n'est à portée,
 *       se protège lui-même.</li>
 * </ul>
 */
public final class Fremissement extends AbstractRole {

    public static final String ID = "FREMISSEMENT";

    public Fremissement() {
        super(
                ID,
                "§3§lLe Frémissement",
                "Votre simple présence protège ceux qui vous entourent. Éliminez les Flammes et gagnez ensemble.",
                RoleType.BATON,
                RoleTeam.BATONS,
                Material.SNOWBALL
        );
        // TODO : registerAbility(new AlizeAbility());
    }
}
