package me.TyAlternative.com.nocturne.role.impl.baton.protection;

import me.TyAlternative.com.nocturne.ability.impl.protection.ZephyrAbility;
import me.TyAlternative.com.nocturne.api.role.RoleTeam;
import me.TyAlternative.com.nocturne.api.role.RoleType;
import me.TyAlternative.com.nocturne.role.AbstractRole;
import me.TyAlternative.com.nocturne.role.RoleIds;
import org.bukkit.Material;

public final class Foehn extends AbstractRole {


    public static final String ID = RoleIds.FOEHN;

    public Foehn() {
        super(
                ID,
                "§8§lLe Foehn",
                "",
                RoleType.BATON,
                RoleTeam.BATONS,
                Material.TUFF
        );
        registerAbility(new ZephyrAbility());
    }
}
