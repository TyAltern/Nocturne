package me.TyAlternative.com.nocturne.mechanics.glowing;


import fr.skytasul.glowingentities.GlowingBlocks;
import fr.skytasul.glowingentities.GlowingEntities;
import me.TyAlternative.com.nocturne.Nocturne;
import me.TyAlternative.com.nocturne.player.NocturnePlayer;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.logging.Logger;

/**
 * Gère la surbrillance des joueurs. </br>
 *
 * Utilise l'API de {@link GlowingEntities} et {@link GlowingBlocks}
 */
@SuppressWarnings("unused")
public final class GlowingManager {

    private final GlowingEntities glowingEntities;
    private final GlowingBlocks glowingBlocks;
    private final Logger logger;

    private final Map<UUID, GlowingEntries> glowingEntriesMap;



    public GlowingManager() {
        this.glowingEntities = new GlowingEntities(Nocturne.getInstance());
        this.glowingBlocks = new GlowingBlocks(Nocturne.getInstance());
        this.logger = Nocturne.getInstance().getLogger();
        this.glowingEntriesMap = new HashMap<>();
    }

    /**
     * AApplique un effet de surbrillance au {@code target} visible uniquement par le {@code viewer}
     *
     * @param viewer    données Nocturne du viewer
     * @param target    données Nocturne de la cible
     * @param glowColor couleur ChatColor (une parmi les 16 couleurs par défauts)
     */
    public void setGlow(@NotNull NocturnePlayer viewer, @NotNull NocturnePlayer target, ChatColor glowColor) {
        if (glowColor == null) glowColor = ChatColor.WHITE;

        if (viewer.getPlayer() == null || target.getPlayer() == null) return;

        try {
            glowingEntities.setGlowing(target.getPlayer(), viewer.getPlayer(), glowColor);

            GlowingEntries entries = glowingEntriesMap.get(viewer.getPlayerId());
            if (entries == null) entries = new GlowingEntries();
            entries.addEntry(target.getPlayerId(),glowColor);
        } catch (ReflectiveOperationException e) {
            logger.warning("L'effet de surbrillance du joueur %s visible par %s n'est pas visible : %s".formatted(target.getPlayer().getName(), viewer.getPlayer().getName(), e.getMessage()));
        }
    }


    public void removeGlow(@NotNull NocturnePlayer viewer, @NotNull NocturnePlayer target) {
        if (viewer.getPlayer() == null || target.getPlayer() == null) return;


        try {
            glowingEntities.unsetGlowing(target.getPlayer(), viewer.getPlayer());

            GlowingEntries entries = glowingEntriesMap.get(viewer.getPlayerId());
            if (entries == null) entries = new GlowingEntries();
            entries.removeEntry(target.getPlayerId());
        } catch (ReflectiveOperationException e) {
            logger.warning("L'effet de surbrillance du joueur %s n'a pas été retiré pour %s : %s".formatted(target.getPlayer().getName(), viewer.getPlayer().getName(), e.getMessage()));
        }
    }

    public void removeGlowForAllTargets(@NotNull NocturnePlayer viewer) {
        if (viewer.getPlayer() == null) return;

        GlowingEntries entries = glowingEntriesMap.get(viewer.getPlayerId());
        if (entries == null) entries = new GlowingEntries();

        try {
            for (UUID uuid : entries.entries.keySet()) {
                Player target = Bukkit.getPlayer(uuid);

                    glowingEntities.unsetGlowing(target, viewer.getPlayer());

            }

        } catch (ReflectiveOperationException e) {
            logger.warning("L'effet de surbrillance des joueurs n'a pas été retiré pour %s : %s".formatted( viewer.getPlayer().getName(), e.getMessage()));
        }
        glowingEntriesMap.remove(viewer.getPlayerId());

    }


    /**
     * Termine la tâche de {@code glowingEntities} & {@code glowingBlock}
     */
    public void disable() {
        glowingBlocks.disable();
        glowingEntities.disable();
    }


    private class GlowingEntries {
        private final Map<UUID, ChatColor> entries;

        public GlowingEntries() {
            this.entries = new HashMap<>();
        }

        public void addEntry(@NotNull UUID targetId, @NotNull ChatColor color) {
            entries.put(targetId, color);
        }

        public @NotNull ChatColor getEntry(@NotNull UUID targetId) {
            return entries.get(targetId);
        }

        public void removeEntry(@NotNull UUID targetId) {
            entries.remove(targetId);
        }

        public void clearEntry() {
            entries.clear();
        }
    }











    private enum ColoredTeam {
        BLACK(NamedTextColor.BLACK),
        DARK_BLUE(NamedTextColor.DARK_BLUE),
        DARK_GREEN(NamedTextColor.DARK_GREEN),
        DARK_AQUA(NamedTextColor.DARK_AQUA),
        DARK_RED(NamedTextColor.DARK_RED),
        DARK_PURPLE(NamedTextColor.DARK_PURPLE),
        GOLD(NamedTextColor.GOLD),
        GRAY(NamedTextColor.GRAY),
        DARK_GRAY(NamedTextColor.DARK_GRAY),
        BLUE(NamedTextColor.BLUE),
        GREEN(NamedTextColor.GREEN),
        AQUA(NamedTextColor.AQUA),
        RED(NamedTextColor.RED),
        LIGHT_PURPLE(NamedTextColor.LIGHT_PURPLE),
        YELLOW(NamedTextColor.YELLOW),
        WHITE(NamedTextColor.WHITE);

        private final Team team;
        private final NamedTextColor textColor;

        ColoredTeam(NamedTextColor textColor) {
            this.textColor = textColor;
            Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
            String teamName = textColor.toString() + "-colored-team";
            Team testTeam = scoreboard.getTeam(teamName);
            if (testTeam == null) {
                this.team = scoreboard.registerNewTeam(teamName);
                this.team.color(textColor);
            } else {
                this.team = testTeam;
            }
        }

        public Team getTeam() { return this.team; }
        public NamedTextColor getTextColor() { return this.textColor; }
    }


}
