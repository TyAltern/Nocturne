package me.TyAlternative.com.nocturne.util;

/**
 * Utilitaires de formatage des durées.
 */
@SuppressWarnings("unused")
public final class TimeUtil {

    private TimeUtil() {}

    /**
     * Formate un nombre de secondes en {@code MM:SS}.
     *
     * @param totalSeconds secondes totales (≥ 0)
     * @return chaîne formatée, ex: {@code "02:35"}
     */
    public static String formatSeconds(int totalSeconds) {
        int seconds = Math.max (0, totalSeconds);
        int minutes = seconds / 60;
        int secs = seconds % 60;
        return "%02d:%02d".formatted(minutes, secs);
    }
}
