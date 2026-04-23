package me.TyAlternative.com.nocturne.ui;

import me.TyAlternative.com.nocturne.Nocturne;
import me.TyAlternative.com.nocturne.ability.AbstractAbility;
import me.TyAlternative.com.nocturne.api.ability.Ability;
import me.TyAlternative.com.nocturne.api.ability.AbilityUseType;
import me.TyAlternative.com.nocturne.api.phase.PhaseType;
import me.TyAlternative.com.nocturne.core.NocturneGame;
import me.TyAlternative.com.nocturne.player.NocturnePlayer;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.ShadowColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public final class ActionBarManager {
    private final NocturneGame game;

    private final Map<UUID, ActionBar> actionBars = new HashMap<>();

    private BukkitTask updateTask;

    public ActionBarManager(NocturneGame game) {
        this.game = game;
    }


    // -------------------------------------------------------------------------
    // Cycle de vie
    // -------------------------------------------------------------------------

    /** Démarre la mise à jour automatique des ActionBars toutes les secondes. */
    public void start() {
        if (!game.getSettings().isActionBarEnabled()) return;

        updateTask = Bukkit.getScheduler().runTaskTimer(
                Nocturne.getInstance(),
                this::update,
                0L, 20L
        );
    }

    /** Arrête la mise à jour */
    public void stop() {
        if (updateTask != null && !updateTask.isCancelled()) {
            updateTask.cancel();
            updateTask = null;
        }
//        removeAll();
    }

    // -------------------------------------------------------------------------
    // Mise à jour
    // -------------------------------------------------------------------------

    private void update() {
        if (!game.getSettings().isActionBarEnabled()) return;

        PhaseType phase = game.getCurrentPhase();

        for (NocturnePlayer nocturnePlayer : game.getPlayerManager().getAll()) {
            Player player = nocturnePlayer.getPlayer();
            if (player == null) continue;
            updatePlayerActionBar(player, nocturnePlayer, phase);
        }
    }

    public void updatePlayerActionBar(@NotNull Player player, @NotNull NocturnePlayer nocturnePlayer, @NotNull PhaseType phase) {
        if (!phase.isInGame() || !nocturnePlayer.isAlive()) {
            player.sendActionBar(getUnicodeWithFont(unicode("E000"), "hud","hotbar", false));
            return;
        }

        if (nocturnePlayer.getRole() == null) return;
        List<Ability> abilities = nocturnePlayer.getRole().getAbilities();
        int counter = 1;
        List<String> abilityId = new ArrayList<>();

        Ability firstAbility = null;

        Ability secondAbility = null;

        Ability thirdAbility = null;

        for (Ability ability : abilities) {
            if (ability == null) continue;
            if (ability.isHidden()) continue;
            if (abilityId.contains(ability.getId())) continue;
            if (ability.getUseType() == AbilityUseType.ACTIVE) {
                switch (counter) {
                    case 1 -> firstAbility = ability;
                    case 2 -> secondAbility = ability;
                    case 3 -> thirdAbility = ability;
                }
                abilityId.add(ability.getId());
                counter++;
            }
        }

        Component actionBarMessage = offset(106)
                .append(getUnicodeWithFont(unicode("E000"), "hud","hotbar", false))
                .append(offset(6))
                .append(getAbilitySlotComponent(nocturnePlayer, firstAbility, phase))
                .append(offset(3))
                .append(getAbilitySlotComponent(nocturnePlayer, secondAbility, phase))
                .append(offset(3))
                .append(getAbilitySlotComponent(nocturnePlayer, thirdAbility, phase));
        player.sendActionBar(actionBarMessage);
    }



    public Component getAbilitySlotComponent(@NotNull NocturnePlayer nocturnePlayer, @Nullable Ability ability, @NotNull PhaseType phase) {
        if (ability == null) {
            return getUnicodeWithFont("\uFFFF","hud","abilities",false);
        }

        if (!(ability instanceof AbstractAbility abstractAbility)) return Component.empty();

        int use = Math.min(9,game.getAbilityManager().getRemainingUsed(nocturnePlayer.getPlayerId(), ability));
        int maxUse = Math.min(9,abstractAbility.getUsageLimit().getMaxUses());
        boolean canUse = use != 0;
        boolean infinityUsage = maxUse == -1;
        if (infinityUsage) canUse = true;
        if (!abstractAbility.isPhaseAllowed(phase)) canUse = false;
        maxUse = Math.max(0, maxUse);
        use = Math.max(0, use);

        int cooldownSec = game.getAbilityManager().getRemainingCooldownSeconds(nocturnePlayer.getPlayerId(), ability.getId());
        int seconds = Math.max(0, cooldownSec);
        int minutes = Math.max(0,Math.min(9, seconds / 60));
        int secs = seconds % 60;
        int secsD = Math.min(5, secs / 10);
        int secsU = Math.max(0,Math.min(9, secs % 10));
        boolean ready = cooldownSec == 0;

        Material material = ability.getMaterial();


        String abilityCardUni = unicode("E000");
        if (!canUse) abilityCardUni = unicode("E00" + (infinityUsage? "4":"2"));
        else if (ready) abilityCardUni = unicode("E00" + (infinityUsage? "3":"1"));
        else if (infinityUsage) abilityCardUni = unicode("E005");
        // 0 ->
        // 1 -> ready
        // 2 -> !canUse
        // 3 -> infinityUsage + ready
        // 4 -> infinityUsage + !canUse
        // 5 -> infinityUsage + notReady



        String color = canUse ? "1" : "2";
        String useUni = unicode("E0" + color + use) + unicode("F0" + color +"1") + unicode("E0" + color + maxUse);

        String timerUni = unicode("E00" + minutes) + unicode("F000") + unicode("E00" + secsD) +unicode("E00" + secsU);


        Component component = getUnicodeWithFont(abilityCardUni, "hud","abilities", false);

//                .append(offset(-25))
//                .append(offset(15))
//                .append(offset(10))

        if (!infinityUsage) component = component
                .append(offset(-24))
                .append(getUnicodeWithFont(useUni, "hud", "values", true))
                .append(offset(10));

        if (!ready && canUse) component = component
                .append(offset(-24))
                .append(getUnicodeWithFont(timerUni, "hud", "values", true))
                .append(offset(10));

        return component;
    }

    private String unicode(String codePointHex) {
        int codePoint = Integer.parseInt(codePointHex, 16);
        return Character.toString(codePoint);
//        char[] chars = Character.toChars(codePoint);
//        return String.valueOf(chars);
    }

    private Component getUnicodeWithFont(@NotNull String unicode, @NotNull String namespace, @NotNull String key, boolean hasShadowColor) {
        Component unicodeComponent = Component.text(unicode).font(Key.key(namespace, key));
        if (!hasShadowColor) {
            unicodeComponent = unicodeComponent.shadowColor(ShadowColor.shadowColor(0, 0, 0, 0));
        }
        return unicodeComponent;
    }

    private Component offset(int offset) {
        if (offset == 0) return Component.empty();
        if (offset > 0) {
            return Component.text(unicode(String.format("E%03d", Math.min(999, offset)))).font(Key.key("offset","positive"));
        }
        return Component.text(unicode(String.format("E%03d", Math.min(999, -offset)))).font(Key.key("offset","negative"));

    }

}