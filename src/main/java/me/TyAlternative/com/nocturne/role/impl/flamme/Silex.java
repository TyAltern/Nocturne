package me.TyAlternative.com.nocturne.role.impl.flamme;

import me.TyAlternative.com.nocturne.ability.impl.curse.AmnesieAbility;
import me.TyAlternative.com.nocturne.api.role.RoleTeam;
import me.TyAlternative.com.nocturne.api.role.RoleType;
import me.TyAlternative.com.nocturne.role.AbstractRole;
import me.TyAlternative.com.nocturne.role.RoleIds;
import org.bukkit.Material;

/**
 * Le Silex — Flamme du duo Silex/Acier, victime d'amnésie.
 *
 * <p>Capacités :
 * <ul>
 *   <li><b>Amnésie</b> (Malédiction, cachée) — ignore son vrai rôle et reçoit
 *       la description d'un rôle aléatoire, dont les capacités fonctionnent
 *       de façon erronée (mode drunk).</li>
 * </ul>
 */
public final class Silex extends AbstractRole {

    public static final String ID = RoleIds.SILEX;

    public Silex() {
        super(
                ID,
                "§8§lLe Silex",
                "Un être ayant perdu son passé. Retrouvez votre partenaire l'Acier et éliminez tous les autres.",
                RoleType.FLAMME,
                RoleTeam.SILEX_ACIER,
                Material.FLINT
        );

         registerHiddenAbility(new AmnesieAbility());
    }
}

