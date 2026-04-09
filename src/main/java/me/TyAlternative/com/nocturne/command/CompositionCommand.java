package me.TyAlternative.com.nocturne.command;

import me.TyAlternative.com.nocturne.composition.CompositionGUI;
import me.TyAlternative.com.nocturne.core.NocturneGame;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

/**
 * Commande {@code /compo} — gestion de la composition des rôles.
 *
 * <p>Sans argument : ouvre le GUI de composition. <br>
 * {@code display} : affiche la composition dans le chat.
 */
@SuppressWarnings("unused")
public final class CompositionCommand implements CommandExecutor, TabCompleter{

    private final NocturneGame game;
    private final CompositionGUI gui;

    public CompositionCommand(NocturneGame game, CompositionGUI gui) {
        this.game = game;
        this.gui = gui;
    }

    @Override
    public boolean onCommand(
            @NotNull CommandSender sender,
            @NotNull Command cmd,
            @NotNull String label,
            @NotNull String @NotNull [] args
    ) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cCommande réservée aux joueurs.");
            return true;
        }

        if (!player.hasPermission("nocturne.composition")) {
            player.sendMessage("§cPermission manquante : nocturne.composition");
            return true;
        }

        if (args.length == 0 || args[0].equalsIgnoreCase("open")) {
            gui.open(player);
            return true;
        }

        if (args[0].equalsIgnoreCase("display") || args[0].equalsIgnoreCase("show")) {
            handleDisplay(player);
            return true;
        }

        gui.open(player);
        return true;
    }

    private void handleDisplay(@NotNull Player player) {
        player.sendMessage("§8══════ §eComposition §8══════");
        Map<String, Integer> comp = game.getCompositionManager().getCompositionMap();

        if (comp.isEmpty()) {
            player.sendMessage("§7Aucun rôle défini. Utilisez §e/compo §7pour configurer.");
        } else {
            for (Map.Entry<String, Integer> entry : comp.entrySet()) {
                player.sendMessage("§7 · §e" + entry.getValue() + "x §r" + entry.getKey());
            }
            player.sendMessage("");
            player.sendMessage("§7Total : §e" + game.getCompositionManager().getTotalPlayers() + " §7joueurs");
        }
        player.sendMessage("§8═════════════════════════");
    }

    @Override
    public @NotNull List<String> onTabComplete(
            @NotNull CommandSender sender,
            @NotNull Command cmd,
            @NotNull String label,
            @NotNull String @NotNull [] args
    ) {
        if (args.length == 1) {
            return List.of("display", "show", "open");
        }
        return List.of();
    }

    /** Retourne le GUI pour l'enregistrement du listener de clic. */
    public @NotNull CompositionGUI getGui() {
        return gui;
    }
}
