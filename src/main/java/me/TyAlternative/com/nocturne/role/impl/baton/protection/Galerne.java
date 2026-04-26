package me.TyAlternative.com.nocturne.role.impl.baton.protection;

import me.TyAlternative.com.nocturne.ability.impl.protection.ScironAbility;
import me.TyAlternative.com.nocturne.api.role.RoleTeam;
import me.TyAlternative.com.nocturne.api.role.RoleType;
import me.TyAlternative.com.nocturne.role.AbstractRole;
import me.TyAlternative.com.nocturne.role.RoleIds;
import org.bukkit.Material;

/**
 * La Galerne — Bâton protecteur de masse, version évoluée du Autan.
 *
 * <p>Capacités :
 * <ul>
 *   <li><b>Borée</b> (Actif, 1/partie) — protège tous les joueurs vivants contre
 *       l'Embrasement des Flammes pour la phase en cours.</li>
 * </ul>
 */
public final class Galerne extends AbstractRole {

    public static final String ID = RoleIds.GALERNE;

    public Galerne() {
        super(
                ID,
                "§9§lLa Galerne",
                "Votre puissance peut tout balayer. Utilisez-la au bon moment pour sauver vos alliés.",
                RoleType.BATON,
                RoleTeam.BATONS,
                Material.PRISMARINE_SHARD
        );
        registerAbility(new ScironAbility());
    }
}