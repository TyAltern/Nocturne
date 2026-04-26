package me.TyAlternative.com.nocturne.role.impl.baton.protection;

import me.TyAlternative.com.nocturne.ability.impl.protection.NotosAbility;
import me.TyAlternative.com.nocturne.api.role.RoleTeam;
import me.TyAlternative.com.nocturne.api.role.RoleType;
import me.TyAlternative.com.nocturne.role.AbstractRole;
import me.TyAlternative.com.nocturne.role.RoleIds;
import org.bukkit.Material;

public final class Sirocco extends AbstractRole {


    public static final String ID = RoleIds.SIROCCO;

    public Sirocco() {
        super(
                ID,
                "§6§lLe Sirocco",
                "",
                RoleType.BATON,
                RoleTeam.BATONS,
                Material.RED_SAND
        );
        registerAbility(new NotosAbility());
    }
}
