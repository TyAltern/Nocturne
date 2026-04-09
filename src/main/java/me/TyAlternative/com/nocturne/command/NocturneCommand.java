package me.TyAlternative.com.nocturne.command;

import me.TyAlternative.com.nocturne.config.ConfigManager;
import me.TyAlternative.com.nocturne.core.NocturneGame;
import me.TyAlternative.com.nocturne.player.NocturnePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Commande principale {@code /nocturne} (alias : {@code /noc}, {@code /n}).
 *
 * <p>Sous-commandes :
 * <ul>
 *   <li>{@code start}  — Démarre la partie.</li>
 *   <li>{@code stop}   — Arrête la partie.</li>
 *   <li>{@code skip}   — Passe à la phase suivante.</li>
 *   <li>{@code role [joueur]} — Affiche le rôle d'un joueur.</li>
 *   <li>{@code debug}  — Affiche l'état interne du jeu.</li>
 *   <li>{@code reload} — Recharge la configuration.</li>
 * </ul>
 *
 * <p>Permission requise : {@code nocturne.admin} (op par défaut).
 */
@SuppressWarnings({"unused", "DataFlowIssue", "SameParameterValue"})
public final class NocturneCommand implements CommandExecutor, TabCompleter {
    private static final List<String> SUBCOMMANDS =
            Arrays.asList("start","stop","skip","role","debug","reload");

    private final NocturneGame game;
    private final ConfigManager configManager;

    public NocturneCommand(@NotNull NocturneGame game, @NotNull ConfigManager configManager) {
        this.game = game;
        this.configManager = configManager;
    }

    // -------------------------------------------------------------------------
    // Exécution
    // -------------------------------------------------------------------------

    @Override
    public boolean onCommand(
            @NotNull CommandSender sender,
            @NotNull Command smd,
            @NotNull String label,
            @NotNull String @NotNull [] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cCommande réservée aux joueurs.");
            return true;
        }

        if (!player.hasPermission("nocturne.admin")) {
            player.sendMessage("§cPermission manquante : nocturne.admin");
            return true;
        }

        if (args.length == 0) {
            sendHelp(player);
            return true;
        }
        switch (args[0].toLowerCase()) {
            case "start"  -> handleStart(player);
            case "stop"   -> handleStop(player);
            case "skip"   -> handleSkip(player);
            case "role"   -> handleRole(player, args);
            case "debug"  -> handleDebug(player);
            case "reload" -> handleReload(player);
            default       -> sendHelp(player);
        }
        return true;
    }


    // -------------------------------------------------------------------------
    // Sub-commandes
    // -------------------------------------------------------------------------

    private void handleStart(@NotNull Player player) {
        if (game.isGameRunning()) {
            player.sendMessage("§cUne partie est déjà en cours !");
            return;
        }
        if (game.startGame()) player.sendMessage("§a Partie démarrée !");
        else player.sendMessage("§cImpossible de démarrer. Vérifiez la composition et les joueurs.");
    }

    private void handleStop(@NotNull Player player) {
        if (!game.isGameRunning()) {
            player.sendMessage("§cAucune partie en cours.");
            return;
        }
        game.stopGame("Arrêt manuel par " + player.getName());
        player.sendMessage("§aPartie arrêtée.");
    }

    private void handleSkip(@NotNull Player player) {
        if (!game.isGameRunning()) {
            player.sendMessage("§cAucune partie en cours.");
            return;
        }
        game.getPhaseManager().skipCurrentPhase();
        player.sendMessage("§aPhase passée.");
    }

    private void handleRole(@NotNull Player player, @NotNull String @NotNull[] args) {
        Player target = args.length >=2 ? player.getServer().getPlayer(args[1]) : player;

        if (target == null) {
            player.sendMessage("§cJoueur introuvable : " + args[1]);
            return;
        }

        NocturnePlayer nocturnePlayer = game.getPlayerManager().get(target.getUniqueId());
        if (nocturnePlayer != null && nocturnePlayer.hasRole()) {
            player.sendMessage("§7Rôle de §e" + target.getName() + " §7: " + nocturnePlayer.getRole().getDisplayName());
        } else {
            player.sendMessage("§7" + target.getName() + " §cn'a pas de rôle.");
        }
    }

    private void handleDebug(@NotNull Player player) {
        player.sendMessage("§8══════ §eNocturne Debug §8══════");
        player.sendMessage("§7Partie : " + (game.isGameRunning() ? "§aEn cours" : "§cArrêtée"));
        player.sendMessage("§7Phase : §e" + game.getCurrentPhase().getDisplayName());
        player.sendMessage("§7Manche : §e" + game.getRoundNumber());
        player.sendMessage("§7Vivants : §e" + game.getPlayerManager().getAliveCount());
        player.sendMessage("§7Timer restant : §e" + game.getPhaseManager().getRemainingSeconds() + "s");
        player.sendMessage("§8══════════════════════════");
    }

    private void handleReload(@NotNull Player player) {
        configManager.reload();
        player.sendMessage("§aConfiguration rechargée !");
    }

    private void sendHelp(@NotNull Player player) {
        player.sendMessage("§8══════ §eNocturne §8══════");
        player.sendMessage("§e/noc start §7— Démarrer la partie");
        player.sendMessage("§e/noc stop §7— Arrêter la partie");
        player.sendMessage("§e/noc skip §7— Passer la phase");
        player.sendMessage("§e/noc role [joueur] §7— Voir un rôle");
        player.sendMessage("§e/noc debug §7— État interne");
        player.sendMessage("§e/noc reload §7— Recharger la config");
        player.sendMessage("§8══════════════════════");
    }

    // -------------------------------------------------------------------------
    // Auto-complétion
    // -------------------------------------------------------------------------

    @Override
    public @NotNull List<String> onTabComplete(
            @NotNull CommandSender sender,
            @NotNull Command cmd,
            @NotNull String label,
            @NotNull String @NotNull [] args
    ) {
        if (args.length == 1) {
            return filterStart(args[0], SUBCOMMANDS);
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("role")){
            return  sender.getServer().getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(name -> name.toLowerCase().startsWith(args[1].toLowerCase()))
                    .collect(Collectors.toList());
        }
        return List.of();
    }

    private @NotNull List<String> filterStart(@NotNull String input, @NotNull List<String> options) {
        String lower = input.toLowerCase();
        return options.stream()
                .filter(s -> s.toLowerCase().startsWith(lower))
                .collect(Collectors.toList());
    }
}
