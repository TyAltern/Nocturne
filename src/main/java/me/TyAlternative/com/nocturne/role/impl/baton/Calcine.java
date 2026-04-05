package me.TyAlternative.com.nocturne.role.impl.baton;

import me.TyAlternative.com.nocturne.api.role.RoleTeam;
import me.TyAlternative.com.nocturne.api.role.RoleType;
import me.TyAlternative.com.nocturne.role.AbstractRole;
import org.bukkit.Material;

/**
 * Le Calciné — Bâton survivant qui retarde son propre Embrasement.
 *
 * <p>Capacités :
 * <ul>
 *   <li><b>Cicatrices</b> (Passif) — si le Calciné est Embrasé par une Flamme,
 *       l'élimination est repoussée à la fin de la manche suivante.
 *       Le Calciné n'est pas informé de son Embrasement.</li>
 * </ul>
 */
public final class Calcine extends AbstractRole {

    public static final String ID = "CALCINE";

    public Calcine() {
        super(
                ID,
                "§4§lLe Calciné",
                "Rescapé d'un précédent Embrasement, la douleur vous est supportable. Mais pas pour longtemps.",
                RoleType.BATON,
                RoleTeam.BATONS,
                Material.GRAY_CONCRETE_POWDER
        );
        // TODO : registerAbility(new CicatricesAbility());
    }
}