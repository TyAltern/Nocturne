package me.TyAlternative.com.nocturne.api.role;

import me.TyAlternative.com.nocturne.role.RoleRegistry;
import org.jetbrains.annotations.NotNull;

/**
 * Fabrique fonctionnelle produisant une nouvelle instance de {@link Role} à chaque appel.
 *
 * <p>Chaque rôle enregistre sa propre fabrique dans le {@link RoleRegistry}
 * via une référence de constructeur :
 *
 * <pre>{@code
 * registry.register("ETINCELLE", Etincelle::new);
 * }</pre>
 *
 * <p>Cela garantit que chaque joueur reçoit une instance isolée, évitant tout
 * partage d'état entre joueurs portant le même rôle.
 */

@FunctionalInterface
public interface RoleFactory {

    /**
     * Crée et retourne une nouvelle instance du rôle associé à cette fabrique.
     *
     * @return instance fraîche, non partagée, prête à être assignée à un joueur
     */
    @NotNull Role create();

}