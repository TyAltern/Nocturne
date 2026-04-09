package me.TyAlternative.com.nocturne.composition;

import me.TyAlternative.com.nocturne.api.role.Role;
import me.TyAlternative.com.nocturne.core.NocturneGame;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Interface graphique de configuration de la composition de la partie.
 *
 * <p>Inventaire de 54 cases (6 lignes) :
 * <ul>
 *   <li>Lignes 1-5 : un item par rôle disponible, affichant la quantité configurée.</li>
 *   <li>Case 49 : bouton de réinitialisation.</li>
 *   <li>Case 53 : bouton d'aide (contrôles).</li>
 *   <li>Bordures : panneaux de verre gris.</li>
 * </ul>
 *
 * <p>Les clics sont traités par {@link CompositionClickHandler}.
 */
@SuppressWarnings("unused")
public final class CompositionGUI {

    /** Titre de l'inventaire, utilisé pour identifier le GUI dans le listener. */
    public static final Component TITLE = Component.text("§8Composition de la partie");

    /** Slots disponibles pour les rôles (hors bordures). */
    private static final List<Integer> ROLE_SLOTS = computeRoleSlots();

    private final NocturneGame game;

    public CompositionGUI(NocturneGame game) {
        this.game = game;
    }

    // -------------------------------------------------------------------------
    // Ouverture
    // -------------------------------------------------------------------------

    /**
     * Ouvre (ou rafraîchit) le GUI de composition pour le joueur.
     *
     * @param player joueur destinataire
     */
    public void open(@NotNull Player player) {
        Inventory inv = Bukkit.createInventory(null, 54, TITLE);

        fillBorders(inv);
        fillRoles(inv);
        addControlButtons(inv);

        player.openInventory(inv);
    }


    // -------------------------------------------------------------------------
    // Construction de l'inventaire
    // -------------------------------------------------------------------------

    private void fillBorders(@NotNull Inventory inv) {
        ItemStack border = createBorder();
        // Lignes 1 et 6
        for (int i = 0; i < 9; i++) inv.setItem(i, border);
        for (int i = 45; i < 54; i++) inv.setItem(i, border);
        // Colonnes 1 et 9
        for (int i = 1; i <= 4; i++) {
            inv.setItem(i * 9,     border);
            inv.setItem(i * 9 + 8, border);
        }
    }

    private void fillRoles(@NotNull Inventory inv) {
        List<Role> roles = game.getRoleRegistry().createAll();
        int slotIdx = 0;

        for (Role role : roles) {
            if (slotIdx >= ROLE_SLOTS.size()) break;
            int count = game.getCompositionManager().getCount(role.getId());
            inv.setItem(ROLE_SLOTS.get(slotIdx), createRoleItem(role, count));
            slotIdx++;
        }
    }

    private void addControlButtons(@NotNull Inventory inv) {
        // Bouton reset — case 49
        ItemStack reset = new ItemStack(Material.BARRIER);
        ItemMeta resetMeta = reset.getItemMeta();
        resetMeta.displayName(Component.text("§c§lRéinitialiser"));
        resetMeta.lore(List.of(
                Component.text("§7Efface toute la composition."),
                Component.empty(),
                Component.text("§c/!\\ Action irréversible /!\\")
        ));
        reset.setItemMeta(resetMeta);
        inv.setItem(49, reset);

        // Bouton aide — case 53
        int total = game.getCompositionManager().getTotalPlayers();
        ItemStack help = new ItemStack(Material.BOOK);
        ItemMeta helpMeta = help.getItemMeta();
        helpMeta.displayName(Component.text("§e§lAide"));
        helpMeta.lore(List.of(
                Component.text("§7Clic gauche : §c-1"),
                Component.text("§7Clic droit : §a+1"),
                Component.text("§7Shift + gauche : §c-5"),
                Component.text("§7Shift + droit : §a+5"),
                Component.empty(),
                Component.text("§7Total : §e" + total + " §7joueur(s)")
        ));
        help.setItemMeta(helpMeta);
        inv.setItem(53, help);
    }


    // -------------------------------------------------------------------------
    // Création des items
    // -------------------------------------------------------------------------

    private @NotNull ItemStack createBorder() {
        ItemStack border = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta meta = border.getItemMeta();
        meta.displayName(Component.empty());
        meta.setHideTooltip(true);
        border.setItemMeta(meta);
        return border;
    }

    private @NotNull ItemStack createRoleItem(@NotNull Role role, int count) {
        ItemStack item = new ItemStack(role.getGuiIcon(), Math.max(1, count));
        ItemMeta meta = item.getItemMeta();

        meta.customName(Component.text(role.getDisplayName()));
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES);

        List<Component> lore = new ArrayList<>();
        lore.add(Component.text("§7Quantité : §e" + count));
        lore.add(Component.empty());

        if (count > 0) {
            meta.addEnchant(Enchantment.UNBREAKING, 1, true);
            lore.add(Component.text("§a✔ §7Actif dans la composition"));
            lore.add(Component.empty());
        }

        lore.add(Component.text("§7Clic droit : §a+1  §7| Shift droit : §a+5"));
        lore.add(Component.text("§7Clic gauche : §c-1  §7| Shift gauche : §c-5"));

        meta.lore(lore);
        item.setItemMeta(meta);
        return item;
    }



    // -------------------------------------------------------------------------
    // Utilitaires
    // -------------------------------------------------------------------------

    /**
     * Trouve le rôle correspondant à un item d'inventaire via son nom d'affichage.
     * Retourne {@code null} si aucun rôle ne correspond.
     */
    public @Nullable Role findRoleByItem(@NotNull ItemStack item) {
        if (!item.hasItemMeta()) return null;
        Component name = item.getItemMeta().displayName();
        if (name == null) return null;

        String plain = PlainTextComponentSerializer.plainText().serialize(name).trim();
        for (Role role : game.getRoleRegistry().createAll()) {
            String rolePlain = role.getDisplayName().replaceAll("§[0-9a-fk-orA-FK-OR]", "").trim();

            if (plain.equalsIgnoreCase(rolePlain)) return role;
        }

        return null;
    }

    /**
     * {@code true} si le titre de l'inventaire correspond au GUI de composition.
     */
    public static boolean isCompositionGUI(@NotNull Component title) {
        return TITLE.equals(title);
    }

    /** Calcule les slots disponibles pour les rôles (hors bordures). */
    private static @NotNull List<Integer> computeRoleSlots() {
        List<Integer> slots = new ArrayList<>();
        for (int row = 1; row <= 4; row++) {
            for (int col = 1; col <= 7; col++) {
                slots.add(row * 9 + col);
            }
        }
        return slots;
    }

}
