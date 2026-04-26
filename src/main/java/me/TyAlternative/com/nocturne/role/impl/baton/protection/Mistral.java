package me.TyAlternative.com.nocturne.role.impl.baton.protection;

import me.TyAlternative.com.nocturne.ability.impl.protection.BoreeAbility;
import me.TyAlternative.com.nocturne.api.role.RoleTeam;
import me.TyAlternative.com.nocturne.api.role.RoleType;
import me.TyAlternative.com.nocturne.role.AbstractRole;
import me.TyAlternative.com.nocturne.role.RoleIds;
import org.bukkit.Material;

public final class Mistral extends AbstractRole {


    public static final String ID = RoleIds.MISTRAL;

    public Mistral() {
        super(
                ID,
                "§7§lLe Mistral",
                "",
                RoleType.BATON,
                RoleTeam.BATONS,
                Material.PHANTOM_MEMBRANE
        );
        registerAbility(new BoreeAbility());
    }
}
