package me.TyAlternative.com.nocturne.role.impl.baton;

import me.TyAlternative.com.nocturne.ability.impl.protection.AquilonAbility;
import me.TyAlternative.com.nocturne.api.role.RoleTeam;
import me.TyAlternative.com.nocturne.api.role.RoleType;
import me.TyAlternative.com.nocturne.role.AbstractRole;
import me.TyAlternative.com.nocturne.role.RoleIds;
import org.bukkit.Material;

/**
 * La Bourrasque — Bâton protecteur de masse, version évoluée du Souffle.
 *
 * <p>Capacités :
 * <ul>
 *   <li><b>Aquilon</b> (Actif, 1/partie) — protège tous les joueurs vivants contre
 *       l'Embrasement des Flammes pour la phase en cours.</li>
 * </ul>
 */
public final class Bourrasque extends AbstractRole {

    public static final String ID = RoleIds.BOURRASQUE;

    public Bourrasque() {
        super(
                ID,
                "§9§lLa Bourrasque",
                "Votre puissance peut tout balayer. Utilisez-la au bon moment pour sauver vos alliés.",
                RoleType.BATON,
                RoleTeam.BATONS,
                Material.PRISMARINE_SHARD
        );
        registerAbility(new AquilonAbility());
    }
}