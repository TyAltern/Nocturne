package me.TyAlternative.com.nocturne.role.impl.baton.information;

import me.TyAlternative.com.nocturne.ability.impl.info.ScintillementAbility;
import me.TyAlternative.com.nocturne.api.role.RoleTeam;
import me.TyAlternative.com.nocturne.api.role.RoleType;
import me.TyAlternative.com.nocturne.role.AbstractRole;
import me.TyAlternative.com.nocturne.role.RoleIds;
import org.bukkit.Material;

public class Vesper extends AbstractRole {

    public static final String ID = RoleIds.VESPER;

    public Vesper() {
        super(
                ID,
                "§7§lVesper",
                "Votre intuition est votre guide. Éliminez les Flammes et gagnez avec les autres Bâtons.",
                RoleType.BATON,
                RoleTeam.BATONS,
                Material.GRAY_CONCRETE_POWDER
        );
        registerAbility(new ScintillementAbility());
    }
}
