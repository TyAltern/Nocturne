package me.TyAlternative.com.nocturne.role.impl.baton;

import me.TyAlternative.com.nocturne.api.role.RoleTeam;
import me.TyAlternative.com.nocturne.api.role.RoleType;
import me.TyAlternative.com.nocturne.role.AbstractRole;
import org.bukkit.Material;

/**
 * Le Gaz — Bâton dont la chance peut se retourner contre lui.
 *
 * <p>Capacités :
 * <ul>
 *   <li><b>Solitude Mortelle</b> (Passif) — protégé par défaut contre l'Embrasement,
 *       mais perd cette protection s'il se retrouve seul en fin de phase.</li>
 *   <li><b>Prise de Feu</b> (Malédiction) — si un joueur Embrasé est à moins de X blocs
 *       en fin de phase, le Gaz prend sa place et le protège.</li>
 * </ul>
 */
public final class Gaz extends AbstractRole {

    public static final String ID = "GAZ";

    public Gaz() {
        super(
                ID,
                "§2§lLe Gaz",
                "Votre chance peut s'avérer être une malédiction. Restez proche de vos alliés.",
                RoleType.BATON,
                RoleTeam.BATONS,
                Material.GLASS_BOTTLE
        );
        // TODO : registerAbility(new SolitudeMortelleAbility());
        // TODO : registerAbility(new PriseDeFeuAbility());
    }
}
