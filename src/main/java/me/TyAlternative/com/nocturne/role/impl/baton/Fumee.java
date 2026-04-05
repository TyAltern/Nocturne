package me.TyAlternative.com.nocturne.role.impl.baton;

import me.TyAlternative.com.nocturne.api.role.RoleTeam;
import me.TyAlternative.com.nocturne.api.role.RoleType;
import me.TyAlternative.com.nocturne.role.AbstractRole;
import org.bukkit.Material;

/**
 * La Fumée — Bâton.
 *
 * <p>Capacités : à définir (Phase 5).
 */
public final class Fumee extends AbstractRole {

    public static final String ID = "FUMEE";

    public Fumee() {
        super(
                ID,
                "§7§lLa Fumée",
                "Insaisissable et trompeuse, éliminez les Flammes et gagnez avec les autres Bâtons.",
                RoleType.BATON,
                RoleTeam.BATONS,
                Material.FIREWORK_STAR
        );
        // TODO : capacités à définir
    }
}