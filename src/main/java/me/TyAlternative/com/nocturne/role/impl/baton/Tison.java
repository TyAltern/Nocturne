package me.TyAlternative.com.nocturne.role.impl.baton;

import me.TyAlternative.com.nocturne.ability.impl.misc.IncandescenceAbility;
import me.TyAlternative.com.nocturne.api.role.RoleTeam;
import me.TyAlternative.com.nocturne.api.role.RoleType;
import me.TyAlternative.com.nocturne.role.AbstractRole;
import me.TyAlternative.com.nocturne.role.RoleIds;
import org.bukkit.Material;

/**
 * Le Tison — Bâton.
 *
 * <p>Capacités : à définir (Phase 5).
 */
public final class Tison extends AbstractRole {

    public static final String ID = RoleIds.TISON;

    public Tison() {
        super(
                ID,
                "§e§lLe Tison",
                "Ardent et résistant, éliminez les Flammes et gagnez avec les autres Bâtons.",
                RoleType.BATON,
                RoleTeam.BATONS,
                Material.BLAZE_ROD
        );
        registerAbility(new IncandescenceAbility());
    }
}