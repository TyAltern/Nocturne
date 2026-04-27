package me.TyAlternative.com.nocturne.role.impl.baton.information;

import me.TyAlternative.com.nocturne.ability.impl.info.OccultationAbility;
import me.TyAlternative.com.nocturne.api.role.RoleTeam;
import me.TyAlternative.com.nocturne.api.role.RoleType;
import me.TyAlternative.com.nocturne.role.AbstractRole;
import me.TyAlternative.com.nocturne.role.RoleIds;
import org.bukkit.Material;

public class Nuit extends AbstractRole {

    public static final String ID = RoleIds.NUIT;

    public Nuit() {
        super(
                ID,
                "§8§lLa Nuit",
                "Votre intuition est votre guide. Éliminez les Flammes et gagnez avec les autres Bâtons.",
                RoleType.BATON,
                RoleTeam.BATONS,
                Material.BLACK_CONCRETE_POWDER
        );
        registerAbility(new OccultationAbility());
    }
}
