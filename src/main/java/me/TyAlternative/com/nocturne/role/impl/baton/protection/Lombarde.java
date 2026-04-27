package me.TyAlternative.com.nocturne.role.impl.baton.protection;

import me.TyAlternative.com.nocturne.ability.impl.protection.EurosAbility;
import me.TyAlternative.com.nocturne.api.role.RoleTeam;
import me.TyAlternative.com.nocturne.api.role.RoleType;
import me.TyAlternative.com.nocturne.role.AbstractRole;
import me.TyAlternative.com.nocturne.role.RoleIds;
import org.bukkit.Material;

public final class Lombarde extends AbstractRole {


    public static final String ID = RoleIds.LOMBARDE;

    public Lombarde() {
        super(
                ID,
                "§f§lLa Lombarde",
                "Vent violent d'est. Votre protection récompense celui qui ose vous chercher. Éliminez les §cFlammes§f et gagnez avec les autres §eBâtons§f.",
                RoleType.BATON,
                RoleTeam.BATONS,
                Material.CALCITE
        );
        registerAbility(new EurosAbility());
    }
}
