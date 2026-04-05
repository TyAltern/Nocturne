package me.TyAlternative.com.nocturne.role.impl.flamme;

import me.TyAlternative.com.nocturne.api.role.RoleTeam;
import me.TyAlternative.com.nocturne.api.role.RoleType;
import me.TyAlternative.com.nocturne.role.AbstractRole;
import org.bukkit.Material;

/**
 * La Torche — Flamme solitaire dont le rayonnement élimine les joueurs proches.
 *
 * <p>Capacités :
 * <ul>
 *   <li><b>Rayonnement</b> (Passif/Ticks) — le joueur ayant passé le plus de temps
 *       dans un rayon défini est éliminé en fin de phase.</li>
 *   <li><b>Étouffement de Flamme</b> (Actif, 1/manche) — désactive le Rayonnement
 *       pour la manche en cours.</li>
 * </ul>
 */
public final class Torche extends AbstractRole {

    public static final String ID = "TORCHE";

    public Torche() {
        super(
                ID,
                "§c§lLa Torche",
                "Être de lumière, embrasez les Bâtons par votre seule présence et gagnez seul.",
                RoleType.FLAMME,
                RoleTeam.SOLITAIRE,
                Material.TORCH
        );

        // TODO : registerAbility(new RayonnementAbility());
        // TODO : registerAbility(new EtouffementFlammeAbility());
    }
}
