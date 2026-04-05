package me.TyAlternative.com.nocturne.role.impl.baton;

import me.TyAlternative.com.nocturne.api.role.RoleTeam;
import me.TyAlternative.com.nocturne.api.role.RoleType;
import me.TyAlternative.com.nocturne.role.AbstractRole;
import org.bukkit.Material;

/**
 * La Cendre — Bâton immunisé contre la Poudre de Cheminée de l'Étincelle.
 *
 * <p>Capacités :
 * <ul>
 *   <li><b>Cendre</b> (Passif) — immunise contre la capacité Poudre de Cheminée
 *       de l'Étincelle : ne peut pas être ciblé par l'échange de position.</li>
 * </ul>
 */
public final class Cendre extends AbstractRole {

    public static final String ID = "CENDRE";

    public Cendre() {
        super(
                ID,
                "§7§lLa Cendre",
                "Vous êtes l'âme d'une ancienne Étincelle. Certains de ses pouvoirs ne vous affectent plus.",
                RoleType.BATON,
                RoleTeam.BATONS,
                Material.GUNPOWDER
        );
        // TODO : registerAbility(new CendreAbility());
    }
}