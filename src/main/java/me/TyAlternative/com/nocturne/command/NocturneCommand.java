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
            Arrays.asList("start","stop","skip","role","debug","reload","player");
    private static final List<String> PLAYERS_SUBCOMMAND =
            Arrays.asList("weight","canVote","canBeVote","isHidden","hideVoteImmunity","debug");

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
            case "player" -> handlePlayer(player, args);
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
        player.sendMessage("§8====== §eNocturne Debug §8======");
        player.sendMessage("§7Partie : " + (game.isGameRunning() ? "§aEn cours" : "§cArrêtée"));
        player.sendMessage("§7Phase : §e" + game.getCurrentPhase().getDisplayName());
        player.sendMessage("§7Manche : §e" + game.getRoundNumber());
        player.sendMessage("§7Vivants : §e" + game.getPlayerManager().getAliveCount());
        player.sendMessage("§7Timer restant : §e" + game.getPhaseManager().getRemainingSeconds() + "s");
        player.sendMessage("§8==========================");
    }

    private void handlePlayer(@NotNull Player player, @NotNull String @NotNull[] args) {
        if (!game.isGameRunning()) {
            player.sendMessage("§cAucune partie en cours.");
            return;
        }

        Player target = args.length >=2 ? player.getServer().getPlayer(args[1]) : player;

        if (target == null) {
            player.sendMessage("§cJoueur introuvable : " + args[1]);
            return;
        }

        NocturnePlayer nocturneTarget = game.getPlayerManager().get(target);
        if (nocturneTarget == null && !nocturneTarget.isAlive()) {
            player.sendMessage("§7" + target.getName() + " §cn'est pas en vie.");
        }
        boolean isSetter = args.length >= 4 && args[3].equalsIgnoreCase("set");
        if (isSetter && (args.length < 5 || !PLAYERS_SUBCOMMAND.contains(args[2]))) {
            player.sendMessage("§cVous devez rentrer une propriété que vous voulez modifier.");
        }

        String property = args.length >= 3 ? args[2] : null;
        switch (property) {
            case "weight" -> {
                if (isSetter && args[4].chars().allMatch(Character::isDigit)) {
                    nocturneTarget.setVoteWeight(Integer.parseInt(args[4]));
                    player.sendMessage("§eLe poids de vote de §6" + target.getName() + "§e a été modifié par §6§l" + nocturneTarget.getVoteWeight() + "§r§e.");
                } else {
                    player.sendMessage("§eLe poids de vote de §6" + target.getName() + "§e est de §6§l" + nocturneTarget.getVoteWeight() + "§r§e.");
                }
            }
            case "canVote" -> {
                if (isSetter && (args[4].equalsIgnoreCase("true") || args[4].equalsIgnoreCase("false"))) {
                    nocturneTarget.setCanVote(Boolean.parseBoolean(args[4]));
                    player.sendMessage("§eLe joueur §6" + target.getName() + "§e " + (nocturneTarget.canVote() ? "peut maintenant" : "ne peut plus") + " voter.");

                } else {
                    player.sendMessage("§eLe joueur §6" + target.getName() + "§e " + (nocturneTarget.canVote() ? "peut" : "ne peut pas") + " voter.");
                }
            }
            case "canBeVote" -> {
                if (isSetter && (args[4].equalsIgnoreCase("true") || args[4].equalsIgnoreCase("false"))) {
                    nocturneTarget.setCanBeVoted(Boolean.parseBoolean(args[4]));
                    player.sendMessage("§eLe joueur §6" + target.getName() + "§e " + (nocturneTarget.canBeVoted() ? "peut maintenant" : "ne peut plus") + " être voté.");

                } else {
                    player.sendMessage("§eLe joueur §6" + target.getName() + "§e " + (nocturneTarget.canBeVoted() ? "peut" : "ne peut pas") + " être voté.");
                }
            }
            case "isHidden" -> {
                if (isSetter && (args[4].equalsIgnoreCase("true") || args[4].equalsIgnoreCase("false"))) {
                    nocturneTarget.setHiddenInVote(Boolean.parseBoolean(args[4]));
                    player.sendMessage("§eLe joueur §6" + target.getName() + "§e " + (nocturneTarget.isVoteImmunityHidden() ? "est maintenant" : "n'est plus") + " caché.");

                } else {
                    player.sendMessage("§eLe joueur §6" + target.getName() + "§e " + (nocturneTarget.isVoteImmunityHidden() ? "est" : "n'est pas") + " caché.");
                }
            }
            case   "hideVoteImmunity" -> {
                if (isSetter && (args[4].equalsIgnoreCase("true") || args[4].equalsIgnoreCase("false"))) {
                    nocturneTarget.setHideVoteImmunity(Boolean.parseBoolean(args[4]));
                    player.sendMessage("§eL'immunité du joueur §6" + target.getName() + "§e " + (nocturneTarget.isVoteImmunityHidden() ? "est maintenant" : "n'est plus") + " cachée.");

                } else {
                    player.sendMessage("§eL'immunité du joueur §6" + target.getName() + "§e " + (nocturneTarget.isVoteImmunityHidden() ? "est" : "n'est pas") + " cachée.");
                }
            }
            case "debug" -> {

                player.sendMessage("§8====== §e" + args[1] +" Debug §8======");
                player.sendMessage("§7Can Vote :            " + (nocturneTarget.canVote()  ? "§aTrue" : "§cFalse"));
                player.sendMessage("§7Can Be Vote :         " + (nocturneTarget.canBeVoted() ? "§aTrue" : "§cFalse"));
                player.sendMessage("§7Is Hidden :           " + (nocturneTarget.isHiddenInVote() ? "§aTrue" : "§cFalse"));
                player.sendMessage("§7Is Vote Imm Hidden :  " + (nocturneTarget.isVoteImmunityHidden() ? "§aTrue" : "§cFalse"));
                player.sendMessage("§7Can Vote :            §e" +  nocturneTarget.getVoteWeight());
                player.sendMessage("§8======="+"§8=".repeat(args[1].length())+"§8=============");
            }
        }
    }


    private void handleReload(@NotNull Player player) {
        configManager.reload();
        player.sendMessage("§aConfiguration rechargée !");
    }

    private void sendHelp(@NotNull Player player) {
        player.sendMessage("§8====== §eNocturne §8======");
        player.sendMessage("§e/noc start §7— Démarrer la partie");
        player.sendMessage("§e/noc stop §7— Arrêter la partie");
        player.sendMessage("§e/noc skip §7— Passer la phase");
        player.sendMessage("§e/noc role [joueur] §7— Voir un rôle");
        player.sendMessage("§e/noc debug §7— État interne");
        player.sendMessage("§e/noc player §7— Modification des données internes");
        player.sendMessage("§e/noc reload §7— Recharger la config");
        player.sendMessage("§8======================");
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
            return  filterPlayerName(args[1], sender);
        }
        if (args.length >= 2 && args[0].equalsIgnoreCase("player")) {
            if (args.length == 2) {
                return  filterPlayerName(args[1], sender);
            }
            if (args.length == 3){
                return filterStart(args[2], PLAYERS_SUBCOMMAND);
            }
            if (args.length == 4) {
                return filterStart(args[3], List.of("get","set"));
            }
            if (args.length == 5 && args[3].equalsIgnoreCase("set")) {
                String property = args[2];
                return switch (property.toLowerCase()) {
                    case "weight" -> List.of("-5","-4","-3","-2","-1","0","1","2","3","4","5","...");

                    case "canvote", "canbevote", "ishidden", "hidevoteimmunity" -> filterStart(args[4],List.of("true", "false"));
                    default -> List.of();
                };
            }
        }
        return List.of();
    }

    private @NotNull List<String> filterPlayerName(@NotNull String input, @NotNull CommandSender sender) {
        return  sender.getServer().getOnlinePlayers().stream()
                .map(Player::getName)
                .filter(name -> name.toLowerCase().startsWith(input.toLowerCase()))
                .collect(Collectors.toList());
    }

    private @NotNull List<String> filterStart(@NotNull String input, @NotNull List<String> options) {
        String lower = input.toLowerCase();
        return options.stream()
                .filter(s -> s.toLowerCase().startsWith(lower))
                .collect(Collectors.toList());
    }
}
