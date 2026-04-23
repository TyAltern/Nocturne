package me.TyAlternative.com.nocturne.role.impl.baton;

import me.TyAlternative.com.nocturne.ability.impl.info.DiscernementMatinalAbility;
import me.TyAlternative.com.nocturne.api.role.RoleTeam;
import me.TyAlternative.com.nocturne.api.role.RoleType;
import me.TyAlternative.com.nocturne.role.AbstractRole;
import me.TyAlternative.com.nocturne.role.RoleIds;
import org.bukkit.Material;

/**
 * L'Aube — Bâton informateur post-vote.
 *
 * <p>Capacités :
 * <ul>
 *   <li>{@link DiscernementMatinalAbility} — après chaque vote, révèle si au moins
 *       une Flamme a voté contre le joueur éliminé.</li>
 * </ul>
 */
public final class Aube extends AbstractRole {

    public static final String ID = RoleIds.AUBE;

    public Aube() {
        super(
                ID,
                "§6§lL'Aube",
                "Discernez les menaces cachées. Éliminez les Flammes et gagnez avec les autres Bâtons.",
                RoleType.BATON,
                RoleTeam.BATONS,
                Material.ORANGE_STAINED_GLASS
        );
        registerAbility(new DiscernementMatinalAbility());
    }
}
