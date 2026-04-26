package me.TyAlternative.com.nocturne.role.impl.baton.misc;

import me.TyAlternative.com.nocturne.api.role.RoleTeam;
import me.TyAlternative.com.nocturne.api.role.RoleType;
import me.TyAlternative.com.nocturne.role.AbstractRole;
import me.TyAlternative.com.nocturne.role.RoleIds;
import org.bukkit.Material;

/**
 * Le Bâton — rôle de base du camp des Bâtons, sans capacité spécifique.
 *
 * <p>La force du Bâton réside dans le nombre et la coopération avec ses alliés.
 */
public final class Baton extends AbstractRole {

    public static final String ID = RoleIds.BATON;

    public Baton() {
        super(
                ID,
                "§e§lLe Bâton",
                "Vous êtes l'objet inflammable par excellence. Éliminez les Flammes et gagnez avec les autres Bâtons.",
                RoleType.BATON,
                RoleTeam.BATONS,
                Material.STICK
        );
        // Aucune capacité — rôle de base
    }
}
