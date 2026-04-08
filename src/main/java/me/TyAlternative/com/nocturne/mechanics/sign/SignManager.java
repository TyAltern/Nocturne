package me.TyAlternative.com.nocturne.mechanics.sign;

import me.TyAlternative.com.nocturne.util.LocationUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Suit les panneaux posés par les joueurs pendant la phase de Gameplay.
 *
 * <p>En fin de phase, {@link #clearAll()} supprime physiquement tous les panneaux
 * du monde et vide le registre interne.
 */
@SuppressWarnings("unused")
public final class SignManager {

    private final List<Location> placedSigns = new ArrayList<>();

    /**
     * Enregistre un panneau nouvellement posé.
     *
     * @param location position du panneau (clonée pour éviter les mutations externes)
     */
    public void add(@NotNull Location location) {
        placedSigns.add(location.clone());
    }

    /**
     * Retire un panneau du registre (après sa destruction par un joueur).
     *
     * @param location position du panneau à retirer
     */
    public void remove(@NotNull Location location) {
        placedSigns.removeIf(loc -> LocationUtil.isSameBlock(loc, location));
    }


    /**
     * {@code true} si un panneau enregistré se trouve à la position donnée.
     *
     * @param location position à vérifier
     */
    public boolean isRegistered(@NotNull Location location) {
        return placedSigns.stream().anyMatch(loc -> LocationUtil.isSameBlock(loc, location));
    }

    /**
     * Supprime physiquement tous les panneaux enregistrés du monde et vide le registre.
     * Appelé en début de chaque phase de Gameplay.
     */
    public void clearAll() {
        // Itérer en ordre inverse pour éviter les problèmes de liste modifiée
        for (int i = placedSigns.size() - 1; i >= 0; i--) {
            Location loc = placedSigns.get(i);
            if (loc.getBlock().getType().toString().contains("SIGN")) {
                loc.getBlock().setType(Material.AIR);
            }
        }
        placedSigns.clear();
    }

    /**
     * Retourne une vue non-modifiable de tous les panneaux enregistrés.
     */
    public @NotNull @Unmodifiable List<Location> getAll() {
        return Collections.unmodifiableList(placedSigns);
    }

    /** Nombre de panneaux actuellement enregistrés. */
    public int count() {
        return placedSigns.size();
    }

}
