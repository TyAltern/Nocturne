package me.TyAlternative.com.nocturne.core.phase.impl;

import me.TyAlternative.com.nocturne.Nocturne;
import me.TyAlternative.com.nocturne.api.phase.GamePhase;
import me.TyAlternative.com.nocturne.api.phase.PhaseType;
import me.TyAlternative.com.nocturne.core.NocturneGame;
import me.TyAlternative.com.nocturne.core.phase.PhaseContext;
import me.TyAlternative.com.nocturne.player.NocturnePlayer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

/**
 * Phase de Gameplay : les joueurs interagissent librement, les Flammes
 * utilisent leurs capacités pour embraser les Bâtons.
 *
 * <h2>Début de phase</h2>
 * <ul>
 *   <li>Anonymat des skins et nametags activé.</li>
 *   <li>Inventaires des joueurs rechargés (panneaux, hache, arc spectral…).</li>
 *   <li>Hooks {@code onGameplayPhaseStart} déclenchés sur tous les rôles.</li>
 *   <li>Usages de capacités remis à zéro pour la nouvelle manche.</li>
 *   <li>Ticker des capacités TICKS démarré.</li>
 * </ul>
 *
 * <h2>Fin de phase</h2>
 * <ul>
 *   <li>Ticker arrêté.</li>
 *   <li>Hooks {@code onGameplayPhaseEnd} déclenchés (les capacités enregistrent
 *       leurs embrasements / disparitions dans ce hook).</li>
 *   <li>Anonymat restauré.</li>
 *   <li>Disparitions puis embrasements traités.</li>
 *   <li>Inventaires effacés.</li>
 * </ul>
 */
@SuppressWarnings({"unused", "DataFlowIssue"})
public final class GameplayPhase implements GamePhase {
    @Override
    public void onStart(@NotNull PhaseContext context) {
        NocturneGame game = Nocturne.getInstance().getGame();

        // Anonymat
        if (game.getSettings().shouldHideSkins()) {
            game.getAnonymityManager().hideAllSkins();
        }
        if (game.getSettings().shouldHideNametags()) {
            game.getAnonymityManager().hideAllNametags();
        }

        // Nettoyer les panneaux de la manche précédente
        game.getSignManager().clearAll();

        // Réinitialise les usages de capacités pour la nouvelle manche
        game.getAbilityManager().resetRoundUsagesForAll();

        // Préparer les inventaires et notifier les rôles.
        for (NocturnePlayer nocturnePlayer : game.getPlayerManager().getAlive()) {
            Player player = nocturnePlayer.getPlayer();
            if (player == null || !nocturnePlayer.hasRole()) continue;

            giveRoundItems(nocturnePlayer);
            safeDispatch(() ->
                nocturnePlayer.getRole().onGameplayPhaseStart(player, nocturnePlayer),
                player.getName(), "onGameplayPhaseStart"
            );
        }

        // Démarrer le ticker des capacités TICKS
        game.getTickingAbilityManager().start(PhaseType.GAMEPLAY);

        game.broadcast("§bPhase de §lGameplay §r§bcommencée !");
    }

    @Override
    public void onEnd(@NotNull PhaseContext context) {
        NocturneGame game = Nocturne.getInstance().getGame();

        // Arrêter le ticker en premier
        game.getTickingAbilityManager().stop();

        // Notifier les rôles
        for (NocturnePlayer nocturnePlayer : game.getPlayerManager().getAlive()) {
            Player player = nocturnePlayer.getPlayer();
            if (player == null || !nocturnePlayer.hasRole()) continue;

            clearInventory(player);
            safeDispatch(() ->
                    nocturnePlayer.getRole().onGameplayPhaseEnd(player, nocturnePlayer),
                    player.getName(), "onGameplayPhaseEnd"
            );
        }

        // Restaurer l'anonymat avant les éliminations
        game.getAnonymityManager().restoreAll();

        // Traiter disparitions puis embrasements
        game.getEliminationManager().processDisparitions(context.getRoundContext());
        game.getEliminationManager().processEmbrasements(context.getRoundContext());
    }

    @Override
    public @NotNull PhaseType getType() {
        return PhaseType.GAMEPLAY;
    }

    @Override
    public long getDurationMs(@NotNull PhaseContext context) {
        return Nocturne.getInstance().getGame().getSettings().getGameplayDurationSeconds() * 1000L;
    }


    // -------------------------------------------------------------------------
    // Gestion des inventaires
    // -------------------------------------------------------------------------

    /**
     * Distribue les items de manche au joueur :
     * panneaux, hache indestructible, lunette, arc, flèches spectrales.
     */
    private void giveRoundItems(@NotNull NocturnePlayer nocturnePlayer) {
        Player player = nocturnePlayer.getPlayer();
        if (player == null) return;

        player.getInventory().clear();

        // Panneaux
        player.getInventory().setItem(0, new ItemStack(Material.OAK_SIGN, 16));

        // Hache indestructible
        ItemStack axe = new ItemStack(Material.DIAMOND_AXE);
        ItemMeta axeMeta = axe.getItemMeta();
        axeMeta.setUnbreakable(true);
        axeMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ATTRIBUTES);
        axe.setItemMeta(axeMeta);
        player.getInventory().setItem(1, axe);

        // Spyglass
        player.getInventory().setItem(6, new ItemStack(Material.SPYGLASS));

        // Arbalète indestructible
        ItemStack crossbow = new ItemStack(Material.CROSSBOW);
        ItemMeta crossbowMeta = crossbow.getItemMeta();
        crossbowMeta.setUnbreakable(true);
        crossbowMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ATTRIBUTES);
        crossbow.setItemMeta(crossbowMeta);
        player.getInventory().setItem(8, crossbow);


        // Flèches spectrales
        int arrowCount = nocturnePlayer.getSpectralArrowsRemaining();
        if (arrowCount > 0) {
            player.getInventory().setItem(7, new ItemStack(Material.SPECTRAL_ARROW, arrowCount));
        }
    }

    private void clearInventory(@NotNull Player player) {
        player.getInventory().clear();
    }

    // -------------------------------------------------------------------------
    // Utilitaire
    // -------------------------------------------------------------------------

    @SuppressWarnings("SameParameterValue")
    private void safeDispatch(@NotNull ThrowingRunnable action, String playerName, String hookName) {
        try {
            action.run();
        } catch (Exception e) {
            Nocturne.getInstance().getLogger().severe(
                    "[Nocturne] Erreur dans %s pour %s : %s".formatted(hookName, playerName, e.getMessage())
            );
        }
    }

    @FunctionalInterface
    private interface ThrowingRunnable {
        void run() throws Exception;
    }
}
