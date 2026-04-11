package me.TyAlternative.com.nocturne;

import me.TyAlternative.com.nocturne.command.CompositionCommand;
import me.TyAlternative.com.nocturne.command.NocturneCommand;
import me.TyAlternative.com.nocturne.composition.CompositionClickHandler;
import me.TyAlternative.com.nocturne.config.ConfigManager;
import me.TyAlternative.com.nocturne.core.NocturneGame;
import me.TyAlternative.com.nocturne.listener.*;
import me.TyAlternative.com.nocturne.role.RoleRegistry;
import me.TyAlternative.com.nocturne.role.impl.baton.*;
import me.TyAlternative.com.nocturne.role.impl.flamme.*;
import me.TyAlternative.com.nocturne.role.impl.neutre.*;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

/**
 * Classe principale du plugin Nocturne.
 *
 * <p>Responsabilités :
 * <ul>
 *   <li>Chargement de la configuration.</li>
 *   <li>Construction du {@link RoleRegistry} et enregistrement de tous les rôles.</li>
 *   <li>Construction du {@link NocturneGame} (qui contient tous les sous-managers).</li>
 *   <li>Enregistrement des commandes et des listeners Bukkit.</li>
 *   <li>Arrêt propre en {@link #onDisable()}.</li>
 * </ul>
 *
 * <p>Accès global via {@link #getInstance()} et {@link #getGame()}.
 */
@SuppressWarnings({"unused", "DataFlowIssue"})
public final class Nocturne extends JavaPlugin {
    private static Nocturne instance;

    private ConfigManager configManager;
    private RoleRegistry roleRegistry;
    private NocturneGame game;

    // -------------------------------------------------------------------------
    // Cycle de vie du plugin
    // -------------------------------------------------------------------------

    @Override
    public void onEnable() {
        instance = this;
         printBanner();

         // 1. Configuration
        configManager = new ConfigManager(this);

        // 2. Vérification des dépendances
        if (!checkDependencies()) {
            getLogger().severe("Dépendances manquantes - désactivation du plugin.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // 3. Registry des rôles (avant NocturneGame)
        roleRegistry = new RoleRegistry(getLogger());
        registerRoles();

        // 4. Construction du jeu (inclut tous les sous-managers, UI compris)
        game = new NocturneGame(configManager.getSettings(), roleRegistry, getLogger());

        // 5. Commandes
        registerCommands();

        // 6. Listeners Bukkit
        registerListeners();

        getLogger().info("======================================");
        getLogger().info("  Nocturne activé — " + roleRegistry.count() + " rôles chargés");
        getLogger().info("======================================");
    }

    @Override
    public void onDisable() {
        if (game != null) {
            game.forceStop();
        }
        getLogger().info("Nocturne désactivé.");
    }

    // -------------------------------------------------------------------------
    // Enregistrement des rôles
    // -------------------------------------------------------------------------

    private void registerRoles() {

        getLogger().info("Enregistrement des rôles...");

        // ── Flammes ──────────────────────────────────────────────────────────

        roleRegistry.register(Etincelle.ID,    Etincelle::new);
        roleRegistry.register(Torche.ID,       Torche::new);
        roleRegistry.register(Flambeau.ID,     Flambeau::new);
        roleRegistry.register(Silex.ID,        Silex::new);
//        roleRegistry.register(Acier.ID,        Acier::new);
//        roleRegistry.register(FeuFollet.ID,    FeuFollet::new);
//        roleRegistry.register(Luciole.ID,      Luciole::new);
//        roleRegistry.register(Lanterne.ID,     Lanterne::new);

        // ── Bâtons — information ─────────────────────────────────────────────
        roleRegistry.register(Aube.ID,         Aube::new);
        roleRegistry.register(Crepuscule.ID,   Crepuscule::new);
        roleRegistry.register(Lueur.ID,        Lueur::new);
        roleRegistry.register(Aurore.ID,       Aurore::new);


        // ── Bâtons — protection ──────────────────────────────────────────────
        roleRegistry.register(Souffle.ID,      Souffle::new);
        roleRegistry.register(Fremissement.ID, Fremissement::new);
        roleRegistry.register(Bourrasque.ID,   Bourrasque::new);
        roleRegistry.register(Rafale.ID,       Rafale::new);


        // ── Bâtons — divers ──────────────────────────────────────────────────
        roleRegistry.register(Baton.ID,        Baton::new);
        roleRegistry.register(Scorie.ID,       Scorie::new);
        roleRegistry.register(Calcine.ID,      Calcine::new);
        roleRegistry.register(Cendre.ID,       Cendre::new);
        roleRegistry.register(Gaz.ID,          Gaz::new);
        roleRegistry.register(Braise.ID,       Braise::new);
        roleRegistry.register(Fumee.ID,        Fumee::new);
        roleRegistry.register(Tison.ID,        Tison::new);


        // ── Neutres ──────────────────────────────────────────────────────────
        roleRegistry.register(Nuee.ID,         Nuee::new);

        getLogger().info("  -> " + roleRegistry.count() + " rôles enregistrés.");

    }

    // -------------------------------------------------------------------------
    // Enregistrement des commandes
    // -------------------------------------------------------------------------

    private void registerCommands() {
        getLogger().info("Enregistrement des commandes...");

        NocturneCommand nocturneCmd = new NocturneCommand(game, configManager);
        getCommand("nocturne").setExecutor(nocturneCmd);
        getCommand("nocturne").setTabCompleter(nocturneCmd);

        CompositionCommand compoCmd = new CompositionCommand(game);
        getCommand("compo").setExecutor(compoCmd);
        getCommand("compo").setTabCompleter(compoCmd);

        getLogger().info("  → Commandes enregistrées.");

    }

    // -------------------------------------------------------------------------
    // Enregistrement des listeners
    // -------------------------------------------------------------------------

    private void registerListeners() {
        getLogger().info("Enregistrement des listeners...");
        PluginManager pm = getServer().getPluginManager();
        DoubleSwapDetector doubleSwapDetector = new DoubleSwapDetector(configManager.getSettings());

        pm.registerEvents(new PlayerInteractionListener(game, doubleSwapDetector), this);
        pm.registerEvents(new BlockListener(game), this);
        pm.registerEvents(new ProjectileListener(game), this);
        pm.registerEvents(new ConnectionListener(game), this);

        // Listener du GUI de composition
        CompositionCommand compoCmd = (CompositionCommand) getCommand("compo").getExecutor();
        pm.registerEvents(new CompositionClickHandler(game, compoCmd.getGui()), this);

        getLogger().info("  -> Listeners enregistrés.");

    }

    // -------------------------------------------------------------------------
    // Vérifications
    // -------------------------------------------------------------------------

    private boolean checkDependencies() {
        if (getServer().getPluginManager().getPlugin("skinsRestorer") == null) {
            getLogger().severe("SkinsRestorer est introuvable !");
            getLogger().severe("Téléchargez-le : https://www.spigotmc.org/resources/skinsrestorer.2124/");
            return false;
        }
        return true;
    }

    // -------------------------------------------------------------------------
    // Affichage
    // -------------------------------------------------------------------------

    private void printBanner() {
        getLogger().info("======================================");
        getLogger().info("   Nocturne v" + getPluginMeta().getVersion());
        getLogger().info("   Chargement en cours...");
        getLogger().info("======================================");

    }


    // -------------------------------------------------------------------------
    // Accesseurs statiques
    // -------------------------------------------------------------------------

    /** Instance singleton du plugin. Disponible dès {@link #onEnable()}. */
    public static @NotNull Nocturne getInstance() {
        return instance;
    }


    /**
     * Façade centrale du jeu, contenant tous les managers.
     * Disponible après {@link #onEnable()}.
     */

    public NocturneGame getGame() {
        return game;
    }
    /** Gestionnaire de configuration. */
    public @NotNull ConfigManager getConfigManager() {
        return configManager;
    }
}
