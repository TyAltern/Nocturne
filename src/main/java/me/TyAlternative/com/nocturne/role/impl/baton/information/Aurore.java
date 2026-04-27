package me.TyAlternative.com.nocturne.role.impl.baton.information;

import me.TyAlternative.com.nocturne.ability.impl.info.DiffractionAbility;
import me.TyAlternative.com.nocturne.ability.impl.info.PolarisationAbility;
import me.TyAlternative.com.nocturne.api.role.RoleTeam;
import me.TyAlternative.com.nocturne.api.role.RoleType;
import me.TyAlternative.com.nocturne.role.AbstractRole;
import me.TyAlternative.com.nocturne.role.RoleIds;
import org.bukkit.Material;

/**
 * L'Aurore — Bâton à capacité en mode drunk.
 *
 * <p>Capacités :
 * <ul>
 *   <li>{@link DiffractionAbility} (drunk) — affiche des particules violettes
 *       autour des joueurs ayant utilisé une capacité active. En mode drunk,
 *       les signaux sont tous des leurres aléatoires : l'information est sans valeur.</li>
 * </ul>
 *
 * <p>Le mode drunk est intentionnel pour ce rôle : l'Aurore reçoit bien des signaux
 * visuels, mais ils sont tous des faux positifs, rendant sa capacité inutile.
 */
public final class Aurore extends AbstractRole {

    public static final String ID = RoleIds.AURORE;

    public Aurore() {
        super(
                ID,
                "§6§lL'Aurore",
                "Votre sagesse illumine le groupe. Éliminez les Flammes et gagnez avec les autres Bâtons.",
                RoleType.BATON,
                RoleTeam.BATONS,
                Material.ORANGE_CONCRETE_POWDER
        );
        registerAbility(new PolarisationAbility());
    }
}
