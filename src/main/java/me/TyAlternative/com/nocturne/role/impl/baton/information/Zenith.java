package me.TyAlternative.com.nocturne.role.impl.baton.information;

import me.TyAlternative.com.nocturne.ability.impl.info.IrradiationAbility;
import me.TyAlternative.com.nocturne.api.role.RoleTeam;
import me.TyAlternative.com.nocturne.api.role.RoleType;
import me.TyAlternative.com.nocturne.role.AbstractRole;
import me.TyAlternative.com.nocturne.role.RoleIds;
import org.bukkit.Material;

public class Zenith extends AbstractRole {

    public static final String ID = RoleIds.ZENITH;

    public Zenith() {
        super(
                ID,
                "§e§lLe Zenith",
                "Votre intuition est votre guide. Éliminez les Flammes et gagnez avec les autres Bâtons.",
                RoleType.BATON,
                RoleTeam.BATONS,
                Material.YELLOW_CONCRETE_POWDER
        );
        registerAbility(new IrradiationAbility());
    }
}
