package me.TyAlternative.com.nocturne.role.impl.baton;

import me.TyAlternative.com.nocturne.api.role.RoleTeam;
import me.TyAlternative.com.nocturne.api.role.RoleType;
import me.TyAlternative.com.nocturne.role.AbstractRole;
import org.bukkit.Material;

/**
 * L'Aube — Bâton informateur post-vote.
 *
 * <p>Capacités :
 * <ul>
 *   <li><b>Discernement Matinal</b> (Passif) — après chaque vote, révèle si tous
 *       les joueurs ayant voté contre l'éliminé étaient des Bâtons.</li>
 * </ul>
 */
public final class Aube extends AbstractRole {

    public static final String ID = "AUBE";

    public Aube() {
        super(
                ID,
                "§6§lL'Aube",
                "Discernez les menaces cachées. Éliminez les Flammes et gagnez avec les autres Bâtons.",
                RoleType.BATON,
                RoleTeam.BATONS,
                Material.ORANGE_STAINED_GLASS
        );
        // TODO : registerAbility(new DiscernementMatinalAbility());
    }
}
