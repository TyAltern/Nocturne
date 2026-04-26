package me.TyAlternative.com.nocturne.role.impl.baton.misc;

import me.TyAlternative.com.nocturne.api.role.RoleTeam;
import me.TyAlternative.com.nocturne.api.role.RoleType;
import me.TyAlternative.com.nocturne.role.AbstractRole;
import me.TyAlternative.com.nocturne.role.RoleIds;
import org.bukkit.Material;

/**
 * La Braise — Bâton.
 *
 * <p>Capacités : à définir (Phase 5).
 */
public final class Braise extends AbstractRole {

    public static final String ID = RoleIds.BRAISE;

    public Braise() {
        super(
                ID,
                "§6§lLa Braise",
                "Incandescente et persistante, éliminez les Flammes et gagnez avec les autres Bâtons.",
                RoleType.BATON,
                RoleTeam.BATONS,
                Material.FIRE_CHARGE
        );
        // TODO : capacités à définir
    }
}
