package me.TyAlternative.com.nocturne.composition;

import me.TyAlternative.com.nocturne.api.role.Role;
import me.TyAlternative.com.nocturne.core.NocturneGame;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Gère les clics dans le GUI de composition et écoute les événements Bukkit associés.
 *
 * <p>Combine le handler de clic et le listener Bukkit dans une même classe
 * pour éviter la prolifération de fichiers pour un composant unique.
 *
 * <h2>Mapping des clics</h2>
 * <ul>
 *   <li>Clic droit : +1</li>
 *   <li>Clic gauche : -1</li>
 *   <li>Shift + droit : +5</li>
 *   <li>Shift + gauche : -5</li>
 *   <li>Case 49 : réinitialisation</li>
 * </ul>
 */
@SuppressWarnings("unused")
public final class CompositionClickHandler implements Listener {

    private static final int RESET_SLOT = 49;
    private static final int HELP_SLOT  = 53;

    private final NocturneGame game;
    private final CompositionGUI gui;

    public CompositionClickHandler(NocturneGame game, CompositionGUI gui) {
        this.game = game;
        this.gui = gui;
    }

    // -------------------------------------------------------------------------
    // Listener Bukkit
    // -------------------------------------------------------------------------

    @EventHandler(priority =  EventPriority.HIGH)
    public void onInventoryClick(@NotNull InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof  Player player)) return;

        if (!CompositionGUI.isCompositionGUI(event.getView().title())) return;

        event.setCancelled(true);

        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || clicked.getType() == Material.AIR) return;

        int slot = event.getRawSlot();

        if (slot == RESET_SLOT) {
            game.getCompositionManager().clear();
            player.sendMessage("§cComposition réinitialisée.");
            gui.open(player);
            return;
        }

        if (slot == HELP_SLOT) return;

        if (clicked.getType() == Material.GRAY_STAINED_GLASS_PANE) return;

        handleRoleClick(player, clicked, event.getClick());

    }


    // -------------------------------------------------------------------------
    // Logique de clic
    // -------------------------------------------------------------------------

    private void handleRoleClick(@NotNull Player player, @NotNull ItemStack item, @NotNull ClickType clickType) {
        Role role = gui.findRoleByItem(item);
        if (role == null) return;

        int delta = switch (clickType) {
            case RIGHT       ->  1;
            case LEFT        -> -1;
            case SHIFT_RIGHT ->  5;
            case SHIFT_LEFT  -> -5;
            default          ->  0;
        };

        if (delta == 0) return;

        game.getCompositionManager().add(role.getId(), delta);
        gui.open(player); // refresh l'inventaire du joueur
    }
}
