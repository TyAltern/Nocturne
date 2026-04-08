package me.TyAlternative.com.nocturne.listener;

import me.TyAlternative.com.nocturne.api.phase.PhaseType;
import me.TyAlternative.com.nocturne.core.NocturneGame;
import me.TyAlternative.com.nocturne.mechanics.sign.SignManager;
import me.TyAlternative.com.nocturne.player.NocturnePlayer;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Gère les interactions avec les blocs pendant la partie.
 *
 * <p>En phase de Gameplay uniquement :
 * <ul>
 *   <li>Seuls les panneaux (OAK_SIGN) peuvent être posés ou détruits.</li>
 *   <li>Les panneaux posés sont enregistrés dans le {@link SignManager}.</li>
 *   <li>Toute autre interaction de bloc est annulée.</li>
 *   <li>Les trappes sont toujours bloquées (évite l'ouverture accidentelle).</li>
 * </ul>
 */
@SuppressWarnings("unused")
public final class BlockListener implements Listener {

    private static final int MAX_SIGNS_IN_INVENTORY = 16;

    private final NocturneGame game;

    public BlockListener(NocturneGame game) {
        this.game = game;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        if (!game.isGameRunning()) return;
        if (game.getCurrentPhase() != PhaseType.GAMEPLAY) {
            event.setCancelled(true);
            return;
        }

        Player player = event.getPlayer();
        NocturnePlayer np = game.getPlayerManager().get(player);
        if (np == null || !np.isAlive()) {
            event.setCancelled(true);
            return;
        }

        Block block = event.getBlock();
        if (isSign(block)) {
            game.getSignManager().add(block.getLocation());
        } else {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        if (!game.isGameRunning()) return;
        if (game.getCurrentPhase() != PhaseType.GAMEPLAY) {
            event.setCancelled(true);
            return;
        }

        Player player = event.getPlayer();
        NocturnePlayer np = game.getPlayerManager().get(player);
        if (np == null || !np.isAlive()) {
            event.setCancelled(true);
            return;
        }

        Block block = event.getBlock();
        if (isSign(block) && game.getSignManager().isRegistered(block.getLocation())) {
            game.getSignManager().remove(block.getLocation());
            event.setDropItems(false);

            // Rembourser un panneau si l'inventaire n'est pas plein
            if (!player.getInventory().contains(Material.OAK_SIGN, MAX_SIGNS_IN_INVENTORY)) {
                player.getInventory().addItem(new ItemStack(Material.OAK_SIGN));
            }
        } else {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onInteract(PlayerInteractEvent event) {
        if (!game.isGameRunning()) return;

        // Bloquer l'ouverture des trappes
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Block clicked = event.getClickedBlock();
            if (clicked != null && clicked.getType().toString().toUpperCase().contains("TRAPDOOR")) {
                event.setCancelled(true);
            }
        }
    }

    private boolean isSign(@NotNull Block block) {
        String type = block.getType().toString().toUpperCase();
        return type.contains("SIGN");
    }
}
