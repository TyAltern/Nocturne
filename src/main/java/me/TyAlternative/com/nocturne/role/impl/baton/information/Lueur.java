package me.TyAlternative.com.nocturne.role.impl.baton.information;

import me.TyAlternative.com.nocturne.ability.impl.info.ReverberationLumineuseAbility;
import me.TyAlternative.com.nocturne.api.role.RoleTeam;
import me.TyAlternative.com.nocturne.api.role.RoleType;
import me.TyAlternative.com.nocturne.role.AbstractRole;
import me.TyAlternative.com.nocturne.role.RoleIds;
import org.bukkit.Material;

/**
 * La Lueur — Bâton.
 *
 * <p>Capacités :
 * <ul>
 *   <li>{@link ReverberationLumineuseAbility} (drunk) — affiche des particules dorées
 *       autour des joueurs ayant été ciblés par une capacité active. En mode drunk,
 *       les signaux sont tous des leurres aléatoires.</li>
 * </ul>
 *
 * <p>Symétrique à l'Aurore : l'Aurore voit les casters, la Lueur voit les targets.
 * Tous deux reçoivent des informations bruitées (mode drunk intentionnel).
 */
public final class Lueur extends AbstractRole {

    public static final String ID = RoleIds.LUEUR;

    public Lueur() {
        super(
                ID,
                "§e§lLa Lueur",
                "Votre intuition est votre guide. Éliminez les Flammes et gagnez avec les autres Bâtons.",
                RoleType.BATON,
                RoleTeam.BATONS,
                Material.YELLOW_STAINED_GLASS
        );
        registerAbility(new ReverberationLumineuseAbility());
    }
}
