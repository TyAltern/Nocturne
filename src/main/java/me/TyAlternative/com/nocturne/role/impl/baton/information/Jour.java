package me.TyAlternative.com.nocturne.role.impl.baton.information;

import me.TyAlternative.com.nocturne.ability.impl.info.TransmissionAbility;
import me.TyAlternative.com.nocturne.api.role.RoleTeam;
import me.TyAlternative.com.nocturne.api.role.RoleType;
import me.TyAlternative.com.nocturne.role.AbstractRole;
import me.TyAlternative.com.nocturne.role.RoleIds;
import org.bukkit.Material;

public final class Jour extends AbstractRole {

    public static final String ID = RoleIds.JOUR;

    public Jour() {
        super(
                ID,
                "§b§lLe Jour",
                "Votre intuition est votre guide. Éliminez les Flammes et gagnez avec les autres Bâtons.",
                RoleType.BATON,
                RoleTeam.BATONS,
                Material.LIGHT_BLUE_CONCRETE_POWDER
        );
        registerAbility(new TransmissionAbility());
    }
}
