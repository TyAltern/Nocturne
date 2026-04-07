package me.TyAlternative.com.nocturne.player;

import  me.TyAlternative.com.nocturne.api.role.RoleTeam;
import  me.TyAlternative.com.nocturne.api.role.RoleType;
import me.TyAlternative.com.nocturne.core.NocturneGame;
import me.TyAlternative.com.nocturne.listener.ConnectionListener;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Gestionnaire centralisé de tous les {@link NocturnePlayer} de la session en cours.
 *
 * <p>Fournit des méthodes de récupération et de filtrage sans jamais contenir
 * de logiques métiers. Toute la logique appartient aux managers spécialisés.
 *
 * <h2>Cycle de vie</h2>
 * <ul>
 *   <li>Un {@link NocturnePlayer} est créé à la connexion ({@link #getOrCreate}).</li>
 *   <li>{@link #resetAllForNewRound()} est appelé entre chaque manche.</li>
 *   <li>{@link #clearAll()} est appelé en fin de partie pour libérer la mémoire.</li>
 * </ul>
 */
@SuppressWarnings("unused")
public class PlayerManager {

    /** Map principale : UUID → données joueur. */
    private final Map<UUID, NocturnePlayer> players = new HashMap<>();


    // -------------------------------------------------------------------------
    // Gestion CRUD
    // -------------------------------------------------------------------------

    /**
     * Retourne le {@link NocturnePlayer} associé à ce joueur, en le créant si nécessaire.
     * Point d'entrée utilisé par le {@link ConnectionListener}.
     */
    public @NotNull NocturnePlayer getOrCreate(@NotNull Player player) {
        return players.computeIfAbsent(player.getUniqueId(), NocturnePlayer::new);
    }

    /**
     * Retourne le {@link NocturnePlayer} associé à ce {@link Player} Bukkit, ou {@code null}
     * s'il n'a pas encore de données enregistrées.
     */
    public @Nullable NocturnePlayer get(@NotNull Player player) {
        return players.get(player.getUniqueId());
    }

    /**
     * Retourne le {@link NocturnePlayer} associé à cet {@link UUID}, ou {@code null} s'il
     * n'existe pas.
     */
    public @Nullable NocturnePlayer get(@NotNull UUID uuid) {
        return players.get(uuid);
    }

    /** {@code true} si des données existent pour ce joueur. */
    public boolean has(@NotNull Player player) {
        return players.containsKey(player.getUniqueId());
    }

    /** Supprime les données d'un joueur. Utiliser avec précaution en cours de partie. */
    public void remove(@NotNull UUID uuid) {
        players.remove(uuid);
    }


    // -------------------------------------------------------------------------
    // Collections et filtres
    // -------------------------------------------------------------------------

    /**
     * Retourne tous les {@link NocturnePlayer} enregistrés, quel que soit leur état.
     * La collection retournée est une vue instantanée non-modifiable.
     */
    public @NotNull @Unmodifiable Collection<NocturnePlayer> getAll() {
        return  Collections.unmodifiableCollection(players.values());
    }

    /**
     * Retourne tous les joueurs dont l'état correspond à {@code state}.
     *
     * @param state état à filtrer
     */
    public @NotNull List<NocturnePlayer> getByState(@NotNull PlayerState state) {
        return players.values().stream()
                .filter(p -> p.getState() == state)
                .collect(Collectors.toList());
    }

    /**
     * Retourne tous les joueurs actuellement vivants ({@link PlayerState#PLAYING}).
     */
    public @NotNull List<NocturnePlayer> getAlive() {
        return players.values().stream()
                .filter(NocturnePlayer::isAlive)
                .collect(Collectors.toList());
    }

    /**
     * Retourne les instances Bukkit {@link Player} de tous les joueurs vivants en ligne.
     * Les joueurs déconnectés sont exclus même si leur état est {@link PlayerState#PLAYING}.
     */
    public @NotNull List<Player> getAlivePlayers() {
        return players.values().stream()
                .map(NocturnePlayer::getPlayer)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * Retourne les joueurs vivants appartenant au type de rôle donné.
     *
     * @param type type de rôle à filtrer
     */
    public @NotNull List<NocturnePlayer> getAliveByType(@NotNull RoleType type) {
        return players.values().stream()
                .filter(p -> p.getRole() != null && p.getRole().getType() == type)
                .collect(Collectors.toList());
    }

    /**
     * Retourne les joueurs vivants appartenant à l'équipe donnée.
     *
     * @param team équipe à filtrer
     */
    public @NotNull List<NocturnePlayer> getAliveByTeam(@NotNull RoleTeam team) {
        return players.values().stream()
                .filter(p -> p.getRole() != null && p.getRole().getTeam() == team)
                .collect(Collectors.toList());
    }

    /**
     * Retourne les joueurs vivants situés dans un rayon donné autour d'un joueur de référence.
     *
     * <p>Le joueur de référence est exclu du résultat.
     * Les joueurs déconnectés (sans instance Bukkit) sont également exclus.
     *
     * @param reference joueur central
     * @param radius    rayon en blocs
     */
    public @NotNull List<NocturnePlayer> getAliveInRadius(
            @NotNull NocturnePlayer reference,
            double radius
    ) {
        Player referencePlayer = reference.getPlayer();
        if (referencePlayer == null) return Collections.emptyList();

        return getAlive().stream()
                .filter(p -> !p.getPlayerId().equals(reference.getPlayerId()))
                .filter(p -> {
                    Player filteredPlayer = p.getPlayer();
                    if (filteredPlayer == null) return false;
                    // Ignorer les joueurs dans un monde différent
                    if (!Bukkit.getWorlds().equals(referencePlayer.getWorld())) return false;
                    return referencePlayer.getLocation().distance(filteredPlayer.getLocation()) <= radius;
                })
                .collect(Collectors.toList());
    }

    // -------------------------------------------------------------------------
    // Statistiques
    // -------------------------------------------------------------------------

    /** Nombre de joueurs vivants. */
    public int getAliveCount() {
        return (int) players.values().stream()
                .filter(NocturnePlayer::isAlive)
                .count();
    }

    /** Nombre de joueurs vivants dans l'équipe donnée. */
    public int getAlivePlayerByTeam(@NotNull RoleTeam team) {
        return (int) players.values().stream()
                .filter(p -> p.getRole() != null && p.getRole().getTeam() == team)
                .count();
    }

    /** Nombre total de joueurs enregistrés, toutes states confondues. */
    public int getTotalCount() {
        return players.size();
    }


    // -------------------------------------------------------------------------
    // Réinitialisations
    // -------------------------------------------------------------------------

    /**
     * Réinitialise les données de manche pour tous les joueurs vivants.
     * À appeler au début de chaque nouvelle manche via le {@link NocturneGame}.
     */
    public void resetAllForNewRound() {
        players.values().forEach(NocturnePlayer::resetForNewRound);
    }


    /**
     * Effectue une réinitialisation complète de tous les joueurs et vide le registre.
     * À appeler uniquement en fin de partie.
     */
    public void clearAll() {
        players.values().forEach(NocturnePlayer::resetFull);
        players.clear();
    }

}
