package me.TyAlternative.com.nocturne.role.impl.baton.protection;

import me.TyAlternative.com.nocturne.ability.impl.curse.MurmurationAbility;
import me.TyAlternative.com.nocturne.ability.impl.protection.CaeciasAbility;
import me.TyAlternative.com.nocturne.api.role.RoleTeam;
import me.TyAlternative.com.nocturne.api.role.RoleType;
import me.TyAlternative.com.nocturne.role.AbstractRole;
import me.TyAlternative.com.nocturne.role.RoleIds;
import org.bukkit.Material;
/**
 * La Bise — Bâton protecteur de proximité.
 */
public final class Bise extends AbstractRole {

    public static final String ID = RoleIds.BISE;

    public Bise() {
        super(
                ID,
                "§f§lLa Bise",
                "Vent du nord-est, protecteur mais sacrificiel. Éliminez les §cFlammes§f et gagnez avec les autres §eBâtons§f.",
                RoleType.BATON,
                RoleTeam.BATONS,
                Material.SNOWBALL
        );
        registerAbility(new CaeciasAbility());
        registerAbility(new MurmurationAbility());
    }
}
