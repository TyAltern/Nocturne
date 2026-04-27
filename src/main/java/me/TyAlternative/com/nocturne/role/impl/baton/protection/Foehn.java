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
                "Vent d'ouest chaud et discret qui descend des montagnes. Votre protection passive se porte naturellement vers ceux qui en ont le plus besoin. Éliminez les §cFlammes§f et gagnez avec les autres §eBâtons§f.",
                RoleType.BATON,
                RoleTeam.BATONS,
                Material.TUFF
        );
        registerAbility(new ZephyrAbility());
    }
}
