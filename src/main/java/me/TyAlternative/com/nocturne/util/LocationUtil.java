package me.TyAlternative.com.nocturne.util;

import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Utilitaires de manipulation de {@link Location}.
 */
@SuppressWarnings("unused")
public class LocationUtil {

    private LocationUtil() {}

    /**
     * {@code true} si les deux locations désignent le même bloc (coordonnées entières).
     * Retourne {@code false} si l'une des locations est {@code null} ou dans des mondes différents.
     */
    public static boolean isSameBlock(@Nullable Location a, @Nullable Location b) {
        if (a == null || b == null) return false;
        if (a.getWorld() == null || !a.getWorld().equals(b.getWorld())) return false;
        return a.getBlockX() == b.getBlockX()
                && a.getBlockY() == b.getBlockY()
                && a.getBlockZ() == b.getBlockZ();
    }

    /**
     * Formate une location en chaîne lisible {@code "x, y, z"}.
     */
    public static @NotNull String format(@NotNull Location loc) {
        return "%.1f, %.1f, %.1f".formatted(loc.getX(), loc.getY(), loc.getZ());
    }
}
