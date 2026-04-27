package me.TyAlternative.com.nocturne.role.impl.baton.protection;

import me.TyAlternative.com.nocturne.ability.impl.protection.ScironAbility;
import me.TyAlternative.com.nocturne.api.role.RoleTeam;
import me.TyAlternative.com.nocturne.api.role.RoleType;
import me.TyAlternative.com.nocturne.role.AbstractRole;
import me.TyAlternative.com.nocturne.role.RoleIds;
import org.bukkit.Material;

public final class Galerne extends AbstractRole {

    public static final String ID = RoleIds.GALERNE;

    public Galerne() {
        super(
                ID,
                "§9§lLa Galerne",
                "Vent du nord-ouest, insaisissable et retors. Votre protection ne vient pas de vous, mais de ceux que vous observez. Éliminez les §cFlammes§f et gagnez avec les autres §eBâtons§f.",
                RoleType.BATON,
                RoleTeam.BATONS,
                Material.PRISMARINE_SHARD
        );
        registerAbility(new ScironAbility());
    }
}