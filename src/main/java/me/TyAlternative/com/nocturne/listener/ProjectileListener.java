package me.TyAlternative.com.nocturne.listener;

import me.TyAlternative.com.nocturne.Nocturne;
import me.TyAlternative.com.nocturne.api.phase.PhaseType;
import me.TyAlternative.com.nocturne.core.NocturneGame;
import me.TyAlternative.com.nocturne.player.NocturnePlayer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.SpectralArrow;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Gère le comportement des flèches spectrales pendant la phase de Gameplay.
 *
 * <p>Mécaniques :
 * <ul>
 *   <li>Une flèche spectrale touchant un joueur révèle son vrai nom à tous.</li>
 *   <li>Une flèche touchant un bloc est annulée sans consommer de charge.</li>
 *   <li>Les dégâts sont toujours annulés (les flèches n'infligent aucun dégât).</li>
 * </ul>
 */
@SuppressWarnings({"unused"})
public final class ProjectileListener implements Listener {

    private final NocturneGame game;

    public ProjectileListener(@NotNull NocturneGame game) {
        this.game = game;
    }


    /**
     * Flèche spectrale touchant un joueur : révèle son nom, annule les dégâts.
     */
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onSpectralArrowHitPlayer(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof SpectralArrow arrow)) return;
        if (!(event.getEntity()  instanceof Player target))       return;
        if (!(arrow.getShooter() instanceof Player shooter))      return;

        if (!game.isGameRunning()) return;
        if (game.getCurrentPhase() != PhaseType.GAMEPLAY) return;

        event.setCancelled(true);

        NocturnePlayer nocturneShooter = game.getPlayerManager().get(shooter);
        if (nocturneShooter == null || !nocturneShooter.isAlive()) return;

        if (nocturneShooter.consumeSpectralArrow()) {
            // Révéler le vrai nom du joueur touché à tous
            game.getAnonymityManager().restoreNametag(target);
            arrow.remove();

            shooter.sendMessage("§bVous avez révélé l'identité de §e" + target.getName() + "§b.");
            Nocturne.getInstance().getLogger().info(
                    "[Nocturne] %s a révélé %s par flèche spectrale."
                            .formatted(shooter.getName(), target.getName())
            );
        }
    }

    /**
     * Flèche spectrale touchant un bloc : la supprimer sans consommer de charge.
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void onSpectralArrowHitBlock(ProjectileHitEvent event) {
        if (!(event.getEntity() instanceof SpectralArrow arrow)) return;
        if (event.getHitBlock() == null)                         return;
        if (!(arrow.getShooter() instanceof Player shooter))     return;
        if (!game.isGameRunning())                               return;
        if (game.getCurrentPhase() != PhaseType.GAMEPLAY)        return;

        NocturnePlayer nocturneShooter = game.getPlayerManager().get(shooter);
        if (nocturneShooter == null || !nocturneShooter.isAlive()) return;

        arrow.remove();
        // Vérifier que le joueur peut bien lancer une flèche
        if (nocturneShooter.consumeSpectralArrow()) {
            // Rembourser la flèche si activée : elle a touché un bloc, pas un joueur
            if (game.getSettings().shouldRedeemSpectralArrowIfMiss()) {
                shooter.give(new ItemStack(Material.SPECTRAL_ARROW));
            }
        }
    }
}
