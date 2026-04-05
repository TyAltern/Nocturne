package me.TyAlternative.com.nocturne.role.impl.baton;

import me.TyAlternative.com.nocturne.api.role.RoleTeam;
import me.TyAlternative.com.nocturne.api.role.RoleType;
import me.TyAlternative.com.nocturne.role.AbstractRole;
import org.bukkit.Material;

/**
 * La Lueur — Bâton.
 *
 * <p>Capacités : à définir (Phase 5).
 */
public final class Lueur extends AbstractRole {

    public static final String ID = "LUEUR";

    public Lueur() {
        super(
                ID,
                "§e§lLa Lueur",
                "Votre intuition est votre guide. Éliminez les Flammes et gagnez avec les autres Bâtons.",
                RoleType.BATON,
                RoleTeam.BATONS,
                Material.YELLOW_STAINED_GLASS
        );
        // TODO : capacités à définir
    }
}
