package me.TyAlternative.com.nocturne.role.impl.neutre;

import me.TyAlternative.com.nocturne.api.role.RoleTeam;
import me.TyAlternative.com.nocturne.api.role.RoleType;
import me.TyAlternative.com.nocturne.role.AbstractRole;
import me.TyAlternative.com.nocturne.role.RoleIds;
import org.bukkit.Material;

/**
 * La Nuée — rôle Neutre à objectif indépendant.
 *
 * <p>Capacités et condition de victoire : à définir (Phase 5).
 */
public final class Nuee extends AbstractRole {

    public static final String ID = RoleIds.NUEE;

    public Nuee() {
        super(
                ID,
                "§8§lLa Nuée",
                "Vous n'appartenez à aucun camp. Votre objectif est le vôtre.",
                RoleType.NEUTRE,
                RoleTeam.BATONS, // TODO : définir une équipe Neutre dédiée si nécessaire
                Material.BLACK_CONCRETE_POWDER
        );
        // TODO : capacités et condition de victoire à définir
    }
}