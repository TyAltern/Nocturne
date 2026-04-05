package me.TyAlternative.com.nocturne.role.impl.baton;

import me.TyAlternative.com.nocturne.api.role.RoleTeam;
import me.TyAlternative.com.nocturne.api.role.RoleType;
import me.TyAlternative.com.nocturne.role.AbstractRole;
import org.bukkit.Material;

/**
 * L'Aurore — Bâton à capacité en mode drunk.
 *
 * <p>Capacités :
 * <ul>
 *   <li><b>Échos d'Interaction</b> (Passif, drunk) — marque aléatoirement des joueurs
 *       avec un effet de particules visible uniquement par l'Aurore.
 *       Le mode drunk signifie que le comportement est erroné : les marquages
 *       sont aléatoires et non basés sur les vraies interactions.</li>
 * </ul>
 */
public final class Aurore extends AbstractRole {

    public static final String ID = "AURORE";

    public Aurore() {
        super(
                ID,
                "§d§lL'Aurore",
                "Votre sagesse illumine le groupe. Éliminez les Flammes et gagnez avec les autres Bâtons.",
                RoleType.BATON,
                RoleTeam.BATONS,
                Material.MAGENTA_STAINED_GLASS
        );
        // TODO : registerDrunkAbility(new EchosInteractionAbility());
    }
}
